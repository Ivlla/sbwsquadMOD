package tech.squadmc.squadmcor.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tech.squadmc.squadmcor.spike.SpikeControl;
import tech.squadmc.squadmcor.client.SpikeClient;
import tech.squadmc.squadmcor.squadmc;

import java.util.function.Supplier;
import java.util.UUID;

public final class SpikeNetwork {
    private static final String VERSION = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(squadmc.MODID, "spike_control"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    private SpikeNetwork() {}

    public static void register() {
        CHANNEL.messageBuilder(ScopeState.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ScopeState::encode).decoder(ScopeState::decode)
                .consumerMainThread(ScopeState::handle).add();
        CHANNEL.messageBuilder(ShotFired.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ShotFired::encode).decoder(ShotFired::decode)
                .consumerMainThread(ShotFired::handle).add();
    }

    // Only input state is sent. Position, look direction, damage and ownership
    // are always taken from the server; no client-supplied entity ID is accepted.
    public record ScopeState(boolean scoped, boolean fireHeld, int slot) {
        private static void encode(ScopeState message, FriendlyByteBuf buffer) {
            buffer.writeBoolean(message.scoped);
            buffer.writeBoolean(message.fireHeld);
            buffer.writeVarInt(message.slot);
        }

        private static ScopeState decode(FriendlyByteBuf buffer) {
            return new ScopeState(buffer.readBoolean(), buffer.readBoolean(), buffer.readVarInt());
        }

        private static void handle(ScopeState message, Supplier<NetworkEvent.Context> context) {
            ServerPlayer sender = context.get().getSender();
            if (sender != null) SpikeControl.update(sender, message.scoped, message.fireHeld, message.slot);
            context.get().setPacketHandled(true);
        }
    }

    public record ShotFired(int slot, UUID launcherId) {
        private static void encode(ShotFired message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.slot);
            buffer.writeUUID(message.launcherId);
        }

        private static ShotFired decode(FriendlyByteBuf buffer) {
            return new ShotFired(buffer.readVarInt(), buffer.readUUID());
        }

        private static void handle(ShotFired message, Supplier<NetworkEvent.Context> context) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> SpikeClient.onShotFired(message.slot, message.launcherId));
            context.get().setPacketHandled(true);
        }
    }
}
