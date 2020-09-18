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

import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Fix resource issues with mods.
 * <p>
 * From: https://github.com/FabricMC/fabric/issues/66#issuecomment-614970964
 */
@Mixin(DefaultResourcePack.class)
public class DefaultResourcePackMixin {
	@Shadow
	@Final
	private static Map<ResourceType, FileSystem> typeToFileSystem;

	@Inject(method = "findInputStream", at = @At("HEAD"), cancellable = true)
	private void onFindInputStream(ResourceType type, Identifier id, CallbackInfoReturnable<@Nullable InputStream> cir) {
		if (DefaultResourcePack.resourcePath != null) {
			// Fall through to Vanilla logic, they have a special case here.
			return;
		}

		FileSystem fs = typeToFileSystem.get(type);

		if (fs == null) {
			// Apparently Minecraft couldn't find its own resources, they'll be an error in the log for this.
			return;
		}

		Path path = fs.getPath(type.getDirectory(), id.getNamespace(), id.getPath());

		if (Files.isRegularFile(path)) {
			try {
				cir.setReturnValue(Files.newInputStream(path));
			} catch (IOException e) {
				// Something went wrong, vanilla doesn't log these errors though.
			}
		}
	}

	@Inject(method = "contains", at = @At("HEAD"), cancellable = true)
	private void onContains(ResourceType type, Identifier id, CallbackInfoReturnable<Boolean> cir) {
		if (DefaultResourcePack.resourcePath != null) {
			// Fall through to Vanilla logic, they have a special case here.
			return;
		}

		FileSystem fs = typeToFileSystem.get(type);

		if (fs == null) {
			// Apparently Minecraft couldn't find its own resources, they'll be an error in the log for this.
			return;
		}

		Path path = fs.getPath(type.getDirectory(), id.getNamespace(), id.getPath());
		cir.setReturnValue(Files.isRegularFile(path));
	}
}
