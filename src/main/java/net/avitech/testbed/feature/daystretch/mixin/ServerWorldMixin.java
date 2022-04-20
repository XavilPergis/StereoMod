package net.avitech.testbed.feature.daystretch.mixin;

import net.avitech.testbed.feature.daystretch.TimeTickableWorld;
import net.avitech.testbed.feature.daystretch.TimeTicker;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements TimeTickableWorld {

	@Invoker("setTimeOfDay(L)V")
	protected abstract void invokeSetTimeOfDay(long time);

	private TimeTicker timeTicker = new TimeTicker((World) (Object) this);

	@Redirect(method = "tickTime()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
	private void redirectTimeOfDayTick(ServerWorld self, long requestedTime) {
		long updatedTimeOfDay = timeTicker.tick();
		if (self.getLevelProperties().getTimeOfDay() != updatedTimeOfDay) {
			invokeSetTimeOfDay(updatedTimeOfDay);
		}
	}

	@SuppressWarnings("resource")
	@Inject(method = "setTimeOfDay", at = @At("HEAD"))
	private void injectSetTimeOfDay(long timeOfDay, CallbackInfo info) {
		ServerWorld self = (ServerWorld) (Object) this;

		// I don't know why, but client-bound time update packets aren't sent when the
		// time of day changes out-of-sequence. Instead, in MinecraftServer::tickWorlds,
		// the server sends the time update packets to every every player in each
		// dimension every 20 ticks.
		//
		// The result of this is that using the time command, waking up after sleeping,
		// or generally any method of changing the server's time of day will not be
		// reflected in the connected clients immediately, so there is potentially up to
		// one second of visual "lag".
		if (didTimeOfDaySkip(self.getLevelProperties().getTimeOfDay(), timeOfDay)) {
			boolean doDaylightCycle = self.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE);
			broadcastPacket(new WorldTimeUpdateS2CPacket(self.getTime(), timeOfDay, doDaylightCycle));
		}
	}

	private static boolean didTimeOfDaySkip(long currentTimeOfDay, long nextTimeOfDay) {
		long expectedTimeOfDay = (currentTimeOfDay + 1) % 24000;
		return nextTimeOfDay != expectedTimeOfDay;
	}

	@SuppressWarnings("resource")
	private void broadcastPacket(Packet<?> packet) {
		ServerWorld self = (ServerWorld) (Object) this;
		PlayerManager playerManager = self.getServer().getPlayerManager();
		playerManager.sendToDimension(packet, self.getRegistryKey());
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
