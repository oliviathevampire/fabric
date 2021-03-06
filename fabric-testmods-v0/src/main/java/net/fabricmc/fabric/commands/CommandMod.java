/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.commands;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;

public class CommandMod implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistry.INSTANCE.register(false, (dispatcher) -> dispatcher.register(
			CommandManager.literal("fabric_test")
				.executes(c -> {
					c.getSource().sendFeedback(new LiteralText("Command works!"), false);
					return Command.SINGLE_SUCCESS;
				})
		));

		CommandRegistry.INSTANCE.register(true, (dispatcher) -> dispatcher.register(
			CommandManager.literal("fabric_test_dedicated")
				.executes(c -> {
					c.getSource().sendFeedback(new LiteralText("Command works!"), false);
					return Command.SINGLE_SUCCESS;
				})
		));
	}
}
