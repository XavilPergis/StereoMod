package net.avitech.testbed.feature.debug.lines;

import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class DebugLine {

    protected Text name;

    public DebugLine(Text name) {
        this.name = name;
    }

    public Text getName() {
        return this.name;
    }

    public abstract void addText(Consumer<String> lineConsumer);

    public static GroupDebugLine group(Text name) {
        return new GroupDebugLine(name);
    }

    public static LiteralDebugLine literal(Text name, Text description) {
        return new LiteralDebugLine(name, description);
    }

    public static LiteralDebugLine literal(Text name, String description) {
        return new LiteralDebugLine(name, Text.of(description));
    }

}
