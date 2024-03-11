package net.avitech.testbed.feature.debug.providers;

import java.util.stream.Stream;

import net.avitech.testbed.feature.debug.DebugInfoConsumer;
import net.avitech.testbed.feature.debug.lines.DebugLine;
import net.avitech.testbed.feature.debug.lines.GroupDebugLine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;

@Environment(EnvType.CLIENT)
public class TargetedBlockProvider implements DebugInfoProvider {

	@Override
	public void appendDebugInfo(DebugInfoConsumer infoConsumer) {
		MinecraftClient client = MinecraftClient.getInstance();

		HitResult blockHit = client.cameraEntity.raycast(20.0, 0.0f, false);
		HitResult fluidHit = client.cameraEntity.raycast(20.0, 0.0f, true);

		if (blockHit.getType() == HitResult.Type.BLOCK) {
			var line = DebugLine.group(Text.of("Targeted Block"));
			BlockPos hitPos = ((BlockHitResult) blockHit).getBlockPos();
			BlockState lookAtState = client.world.getBlockState(hitPos);

			line.withPrimary(String.format("%d, %d, %d", hitPos.getX(), hitPos.getY(), hitPos.getZ()));
			line.withSecondary(Registries.BLOCK.getId(lookAtState.getBlock()).toString());

			addStateProperties(line, lookAtState);
			addStateTags(line, lookAtState.streamTags());

			infoConsumer.addInfo(line);
		}

		if (fluidHit.getType() == HitResult.Type.BLOCK) {
			var line = DebugLine.group(Text.of("Targeted Fluid"));
			BlockPos hitPos = ((BlockHitResult) fluidHit).getBlockPos();
			FluidState lookAtState = client.world.getFluidState(hitPos);

			if (!lookAtState.isEmpty()) {
				line.withPrimary(String.format("%d, %d, %d", hitPos.getX(), hitPos.getY(), hitPos.getZ()));
				line.withSecondary(Registries.FLUID.getId(lookAtState.getFluid()).toString());

				addStateProperties(line, lookAtState);
				addStateTags(line, lookAtState.streamTags());

				infoConsumer.addInfo(line);
			}
		}
	}

	private static <T> void addStateTags(GroupDebugLine line, Stream<TagKey<T>> tags) {
		tags.map(tag -> String.format("%s%s#%s", Formatting.YELLOW, Formatting.ITALIC, tag.id()))
				.forEach(tag -> line.withSecondary(tag));

	}

	private static void addStateProperties(GroupDebugLine line, State<?, ?> state) {
		for (var entry : state.getEntries().entrySet()) {
			Property<?> property = entry.getKey();
			Comparable<?> comparable = entry.getValue();

			StringBuilder propertyString = new StringBuilder();

			propertyString.append(property.getName());
			propertyString.append(": ");

			if (property instanceof IntProperty) {
				propertyString.append(Formatting.AQUA + Util.getValueAsString(property, comparable));
			} else if (property instanceof BooleanProperty) {
				Formatting color = Boolean.TRUE.equals(comparable) ? Formatting.GREEN : Formatting.RED;
				propertyString.append(color + Util.getValueAsString(property, comparable));
			} else if (property instanceof EnumProperty<?>) {
				propertyString
						.append(Formatting.GREEN + "\"" + Util.getValueAsString(property, comparable) + "\"");
			} else {
				propertyString.append(Util.getValueAsString(property, comparable));
			}

			line.withSecondary(propertyString.toString());
		}
	}

}
