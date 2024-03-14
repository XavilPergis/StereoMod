package net.avitech.stereomod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
@Environment(EnvType.CLIENT)
public abstract class KeyboardMixin {

    @Shadow
    protected abstract boolean processDebugKeys(int key);

    @Inject(method = "processF3", at = @At("HEAD"), cancellable = true)
    private void enableAdditionalDebugFeatures(int key, CallbackInfoReturnable<Boolean> info) {
        if (processDebugKeys(key)) {
            info.setReturnValue(true);
        }
    }

}
