package net.avitech.testbed.feature.debug.lines;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GroupDebugLine extends DebugLine {

	private Optional<Text> primaryText = Optional.empty();
	private List<Text> secondaryTexts = Lists.newArrayList();

	public GroupDebugLine(Text name) {
		super(name);
	}

	public GroupDebugLine withPrimary(Text primary) {
		primaryText = Optional.of(primary);
		return this;
	}

	public GroupDebugLine withPrimary(String primary) {
		return withPrimary(Text.of(primary));
	}

	public GroupDebugLine withSecondary(Text secondary) {
		secondaryTexts.add(secondary);
		return this;
	}

	public GroupDebugLine withSecondary(String secondary) {
		return withSecondary(Text.of(secondary));
	}

	@Override
	public void addText(Consumer<String> lineConsumer) {
		if (primaryText.isEmpty() && secondaryTexts.isEmpty())
			return;

		lineConsumer.accept("");

		String groupFormatting = secondaryTexts.isEmpty() ? "" : Formatting.UNDERLINE.toString();
		lineConsumer.accept(groupFormatting
				+ Formatting.BOLD
				+ this.name.getString()
				+ Formatting.RESET + groupFormatting
				+ this.primaryText.map(text -> String.format(": %s", text.getString())).orElse("")
				+ Formatting.RESET);
		this.secondaryTexts.stream().map(Text::getString).forEachOrdered(lineConsumer);
	}

}
