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

package net.fabricmc.fabric.api.world;

import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

import java.util.function.Supplier;

/**
 * Fabric version of ChunkGeneratorType which utilizes the FabricChunkGeneratorFactory.
 * ChunkGeneratorType is a registry wrapper for ChunkGenerator, similar to BlockEntityType or EntityType.
 * @param <C> ChunkGeneratorConfig
 * @param <T> ChunkGenerator
 */
public class FabricChunkGeneratorType<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> extends ChunkGeneratorType<C, T> {

	private FabricChunkGeneratorFactory<C, T> factory;

	/**
	 * @param factory factory instance to provide a ChunkGenerator
	 * @param buffetScreenOption whether or not the ChunkGeneratorType should appear in the buffet screen options page
	 * @param settingsSupplier config supplier
	 */
	public FabricChunkGeneratorType(FabricChunkGeneratorFactory<C, T> factory, boolean buffetScreenOption, Supplier<C> settingsSupplier) {
		super(null, buffetScreenOption, settingsSupplier);
		this.factory = factory;
	}

	/**
	 * Called to get an instance of the ChunkGeneratorType's ChunkGenerator.
	 * @param world DimensionType's world instance
	 * @param biomeSource BiomeSource to use while generating the world
	 * @param config ChunkGenerator config instance
	 * @return returns an instance of the ChunkGeneratorType's ChunkGenerator.
	 */
	@Override
	public T create(World world, BiomeSource biomeSource, C config) {
		return factory.create(world, biomeSource, config);
	}

}
