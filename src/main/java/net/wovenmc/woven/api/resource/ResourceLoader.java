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

package net.wovenmc.woven.api.resource;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.wovenmc.woven.impl.resource.ResourceLoaderImpl;

/**
 * Represents the resource loader.
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ResourceLoader {
	/**
	 * Returns the resource loader implementation instance.
	 *
	 * @return The resource loader instance.
	 */
	static ResourceLoader get() {
		return ResourceLoaderImpl.INSTANCE;
	}

	/**
	 * Get the {@code ResourceManagerHelper} instance for a given resource type.
	 *
	 * @param type The given resource type.
	 * @return The {@code ResourceManagerHelper} instance.
	 * @see ResourceManagerHelper#get(ResourceType)
	 */
	default ResourceManagerHelper getResourceManagerHelper(ResourceType type) {
		return ResourceManagerHelper.get(type);
	}

	/**
	 * Registers a built-in resource pack.
	 * <p>
	 * A built-in resource pack is an extra resource pack provided by your mod which is not necessarily always active, it's similar to the "Programmer Art" resource pack.
	 * <p>
	 * Why and when to use it? A built-in resource pack should be used to provide extra assets/data that should be optional with your mod but still directly provided by it.
	 * For example it could provide textures of your mod in another resolution, or could allow to provide different styles of your assets.
	 * It also can be used as an always-active built-in resource pack to provide conditional assets/data in case other mods are present for example.
	 * <p>
	 * The path of the built-in resource pack is {@code <mod JAR root>/resourcepacks/<identifier path>/}.
	 * <p>
	 * Note about the activation type parameter set to {@link ResourcePackActivationType#DEFAULT_ENABLED}: a resource pack cannot be enabled by default, only data packs can.
	 * Making this work for resource packs is near impossible without touching how Vanilla handles disabled resource packs.
	 *
	 * @param id             The identifier of the resource pack.
	 * @param container      The mod container.
	 * @param activationType The activation type of the resource pack.
	 * @return True if successfully registered the resource pack, else false.
	 */
	boolean registerBuiltinResourcePack(Identifier id, ModContainer container, ResourcePackActivationType activationType);
}
