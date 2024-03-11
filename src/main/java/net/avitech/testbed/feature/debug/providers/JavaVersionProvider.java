package net.avitech.testbed.feature.debug.providers;

import net.avitech.testbed.feature.debug.DebugInfoConsumer;
import net.avitech.testbed.feature.debug.lines.DebugLine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class JavaVersionProvider implements DebugInfoProvider {

    @Override
    public void appendDebugInfo(DebugInfoConsumer infoConsumer) {
        String javaVersion = System.getProperty("java.version");
        int wordSize = MinecraftClient.getInstance().is64Bit() ? 64 : 32;
        infoConsumer.addInfo(DebugLine.literal(
                Text.of("Java Version"),
                String.format("%s %dbit", javaVersion, wordSize)));
    }

}
