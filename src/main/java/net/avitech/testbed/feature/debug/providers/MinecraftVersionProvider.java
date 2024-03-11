package net.avitech.testbed.feature.debug.providers;

import net.avitech.testbed.feature.debug.DebugInfoConsumer;
import net.avitech.testbed.feature.debug.lines.DebugLine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MinecraftVersionProvider implements DebugInfoProvider {

    @Override
    public void appendDebugInfo(DebugInfoConsumer infoConsumer) {
        MinecraftClient client = MinecraftClient.getInstance();

        String versionName = SharedConstants.getGameVersion().getName();
        String gameVersion = client.getGameVersion();
        String clientBrand = ClientBrandRetriever.getClientModName();
        String versionType = "release".equalsIgnoreCase(client.getVersionType()) ? "" : client.getVersionType();

        infoConsumer.addInfo(DebugLine.literal(Text.of("Minecraft Version"),
                String.format("%s (%s/%s/%s)", versionName, gameVersion, clientBrand, versionType)));
    }

}
