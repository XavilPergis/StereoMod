package net.avitech.testbed.feature.stackablepotion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.item.Items;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @ModifyArg(method = "<clinit>", slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/item/PotionItem;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;maxCount(I)Lnet/minecraft/item/Item$Settings;", ordinal = 0))
    private static int modifyPotionStackSize(int previousStackSize) {
        return 16;
    }

    @ModifyArg(method = "<clinit>", slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/item/SplashPotionItem;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;maxCount(I)Lnet/minecraft/item/Item$Settings;", ordinal = 0))
    private static int modifySplashPotionStackSize(int previousStackSize) {
        return 4;
    }

    @ModifyArg(method = "<clinit>", slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/item/LingeringPotionItem;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;maxCount(I)Lnet/minecraft/item/Item$Settings;", ordinal = 0))
    private static int modifyLingeringPotionStackSize(int previousStackSize) {
        return 4;
    }
}
