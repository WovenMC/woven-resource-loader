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

package net.wovenmc.woven.impl.resource.client.pack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.wovenmc.woven.api.resource.ModResourcePack;
import net.wovenmc.woven.mixin.resource.AbstractFileResourcePackAccessor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represents the Programmer Art resource pack.
 * <p>
 * It contains the original resource pack provided by Minecraft and the mod Programmer Art extension resource packs.
 *
 * @version 0.1.0
 * @since 0.1.0
 */
@Environment(EnvType.CLIENT)
public class ProgrammerArtResourcePack implements ResourcePack {
	private AbstractFileResourcePack originalResourcePack;
	private List<ModResourcePack> modResourcePacks;

	public ProgrammerArtResourcePack(AbstractFileResourcePack originalResourcePack, List<ModResourcePack> modResourcePacks) {
		this.originalResourcePack = originalResourcePack;
		this.modResourcePacks = modResourcePacks;
	}

	@Override
	public InputStream openRoot(String fileName) throws IOException {
		if (!fileName.contains("/") && !fileName.contains("\\")) {
			// There should be nothing to read at the root of mod's Programmer Art extensions.
			return this.originalResourcePack.openRoot(fileName);
		} else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		if (this.originalResourcePack.contains(type, id)) {
			return this.originalResourcePack.open(type, id);
		}

		for (int i = this.modResourcePacks.size() - 1; i >= 0; i--) {
			ResourcePack pack = this.modResourcePacks.get(i);

			if (pack.contains(type, id)) {
				return pack.open(type, id);
			}
		}

		throw new ResourceNotFoundException(((AbstractFileResourcePackAccessor) this.originalResourcePack).getBase(),
				String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath()));
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		Collection<Identifier> resources = this.originalResourcePack.findResources(type, namespace, prefix, maxDepth, pathFilter);

		for (int i = this.modResourcePacks.size() - 1; i >= 0; i--) {
			ResourcePack pack = this.modResourcePacks.get(i);
			Collection<Identifier> modResources = pack.findResources(type, namespace, prefix, maxDepth, pathFilter);

			for (Identifier resource : modResources) {
				if (!resources.contains(resource)) {
					resources.add(resource);
				}
			}
		}

		return resources;
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		if (this.originalResourcePack.contains(type, id)) {
			return true;
		}

		for (int i = this.modResourcePacks.size() - 1; i >= 0; i--) {
			ResourcePack pack = this.modResourcePacks.get(i);

			if (pack.contains(type, id)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		Set<String> namespaces = this.originalResourcePack.getNamespaces(type);

		for (int i = this.modResourcePacks.size() - 1; i >= 0; i--) {
			ResourcePack pack = this.modResourcePacks.get(i);
			namespaces.addAll(pack.getNamespaces(type));
		}

		return namespaces;
	}

	@Override
	public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return this.originalResourcePack.parseMetadata(metaReader);
	}

	@Override
	public String getName() {
		return "Programmer Art";
	}

	@Override
	public void close() {
		this.originalResourcePack.close();
	}
}
