package tech.squadmc.squadmcor.network;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Tells a client to reload its resources after /squadmc reload. */
public class SquadReloadPacket {

    public SquadReloadPacket() {
    }

    public SquadReloadPacket(FriendlyByteBuf buffer) {
    }

    public void toBytes(FriendlyByteBuf buffer) {
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Client::reload));
        context.get().setPacketHandled(true);
    }

    private static final class Client {
        private static void reload() {
            Minecraft mc = Minecraft.getInstance();
            mc.reloadResourcePacks().thenRun(() -> mc.execute(() -> {
                if (mc.level == null) return;
                for (Entity entity : mc.level.entitiesForRendering()) {
                    if (entity instanceof VehicleEntity vehicle) vehicle.data().update();
                }
            }));
        }
    }
}
