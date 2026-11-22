// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server -> client: one shot from a vehicle weapon with Squad fire sounds (see SquadWeaponFire). */
public class SquadFirePacket {

    private final int range;
    private final int vehicleId;
    private final String weapon;
    private final int shooterId;
    private final String vehicleType;
    private final net.minecraft.world.phys.Vec3 position;

    public SquadFirePacket(int vehicleId, String weapon, int shooterId, String vehicleType, net.minecraft.world.phys.Vec3 position, int range) {
        this.range = range;
        this.vehicleId = vehicleId;
        this.weapon = weapon;
        this.shooterId = shooterId;
        this.vehicleType = vehicleType;
        this.position = position;
    }

    public static void encode(SquadFirePacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.vehicleId);
        buf.writeUtf(msg.weapon, 64);
        buf.writeVarInt(msg.shooterId);
        buf.writeUtf(msg.vehicleType, 128);
        buf.writeDouble(msg.position.x);
        buf.writeDouble(msg.position.y);
        buf.writeDouble(msg.position.z);
        buf.writeVarInt(msg.range);
    }

    public static SquadFirePacket decode(FriendlyByteBuf buf) {
        return new SquadFirePacket(buf.readVarInt(), buf.readUtf(64), buf.readVarInt(), buf.readUtf(128),
                new net.minecraft.world.phys.Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readVarInt());
    }

    public static void handle(SquadFirePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                // Indirection keeps client classes off dedicated-server classloading.
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        tech.squadmc.squadmcor.heli.client.sound.SquadWeaponSounds.onShot(msg.vehicleId, msg.weapon, msg.shooterId, msg.vehicleType, msg.position, msg.range)));
        ctx.get().setPacketHandled(true);
    }
}
