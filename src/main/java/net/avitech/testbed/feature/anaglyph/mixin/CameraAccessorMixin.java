package net.avitech.testbed.feature.anaglyph.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

@Mixin(Camera.class)
@Environment(EnvType.CLIENT)
public interface CameraAccessorMixin {

    @Invoker
    void invokeMoveBy(double x, double y, double z);

    @Invoker
    void invokeSetRotation(float yaw, float pitch);

    @Invoker
    void invokeSetPos(double x, double y, double z);

    @Invoker
    void invokeSetPos(Vec3d pos);

}
