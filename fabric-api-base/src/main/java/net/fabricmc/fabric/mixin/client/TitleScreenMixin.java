package net.fabricmc.fabric.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin() {
		super(new TranslatableText("narrator.screen.title"));
	}

	@Inject(method = "render(IIF)V", at = @At("RETURN"))
	public void fabricInfo(int mouseX, int mouseY, float delta, CallbackInfo info) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			if (FabricLoader.getInstance().isModLoaded("fabric")) {
				this.minecraft.textRenderer.draw("API: v" + FabricLoader.getInstance().getModContainer("fabric").orElseThrow(null).getMetadata().getVersion(),
					2, this.height - 30, 0xFFFFFF);
				Objects.requireNonNull(this.minecraft).textRenderer.draw("Loader: v" + FabricLoader.getInstance().getModContainer("fabricloader").orElseThrow(null).getMetadata().getVersion(),
					2, this.height - 40, 0xFFFFFF);
			} else {
				Objects.requireNonNull(this.minecraft).textRenderer.draw("Loader: v" + FabricLoader.getInstance().getModContainer("fabricloader").orElseThrow(null).getMetadata().getVersion(),
					2, this.height - 30, 0xFFFFFF);
			}
			Objects.requireNonNull(this.minecraft).textRenderer.draw("Loaded mods: " + FabricLoader.getInstance().getAllMods().size(), 2, this.height - 20,
				0xFFFFFF);
		} else {
			Objects.requireNonNull(this.minecraft).textRenderer.draw("Loaded mods: " + FabricLoader.getInstance().getAllMods().size(), 2, this.height - 20,
				0xFFFFFF);
		}
	}

}
