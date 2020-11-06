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

package net.fabricmc.fabric.api.object.builder.v1.block;

import net.fabricmc.fabric.impl.object.builder.BlockSettingsInternals;
import net.fabricmc.fabric.impl.object.builder.FabricBlockInternals;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockAccessor;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.function.ToIntFunction;

/**
 * Fabric's version of Block.Settings. Adds additional methods and hooks
 * not found in the original class.
 *
 * <p>To use it, simply replace Block.Settings.of() with
 * FabricBlockSettings.of().
 */
public class FabricBlockSettings extends AbstractBlock.Settings {
	protected FabricBlockSettings(Material material, MapColor color) {
		super(material, color);
	}

	protected FabricBlockSettings(AbstractBlock.Settings settings) {
		super(((AbstractBlockSettingsAccessor) settings).getMaterial(), ((AbstractBlockSettingsAccessor) settings).getMapColorProvider());
		// Mostly Copied from vanilla's copy method
		// Note: If new methods are added to Block settings, an accessor must be added here
		AbstractBlockSettingsAccessor thisAccessor = (AbstractBlockSettingsAccessor) this;
		AbstractBlockSettingsAccessor otherAccessor = (AbstractBlockSettingsAccessor) settings;

		thisAccessor.setMaterial(otherAccessor.getMaterial());
		this.hardness(otherAccessor.getHardness());
		this.resistance(otherAccessor.getResistance());
		this.collidable(otherAccessor.getCollidable());
		thisAccessor.setRandomTicks(otherAccessor.getRandomTicks());
		this.luminance(otherAccessor.getLuminance());
		thisAccessor.setMapColorProvider(otherAccessor.getMapColorProvider());
		this.sounds(otherAccessor.getSoundGroup());
		this.slipperiness(otherAccessor.getSlipperiness());
		this.velocityMultiplier(otherAccessor.getVelocityMultiplier());
		thisAccessor.setDynamicBounds(otherAccessor.getDynamicBounds());
		thisAccessor.setOpaque(otherAccessor.getOpaque());
		thisAccessor.setIsAir(otherAccessor.getIsAir());
		thisAccessor.setToolRequired(otherAccessor.isToolRequired());

		// Now attempt to copy fabric specific data
		BlockSettingsInternals otherInternals = (BlockSettingsInternals) settings;
		FabricBlockInternals.ExtraData extraData = otherInternals.getExtraData();

		if (extraData != null) { // If present, populate the extra data on our new settings
			((BlockSettingsInternals) this).setExtraData(extraData);
		}
	}

	public static FabricBlockSettings of(Material material) {
		return of(material, material.getColor());
	}

	public static FabricBlockSettings of(Material material, MapColor color) {
		return new FabricBlockSettings(material, color);
	}

	public static FabricBlockSettings of(Material material, DyeColor color) {
		return new FabricBlockSettings(material, color.getMapColor());
	}

	public static FabricBlockSettings copyOf(AbstractBlock block) {
		return new FabricBlockSettings(((AbstractBlockAccessor) block).getSettings());
	}

	public static FabricBlockSettings copyOf(AbstractBlock.Settings settings) {
		return new FabricBlockSettings(settings);
	}

	@Override
	public FabricBlockSettings noCollision() {
		super.noCollision();
		return this;
	}

	@Override
	public FabricBlockSettings nonOpaque() {
		super.nonOpaque();
		return this;
	}

	@Override
	public FabricBlockSettings slipperiness(float value) {
		super.slipperiness(value);
		return this;
	}

	@Override
	public FabricBlockSettings velocityMultiplier(float velocityMultiplier) {
		super.velocityMultiplier(velocityMultiplier);
		return this;
	}

	@Override
	public FabricBlockSettings jumpVelocityMultiplier(float jumpVelocityMultiplier) {
		super.jumpVelocityMultiplier(jumpVelocityMultiplier);
		return this;
	}

	@Override
	public FabricBlockSettings sounds(BlockSoundGroup group) {
		super.sounds(group);
		return this;
	}

	/**
	 * @deprecated Please use {@link FabricBlockSettings#luminance(ToIntFunction)}.
	 */
	public FabricBlockSettings lightLevel(ToIntFunction<BlockState> levelFunction) {
		return this.luminance(levelFunction);
	}

	@Override
	public FabricBlockSettings luminance(ToIntFunction<BlockState> luminanceFunction) {
		super.luminance(luminanceFunction);
		return this;
	}

	@Override
	public FabricBlockSettings strength(float hardness, float resistance) {
		super.strength(hardness, resistance);
		return this;
	}

	@Override
	public FabricBlockSettings breakInstantly() {
		super.breakInstantly();
		return this;
	}

	public FabricBlockSettings strength(float strength) {
		super.strength(strength);
		return this;
	}

	@Override
	public FabricBlockSettings ticksRandomly() {
		super.ticksRandomly();
		return this;
	}

	@Override
	public FabricBlockSettings dynamicBounds() {
		super.dynamicBounds();
		return this;
	}

	@Override
	public FabricBlockSettings dropsNothing() {
		super.dropsNothing();
		return this;
	}

	@Override
	public FabricBlockSettings dropsLike(Block block) {
		super.dropsLike(block);
		return this;
	}

	@Override
	public FabricBlockSettings air() {
		super.air();
		return this;
	}

	@Override
	public FabricBlockSettings allowsSpawning(AbstractBlock.TypedContextPredicate<EntityType<?>> predicate) {
		super.allowsSpawning(predicate);
		return this;
	}

	@Override
	public FabricBlockSettings solidBlock(AbstractBlock.ContextPredicate predicate) {
		super.solidBlock(predicate);
		return this;
	}

	@Override
	public FabricBlockSettings suffocates(AbstractBlock.ContextPredicate predicate) {
		super.suffocates(predicate);
		return this;
	}

	@Override
	public FabricBlockSettings blockVision(AbstractBlock.ContextPredicate predicate) {
		super.blockVision(predicate);
		return this;
	}

	@Override
	public FabricBlockSettings postProcess(AbstractBlock.ContextPredicate predicate) {
		super.postProcess(predicate);
		return this;
	}

	@Override
	public FabricBlockSettings emissiveLighting(AbstractBlock.ContextPredicate predicate) {
		super.emissiveLighting(predicate);
		return this;
	}

	/* FABRIC ADDITIONS*/

	/**
	 * @deprecated Please use {@link FabricBlockSettings#luminance(int)}.
	 */
	@Deprecated
	public FabricBlockSettings lightLevel(int lightLevel) {
		this.luminance(lightLevel);
		return this;
	}

	public FabricBlockSettings luminance(int luminance) {
		this.luminance(ignored -> luminance);
		return this;
	}

	public FabricBlockSettings hardness(float hardness) {
		((AbstractBlockSettingsAccessor) this).setHardness(hardness);
		return this;
	}

	public FabricBlockSettings resistance(float resistance) {
		((AbstractBlockSettingsAccessor) this).setResistance(Math.max(0.0F, resistance));
		return this;
	}

	public FabricBlockSettings drops(Identifier dropTableId) {
		((AbstractBlockSettingsAccessor) this).setLootTableId(dropTableId);
		return this;
	}

	/**
	 * Make the block require tool to drop and slows down mining speed if the incorrect tool is used.
	 */
	@Override
	public FabricBlockSettings requiresTool() {
		super.requiresTool();
		return this;
	}

	/* FABRIC DELEGATE WRAPPERS */

	/**
	 * @deprecated Please migrate to {@link FabricBlockSettings#mapColor(MapColor)}
	 */
	@Deprecated
	public FabricBlockSettings materialColor(MapColor color) {
		return this.mapColor(color);
	}

	/**
	 * @deprecated Please migrate to {@link FabricBlockSettings#mapColor(DyeColor)}
	 */
	@Deprecated
	public FabricBlockSettings materialColor(DyeColor color) {
		return this.mapColor(color);
	}

	public FabricBlockSettings mapColor(MapColor color) {
		((AbstractBlockSettingsAccessor) this).setMapColorProvider(ignored -> color);
		return this;
	}

	public FabricBlockSettings mapColor(DyeColor color) {
		return this.mapColor(color.getMapColor());
	}

	public FabricBlockSettings collidable(boolean collidable) {
		((AbstractBlockSettingsAccessor) this).setCollidable(collidable);
		return this;
	}

	/* FABRIC HELPERS */

	/**
	 * Makes the block breakable by any tool if {@code breakByHand} is set to true.
	 */
	public FabricBlockSettings breakByHand(boolean breakByHand) {
		FabricBlockInternals.computeExtraData(this).breakByHand(breakByHand);
		return this;
	}

	/**
	 * Please make the block require a tool if you plan to disable drops and slow the breaking down using the
	 * incorrect tool by using {@link FabricBlockSettings#requiresTool()}.
	 */
	public FabricBlockSettings breakByTool(Tag<Item> tag, int miningLevel) {
		FabricBlockInternals.computeExtraData(this).addMiningLevel(tag, miningLevel);
		return this;
	}

	/**
	 * Please make the block require a tool if you plan to disable drops and slow the breaking down using the
	 * incorrect tool by using {@link FabricBlockSettings#requiresTool()}.
	 */
	public FabricBlockSettings breakByTool(Tag<Item> tag) {
		return this.breakByTool(tag, 0);
	}


	/**
	 * Sets the piston behavior of the block, if not set it defaults to the value of
	 * {@link Material#getPistonBehavior()} for the block's material.
	 */
	public FabricBlockSettings pistonBehavior(PistonBehavior pistonBehavior) {
		FabricBlockInternals.computeExtraData(this).setPistonBehavior(pistonBehavior);
		return this;
	}

	/**
	 * Sets whether the block is replaceable, if not set it defaults to the value of {@link Material#isReplaceable()}
	 * for the block's material.
	 */
	public FabricBlockSettings replaceable(boolean replaceable) {
		FabricBlockInternals.computeExtraData(this).setReplaceable(replaceable);
		return this;
	}

	/**
	 * Sets whether the block is solid, if not set it defaults to the value of {@link Material#isSolid()} for the
	 * block's material.
	 */
	public FabricBlockSettings solid(boolean solid) {
		FabricBlockInternals.computeExtraData(this).setSolid(solid);
		return this;
	}

}
