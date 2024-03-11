package net.avitech.testbed.feature.stackablepotion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(SplashPotionItem.class)
public abstract class SplashPotionItemMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void addCooldownToSplashPotions(World world, PlayerEntity user, Hand hand,
            CallbackInfoReturnable<Void> info) {
        if (!PotionUtil.getPotionEffects(user.getStackInHand(hand)).isEmpty()) {
            user.getItemCooldownManager().set((SplashPotionItem) (Object) this, 40);
        }
    }

}
