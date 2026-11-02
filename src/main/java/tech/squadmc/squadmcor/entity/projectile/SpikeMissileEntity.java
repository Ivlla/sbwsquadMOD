package tech.squadmc.squadmcor.entity.projectile;

import com.atsuishio.superbwarfare.entity.projectile.MissileProjectile;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.squadmc.squadmcor.spike.SpikeControl;
import tech.squadmc.squadmcor.squadmc;

import java.util.UUID;

public final class SpikeMissileEntity extends MissileProjectile implements GeoEntity {
    public static final float DIRECT_DAMAGE = 500.0F;
    private static final int MAX_LIFE_TICKS = 400;
    public static final double LAUNCH_LIFT = 0.12;
    private static final double CRUISE_SPEED = 3.0;
    private static final double VELOCITY_MEMORY = 0.6375; // Malyutka: 0.75 * 0.85.
    private static final int LOFT_TICKS = 30;
    private static final double LOFT_BIAS = 0.08;
    private static final float TURN_RATE = 2.2F;
    private static final ResourceKey<DamageType> HIT_DAMAGE = ResourceKey.create(
            Registries.DAMAGE_TYPE, new ResourceLocation(squadmc.MODID, "spike_hit"));
    private static final RawAnimation FLIGHT = RawAnimation.begin().thenLoop("animation.jvm.idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private UUID launcherId;
    private int launcherSlot = -1;
    private long controlRevision = -1;
    private boolean controlLost = true;
    private boolean hitProcessed;
    private int flightTicks;
    private Vec3 coastVelocity = Vec3.ZERO;

    public SpikeMissileEntity(EntityType<? extends SpikeMissileEntity> type, Level level) {
        super(type, level);
        noCulling = true;
        setDamage(DIRECT_DAMAGE);
        setExplosionDamage(0);
        setExplosionRadius(0);
        setNoGravity(true);
    }

    public void initializeControl(ServerPlayer player, UUID launcherId) {
        setOwner(player);
        this.launcherId = launcherId;
        launcherSlot = player.getInventory().selected;
        controlRevision = SpikeControl.revision(player);
        controlLost = false;
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            if (++flightTicks > MAX_LIFE_TICKS || entityData.get(HEALTH) <= 0 || isInWater()) {
                discard();
                return;
            }
            if (!controlLost && (!(getOwner() instanceof ServerPlayer player)
                    || player.level() != level()
                    || !SpikeControl.canGuide(player, controlRevision, launcherSlot, launcherId))) {
                // One-way transition: returning to ADS cannot reconnect this missile.
                controlLost = true;
                coastVelocity = getDeltaMovement();
            }
            if (controlLost) setDeltaMovement(coastVelocity);
        }
        // Match Malyutka's order: move/collide first, then steer for the next tick.
        super.tick();
        if (isRemoved()) return;
        mediumTrail();
        if (!level().isClientSide) {
            if (controlLost) {
                setDeltaMovement(coastVelocity);
            } else if (getOwner() instanceof ServerPlayer player) {
                guideAlongSight(player);
            }
            // Send the final velocity, not the previous tick's steering result.
            super.syncMotion();
        }
    }

    private void guideAlongSight(ServerPlayer player) {
        double lift = LOFT_BIAS * Math.max(0.0, 1.0 - (double) flightTicks / LOFT_TICKS);
        Vec3 sight = player.getLookAngle().add(0, lift, 0).normalize();
        Vec3 relative = position().subtract(player.getEyePosition()).normalize();
        // Same sight-line correction and inherited turn() as SBW's wire-guided
        // missile / this addon's Malyutka. No moving ray-hit target to orbit.
        Vec3 command = sight.scale(1.6).subtract(relative);
        turn(command, Mth.clamp((flightTicks - 1) * 0.15F, 0, TURN_RATE));
        setDeltaMovement(getDeltaMovement().scale(VELOCITY_MEMORY)
                .add(getLookAngle().scale(CRUISE_SPEED * (1.0 - VELOCITY_MEMORY))));
        hasImpulse = true;
    }

    @Override
    public void syncMotion() {
        // FastThrowableProjectile calls this before our steering. Send once at
        // the end of tick() instead, using the original SBW packet implementation.
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return entity != getOwner() && !(entity instanceof SpikeMissileEntity)
                && (getOwner() == null || entity != getOwner().getVehicle()) && super.canHitEntity(entity);
    }

    @Override
    public void onHitEntity(EntityHitResult result) {
        if (level().isClientSide || hitProcessed || isRemoved()) return;
        Entity target = result.getEntity();
        if (!canHitEntity(target)) return;
        hitProcessed = true;
        DamageSource source = new DamageSource(level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(HIT_DAMAGE), this, getOwner());
        // No super.onHitEntity(), secondary explosion, splash loop or top-hit multiplier.
        DamageHandler.doDamage(target, source, DIRECT_DAMAGE);
        discard();
    }

    @Override
    public void onHitBlock(BlockHitResult result) {
        if (!level().isClientSide) discard();
    }

    @Override
    public void causeExplode(Vec3 position) {
        // Also suppress explosion calls originating from another SBW system.
        if (!level().isClientSide) discard();
    }

    @Override
    public void destroyBlock(BlockHitResult result) {
        // SPIKE never damages terrain.
    }

    @Override
    public boolean forceLoadChunk() {
        // FastThrowableProjectile checks this after its collision callbacks.
        // Do not reacquire chunk tickets after discard() has released them.
        return !isRemoved();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SpikeFlightTicks", flightTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        flightTicks = tag.getInt("SpikeFlightTicks");
        // A world reload must not restore a stale control connection.
        controlLost = true;
        coastVelocity = getDeltaMovement();
        setDamage(DIRECT_DAMAGE);
        setExplosionDamage(0);
        setExplosionRadius(0);
        setNoGravity(true);
    }

    @Override protected Item getDefaultItem() { return ModItems.JAVELIN_MISSILE.get(); }
    @Override public SoundEvent getSound() { return ModSounds.ROCKET_FLY.get(); }
    @Override public float getVolume() { return 0.4F; }
    @Override public float getMaxHealth() { return 20; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "flight", 0, state -> state.setAndContinue(FLIGHT)));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
