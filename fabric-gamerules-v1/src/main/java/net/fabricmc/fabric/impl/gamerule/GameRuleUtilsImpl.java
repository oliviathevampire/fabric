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

package net.fabricmc.fabric.impl.gamerule;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.gamerule.GameRuleUtils;
import net.fabricmc.fabric.mixin.gamerule.MixinBooleanRule;
import net.fabricmc.fabric.mixin.gamerule.MixinGameRules;
import net.fabricmc.fabric.mixin.gamerule.MixinIntRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GameRuleUtilsImpl implements GameRuleUtils {

	private Constructor<GameRules.RuleType> typeConstructor = null;

	@Override
	public <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(Identifier name, GameRules.RuleType<T> ruleType) {
		return MixinGameRules.callRegister(name.toString(), ruleType);
	}

	@Override
	public GameRules.RuleType<GameRules.BooleanRule> createBooleanRule(boolean defaultValue) {
		return MixinBooleanRule.callOf(defaultValue);
	}

	@Override
	public GameRules.RuleType<GameRules.IntRule> createIntRule(int defaultValue) {
		return MixinIntRule.callOf(defaultValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends GameRules.Rule<T>> GameRules.RuleType<T> createCustomRule(Supplier<ArgumentType<?>> argumentType, Function<GameRules.RuleType<T>, T> factory, BiConsumer<MinecraftServer, T> notifier) {
		try {
			if (typeConstructor == null) {
				Constructor<GameRules.RuleType> constructor = GameRules.RuleType.class.getDeclaredConstructor(Supplier.class, Function.class, BiConsumer.class);
				constructor.setAccessible(true);
				typeConstructor = constructor;
			}
			return typeConstructor.newInstance(argumentType, factory, notifier);
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
