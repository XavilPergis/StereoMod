package net.avitech.testbed.feature.anaglyph.mixin;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.avitech.testbed.TestbedMod;
import net.avitech.testbed.feature.anaglyph.AnaglyphConfig;
import net.avitech.testbed.feature.anaglyph.Eye;
import net.avitech.testbed.feature.anaglyph.StereoCameraParams;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.BlockView;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    /**
     * @return the distance to the far clipping plane
     */
    @Shadow
    abstract float method_32796();

    @Shadow
    abstract Matrix4f getBasicProjectionMatrix(double fov);

    @Shadow
    abstract void renderWorld(float tickDelta, long limitTime, MatrixStack matrices);

    @Unique
    private @Nonnull Optional<Eye> currentEye = Optional.empty();

    @Unique
    private final Map<Eye, Framebuffer> eyeBuffers = Maps.newEnumMap(Eye.class);

    @Unique
    private Framebuffer getEyeBuffer(Eye eye) {
        if (!this.eyeBuffers.containsKey(eye)) {
            TestbedMod.LOGGER.info("Creating swap buffer for eye " + eye);
            int width = this.client.getFramebuffer().textureWidth;
            int height = this.client.getFramebuffer().textureHeight;
            Framebuffer buffer = new SimpleFramebuffer(width, height, false, false);
            this.eyeBuffers.put(eye, buffer);
        }

        return this.eyeBuffers.get(eye);
    }

    @Inject(method = "onResized", at = @At("HEAD"))
    private void onResized(int width, int height, CallbackInfo info) {
        TestbedMod.LOGGER.info("Resizing all eye buffers");
        for (Framebuffer buffer : this.eyeBuffers.values()) {
            buffer.resize(width, height, false);
        }
    }

    @Unique
    private @Nonnull StereoCameraParams getCurrentCameraParams(AnaglyphConfig config, double verticalFov) {
        Window window = this.client.getWindow();

        StereoCameraParams params = new StereoCameraParams();
        params.aspectRatio = (float) window.getFramebufferWidth() / (float) window.getFramebufferHeight();
        params.verticalFov = (float) verticalFov;
        params.planeNear = 0.05f;
        params.planeFar = method_32796();

        // not sure of the best place to put these. They should be user-configurable.
        params.eyeDistance = (float) config.eyeDistance;
        params.focalLength = (float) config.focalLength;

        return params;
    }

    @Unique
    private static Matrix4f perspectiveProjectionMatrix(
            float left, float right,
            float bottom, float top,
            float near, float far) {
        Matrix4f returnValue = new Matrix4f();
        Matrix4fMixin mat = (Matrix4fMixin) (Object) returnValue;
        mat.setA00((2f * near) / (right - left));
        // a01
        // a02
        // a03

        // a10
        mat.setA11((2f * near) / (top - bottom));
        // a12
        // a13

        // a20
        // a21
        mat.setA22(-(far + near) / (far - near));
        mat.setA23(-1f);

        mat.setA30(-near * (right + left) / (right - left));
        mat.setA31(-near * (top + bottom) / (top - bottom));
        mat.setA32(-(2f * far * near) / (far - near));
        // a33

        returnValue.transpose();

        return returnValue;
    }

    // http://paulbourke.net/stereographics/stereorender/
    @Unique
    private static @Nonnull Matrix4f getProjectionMatrixForEyeAsymmetric(@Nonnull Eye eye,
            @Nonnull StereoCameraParams params) {

        // params.planeNear = 0.55f;
        // params.planeFar = 2f;

        // we use a conceptual "center camera" to calculate some of the properties of
        // each eye camera. This lets us define an overall fov, and to keep a similar
        // (not sure if exactly the same) fov for each eye camera, even for varying
        // focal lengths.
        float nearHalfHeight = params.planeNear * (float) Math.tan(Math.toRadians(params.verticalFov) / 2.0);

        float planeTop = nearHalfHeight;
        float planeRight = planeTop * params.aspectRatio;

        float planeBottom = -planeTop;
        float planeLeft = -planeRight;

        // the distance of the origin of the current eye's frustum to the origin of the
        // "center" frustum.
        float eyeOriginX = params.eyeDistance / 2f;
        if (eye == Eye.LEFT)
            eyeOriginX *= -1f;

        // the distance that the left and right frustum planes need to be shifted. took
        // me so long to figure this one out!!
        float planeXShift = eyeOriginX * (params.planeNear / params.focalLength);
        planeLeft += planeXShift;
        planeRight += planeXShift;

        return perspectiveProjectionMatrix(planeLeft, planeRight, planeBottom, planeTop, params.planeNear,
                params.planeFar);
    }

    @Redirect(method = "renderWorld", slice = @Slice(from = @At(value = "INVOKE", target = "getFov"), to = @At(value = "INVOKE", target = "bobViewWhenHurt")), at = @At(value = "INVOKE", target = "getBasicProjectionMatrix"))
    private Matrix4f handleWorldProjectionMatrix(GameRenderer renderer, double verticalFov) {
        // Just forward this to the normal implementation if we're not currently
        // rendering either eye's view. I don't think this should happen regardless,
        // but, yknow, why not be safe?
        // return getBasicProjectionMatrix(verticalFov);
        if (this.currentEye.isEmpty()) {
            return getBasicProjectionMatrix(verticalFov);
        }

        AnaglyphConfig config = AnaglyphConfig.getConfig();
        return getProjectionMatrixForEyeAsymmetric(this.currentEye.get(), getCurrentCameraParams(config, verticalFov));
    }

    @Redirect(method = "render", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/Profiler;push"), to = @At(value = "INVOKE", target = "updateWorldIcon")), at = @At(value = "INVOKE", target = "renderWorld"))
    private void renderWorldMaybeStereo(GameRenderer renderer, float tickDelta, long limitTime, MatrixStack matrices) {
        AnaglyphConfig config = AnaglyphConfig.getConfig();
        if (!config.anaglyphEnabled) {
            // just forward the call as usual if stereo rendering is disabled.
            renderWorld(tickDelta, limitTime, matrices);
        } else {
            renderWorldStereo(config, tickDelta, limitTime);
        }
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update"))
    private void setCameraPositionToCurrentEye(Camera camera, BlockView area, Entity focusedEntity, boolean thirdPerson,
            boolean inverseView, float tickDelta) {
        camera.update(area, focusedEntity, thirdPerson, inverseView, tickDelta);

        if (this.currentEye.isPresent()) {
            // TODO: code duplication w/ projection matrix setup
            float camOffsetX = (float) AnaglyphConfig.getConfig().eyeDistance / 2f;
            if (this.currentEye.get() == Eye.RIGHT)
                camOffsetX *= -1f;

            ((CameraAccessorMixin) (Object) camera).invokeMoveBy(0f, 0f, camOffsetX);
        }
    }

    @Unique
    private void renderWorldStereo(AnaglyphConfig config, float tickDelta, long limitTime) {

        drawEye(Eye.LEFT, config, tickDelta, limitTime);
        drawEye(Eye.RIGHT, config, tickDelta, limitTime);

        this.client.getProfiler().push("anaglyph_compose");
        Framebuffer targetBuffer = this.client.getFramebuffer();
        targetBuffer.clear(false);

        RenderSystem.enableBlend();
        RenderSystem.blendEquation(GL20.GL_FUNC_ADD);
        RenderSystem.blendFunc(GL20.GL_ONE, GL20.GL_ONE);
        for (var entry : this.eyeBuffers.entrySet()) {
            if (config.isEyeEnabled(entry.getKey())) {
                copyFramebuffer(entry.getValue(), targetBuffer);
            }
        }

        targetBuffer.beginWrite(true);
        this.client.getProfiler().pop();
    }

    @Unique
    private void drawEye(@Nonnull Eye eye, @Nonnull AnaglyphConfig config,
            float tickDelta, long limitTime) {
        if (!config.isEyeEnabled(eye)) {
            return;
        }

        this.client.getProfiler().push("anaglyph_" + eye.toString().toLowerCase());

        // render the world to the main framebuffer
        this.client.getProfiler().push("render_world");
        this.currentEye = Optional.of(eye);
        this.client.getFramebuffer().beginWrite(false);
        renderWorld(tickDelta, limitTime, new MatrixStack());
        this.currentEye = Optional.empty();

        // copy the main framebuffer to the current eye's buffer
        this.client.getProfiler().swap("anaglyph_buffer_copy");
        Framebuffer targetBuffer = getEyeBuffer(eye);
        targetBuffer.clear(false);
        copyFramebuffer(this.client.getFramebuffer(), targetBuffer);

        // clear each disabled color channel
        this.client.getProfiler().swap("clear_disabled");
        boolean enableRed = config.filters.red() == eye;
        boolean enableGreen = config.filters.green() == eye;
        boolean enableBlue = config.filters.blue() == eye;
        RenderSystem.colorMask(!enableRed, !enableGreen, !enableBlue, false);
        targetBuffer.setClearColor(0f, 0f, 0f, 0f);
        targetBuffer.clear(false);
        RenderSystem.colorMask(true, true, true, true);

        this.client.getProfiler().pop();
        this.client.getProfiler().pop();
    }

    @Unique
    private static void copyFramebuffer(Framebuffer src, Framebuffer dst) {
        dst.beginWrite(true);
        src.draw(dst.textureWidth, dst.textureHeight, false);
        dst.endWrite();
    }

}
