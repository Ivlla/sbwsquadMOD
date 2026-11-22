package tech.squadmc.squadmcor.entity;

import com.atsuishio.superbwarfare.entity.projectile.WireGuideMissileEntity;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import tech.squadmc.squadmcor.init.ModSounds;

public class KonkursDummyProjectile extends ThrowableItemProjectile {
    private static final int LAUNCH_DELAY = 30; // 30 С‚РёРєРѕРІ Р·Р°РґРµСЂР¶РєРё РїРµСЂРµРґ РїСѓСЃРєРѕРј РџРўРЈР 
    private int delayTicks = 0;


    // --- РќРђРЎРўР РћР™РљР РЈР РћРќРђ РљРћРќРљРЈР РЎ ---

    public KonkursDummyProjectile(EntityType<? extends KonkursDummyProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        var shot = getPersistentData();
        shot.putFloat("Velocity", velocity);
        Entity owner = getOwner();
        if (owner != null && owner.getVehicle() instanceof VehicleEntity vehicle) {
            var gun = vehicle.getGunData(owner);
            if (gun != null) {
                shot.putFloat("Damage", gun.get(GunProp.DAMAGE).floatValue());
                shot.putFloat("ExplosionDamage", gun.get(GunProp.EXPLOSION_DAMAGE).floatValue());
                shot.putFloat("ExplosionRadius", gun.get(GunProp.EXPLOSION_RADIUS).floatValue());
                shot.putInt("ProjectileLife", gun.get(GunProp.PROJECTILE_LIFE));
            }
        }
        super.shoot(x, y, z, velocity, inaccuracy);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            Entity owner = this.getOwner();
            if (owner != null) {
                this.setPos(owner.getX(), owner.getY() + owner.getEyeHeight() - 0.3D, owner.getZ());
                this.setDeltaMovement(Vec3.ZERO);
            }

            this.delayTicks++;
            if (this.delayTicks >= LAUNCH_DELAY) {
                this.launchRealMissile();
                this.discard();
                return;
            }
        }
        super.tick();
    }

    private void launchRealMissile() {
        Entity playerOwner = this.getOwner();
        if (playerOwner == null) return;

        Entity vehicle = playerOwner.getVehicle();
        if (vehicle == null) return;

        this.level().playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                ModSounds.KONKURS_LAUNCH_BLAST.get(),
                SoundSource.NEUTRAL,
                3.0F,
                1.0F
        );

        this.level().broadcastEntityEvent(vehicle, (byte) 101);

        EntityType<?> missileType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("superbwarfare", "wire_guide_missile"));
        if (missileType != null) {
            Entity spawned = missileType.create(this.level());

            if (spawned instanceof WireGuideMissileEntity missile) {
                Vec3 lookDirection = playerOwner.getLookAngle();
                Vec3 spawnPos = this.position().add(lookDirection.scale(1.5D));
                missile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                // РџСЂРёРЅСѓРґРёС‚РµР»СЊРЅРѕ Р·Р°РґР°РµРј СѓСЂРѕРЅ Рё РІР·СЂС‹РІРЅС‹Рµ СЃРІРѕР№СЃС‚РІР° СЂР°РєРµС‚Рµ
                missile.setDamage(getPersistentData().getFloat("Damage"));
                missile.setExplosionDamage(getPersistentData().getFloat("ExplosionDamage"));
                missile.setExplosionRadius(getPersistentData().getFloat("ExplosionRadius"));

                missile.setLife(Math.max(1, (int) getPersistentData().getInt("ProjectileLife")));
                missile.setOwner(playerOwner);
                missile.setLauncherVehicle(vehicle.getUUID());
                missile.shoot(lookDirection.x, lookDirection.y, lookDirection.z, getPersistentData().getFloat("Velocity"), 0.0F);

                missile.setYRot(playerOwner.getYRot());
                missile.setXRot(playerOwner.getXRot());

                this.level().addFreshEntity(missile);
            }
        }
    }
}
