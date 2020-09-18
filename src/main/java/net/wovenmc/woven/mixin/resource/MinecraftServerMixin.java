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

package net.wovenmc.woven.mixin.resource;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.server.MinecraftServer;
import net.wovenmc.woven.impl.resource.ModNioResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Redirect(method = "loadDataPacks", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
	private static boolean onCheckDisabled(List<String> list, Object o, ResourcePackManager packManager) {
		String profileName = (String) o;
		boolean contains = list.contains(profileName);

		if (contains) {
			return true;
		}

		ResourcePackProfile profile = packManager.getProfile(profileName);

		if (profile != null
				&& ((ResourcePackProfileAccessor) profile).getResourcePackSource() == ResourcePackSource.PACK_SOURCE_BUILTIN
				&& !profileName.equals("vanilla")) {
			ResourcePack pack = profile.createResourcePack();
			// Prevents automatic load for built-in data packs provided by mods.
			return pack instanceof ModNioResourcePack && !((ModNioResourcePack) pack).isDefaultEnabled();
		}

		return false;
	}
}
