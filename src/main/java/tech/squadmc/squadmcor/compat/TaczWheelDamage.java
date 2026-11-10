package tech.squadmc.squadmcor.compat;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3d;
import tech.squadmc.squadmcor.repair.WheelLoss;
import tech.squadmc.squadmcor.squadmc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Server-side wheel intersection checks for TACZ kinetic bullets.
 *
 * Vehicles in SBW extend Entity (not LivingEntity), so LivingHurtEvent is
 * never fired for them. TACZ bullets are tracked without linking the TACZ API.
 * Tests use the projectile's forward motion before its own tick may discard it.
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class TaczWheelDamage {
    private static final String TACZ_BULLET = "com.tacz.guns.entity.EntityKineticBullet";
    private static final float BULLET_WHEEL_DAMAGE = 12F;
    private static final double MAX_TRACE = 16D;
    private static final Map<Entity, Set<Integer>> BULLETS = new HashMap<>();

    private TaczWheelDamage() {}

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        if (entity.getClass().getName().equals(TACZ_BULLET)) {
            BULLETS.put(entity, new HashSet<>());
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !(event.level instanceof ServerLevel level)) return;
        Iterator<Map.Entry<Entity, Set<Integer>>> iterator = BULLETS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Entity, Set<Integer>> tracked = iterator.next();
            Entity bullet = tracked.getKey();
            if (bullet.isRemoved() || !bullet.isAlive()) {
                iterator.remove();
                continue;
            }
            if (bullet.level() != level) continue;
            Vec3 motion = bullet.getDeltaMovement();
            if (motion.lengthSqr() < 1.0E-8) continue;
            // Avoid tracing across large regions for glitched projectile speeds.
            Vec3 delta = motion.length() > MAX_TRACE ? motion.normalize().scale(MAX_TRACE) : motion;
            Vec3 start = bullet.position();
            Vec3 end = start.add(delta);
            AABB swept = bullet.getBoundingBox().expandTowards(delta).inflate(1.5D);
            Vector3d from = new Vector3d(start.x, start.y, start.z);
            Vector3d to = new Vector3d(end.x, end.y, end.z);
            for (VehicleEntity vehicle : level.getEntitiesOfClass(VehicleEntity.class, swept)) {
                if (vehicle.isWreck() || !WheelLoss.applies(vehicle)
                        || tracked.getValue().contains(vehicle.getId())) continue;
                OBB.Part side = null;
                double nearest = Double.POSITIVE_INFINITY;
                for (OBB obb : vehicle.getOBBs()) {
                    if (obb.part != OBB.Part.WHEEL_LEFT && obb.part != OBB.Part.WHEEL_RIGHT) continue;
                    Optional<Vector3d> intersection = obb.clip(new Vector3d(from), new Vector3d(to));
                    if (intersection.isEmpty()) continue;
                    double distance = from.distanceSquared(intersection.get());
                    if (distance < nearest) {
                        nearest = distance;
                        side = obb.part;
                    }
                }
                if (side == null) continue;
                tracked.getValue().add(vehicle.getId());
                if (side == OBB.Part.WHEEL_LEFT && vehicle.getLeftWheelMaxHealth() > 0F) {
                    vehicle.setLeftWheelHealth(Math.max(0F, vehicle.getLeftWheelHealth() - BULLET_WHEEL_DAMAGE));
                } else if (side == OBB.Part.WHEEL_RIGHT && vehicle.getRightWheelMaxHealth() > 0F) {
                    vehicle.setRightWheelHealth(Math.max(0F, vehicle.getRightWheelHealth() - BULLET_WHEEL_DAMAGE));
                }
                WheelLoss.update(vehicle);
            }
        }
    }
}
