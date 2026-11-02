package tech.squadmc.squadmcor.heli;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Сервер → клиент: выстрел с вертолёта, сила толчка камеры для тех, кто сидит внутри. */
public class HeliFirePacket {
    public static final float GUN = 0.06F;
    public static final float CANNON = 0.22F;
    public static final float MISSILE = 0.75F;
    public static final float ROCKET = 1.0F;

    private final int vehicleId;
    private final float kick;

    public HeliFirePacket(int vehicleId, float kick) {
        this.vehicleId = vehicleId;
        this.kick = kick;
    }

    public HeliFirePacket(FriendlyByteBuf buf) {
        this.vehicleId = buf.readVarInt();
        this.kick = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.vehicleId);
        buf.writeFloat(this.kick);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> HeliCameraShake.onFire(this.vehicleId, this.kick)));
        ctx.get().setPacketHandled(true);
    }
}
