package net.avitech.testbed.feature.debug;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import net.avitech.testbed.feature.debug.providers.DebugInfoProvider;
import net.avitech.testbed.feature.debug.providers.HardwareInfoProvider;
import net.avitech.testbed.feature.debug.providers.JavaVersionProvider;
import net.avitech.testbed.feature.debug.providers.MemoryUsageProvider;
import net.avitech.testbed.feature.debug.providers.TargetedBlockProvider;
import net.avitech.testbed.feature.debug.providers.TestProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

@Environment(EnvType.CLIENT)
public final class Debug {

	public static final Identifier DEBUG_INFO_PROVIDER_ID = new Identifier("debug_info_provider");
	public static final Identifier DEBUG_INFO_CONSUMER_FACTORY_ID = new Identifier("debug_info_consumer");

	public static final RegistryKey<Registry<DebugInfoProvider>> DEBUG_INFO_PROVIDER_KEY;
	public static final Registry<DebugInfoProvider> DEBUG_INFO_PROVIDER;
	public static final RegistryKey<Registry<DebugInfoConsumerFactory>> DEBUG_INFO_CONSUMER_FACTORY_KEY;
	public static final Registry<DebugInfoConsumerFactory> DEBUG_INFO_CONSUMER_FACTORY;

	public static final DebugInfoConsumerFactory DEFAULT_CONSUMER_FACTORY;

	static {
		DEBUG_INFO_PROVIDER_KEY = RegistryKey.ofRegistry(DEBUG_INFO_PROVIDER_ID);
		DEBUG_INFO_PROVIDER = FabricRegistryBuilder.createSimple(DebugInfoProvider.class, DEBUG_INFO_PROVIDER_ID)
				.buildAndRegister();
		DEBUG_INFO_CONSUMER_FACTORY_KEY = RegistryKey.ofRegistry(DEBUG_INFO_CONSUMER_FACTORY_ID);
		DEBUG_INFO_CONSUMER_FACTORY = FabricRegistryBuilder
				.createSimple(DebugInfoConsumerFactory.class, DEBUG_INFO_CONSUMER_FACTORY_ID)
				.buildAndRegister();

		DEFAULT_CONSUMER_FACTORY = () -> new DefaultInfoConsumer();

		Registry.register(DEBUG_INFO_PROVIDER, "test", new TestProvider());
		Registry.register(DEBUG_INFO_PROVIDER, "hardware_info", new HardwareInfoProvider());
		Registry.register(DEBUG_INFO_PROVIDER, "java_version", new JavaVersionProvider());
		Registry.register(DEBUG_INFO_PROVIDER, "memory_usage", new MemoryUsageProvider());
		Registry.register(DEBUG_INFO_PROVIDER, "targeted_block", new TargetedBlockProvider());
	}

	public static final Debug INSTANCE = new Debug();

	private Set<Identifier> enabledInfoProvidersLeft = Sets.newHashSet();
	private Set<Identifier> enabledInfoProvidersRight = Sets.newHashSet(
			new Identifier("test"),
			new Identifier("hardware_info"),
			new Identifier("java_version"),
			new Identifier("targeted_block"),
			new Identifier("memory_usage"));
	private Identifier activeInfoConsumerFactory;

	public DebugInfoConsumer createConsumer() {
		return DEBUG_INFO_CONSUMER_FACTORY.getOrEmpty(activeInfoConsumerFactory).orElse(DEFAULT_CONSUMER_FACTORY)
				.createConsumer();
	}

	public void getOrderedInfoProvidersLeft(Consumer<DebugInfoProvider> consumer) {
		iterateProviders(enabledInfoProvidersLeft.stream(), consumer);
	}

	public void getOrderedInfoProvidersRight(Consumer<DebugInfoProvider> consumer) {
		iterateProviders(enabledInfoProvidersRight.stream(), consumer);
	}

	private static void iterateProviders(Stream<Identifier> ids, Consumer<DebugInfoProvider> consumer) {
		ids.map(id -> DEBUG_INFO_PROVIDER.getOrEmpty(id))
				.filter(Optional::isPresent).map(Optional::get)
				.forEachOrdered(consumer::accept);
	}

}
