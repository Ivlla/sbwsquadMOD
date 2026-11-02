package tech.squadmc.squadmcor.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import tech.squadmc.squadmcor.entity.SquadBaseVehicleEntity;

import java.util.function.Supplier;

/**
 * getShootAnimationTimer(...) достоверно ненулевой только на клиенте (это
 * клиентская анимация выстрела), поэтому опираться на него в серверном
 * tick() нельзя — там он всегда 0. Этот пакет — "пульс" от клиента: пока
 * клиент видит getShootAnimationTimer(0,0) > 0 (водитель жмёт огонь по
 * SmokeLauncher), он раз в тик шлёт этот пакет, а сервер продлевает
 * "запас" (grace) дымогенератора через pulseSmokeGenerator().
 */
public class SmokeGeneratorPulsePacket {
    public SmokeGeneratorPulsePacket() {}
    public SmokeGeneratorPulsePacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Entity vehicle = player.getVehicle();
            if (vehicle instanceof SquadBaseVehicleEntity squadVehicle
                    && squadVehicle.hasSmokeGenerator()
                    && squadVehicle.getSeatIndex(player) == 0) {
                squadVehicle.pulseSmokeGenerator();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}