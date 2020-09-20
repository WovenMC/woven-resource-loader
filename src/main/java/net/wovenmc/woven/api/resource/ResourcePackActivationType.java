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

/**
 * Represents the resource pack activation type.
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public enum ResourcePackActivationType {
	/**
	 * Normal activation. The user has full control over the activation of the resource pack.
	 */
	NORMAL,
	/**
	 * Enabled by default. The user has still full control over the activation of the resource pack.
	 * <p>
	 * Note: this setting can only be satisfied on data packs, client resource packs cannot be by default enabled.
	 */
	DEFAULT_ENABLED,
	/**
	 * Always enabled. The user cannot disable the resource pack.
	 */
	ALWAYS_ENABLED;

	/**
	 * Returns whether this resource pack will be enabled by default or not.
	 *
	 * @return True if enabled by default, else false.
	 */
	public boolean isEnabledByDefault() {
		return this == DEFAULT_ENABLED || this == ALWAYS_ENABLED;
	}
}
