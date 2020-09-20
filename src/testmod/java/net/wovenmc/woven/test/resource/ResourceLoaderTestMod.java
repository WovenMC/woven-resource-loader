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

package net.wovenmc.woven.test.resource;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.wovenmc.woven.api.resource.ResourceLoader;
import net.wovenmc.woven.api.resource.ResourcePackActivationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceLoaderTestMod implements ModInitializer {
	public static final String NAMESPACE = "woven_resource_loader_testmod";

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		boolean result = FabricLoader.getInstance().getModContainer(NAMESPACE)
				.map(container -> ResourceLoader.get().registerBuiltinResourcePack(new Identifier(NAMESPACE, "test"),
						container,
						ResourcePackActivationType.ALWAYS_ENABLED))
				.orElse(false);

		if (result) {
			LOGGER.info("Successfully registered the built-in resource pack.");
		} else {
			LOGGER.warn("Failed to register the built-in resource pack.");
		}
	}
}
