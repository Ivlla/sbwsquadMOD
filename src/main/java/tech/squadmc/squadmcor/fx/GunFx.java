package tech.squadmc.squadmcor.fx;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import tech.squadmc.squadmcor.network.GunFxPacket;
import tech.squadmc.squadmcor.network.ModNetworking;

import javax.annotation.Nullable;

/** Сервер: выбирает эффект выстрела по снаряду и рассылает его. */
public final class GunFx {
    private static final double RANGE = 192.0;

    public enum Kind { NONE, SMALL, MEDIUM, HEAVY, ROCKET, GRAD }

    private GunFx() {}

    public static Kind kindOf(GunData data) {
        String projectile = data.get(GunProp.PROJECTILE).getId();
        if (projectile.contains("tda_dummy") || projectile.contains("mk_84") || projectile.contains("bomb")) return Kind.NONE;
        if (projectile.contains("rocket") || projectile.contains("missile") || projectile.contains("hellfire")
                || projectile.contains("tow") || projectile.contains("konkurs") || projectile.contains("malyutka")
                || projectile.contains("kh_")) return Kind.ROCKET;
        if (projectile.contains("small_cannon") || projectile.contains("grenade")) return Kind.MEDIUM;
        if (projectile.contains("cannon") || projectile.contains("mortar") || projectile.contains("shell")) return Kind.HEAVY;
        return Kind.SMALL;
    }

    public static Vec3 direction(VehicleEntity vehicle, @Nullable LivingEntity living) {
        return living != null ? vehicle.getShootVec(living, 1F) : vehicle.getViewVector(1F);
    }

    public static void send(ServerLevel level, Kind kind, Vec3 pos, Vec3 direction, int vehicleId, int rocket) {
        ModNetworking.CHANNEL.send(PacketDistributor.NEAR.with(() ->
                        new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, RANGE, level.dimension())),
                new GunFxPacket(kind, pos, direction, vehicleId, rocket));
    }
}
