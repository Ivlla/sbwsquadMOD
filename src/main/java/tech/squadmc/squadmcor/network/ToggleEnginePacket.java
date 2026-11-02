package tech.squadmc.squadmcor.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import tech.squadmc.squadmcor.entity.SquadBaseVehicleEntity;

import java.util.function.Supplier;

public class ToggleEnginePacket {
    public ToggleEnginePacket() {}
    public ToggleEnginePacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Entity vehicle = player.getVehicle();
            if (vehicle instanceof SquadBaseVehicleEntity squadVehicle) {
                int seat = squadVehicle.getSeatIndex(player);
                if (seat == 0) { // РўРѕР»СЊРєРѕ РІРѕРґРёС‚РµР»СЊ РјРѕР¶РµС‚ СѓРїСЂР°РІР»СЏС‚СЊ РґРІРёРіР°С‚РµР»РµРј
                    boolean newState = !squadVehicle.isEngineStarted();
                    squadVehicle.setEngineStarted(newState);

                    if (newState) {
                        player.displayClientMessage(Component.translatable("message.squadmc.engine_started"), true);
                        player.level().playSound(null, squadVehicle.getX(), squadVehicle.getY(), squadVehicle.getZ(),
                                SoundEvents.MINECART_RIDING, SoundSource.NEUTRAL, 0.7F, 1.2F);
                    } else {
                        player.displayClientMessage(Component.translatable("message.squadmc.engine_off"), true);
                        player.level().playSound(null, squadVehicle.getX(), squadVehicle.getY(), squadVehicle.getZ(),
                                SoundEvents.IRON_GOLEM_DEATH, SoundSource.NEUTRAL, 0.4F, 0.5F);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
