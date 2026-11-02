package tech.squadmc.squadmcor.network;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import tech.squadmc.squadmcor.fx.MtsFxClient;
import tech.squadmc.squadmcor.fx.MtsFxPreset;

import java.util.function.Supplier;

public class MtsFxPacket {
    private final MtsFxPreset preset;
    private final boolean vehicleHit;
    private final Vec3 pos;
    private final Direction normal;

    public MtsFxPacket(MtsFxPreset preset, boolean vehicleHit, Vec3 pos, Direction normal) {
        this.preset = preset;
        this.vehicleHit = vehicleHit;
        this.pos = pos;
        this.normal = normal;
    }

    public MtsFxPacket(FriendlyByteBuf buf) {
        this.preset = buf.readEnum(MtsFxPreset.class);
        this.vehicleHit = buf.readBoolean();
        this.pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.normal = buf.readEnum(Direction.class);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(this.preset);
        buf.writeBoolean(this.vehicleHit);
        buf.writeDouble(this.pos.x);
        buf.writeDouble(this.pos.y);
        buf.writeDouble(this.pos.z);
        buf.writeEnum(this.normal);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> MtsFxClient.play(this.preset, this.vehicleHit, this.pos, this.normal)));
        ctx.get().setPacketHandled(true);
    }
}
