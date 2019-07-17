/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

/**
 * Callback for dropping an item.
 * Is hooked in before the item is dropped.
 * <p>
 * Upon return:
 * - SUCCESS cancels further processing and drops the item.
 * - PASS falls back to further processing.
 * - FAIL cancels further processing and does not drop the item.
 */
public interface DropItemCallback {
	public static final Event<DropItemCallback> EVENT = EventFactory.createArrayBacked(DropItemCallback.class,
		(listeners) -> (player, stack) -> {
			for (DropItemCallback event : listeners) {
				ActionResult result = event.interact(player, stack);
				if (result != ActionResult.PASS) {
					return result;
				}
			}

			return ActionResult.PASS;
		}
	);

	ActionResult interact(PlayerEntity player, ItemStack stack);
}
