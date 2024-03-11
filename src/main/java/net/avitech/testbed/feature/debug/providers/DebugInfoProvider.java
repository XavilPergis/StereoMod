package net.avitech.testbed.feature.debug.providers;

import net.avitech.testbed.feature.debug.DebugInfoConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface DebugInfoProvider {

    /**
     * 
     * @param infoHandler
     */
    void appendDebugInfo(DebugInfoConsumer infoConsumer);

}
