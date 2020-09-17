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

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.TranslatableText;
import net.wovenmc.woven.api.resource.ModResourcePack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a resource pack provider for mod-related resource packs.
 */
public class ModResourcePackProvider implements ResourcePackProvider {
	public static final ResourcePackSource RESOURCE_PACK_SOURCE = text -> new TranslatableText("pack.nameAndSource", text, new TranslatableText("pack.source.woven"));
	public static final ModResourcePackProvider CLIENT_RESOURCE_PACK_PROVIDER = new ModResourcePackProvider(ResourceType.CLIENT_RESOURCES);
	private final ResourceType type;

	public ModResourcePackProvider(ResourceType type) {
		this.type = type;
	}

	@Override
	public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
		/*
			Register order rule in this provider:
			1. Mod resource packs
			2. Mod built-in resource packs
			Register order rule globally:
			1. Default and Vanilla built-in resource packs
			2. Mod resource packs
			3. Virtual resource packs generated without resource pack context
			4. Mod built-in resource packs
			5. User resource packs
			6. Virtual resource packs generated with resource pack context
		 */

		// Build a list of mod resource packs.
		List<ModResourcePack> packs = new ArrayList<>();
		ResourceLoaderImpl.appendModResourcePacks(packs, this.type, null);

		for (ModResourcePack pack : packs) {
			// Make the resource pack profile for mod resource packs.
			// Mod resource packs must always be enabled to avoid issues
			// and inserted on top to ensure that they are applied before user resource packs and after default/programmer art resource pack.
			ResourcePackProfile resourcePackProfile = ResourcePackProfile.of("woven/" + pack.getModMetadata().getId(),
					true, () -> pack, factory, ResourcePackProfile.InsertionPosition.TOP,
					RESOURCE_PACK_SOURCE);

			if (resourcePackProfile != null) {
				consumer.accept(resourcePackProfile);
			}
		}
	}
}
