package net.avitech.stereomod;

import java.util.Arrays;
import java.util.function.IntFunction;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.MathHelper;
import virtuoel.pehkui.api.ScaleTypes;

@Environment(EnvType.CLIENT)
public enum StereoConfig {
	INSTANCE;

	public enum Mode implements TranslatableOption {
		NONE(0, "options.stereo.mode.none"),
		GLASSES(1, "options.stereo.mode.glasses"),
		CROSS_EYE_SIDE_BY_SIDE(2, "options.stereo.mode.cross_eye"),
		WALL_EYE_SIDE_BY_SIDE(3, "options.stereo.mode.wall_eye");

		private static final IntFunction<Mode> BY_ID = ValueLists.createIdToValueFunction(
				Mode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);
		private final int id;
		private final String key;

		private Mode(int id, String key) {
			this.id = id;
			this.key = key;
		}

		@Override
		public int getId() {
			return this.id;
		}

		public static Mode byId(int id) {
			return BY_ID.apply(id);
		}

		@Override
		public String getTranslationKey() {
			return this.key;
		}

		public boolean isEnabled() {
			return this != NONE;
		}

		public boolean isSideBySide() {
			return this == CROSS_EYE_SIDE_BY_SIDE || this == WALL_EYE_SIDE_BY_SIDE;
		}

		@Nullable
		public Eye getOutputSide(Eye currentEye) {
			if (this == CROSS_EYE_SIDE_BY_SIDE)
				return currentEye.opposite();
			if (this == WALL_EYE_SIDE_BY_SIDE)
				return currentEye;
			return null;
		}
	}

	public enum GlassesMode implements TranslatableOption {
		RED_CYAN(0, "options.stereo.glasses.red_cyan", new EyeFilters(Eye.LEFT, Eye.RIGHT, Eye.RIGHT)),
		GREEN_MAGENTA(1, "options.stereo.glasses.green_magenta", new EyeFilters(Eye.RIGHT, Eye.LEFT, Eye.RIGHT)),
		YELLOW_BLUE(2, "options.stereo.glasses.yellow_blue", new EyeFilters(Eye.LEFT, Eye.LEFT, Eye.RIGHT)),
		RED_BLUE(3, "options.stereo.glasses.red_blue", new EyeFilters(Eye.LEFT, null, Eye.RIGHT)),
		GREEN_RED(4, "options.stereo.glasses.green_red", new EyeFilters(Eye.RIGHT, Eye.LEFT, null));

		private static final IntFunction<GlassesMode> BY_ID = ValueLists.createIdToValueFunction(
				GlassesMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);
		public final EyeFilters filters;
		private final int id;
		private final String key;

		private GlassesMode(int id, String key, EyeFilters filters) {
			this.id = id;
			this.key = key;
			this.filters = filters;
		}

		@Override
		public int getId() {
			return this.id;
		}

		public static GlassesMode byId(int id) {
			return BY_ID.apply(id);
		}

		@Override
		public String getTranslationKey() {
			return this.key;
		}
	}

	/**
	 * Describes the properties of a physical anaglyph filter. Each color channel
	 * can be let through at most one eye, or else the effect does not work.
	 */
	public static record EyeFilters(Eye red, Eye green, Eye blue) {
	}

	public static final double BASE_FOCAL_LENGTH_MIN = 1e-3;
	public static final double BASE_FOCAL_LENGTH_MAX = 1e5;
	public static final double BASE_FOCAL_LENGTH_DEFAULT = 10;
	public static final double BASE_EYE_OFFSET_MIN = 1e-3;
	public static final double BASE_EYE_OFFSET_MAX = 0.5;
	public static final double BASE_EYE_OFFSET_DEFAULT = 0.1;

	private static double logMapping(double x, double S, double E) {
		final double logS = Math.log(S), logE = Math.log(E);
		return Math.exp(MathHelper.lerp(x, logS, logE));
	}

	private static double invLogMapping(double x, double S, double E) {
		return Math.log(x / S) / Math.log(E / S);
	}

	/**
	 * @see #getFocalLength(float)
	 */
	public final SimpleOption<Double> focalLengthOption = new SimpleOption<Double>(
			"options.stereo.focal_length",
			SimpleOption.emptyTooltip(),
			(optionText, value) -> Text.translatable("options.generic_value", optionText, String.format("%.2f", value)),
			SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(
					t -> logMapping(t, BASE_FOCAL_LENGTH_MIN, BASE_FOCAL_LENGTH_MAX),
					v -> invLogMapping(v, BASE_FOCAL_LENGTH_MIN, BASE_FOCAL_LENGTH_MAX)),
			Codec.doubleRange(BASE_FOCAL_LENGTH_MIN, BASE_FOCAL_LENGTH_MAX),
			BASE_FOCAL_LENGTH_DEFAULT,
			value -> {
			});

	/**
	 * @see #getEyeOffset(float)
	 */
	public final SimpleOption<Double> eyeOffsetOption = new SimpleOption<Double>(
			"options.stereo.eye_offset",
			SimpleOption.emptyTooltip(),
			(optionText, value) -> Text.translatable("options.generic_value", optionText, String.format("%.2f", value)),
			SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(
					t -> logMapping(t, BASE_EYE_OFFSET_MIN, BASE_EYE_OFFSET_MAX),
					v -> invLogMapping(v, BASE_EYE_OFFSET_MIN, BASE_EYE_OFFSET_MAX)),
			Codec.doubleRange(BASE_EYE_OFFSET_MIN, BASE_EYE_OFFSET_MAX),
			BASE_EYE_OFFSET_DEFAULT,
			value -> {
			});

	/**
	 * The current stereo rendering mode (disabled, 3d glasses, or side-by-side)
	 */
	public final SimpleOption<Mode> stereoModeOption = new SimpleOption<Mode>(
			"options.stereo.mode",
			SimpleOption.emptyTooltip(),
			SimpleOption.enumValueText(),
			new SimpleOption.PotentialValuesBasedCallbacks<Mode>(
					Arrays.asList(Mode.values()),
					Codec.INT.xmap(Mode::byId, Mode::getId)),
			Mode.NONE,
			value -> {
			});

	/**
	 * The type of 3d glasses.
	 */
	public final SimpleOption<GlassesMode> glassesModeOption = new SimpleOption<GlassesMode>(
			"options.stereo.glasses_mode",
			SimpleOption.emptyTooltip(),
			SimpleOption.enumValueText(),
			new SimpleOption.PotentialValuesBasedCallbacks<GlassesMode>(
					Arrays.asList(GlassesMode.values()),
					Codec.INT.xmap(GlassesMode::byId, GlassesMode::getId)),
			GlassesMode.RED_CYAN,
			value -> {
			});

	public static SimpleOption<?>[] getOptions() {
		return new SimpleOption<?>[] {
				INSTANCE.focalLengthOption, INSTANCE.eyeOffsetOption,
				INSTANCE.stereoModeOption, INSTANCE.glassesModeOption };
	}

	public double getEyeOffset() {
		return getEyeOffset(MinecraftClient.getInstance().getTickDelta());
	}

	/**
	 * @param partialTick
	 * @return The distance from the center of the camera's position to either eye.
	 */
	public double getEyeOffset(float partialTick) {
		double res = eyeOffsetOption.getValue();
		if (FabricLoader.getInstance().isModLoaded("pehkui")) {
			final var client = MinecraftClient.getInstance();
			final var camEntity = client.cameraEntity != null ? client.cameraEntity : client.player;
			res *= ScaleTypes.WIDTH.getScaleData(camEntity).getScale(partialTick);
		}
		return res;
	}

	public double getFocalLength() {
		return getFocalLength(MinecraftClient.getInstance().getTickDelta());
	}

	/**
	 * The distance from the camera at which things appear at zero depth. Anything
	 * closer than this distance will look like it's popping out of the screen, and
	 * anything further away will look sunken into the screen.
	 * 
	 * @param partialTick
	 * @return The camera focal length.
	 */
	public double getFocalLength(float partialTick) {
		double res = focalLengthOption.getValue();
		if (FabricLoader.getInstance().isModLoaded("pehkui")) {
			final var client = MinecraftClient.getInstance();
			final var camEntity = client.cameraEntity != null ? client.cameraEntity : client.player;
			res *= ScaleTypes.WIDTH.getScaleData(camEntity).getScale(partialTick);
		}
		return res;
	}

	public EyeFilters getFilters() {
		return this.glassesModeOption.getValue().filters;
	}

}
