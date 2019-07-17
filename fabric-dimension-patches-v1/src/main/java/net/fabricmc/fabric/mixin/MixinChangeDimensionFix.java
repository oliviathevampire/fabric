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

package net.fabricmc.fabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;

@Mixin(value = PortalForcer.class, priority = -100000)
public class MixinChangeDimensionFix
{
	@Shadow
	@Final
	private ServerWorld world;

	@Inject(at = @At("HEAD"), method = "usePortal", cancellable = true)
	private void usePortal(final Entity entity, final float yaw, final CallbackInfoReturnable<Boolean> info) {
		if(entity.getLastPortalDirectionVector() == null) {
			BlockPos topPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(entity.getPos().getX(), 0, entity.getPos().getZ()));

			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity) entity).networkHandler.teleportRequest(topPos.getX(), topPos.getY(), topPos.getZ(), 0, 0, new HashSet<>());
				((ServerPlayerEntity) entity).networkHandler.syncWithPlayerPosition();
			} else {
				entity.setPosition(topPos.getX(), topPos.getY(), topPos.getZ());
			}

			info.setReturnValue(true);
			info.cancel();
		}
	}
}
