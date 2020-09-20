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

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.loot.LootManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.wovenmc.woven.api.resource.IdentifiableResourceReloadListener;
import net.wovenmc.woven.api.resource.ResourceReloadListenerKeys;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class IdentifiableResourceReloadListenersMixin {
	@Mixin({
			/* public */
			SoundLoader.class, BakedModelManager.class, LanguageManager.class, TextureManager.class,
			/* private */
			WorldRenderer.class, BlockRenderManager.class, ItemRenderer.class
	})
	public abstract static class Client implements IdentifiableResourceReloadListener {
		private Identifier woven$id;
		private Collection<Identifier> woven$dependencies;

		@Override
		@SuppressWarnings("ConstantConditions")
		public Identifier getIdentifier() {
			if (this.woven$id == null) {
				Object self = this;

				if (self instanceof SoundLoader) {
					this.woven$id = ResourceReloadListenerKeys.SOUNDS;
				} else if (self instanceof BakedModelManager) {
					this.woven$id = ResourceReloadListenerKeys.MODELS;
				} else if (self instanceof LanguageManager) {
					this.woven$id = ResourceReloadListenerKeys.LANGUAGES;
				} else if (self instanceof TextureManager) {
					this.woven$id = ResourceReloadListenerKeys.TEXTURES;
				} else {
					this.woven$id = new Identifier("minecraft", "private/"
							+ self.getClass().getSimpleName().toLowerCase(Locale.ROOT));
				}
			}

			return this.woven$id;
		}

		@Override
		@SuppressWarnings("ConstantConditions")
		public Collection<Identifier> getDependencies() {
			if (this.woven$dependencies == null) {
				Object self = this;

				if (self instanceof BakedModelManager || self instanceof WorldRenderer) {
					this.woven$dependencies = Collections.singletonList(ResourceReloadListenerKeys.TEXTURES);
				} else if (self instanceof ItemRenderer || self instanceof BlockRenderManager) {
					this.woven$dependencies = Collections.singletonList(ResourceReloadListenerKeys.MODELS);
				} else {
					this.woven$dependencies = Collections.emptyList();
				}
			}

			return this.woven$dependencies;
		}
	}

	@Mixin(targets = "net/minecraft/client/font/FontManager$1")
	public abstract static class FontManager implements IdentifiableResourceReloadListener {
		@Override
		public Identifier getIdentifier() {
			return ResourceReloadListenerKeys.FONTS;
		}
	}

	@Mixin(value = {
			/* public */
			RecipeManager.class, ServerAdvancementLoader.class, CommandFunctionManager.class, LootManager.class, TagManagerLoader.class
	})
	public abstract static class Server implements IdentifiableResourceReloadListener {
		private Identifier woven$id;
		private Collection<Identifier> woven$dependencies;

		@Override
		@SuppressWarnings("ConstantConditions")
		public Identifier getIdentifier() {
			if (this.woven$id == null) {
				Object self = this;

				if (self instanceof RecipeManager) {
					this.woven$id = ResourceReloadListenerKeys.RECIPES;
				} else if (self instanceof ServerAdvancementLoader) {
					this.woven$id = ResourceReloadListenerKeys.ADVANCEMENTS;
				} else if (self instanceof CommandFunctionManager) {
					this.woven$id = ResourceReloadListenerKeys.FUNCTIONS;
				} else if (self instanceof LootManager) {
					this.woven$id = ResourceReloadListenerKeys.LOOT_TABLES;
				} else if (self instanceof TagManagerLoader) {
					this.woven$id = ResourceReloadListenerKeys.TAGS;
				} else {
					this.woven$id = new Identifier("minecraft", "private/" + self.getClass().getSimpleName().toLowerCase(Locale.ROOT));
				}
			}

			return this.woven$id;
		}

		@Override
		@SuppressWarnings("ConstantConditions")
		public Collection<Identifier> getDependencies() {
			if (this.woven$dependencies == null) {
				Object self = this;

				if (self instanceof TagManagerLoader) {
					this.woven$dependencies = Collections.emptyList();
				} else {
					this.woven$dependencies = Collections.singletonList(ResourceReloadListenerKeys.TAGS);
				}
			}

			return this.woven$dependencies;
		}
	}
}
