package net.avitech.testbed.feature.daystretch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.avitech.testbed.feature.daystretch.TimeTickableWorld;
import net.avitech.testbed.feature.daystretch.TimeTicker;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin implements TimeTickableWorld {
    @Invoker("setTimeOfDay")
    protected abstract void invokeSetTimeOfDay(long time);

    private TimeTicker timeTicker = new TimeTicker((World) (Object) this);

    @Redirect(method = "tickTime()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setTimeOfDay(J)V"))
    private void redirectTimeOfDayTick(ClientWorld self, long requestedTime) {
        long updatedTimeOfDay = timeTicker.tick();
        if (self.getLevelProperties().getTimeOfDay() != updatedTimeOfDay) {
            invokeSetTimeOfDay(updatedTimeOfDay);
        }
    }

    @Override
    public TimeTicker getTimeTicker() {
        return timeTicker;
    }

    @Override
    public World asMinecraftWorld() {
        return (World) (Object) this;
    }

}
