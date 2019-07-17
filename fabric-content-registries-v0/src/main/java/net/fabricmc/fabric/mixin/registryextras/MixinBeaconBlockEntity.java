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

package net.fabricmc.fabric.mixin.registryextras;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BeaconBlockEntity.class)
public abstract class MixinBeaconBlockEntity{

	@ModifyVariable(method = "updateLevel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0))
	private Block checkBeaconCompatible(Block original) {
		Tag<Block> beaconBaseTag = BlockTags.getContainer().get(new Identifier("fabric", "beacon_base"));
		if (beaconBaseTag != null && beaconBaseTag.contains(original)) return Blocks.IRON_BLOCK;
		else return original;
	}
}
