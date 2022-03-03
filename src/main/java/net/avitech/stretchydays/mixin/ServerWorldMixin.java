package net.avitech.stretchydays.mixin;

import net.avitech.stretchydays.StretchyDaysMod;
import net.minecraft.server.world.ServerWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
	@Invoker("setTimeOfDay(L)V")
	protected abstract void invokeSetTimeOfDay(long time);

	private int ticksSinceLastTimeAdvance = 0;

	@Redirect(method = "tickTime()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
	private void redirectTimeOfDayTick(ServerWorld serverWorld, long requestedTime) {
		long advance = StretchyDaysMod.getTimeAdvance(serverWorld.getLevelProperties().getTimeOfDay(),
				++ticksSinceLastTimeAdvance);
		if (advance != 0L) {
			invokeSetTimeOfDay(serverWorld.getLevelProperties().getTimeOfDay() + advance);
			ticksSinceLastTimeAdvance = 0;
		}
	}
}
