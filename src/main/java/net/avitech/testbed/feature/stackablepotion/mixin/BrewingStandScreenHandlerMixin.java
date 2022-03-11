package net.avitech.testbed.feature.stackablepotion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;

@Mixin(BrewingStandScreenHandler.class)
public abstract class BrewingStandScreenHandlerMixin {

    @Redirect(method = "transferSlot", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/BrewingStandScreenHandler$PotionSlot;matches")), at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0))
    private int redirectPotionStackCountCheck(ItemStack stack) {
        // we need to make this 1 so that all potion transfers succeed, not just ones
        // that are transferred with a stack size of 1.
        return 1;
    }

}
