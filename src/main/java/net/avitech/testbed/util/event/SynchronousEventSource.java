package net.avitech.testbed.util.event;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.compress.utils.Lists;

public class SynchronousEventSource<E> {

    private final List<Consumer<E>> eventSinks = Lists.newArrayList();

    public void register(Consumer<E> sink) {
        this.eventSinks.add(sink);
    }

    public void submit(E event) {
        this.eventSinks.forEach(sink -> sink.accept(event));
    }

}
