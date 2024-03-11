package net.avitech.testbed.feature.debug.mixin;

import java.util.List;

import com.google.common.collect.Lists;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.avitech.testbed.feature.debug.Debug;
import net.avitech.testbed.feature.debug.DebugInfoConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.DebugHud;

@Mixin(DebugHud.class)
@Environment(EnvType.CLIENT)
public abstract class DebugHudMixin {

    // @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    // private void wrapGetLeftText(CallbackInfoReturnable<List<String>> info) {
    //     DebugInfoConsumer consumer = Debug.INSTANCE.createConsumer();
    //     Debug.INSTANCE.getOrderedInfoProvidersLeft(provider -> provider.appendDebugInfo(consumer));

    //     List<String> list = Lists.newArrayList();
    //     consumer.getOrderedLines(info.getReturnValue()).forEachOrdered(list::add);
    //     info.setReturnValue(list);
    // }

    // @Inject(method = "getRightText", at = @At("RETURN"), cancellable = true)
    // private void wrapGetRightText(CallbackInfoReturnable<List<String>> info) {
    //     DebugInfoConsumer consumer = Debug.INSTANCE.createConsumer();
    //     Debug.INSTANCE.getOrderedInfoProvidersRight(provider -> provider.appendDebugInfo(consumer));

    //     List<String> list = Lists.newArrayList();
    //     consumer.getOrderedLines(info.getReturnValue()).forEachOrdered(list::add);
    //     info.setReturnValue(list);
    // }

}
