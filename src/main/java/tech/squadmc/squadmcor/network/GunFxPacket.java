package tech.squadmc.squadmcor.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import tech.squadmc.squadmcor.fx.GunFx;

import java.util.function.Supplier;

/** Эффект выстрела: вид, точка, направление. Для Града ещё машина и номер ракеты. */
public class GunFxPacket {
    private final GunFx.Kind kind;
    private final Vec3 pos;
    private final Vec3 direction;
    private final int vehicleId;
    private final int rocket;

    public GunFxPacket(GunFx.Kind kind, Vec3 pos, Vec3 direction, int vehicleId, int rocket) {
        this.kind = kind;
        this.pos = pos;
        this.direction = direction;
        this.vehicleId = vehicleId;
        this.rocket = rocket;
    }

    public GunFxPacket(FriendlyByteBuf buf) {
        this.kind = buf.readEnum(GunFx.Kind.class);
        this.pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.direction = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
        this.vehicleId = buf.readVarInt();
        this.rocket = buf.readVarInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(this.kind);
        buf.writeDouble(this.pos.x);
        buf.writeDouble(this.pos.y);
        buf.writeDouble(this.pos.z);
        buf.writeFloat((float) this.direction.x);
        buf.writeFloat((float) this.direction.y);
        buf.writeFloat((float) this.direction.z);
        buf.writeVarInt(this.vehicleId);
        buf.writeVarInt(this.rocket);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> tech.squadmc.squadmcor.client.GunFxClient.play(this.kind, this.pos, this.direction, this.vehicleId, this.rocket)));
        ctx.get().setPacketHandled(true);
    }
}
