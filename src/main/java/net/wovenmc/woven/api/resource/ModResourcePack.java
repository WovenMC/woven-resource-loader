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

import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.resource.ResourcePack;

/**
 * Represents a mod-provided resource pack.
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ModResourcePack extends ResourcePack {
	/**
	 * Returns the mod metadata associated with the mod providing this resource pack.
	 *
	 * @return The mod metadata.
	 */
	ModMetadata getModMetadata();

	/**
	 * Returns the activation type of this resource pack.
	 *
	 * @return The activation type.
	 */
	default ResourcePackActivationType getActivationType() {
		return ResourcePackActivationType.NORMAL;
	}
}
