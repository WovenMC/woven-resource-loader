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

import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;

/**
 * Interface for "identifiable" resource reload listeners.
 *
 * @version 0.1.0
 * @since 0.1.0
 * @see ResourceReloadListenerKeys
 */
public interface IdentifiableResourceReloadListener extends ResourceReloadListener {
	/**
	 * Gets the unique identifier of this listener.
	 *
	 * @return The unique identifier of this listener.
	 */
	Identifier getIdentifier();

	/**
	 * Returns the identifiers of listeners this listener expects to have been executed before itself.
	 * Please keep in mind that this only takes effect during the application stage!
	 *
	 * @return The identifiers of listeners this listener expects to have been executed before itself.
	 */
	default Collection<Identifier> getDependencies() {
		return Collections.emptyList();
	}
}
