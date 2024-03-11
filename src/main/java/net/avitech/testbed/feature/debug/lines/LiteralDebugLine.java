package net.avitech.testbed.feature.debug.lines;

import java.util.function.Consumer;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LiteralDebugLine extends DebugLine {

	private final Text description;

	public LiteralDebugLine(Text name, Text description) {
		super(name);
		this.description = description;
	}

	@Override
	public void addText(Consumer<String> lineConsumer) {
		lineConsumer.accept(String.format("%s%s%s: %s", Formatting.BOLD, this.name.getString(), Formatting.RESET,
				description.getString()));
	}

}
