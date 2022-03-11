package net.avitech.testbed.feature.stackablepotion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(LingeringPotionItem.class)
public abstract class LingeringPotionItemMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void addCooldownToLingeringPotions(World world, PlayerEntity user, Hand hand,
            CallbackInfoReturnable<Void> info) {
        if (!PotionUtil.getPotionEffects(user.getStackInHand(hand)).isEmpty()) {
            user.getItemCooldownManager().set((LingeringPotionItem) (Object) this, 40);
        }
    }

}
