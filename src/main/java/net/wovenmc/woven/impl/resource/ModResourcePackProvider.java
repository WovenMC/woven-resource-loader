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

import java.util.function.Consumer;

/**
 * Represents a resource pack provider for mod-related resource packs.
 */
public class ModResourcePackProvider implements ResourcePackProvider {
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
	}
}
