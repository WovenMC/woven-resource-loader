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
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.Config;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ResourceLoaderMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
		System.out.println("BEGIN WOVEN RESOURCE LOADER MIXIN LOAD");

		Iterator<Config> it = Mixins.getConfigs().iterator();

		while (it.hasNext()) {
			Config config = it.next();
			System.out.println(config);

			if (config.getName().contains("fabric-resource-loader")) {
				try {
					Field internalConfigField = Config.class.getDeclaredField("config");
					internalConfigField.setAccessible(true);
					Object internalConfig = internalConfigField.get(config);
					Class<?> internalConfigClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinConfig");
					Field env = internalConfigClass.getDeclaredField("env");
					env.setAccessible(true);
					env.set(internalConfig, null);
				} catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
					e.printStackTrace();
				}

				System.out.println("YEET");
			}
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		boolean compatPackage = mixinClassName.contains("mixin.resource.compat");

		if (compatPackage) {
			return FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0");
		}

		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}