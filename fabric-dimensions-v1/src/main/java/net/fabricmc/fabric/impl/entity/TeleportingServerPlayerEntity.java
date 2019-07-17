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

package net.fabricmc.fabric.impl.entity;

import net.minecraft.server.world.ServerWorld;

/**
 * Represents a player teleporting using changeDimension.
 * Allows private fields to be set from a mixin.
 */
public interface TeleportingServerPlayerEntity {
    void handleDimensionCriterions(ServerWorld serverWorld);

    void set13978(int set);
    void set13997(float set);
    void set13979(int set);
}
