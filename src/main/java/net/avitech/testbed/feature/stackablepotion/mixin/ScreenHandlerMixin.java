package net.avitech.testbed.feature.stackablepotion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    // @Invoker
    // abstract ItemStack invokeTransferSlot(PlayerEntity player, int index);

    // @Accessor
    // abstract DefaultedList<Slot> getSlots();

    // // annihilate the vanilla insertItem implementation, because i dont know how to
    // // be more precise with this :(
    // @Inject(method = "insertItem", at = @At(value = "HEAD"), cancellable = true)
    // private void fixInsertItemForQuickTransfer(ItemStack sourceStack, int startSlotIndex, int endSlotIndex,
    //         boolean iterateBackwards, CallbackInfoReturnable<Boolean> info) {
    //     info.setReturnValue(doInsertItem(sourceStack, startSlotIndex, endSlotIndex, iterateBackwards));
    // }

    // // this returns true if any amount of items from the source stack was
    // // successfully moved into one of the slots in the given slot range.
    // @Unique
    // private boolean doInsertItem(ItemStack sourceStack, int startSlotIndex, int endSlotIndex,
    //         boolean iterateBackwards) {
    //     boolean didTransferItems = false;
    //     int currentSlotIndex = iterateBackwards ? endSlotIndex - 1 : startSlotIndex;
    //     // try to move items out of the source stack, and into the slots covered by the
    //     // given slot range.
    //     while (!sourceStack.isEmpty() && currentSlotIndex >= startSlotIndex && currentSlotIndex < endSlotIndex) {
    //         Slot destinationSlot = getSlots().get(currentSlotIndex);
    //         if (sourceStack.getCount() != destinationSlot.insertStack(sourceStack).getCount()) {
    //             didTransferItems = true;
    //         }

    //         currentSlotIndex += iterateBackwards ? -1 : 1;
    //     }
    //     return didTransferItems;
    // }

}
