package tech.squadmc.squadmcor.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tech.squadmc.squadmcor.squadmc;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(squadmc.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, ToggleEnginePacket.class, ToggleEnginePacket::toBytes, ToggleEnginePacket::new, ToggleEnginePacket::handle);
        CHANNEL.registerMessage(packetId++, SmokeGeneratorPulsePacket.class, SmokeGeneratorPulsePacket::toBytes, SmokeGeneratorPulsePacket::new, SmokeGeneratorPulsePacket::handle);
        CHANNEL.registerMessage(packetId++, MtsFxPacket.class, MtsFxPacket::toBytes, MtsFxPacket::new, MtsFxPacket::handle);
        CHANNEL.registerMessage(packetId++, GunFxPacket.class, GunFxPacket::toBytes, GunFxPacket::new, GunFxPacket::handle);
        CHANNEL.registerMessage(packetId++, tech.squadmc.squadmcor.heli.network.EngineControlPacket.class,
                tech.squadmc.squadmcor.heli.network.EngineControlPacket::encode,
                tech.squadmc.squadmcor.heli.network.EngineControlPacket::decode,
                tech.squadmc.squadmcor.heli.network.EngineControlPacket::handle);
        CHANNEL.registerMessage(packetId++, tech.squadmc.squadmcor.heli.network.SquadFirePacket.class,
                tech.squadmc.squadmcor.heli.network.SquadFirePacket::encode,
                tech.squadmc.squadmcor.heli.network.SquadFirePacket::decode,
                tech.squadmc.squadmcor.heli.network.SquadFirePacket::handle);
        CHANNEL.registerMessage(packetId++, SquadReloadPacket.class, SquadReloadPacket::toBytes, SquadReloadPacket::new, SquadReloadPacket::handle);
        CHANNEL.registerMessage(packetId++, tech.squadmc.squadmcor.heli.HeliFirePacket.class,
                tech.squadmc.squadmcor.heli.HeliFirePacket::toBytes,
                tech.squadmc.squadmcor.heli.HeliFirePacket::new,
                tech.squadmc.squadmcor.heli.HeliFirePacket::handle);
    }
}