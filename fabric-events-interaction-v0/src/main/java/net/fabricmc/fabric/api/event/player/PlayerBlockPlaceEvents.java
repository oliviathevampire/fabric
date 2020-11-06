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
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class PlayerBlockPlaceEvents {

	private PlayerBlockPlaceEvents() { }

	/**
	 * Callback before a block is placed.
	 * Called on server only
	 *
	 * <p>If any listener cancels a block placing action, that block placing
	 * action is canceled and {@link #CANCELED} event is fired. Otherwise, the
	 * {@link #AFTER} event is fired.</p>
	 */
	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class,
			(listeners) -> (world, player, pos, state) -> {
				for (Before event : listeners) {
					boolean result = event.beforeBlockPlace(world, player, pos, state);

					if (!result) {
						return false;
					}
				}

				return true;
			}
	);

	/**
	 * Callback after a block is placed.
	 *
	 * <p>Called on server only
	 */
	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class,
			(listeners) -> (world, player, pos, state) -> {
				for (After event : listeners) {
					event.afterBlockPlace(world, player, pos, state);
				}
			}
	);

	/**
	 * Callback when a block place has been canceled.
	 *
	 * <p>Called on server only. May be used on logical server to send packets to revert client-side block changes.
	 */
	public static final Event<Canceled> CANCELED = EventFactory.createArrayBacked(Canceled.class,
			(listeners) -> (world, player, pos, state) -> {
				for (Canceled event : listeners) {
					event.onBlockPlaceCanceled(world, player, pos, state);
				}
			}
	);

	@FunctionalInterface
	public interface Before {
		/**
		 * Called before a block is placed and allows canceling the block placing.
		 *
		 * <p>Implementations should not modify the world or assume the block place has completed or failed.</p>
		 *
		 * @param world the world in which the block is placed
		 * @param player the player placing the block
		 * @param pos the position at which the block is placed
		 * @param state the block state <strong>before</strong> the block is placed
		 * @return {@code false} to cancel block placing action, or {@code true} to pass to next listener
		 */
		boolean beforeBlockPlace(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state);
	}

	@FunctionalInterface
	public interface After {
		/**
		 * Called after a block is successfully placed.
		 *
		 * @param world the world where the block was placed
		 * @param player the player who placed the block
		 * @param pos the position where the block was placed
		 * @param state the block state <strong>before</strong> the block was placed
		 */
		void afterBlockPlace(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state);
	}

	@FunctionalInterface
	public interface Canceled {
		/**
		 * Called when a block place has been canceled.
		 *
		 * @param world the world where the block was going to be placed
		 * @param player the player who was going to place the block
		 * @param pos the position where the block was going to be placed
		 * @param state the block state of the block that was going to be place
		 */
		void onBlockPlaceCanceled(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state);
	}
}