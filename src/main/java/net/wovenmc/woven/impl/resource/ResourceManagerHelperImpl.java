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

import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.wovenmc.woven.api.resource.IdentifiableResourceReloadListener;
import net.wovenmc.woven.api.resource.ResourceManagerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceManagerHelperImpl implements ResourceManagerHelper {
	private static final Map<ResourceType, ResourceManagerHelperImpl> RESOURCE_MANAGERS = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	private final Set<IdentifiableResourceReloadListener> addedListeners = new LinkedHashSet<>();
	private final Set<Identifier> addedListenerIds = new HashSet<>();

	public static ResourceManagerHelper get(ResourceType type) {
		return RESOURCE_MANAGERS.computeIfAbsent(type, t -> new ResourceManagerHelperImpl());
	}

	@Override
	public void registerReloadListener(IdentifiableResourceReloadListener listener) {
		if (!this.addedListenerIds.add(listener.getIdentifier())) {
			LOGGER.warn("Tried to register resource reload listener \"" + listener.getIdentifier() + "\" twice!");
			return;
		}

		if (!this.addedListeners.add(listener)) {
			throw new RuntimeException("Listener with previously unknown ID \"" + listener.getIdentifier() + "\" already in listener set!");
		}
	}

	public static void sort(ResourceType type, List<ResourceReloadListener> listeners) {
		ResourceManagerHelperImpl instance = RESOURCE_MANAGERS.get(type);

		if (instance != null) {
			instance.sort(listeners);
		}
	}

	protected void sort(List<ResourceReloadListener> listeners) {
		listeners.removeAll(this.addedListeners);

		// General rules:
		// - We *do not* touch the ordering of vanilla listeners. Ever.
		//   While dependency values are provided where possible, we cannot
		//   trust them 100%. Only code doesn't lie.
		// - We addReloadListener all custom listeners after vanilla listeners. Same reasons.

		List<IdentifiableResourceReloadListener> listenersToAdd = new ArrayList<>(this.addedListeners);
		Set<Identifier> resolvedIds = new HashSet<>();

		for (ResourceReloadListener listener : listeners) {
			if (listener instanceof IdentifiableResourceReloadListener) {
				resolvedIds.add(((IdentifiableResourceReloadListener) listener).getIdentifier());
			}
		}

		int lastSize = -1;

		while (listeners.size() != lastSize) {
			lastSize = listeners.size();

			Iterator<IdentifiableResourceReloadListener> it = listenersToAdd.iterator();

			while (it.hasNext()) {
				IdentifiableResourceReloadListener listener = it.next();

				if (resolvedIds.containsAll(listener.getDependencies())) {
					resolvedIds.add(listener.getIdentifier());
					listeners.add(listener);
					it.remove();
				}
			}
		}

		for (IdentifiableResourceReloadListener listener : listenersToAdd) {
			LOGGER.warn("Could not resolve dependencies for listener \"" + listener.getIdentifier() + "\"!");
		}
	}
}
