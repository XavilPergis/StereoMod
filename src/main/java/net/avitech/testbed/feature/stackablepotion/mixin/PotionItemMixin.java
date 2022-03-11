package net.avitech.testbed.feature.stackablepotion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin {

    @Redirect(method = "finishUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack"))
    private boolean redirectGlassBottleInsertion(PlayerInventory inventory, ItemStack stack) {
        inventory.offerOrDrop(stack);
        // I don't think the return value here actually matters
        return true;
    }

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void adjustPotionUseTime(ItemStack stack, CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(24);
    }

}
