package tech.squadmc.squadmcor.compat;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3d;
import tech.squadmc.squadmcor.repair.WheelLoss;
import tech.squadmc.squadmcor.squadmc;

import java.util.Optional;

/**
 * TACZ compatibility without a hard dependency on TACZ classes.
 * SBW's OBBs are world-space and identify the physical left/right wheel components.
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class TaczWheelDamage {
    private static final double TRACE_LENGTH = 12.0D;
    private static final float WHEEL_DAMAGE_MULTIPLIER = 1.0F;

    private TaczWheelDamage() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHit(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof VehicleEntity vehicle)
                || vehicle.level().isClientSide || vehicle.isWreck()
                || !WheelLoss.applies(vehicle) || event.getAmount() <= 0.0F) {
            return;
        }

        Entity bullet = event.getSource().getDirectEntity();
        if (bullet == null || !bullet.getClass().getName().startsWith("com.tacz.guns.")) return;

        Vec3 velocity = bullet.getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-8) return;

        Vec3 start = bullet.position().subtract(velocity.normalize().scale(TRACE_LENGTH));
        Vec3 end = bullet.position().add(velocity.normalize().scale(TRACE_LENGTH));
        Vector3d from = new Vector3d(start.x, start.y, start.z);
        Vector3d to = new Vector3d(end.x, end.y, end.z);

        OBB.Part hitPart = null;
        double nearest = Double.POSITIVE_INFINITY;
        for (OBB obb : vehicle.getOBBs()) {
            if (obb.part != OBB.Part.WHEEL_LEFT && obb.part != OBB.Part.WHEEL_RIGHT) continue;
            Optional<Vector3d> hit = obb.clip(new Vector3d(from), new Vector3d(to));
            if (hit.isEmpty()) continue;
            double distance = from.distanceSquared(hit.get());
            if (distance < nearest) {
                nearest = distance;
                hitPart = obb.part;
            }
        }
        if (hitPart == null) return;

        float amount = Math.max(0.0F, event.getAmount() * WHEEL_DAMAGE_MULTIPLIER);
        if (hitPart == OBB.Part.WHEEL_LEFT && vehicle.getLeftWheelMaxHealth() > 0.0F) {
            vehicle.setLeftWheelHealth(Math.max(0.0F, vehicle.getLeftWheelHealth() - amount));
        } else if (hitPart == OBB.Part.WHEEL_RIGHT && vehicle.getRightWheelMaxHealth() > 0.0F) {
            vehicle.setRightWheelHealth(Math.max(0.0F, vehicle.getRightWheelHealth() - amount));
        }
        WheelLoss.update(vehicle);
    }
}
