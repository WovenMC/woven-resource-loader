/*
 * Copyright (c) 2020 WovenMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wovenmc.woven.impl.resource;

import com.google.common.base.Charsets;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.SharedConstants;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.wovenmc.woven.api.resource.ModResourcePack;
import net.wovenmc.woven.api.resource.ResourcePackActivationType;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Represents a mod resource pack using non-blocking IO.
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public class ModNioResourcePack extends AbstractFileResourcePack implements ModResourcePack {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern NAMESPACE_PATTERN = Pattern.compile("[a-z0-9_\\-\\.]+");
	private final ModMetadata modMetadata;
	private final Path basePath;
	private final String separator;
	private final ResourcePackActivationType activationType;

	public ModNioResourcePack(ModMetadata modMetadata, Path path, ResourcePackActivationType activationType) {
		super(null);
		this.modMetadata = modMetadata;
		this.basePath = path;
		this.separator = this.basePath.getFileSystem().getSeparator();
		this.activationType = activationType;
	}

	protected @Nullable Path resolvePath(String path) {
		Path childPath = this.basePath.resolve(path.replace("/", this.separator))
				.toAbsolutePath().normalize();

		if (childPath.startsWith(this.basePath) && Files.exists(childPath)) {
			return childPath;
		}

		return null;
	}

	private @Nullable InputStream openDefault(@NotNull String path) throws IOException {
		switch (path) {
		case "pack.mcmeta":
			String description = this.modMetadata.getDescription();

			if (description == null) {
				description = "";
			} else {
				description = description.replaceAll("\"", "\\\"");
			}

			String content = String.format("{\"pack\":{\"pack_format\":%d,\"description\":\"%s\"}}",
					SharedConstants.getGameVersion().getPackVersion(), description);
			return IOUtils.toInputStream(content, Charsets.UTF_8);
		case "pack.png":
			Optional<String> iconPath = this.modMetadata.getIconPath(512);
			return iconPath.isPresent() ? this.openFile(iconPath.get()) : null;
		default:
			return null;
		}
	}

	@Override
	protected InputStream openFile(String filePath) throws IOException {
		InputStream stream;

		Path path = this.resolvePath(filePath);

		if (path != null && Files.isRegularFile(path)) {
			return Files.newInputStream(path);
		}

		stream = this.openDefault(filePath);

		if (stream != null) {
			return stream;
		}

		throw new FileNotFoundException("\"" + filePath + "\" in Fabric mod \"" + this.modMetadata.getId() + "\"");
	}

	/**
	 * Returns whether the specified path is a default path.
	 * <p>
	 * A default path can be {@code pack.mcmeta} or {@code pack.png} for example.
	 *
	 * @param path The path.
	 * @return True if the path is a default path, else false.
	 */
	private boolean containsDefault(String path) {
		switch (path) {
		case "pack.mcmeta":
			return true;
		case "pack.png":
			return this.modMetadata.getIconPath(512).isPresent();
		default:
			return false;
		}
	}

	@Override
	protected boolean containsFile(String filePath) {
		if (this.containsDefault(filePath)) {
			return true;
		}

		Path path = this.resolvePath(filePath);
		return path != null && Files.isRegularFile(path);
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		List<Identifier> identifiers = new ArrayList<>();
		String prefixPath = prefix.replace("/", this.separator);

		Path namespacePath = this.resolvePath(type.getDirectory() + this.separator + namespace);

		if (namespacePath != null) {
			Path searchPath = namespacePath.resolve(prefixPath).toAbsolutePath().normalize();

			if (Files.exists(searchPath)) {
				try {
					Files.walk(searchPath, maxDepth)
							.filter(Files::isRegularFile)
							.filter(path -> {
								String fileName = path.getFileName().toString();
								return !fileName.endsWith(".mcmeta") && pathFilter.test(fileName);
							})
							.map(namespacePath::relativize)
							.map(path -> path.toString().replace(separator, "/"))
							.forEach(path -> {
								try {
									identifiers.add(new Identifier(namespace, path));
								} catch (InvalidIdentifierException e) {
									LOGGER.warn(e.getMessage());
								}
							});
				} catch (IOException e) {
					LOGGER.warn(
							String.format("Cannot find resources at \"%s\" in namespace \"%s\", resource pack \"%s\" from mod %s!",
									prefix, namespace, this.getName(), this.modMetadata.getId()),
							e
					);
				}
			}
		}

		return identifiers;
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		Path typePath = this.resolvePath(type.getDirectory());

		if (typePath == null || !(Files.isDirectory(typePath))) {
			return Collections.emptySet();
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(typePath, Files::isDirectory)) {
			Set<String> namespaces = new HashSet<>();

			for (Path path : stream) {
				String name = path.getFileName().toString();
				// name may contain trailing slashes, remove them.
				name = name.replace(this.separator, "");

				if (NAMESPACE_PATTERN.matcher(name).matches()) {
					namespaces.add(name);
				} else {
					LOGGER.warn("Invalid namespace \"{}\" in resource pack \"{}\" from mod {}.",
							name, this.getName(), this.modMetadata.getId());
				}
			}

			return namespaces;
		} catch (IOException e) {
			LOGGER.warn("Cannot get namespaces from mod " + this.modMetadata.getId() + " in resource pack \"" + this.getName() + "\".", e);
			return Collections.emptySet();
		}
	}

	@Override
	public void close() {
	}

	@Override
	public String getName() {
		if (this.modMetadata.getName() != null) {
			return this.modMetadata.getName();
		} else {
			return "Fabric Mod \"" + this.modMetadata.getId() + "\"";
		}
	}

	@Override
	public ModMetadata getModMetadata() {
		return this.modMetadata;
	}

	@Override
	public ResourcePackActivationType getActivationType() {
		return this.activationType;
	}
}
