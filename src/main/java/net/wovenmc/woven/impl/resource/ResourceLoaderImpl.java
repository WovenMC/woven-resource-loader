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

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.wovenmc.woven.api.resource.ModResourcePack;
import net.wovenmc.woven.api.resource.ResourceLoader;
import net.wovenmc.woven.api.resource.ResourcePackActivationType;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents the implementation of resource loader.
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public class ResourceLoaderImpl implements ResourceLoader {
	public static final ResourceLoaderImpl INSTANCE = new ResourceLoaderImpl();

	private final Set<Pair<String, ModNioResourcePack>> builtinResourcePacks = new HashSet<>();

	@Override
	public boolean registerBuiltinResourcePack(Identifier id, ModContainer container, ResourcePackActivationType activationType) {
		String separator = container.getRootPath().getFileSystem().getSeparator();

		Path resourcePackPath = container.getRootPath().resolve("resourcepacks" + separator + id.getPath().replace("/", separator))
				.toAbsolutePath().normalize();

		if (!Files.exists(resourcePackPath)) {
			return false;
		}

		String name = id.getNamespace() + "/" + id.getPath();
		builtinResourcePacks.add(new Pair<>(name, new ModNioResourcePack(container.getMetadata(), resourcePackPath, activationType) {
			@Override
			public String getName() {
				return name;
			}
		}));

		return true;
	}

	public void registerBuiltinResourcePacks(ResourceType resourceType, Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
		// Loop through each registered built-in resource packs and add them if valid.
		for (Pair<String, ModNioResourcePack> entry : this.builtinResourcePacks) {
			// Add the built-in pack only if namespaces for the specified resource type are present.
			if (!entry.getRight().getNamespaces(resourceType).isEmpty()) {
				// Make the resource pack profile for built-in pack, should never be always enabled.
				ResourcePackProfile profile = ResourcePackProfile.of(entry.getLeft(),
						entry.getRight().getActivationType() == ResourcePackActivationType.ALWAYS_ENABLED,
						entry::getRight, factory, ResourcePackProfile.InsertionPosition.TOP,
						ResourcePackSource.PACK_SOURCE_BUILTIN);

				if (profile != null) {
					consumer.accept(profile);
				}
			}
		}
	}

	/**
	 * Appends mod resource packs to the given list.
	 *
	 * @param packs   The resource pack list to append.
	 * @param type    The type of resource.
	 * @param subPath The resource pack sub path directory in mods, may be null.
	 */
	public static void appendModResourcePacks(List<ModResourcePack> packs, ResourceType type, @Nullable String subPath) {
		for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
			if (container.getMetadata().getType().equals("builtin")) {
				continue;
			}

			Path path = container.getRootPath();

			if (subPath != null) {
				Path childPath = path.resolve(subPath.replaceAll("/", path.getFileSystem().getSeparator())).toAbsolutePath().normalize();

				if (!childPath.startsWith(path) || !Files.exists(childPath)) {
					continue;
				}

				path = childPath;
			}

			ModResourcePack resourcePack = new ModNioResourcePack(container.getMetadata(), path, ResourcePackActivationType.ALWAYS_ENABLED);

			if (!resourcePack.getNamespaces(type).isEmpty()) {
				packs.add(resourcePack);
			}
		}
	}
}
