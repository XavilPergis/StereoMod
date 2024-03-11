package net.avitech.testbed.feature.debug.providers;

import net.avitech.testbed.feature.debug.DebugInfoConsumer;
import net.avitech.testbed.feature.debug.lines.DebugLine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MemoryUsageProvider implements DebugInfoProvider {

    @Override
    public void appendDebugInfo(DebugInfoConsumer infoConsumer) {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        long usedMemory = totalMemory - freeMemory;
        long usedMemoryPercentage = 100L * usedMemory / maxMemory;
        long allocatedMemoryPercentage = 100L * totalMemory / maxMemory;

        infoConsumer.addInfo(DebugLine.group(Text.of("Memory"))
                .withSecondary(String.format("Used: %2d%% %d/%d MiB", usedMemoryPercentage, usedMemory / (1024 * 1024),
                        maxMemory / (1024 * 1024)))
                .withSecondary(String.format("Allocated: %2d%% %d MiB", allocatedMemoryPercentage,
                        totalMemory / (1024 * 1024))));
    }

}
