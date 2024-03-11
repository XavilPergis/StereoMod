package net.avitech.testbed.util.event;

public final class ClientEvents {

    public static SynchronousEventSource<BeginFrame> BEGIN_FRAME = new SynchronousEventSource<>();

    public static class BeginFrame {
    }

}
