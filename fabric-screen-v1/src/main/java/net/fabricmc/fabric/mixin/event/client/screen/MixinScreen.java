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

package net.fabricmc.fabric.mixin.event.client.screen;

import net.fabricmc.fabric.api.event.client.screen.ScreenAccess;
import net.fabricmc.fabric.api.event.client.screen.ScreenInitCallback;
import net.fabricmc.fabric.impl.event.client.screen.ButtonList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen implements ScreenAccess {

	@Shadow
	protected @Final List<AbstractButtonWidget> buttons;

	@Shadow
	protected @Final List<Element> children;

	private List<AbstractButtonWidget> fabricButtons;

	@Override
	public List<AbstractButtonWidget> getButtons() {
		// Lazy init to account for class initialization
		// order and to prevent creating the list when it's not needed.
		if (fabricButtons == null) {
			fabricButtons = new ButtonList<>(buttons, children);
		}
		return fabricButtons;
	}

	@Override
	public Screen getScreen() {
		return (Screen) (Object) this;
	}

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("RETURN"))
	private void onInit(MinecraftClient client, int width, int height, CallbackInfo ci) {
		ScreenInitCallback.EVENT.invoker().onInit(client, getScreen());
	}
}
