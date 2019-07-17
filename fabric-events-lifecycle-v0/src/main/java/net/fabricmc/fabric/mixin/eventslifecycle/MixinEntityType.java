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

package net.fabricmc.fabric.mixin.eventslifecycle;

import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.entity.EntityTickCallback;
import net.fabricmc.fabric.impl.event.EntityTypeCaller;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Arrays;

@Mixin(EntityType.class)
public class MixinEntityType<T extends Entity> implements EntityTypeCaller {

	private EntityTickCallback<T> event;

	@Override
	public EntityTickCallback getEntityEvent() {
		if (event == null) {
			event = EventFactory.createArrayBacked(EntityTickCallback.class,
				(listeners) -> {
					if (EventFactory.isProfilingEnabled()) {
						return (player) -> {
							Profiler profiler = player.getServer().getProfiler();
							profiler.push("fabricEntityTick");
							Arrays.stream(listeners).forEachOrdered(event -> {
								profiler.push(EventFactory.getHandlerName(event));
								event.tick(player);
								profiler.pop();
							});
							profiler.pop();
						};
					} else {
						return (player) -> {
							Arrays.stream(listeners).forEachOrdered(event -> event.tick(player));
						};
					}
				}
			).invoker();
		}
		return event;
	}
}
