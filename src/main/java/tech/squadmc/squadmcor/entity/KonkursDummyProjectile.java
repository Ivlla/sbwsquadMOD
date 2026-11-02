package tech.squadmc.squadmcor.entity;

import com.atsuishio.superbwarfare.entity.projectile.WireGuideMissileEntity;
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
    private float initialSpeed;

    // --- РќРђРЎРўР РћР™РљР РЈР РћРќРђ РљРћРќРљРЈР РЎ ---
    private static final float MISSILE_DAMAGE = 900.0F;      // РЈСЂРѕРЅ РїСЂРё РїСЂСЏРјРѕРј РїРѕРїР°РґР°РЅРёРё РІ С†РµР»СЊ
    private static final float EXPLOSION_DAMAGE = 150.0F;    // РЈСЂРѕРЅ РѕС‚ РІР·СЂС‹РІР° СЂР°РєРµС‚С‹
    private static final float EXPLOSION_RADIUS = 5.0F;      // Р Р°РґРёСѓСЃ РІР·СЂС‹РІР° (РІ Р±Р»РѕРєР°С…)

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
        this.initialSpeed = velocity;
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
                missile.setDamage(MISSILE_DAMAGE);
                missile.setExplosionDamage(EXPLOSION_DAMAGE);
                missile.setExplosionRadius(EXPLOSION_RADIUS);

                missile.setOwner(playerOwner);
                missile.setLauncherVehicle(vehicle.getUUID());
                missile.shoot(lookDirection.x, lookDirection.y, lookDirection.z, this.initialSpeed, 0.0F);

                missile.setYRot(playerOwner.getYRot());
                missile.setXRot(playerOwner.getXRot());

                this.level().addFreshEntity(missile);
            }
        }
    }
}
