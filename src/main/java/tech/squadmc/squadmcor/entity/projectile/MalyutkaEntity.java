package tech.squadmc.squadmcor.entity.projectile;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.projectile.MissileProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.ProjectileTool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class MalyutkaEntity extends MissileProjectile implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // --- РќРђРЎРўР РћР™РљР РЈР РћРќРђ РњРђР›Р®РўРљР ---
    private static final int MISSILE_DAMAGE = 1500;       // РЈСЂРѕРЅ РїСЂРё РїСЂСЏРјРѕРј РїРѕРїР°РґР°РЅРёРё РІ С†РµР»СЊ
    private static final int EXPLOSION_DAMAGE = 80;       // РЈСЂРѕРЅ РѕС‚ РІР·СЂС‹РІР° СЂР°РєРµС‚С‹
    private static final int EXPLOSION_RADIUS = 6;        // Р Р°РґРёСѓСЃ РІР·СЂС‹РІР° (РІ Р±Р»РѕРєР°С…)

    public UUID launcherVehicle;

    public MalyutkaEntity(EntityType<? extends MalyutkaEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

@Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.MEDIUM_ANTI_GROUND_MISSILE.get();
    }

    private void explodeAndDiscard() {
        if (this.level() instanceof ServerLevel) {
            ProjectileTool.causeCustomExplode(this,
                    ModDamageTypes.causeProjectileExplosionDamage(this.level().registryAccess(), this, this.getOwner()),
                    this, EXPLOSION_DAMAGE, EXPLOSION_RADIUS);
        }
        this.discard();
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level() instanceof ServerLevel) {
            BlockPos resultPos = blockHitResult.getBlockPos();
            float hardness = this.level().getBlockState(resultPos).getBlock().defaultDestroyTime();

            if (hardness != -1 && ExplosionConfig.EXPLOSION_DESTROY.get() && ExplosionConfig.EXTRA_EXPLOSION_EFFECT.get()) {
                this.level().destroyBlock(resultPos, true);
            }
            explodeAndDiscard();
        }
    }

    @Override
    public void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (this.getOwner() != null && this.getOwner().getVehicle() != null && entity == this.getOwner().getVehicle() || entity instanceof MalyutkaEntity)
            return;
        if (this.level() instanceof ServerLevel) {
            DamageHandler.doDamage(entity, ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), MISSILE_DAMAGE);
            if (entity instanceof LivingEntity) {
                entity.invulnerableTime = 0;
            }
            causeExplode(result.getLocation());
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        mediumTrail();

        // РЎРїР°РІРЅ С‡Р°СЃС‚РёС† С„РµР№РµСЂРІРµСЂРєР° РїРѕР·Р°РґРё СЂР°РєРµС‚С‹ (С‚РѕР»СЊРєРѕ РЅР° РєР»РёРµРЅС‚Рµ)
        if (this.level().isClientSide) {
            Vec3 motion = this.getDeltaMovement();
            double speed = motion.length();
            if (speed > 0.1) {
                Vec3 backVec = motion.normalize().scale(-0.5);
                double px = this.getX() + backVec.x;
                double py = this.getY() + backVec.y + 0.15;
                double pz = this.getZ() + backVec.z;

                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.FIREWORK,
                        px, py, pz,
                        -motion.x * 0.15 + (this.random.nextFloat() - 0.5) * 0.05,
                        -motion.y * 0.15 + (this.random.nextFloat() - 0.5) * 0.05,
                        -motion.z * 0.15 + (this.random.nextFloat() - 0.5) * 0.05);
            }
        }

        // Р’С‹РїРѕР»РЅСЏРµРј СЂР°СЃС‡РµС‚С‹ РЅР°РІРµРґРµРЅРёСЏ РўРћР›Р¬РљРћ РЅР° СЃРµСЂРІРµСЂРµ, С‡С‚РѕР±С‹ РёР·Р±РµР¶Р°С‚СЊ СЂР°СЃСЃРёРЅС…СЂРѕРЅРёР·Р°С†РёРё
        if (!this.level().isClientSide) {
            if (tickCount > 0 && this.getOwner() != null && getOwner().getVehicle() instanceof VehicleEntity vehicle) {
                Entity shooter = this.getOwner();

                if (launcherVehicle == null && tickCount < 5) {
                    launcherVehicle = vehicle.getUUID();
                }

                if (launcherVehicle != null && launcherVehicle.equals(vehicle.getUUID())) {
                    Vec3 baseLook;
                    if ((vehicle.getVehicleType() == VehicleType.AIRPLANE || vehicle.getVehicleType() == VehicleType.HELICOPTER) && shooter == vehicle.getFirstPassenger()) {
                        baseLook = shooter.getViewVector(1);
                    } else {
                        baseLook = vehicle.getBarrelVector(1);
                    }

                    Vec3 lookVec = baseLook.scale(1.6);
                    Vec3 missileVec = vehicle.getShootPosForHud(shooter, 1).vectorTo(position()).normalize();
                    Vec3 toVec = missileVec.vectorTo(lookVec);

                    float turnSpeed = Mth.clamp((tickCount - 1) * 0.15f, 0, 2.2f);

                    turn(toVec, turnSpeed);

                    this.setDeltaMovement(this.getDeltaMovement().scale(0.75).add(getLookAngle().scale(2.2)));
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.85, 0.85, 0.85));

                    // РџРѕРјРµС‡Р°РµРј, С‡С‚Рѕ РґРІРёР¶РµРЅРёРµ РёР·РјРµРЅРёР»РѕСЃСЊ, Р·Р°СЃС‚Р°РІР»СЏСЏ СЃРµСЂРІРµСЂ СЃРёРЅС…СЂРѕРЅРёР·РёСЂРѕРІР°С‚СЊ РїРѕР·РёС†РёСЋ
                    this.hasImpulse = true;
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @NotNull SoundEvent getSound() {
        return ModSounds.ROCKET_FLY.get();
    }

    @Override
    public float getVolume() {
        return 0.4f;
    }

    @Override
    public float getMaxHealth() {
        return 20;
    }
}
