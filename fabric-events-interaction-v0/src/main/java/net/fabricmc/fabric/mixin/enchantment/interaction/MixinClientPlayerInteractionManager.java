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

package net.fabricmc.fabric.mixin.enchantment.interaction;

import net.fabricmc.fabric.api.event.client.player.ClientBlockBreakEvent;
import net.fabricmc.fabric.api.event.client.player.ClientBlockPlaceEvent;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
	@Shadow
	private MinecraftClient client;
	@Shadow
	private ClientPlayNetworkHandler networkHandler;
	@Shadow
	private GameMode gameMode;

	@Shadow
	private float currentBreakingProgress;
	@Unique
	private BlockState blockBreakingState;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z", ordinal = 0), method = "attackBlock", cancellable = true)
	public void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
		ActionResult result = AttackBlockCallback.EVENT.invoker().interact(client.player, client.world, Hand.MAIN_HAND, pos, direction);

		if (result != ActionResult.PASS) {
			info.setReturnValue(result == ActionResult.SUCCESS);
			info.cancel();
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z", ordinal = 0), method = "updateBlockBreakingProgress", cancellable = true)
	public void method_2902(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
		if (!gameMode.isCreative()) {
			return;
		}

		ActionResult result = AttackBlockCallback.EVENT.invoker().interact(client.player, client.world, Hand.MAIN_HAND, pos, direction);

		if (result != ActionResult.PASS) {
			info.setReturnValue(result == ActionResult.SUCCESS);
			info.cancel();
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", ordinal = 0), method = "interactBlock", cancellable = true)
	public void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> info) {
		ActionResult result = UseBlockCallback.EVENT.invoker().interact(player, world, hand, blockHitResult);

		if (result != ActionResult.PASS) {
			if (result == ActionResult.SUCCESS) {
				this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
			}

			info.setReturnValue(result);
			info.cancel();
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0), method = "interactItem", cancellable = true)
	public void interactItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		TypedActionResult<ItemStack> result = UseItemCallback.EVENT.invoker().interact(player, world, hand);

		if (result.getResult() != ActionResult.PASS) {
			if (result.getResult() == ActionResult.SUCCESS) {
				this.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand));
			}

			info.setReturnValue(result.getResult());
			info.cancel();
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0), method = "attackEntity", cancellable = true)
	public void attackEntity(PlayerEntity player, Entity entity, CallbackInfo info) {
		ActionResult result = AttackEntityCallback.EVENT.invoker().interact(player, player.getEntityWorld(), Hand.MAIN_HAND /* TODO */, entity, null);

		if (result != ActionResult.PASS) {
			if (result == ActionResult.SUCCESS) {
				this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, player.isSneaking()));
			}

			info.cancel();
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0), method = "interactEntityAtLocation", cancellable = true)
	public void interactEntityAtLocation(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		ActionResult result = UseEntityCallback.EVENT.invoker().interact(player, player.getEntityWorld(), hand, entity, hitResult);

		if (result != ActionResult.PASS) {
			if (result == ActionResult.SUCCESS) {
				Vec3d hitVec = hitResult.getPos().subtract(entity.getX(), entity.getY(), entity.getZ());
				this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand, hitVec, player.isSneaking()));
			}

			info.setReturnValue(result);
			info.cancel();
			return;
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 2), method = "interactBlock", cancellable = true)
	public void beforeBlockPlace(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		Item item = player.getStackInHand(hand).getItem();

		if (item instanceof BlockItem) {
			ItemUsageContext usageContext = new ItemUsageContext(player, hand, hitResult);
			ItemPlacementContext placementContext = new ItemPlacementContext(usageContext);

			BlockState futureBlockState = ((BlockItem) item).getBlock().getPlacementState(placementContext);

			boolean result = ClientBlockPlaceEvent.BEFORE.invoker().beforeBlockPlace(world, player, placementContext.getBlockPos(), futureBlockState);

			if (!result) {
				ClientBlockPlaceEvent.CANCELED.invoker().onBlockPlaceCanceled(world, player, placementContext.getBlockPos(), futureBlockState);
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}

	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"), method = "interactBlock")
	public void afterBlockPlace(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		Item item = player.getStackInHand(hand).getItem();

		if (item instanceof BlockItem) {
			BlockPos targetBlock = hitResult.getBlockPos().offset(hitResult.getSide());
			ClientBlockPlaceEvent.AFTER.invoker().afterBlockPlace(world, player, targetBlock, world.getBlockState(targetBlock));
		}
	}

	@Inject(method = "attackBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onBlockAttacked(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)V"), cancellable = true)
	public void blockBreakStart(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		ClientWorld world = this.client.world;

		if (world != null) {
			blockBreakingState = world.getBlockState(pos);
			boolean result = ClientBlockBreakEvent.ON_START.invoker().onBlockBreakStart(world, this.client.player, pos, blockBreakingState, 0.0F);

			if (!result) {
				ClientBlockBreakEvent.ON_CANCEL.invoker().onBlockBreakCancel(world, this.client.player, pos, blockBreakingState, 0.0F);
				cir.setReturnValue(true);
			} else {
				blockBreakProgression(pos, world, blockBreakingState, cir, 0.0F);
			}
		}
	}

	@Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onBlockAttacked(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)V"), cancellable = true)
	public void blockBreakProgressEvent(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		ClientWorld world = client.world;

		if (world != null) {
			blockBreakProgression(pos, world, world.getBlockState(pos), cir, Math.min(currentBreakingProgress, 1));
		}
	}

	private void blockBreakProgression(BlockPos pos, ClientWorld world, BlockState blockBreakingState, CallbackInfoReturnable<Boolean> cir, float progress) {
		boolean result = ClientBlockBreakEvent.ON_PROGRESS.invoker().onBlockBreakProgress(world, client.player, pos, blockBreakingState, progress);

		if (!result) {
			ClientBlockBreakEvent.ON_CANCEL.invoker().onBlockBreakCancel(world, client.player, pos, blockBreakingState, progress);
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBroken(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"), method = "breakBlock")
	public void blockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		ClientWorld world = this.client.world;

		if (world != null) {
			ClientBlockBreakEvent.ON_BREAK.invoker().onBlockBreak(world, client.player, pos, blockBreakingState, 1.0F);
		}
	}
}