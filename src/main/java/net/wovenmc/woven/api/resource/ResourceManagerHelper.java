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

import net.minecraft.resource.ResourceType;
import net.wovenmc.woven.impl.resource.ResourceManagerHelperImpl;

/**
 * Helper for working with {@link net.minecraft.resource.ResourceManager} instances.
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ResourceManagerHelper {
	/**
	 * Get the {@code ResourceManagerHelper} instance for a given resource type.
	 *
	 * @param type The given resource type.
	 * @return The {@code ResourceManagerHelper} instance.
	 */
	static ResourceManagerHelper get(ResourceType type) {
		return ResourceManagerHelperImpl.get(type);
	}

	/**
	 * Register a resource reload listener for a given resource manager type.
	 *
	 * @param listener The resource reload listener.
	 */
	void registerReloadListener(IdentifiableResourceReloadListener listener);
}
