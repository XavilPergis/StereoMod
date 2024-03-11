package net.avitech.testbed.feature.debug;

import java.util.List;
import java.util.stream.Stream;

import net.avitech.testbed.feature.debug.lines.DebugLine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface DebugInfoConsumer {

    /**
     * 
     * @param name
     */
    void addInfo(DebugLine info);

    /**
     * 
     * @return
     */
    Stream<String> getOrderedLines(List<String> defaultDebugLines);

}
