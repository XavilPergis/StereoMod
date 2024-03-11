package net.avitech.testbed.feature.debug.providers;

import com.mojang.blaze3d.platform.GlDebugInfo;

import net.avitech.testbed.feature.debug.DebugInfoConsumer;
import net.avitech.testbed.feature.debug.lines.DebugLine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class HardwareInfoProvider implements DebugInfoProvider {

    @Override
    public void appendDebugInfo(DebugInfoConsumer infoConsumer) {

        MinecraftClient.getInstance().getWindow().getFramebufferWidth();
        MinecraftClient.getInstance().getWindow().getFramebufferHeight();

        Window window = MinecraftClient.getInstance().getWindow();
        String videoMode = window.getVideoMode().map(VideoMode::asString).orElse("<unknown>");

        infoConsumer.addInfo(DebugLine.literal(Text.of("CPU"), Text.of(GlDebugInfo.getCpuInfo())));
        infoConsumer.addInfo(DebugLine.literal(Text.of("GPU"), Text.of(GlDebugInfo.getRenderer())));
        infoConsumer.addInfo(DebugLine.literal(Text.of("OpenGL Version"),
                Text.of(String.format("%s (%s)", GlDebugInfo.getVersion(), GlDebugInfo.getVendor()))));
        infoConsumer.addInfo(DebugLine.literal(Text.of("Display"), Text.of(String.format("%dx%d, %dHz, %s",
                window.getFramebufferWidth(), window.getFramebufferHeight(), window.getRefreshRate(), videoMode))));
    }

}
