package net.avitech.testbed.feature.debug;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import net.avitech.testbed.feature.debug.lines.DebugLine;

public class DefaultInfoConsumer implements DebugInfoConsumer {

    private final List<DebugLine> debugInfos = Lists.newArrayList();

    @Override
    public void addInfo(DebugLine info) {
        debugInfos.add(info);
    }

    @Override
    public Stream<String> getOrderedLines(List<String> defaultDebugLines) {
        Stream<String> infoStream = debugInfos.stream().mapMulti((info, consumer) -> info.addText(consumer));
        return Stream.concat(infoStream, defaultDebugLines.stream());
        // return infoStream;
    }

}
