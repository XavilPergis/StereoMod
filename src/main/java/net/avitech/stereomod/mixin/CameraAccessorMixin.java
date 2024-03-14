package net.avitech.stereomod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
@Environment(EnvType.CLIENT)
public interface CameraAccessorMixin {

    @Invoker("moveBy")
    void stereo_moveBy(double x, double y, double z);

    @Invoker("setRotation")
    void stereo_setRotation(float yaw, float pitch);

    @Invoker("setPos")
    void stereo_setPos(double x, double y, double z);

}
