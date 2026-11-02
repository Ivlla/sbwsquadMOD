// Based on CamoVehicleBase from Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt).
// Modified for SquadMC: FCP's hold and menu are replaced by SBW's own vehicle container.
package tech.squadmc.squadmcor.heli;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import tech.squadmc.squadmcor.fx.MtsFxServer;
import tech.squadmc.squadmcor.heli.entity.vehicle.VehicleSkins;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControlled;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.SquadWeaponFire;
import tech.squadmc.squadmcor.network.ModNetworking;

import javax.annotation.Nullable;

/** Общая база вертолётов: запуск двигателя, звуки Squad, скины, отдача оружия на камеру. */
public abstract class HeliVehicleBase extends GeoVehicleEntity implements EngineControlled {

    private static final EntityDataAccessor<Byte> ENGINE_STATE =
            SynchedEntityData.defineId(HeliVehicleBase.class, EntityDataSerializers.BYTE);

    public HeliVehicleBase(EntityType<? extends VehicleEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ENGINE_STATE, EngineControl.OFF);
    }

    @Override
    public byte fcp$getEngineState() {
        return this.entityData.get(ENGINE_STATE);
    }

    @Override
    public void fcp$setEngineState(byte state) {
        this.entityData.set(ENGINE_STATE, state);
    }

    @Override
    public boolean forwardInputDown() {
        return !EngineControl.blocksInput(this) && super.forwardInputDown();
    }

    @Override
    public boolean backInputDown() {
        return !EngineControl.blocksInput(this) && super.backInputDown();
    }

    @Override
    public boolean leftInputDown() {
        return !EngineControl.blocksInput(this) && super.leftInputDown();
    }

    @Override
    public boolean rightInputDown() {
        return !EngineControl.blocksInput(this) && super.rightInputDown();
    }

    @Override
    public boolean upInputDown() {
        return !EngineControl.blocksInput(this) && super.upInputDown();
    }

    @Override
    public boolean downInputDown() {
        return !EngineControl.blocksInput(this) && super.downInputDown();
    }

    /** Звук поворота турели SBW: отключён там, где играет цикл из Squad. */
    @Override
    public void turretTurnSound(float diffX, float diffY, float pitch) {
        if (SquadWeaponFire.TURRETS.contains(EngineControl.id(this))) return;
        super.turretTurnSound(diffX, diffY, pitch);
    }

    /** SBW вызывает это один раз на выстрел на сервере, поэтому здесь же звук, вспышка и отдача. */
    @Override
    public void playShootSound3p(@Nullable LivingEntity living, @Nullable GunData data, @Nullable Vec3 pos) {
        super.playShootSound3p(living, data, pos);
        SquadWeaponFire.onShoot(this, living, data);
        if (data == null || !(this.level() instanceof ServerLevel level)) return;

        String projectile = data.get(GunProp.PROJECTILE).getId();
        boolean rocket = projectile.contains("rocket");
        boolean missile = projectile.contains("missile") || projectile.contains("hellfire");
        boolean cannon = projectile.contains("cannon");
        float kick = rocket ? HeliFirePacket.ROCKET : missile ? HeliFirePacket.MISSILE : cannon ? HeliFirePacket.CANNON : HeliFirePacket.GUN;

        if ((rocket || missile) && pos != null) MtsFxServer.rocketLaunch(level, pos);
        ModNetworking.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                new HeliFirePacket(this.getId(), kick));
    }

    /** Свой цикл двигателя SBW молчит там, где играет многослойный двигатель из Squad. */
    @Override
    public float getEngineSoundVolume() {
        return EngineControl.usesSquadSound(this) ? 0f : super.getEngineSoundVolume();
    }

    public ResourceLocation getCurrentTexture() {
        return VehicleSkins.currentTexture(this);
    }

    @Override
    public void tick() {
        super.tick();
        EngineControl.tick(this);
    }

    /** Запас (в блоках) для отсечения рендера, потому что лопасти намного шире хитбокса. */
    protected double renderCullPadding() {
        return rotorRadius();
    }

    /** Примерный радиус несущего винта: от него зависит размер пыли от винта. */
    public double rotorRadius() {
        String id = EngineControl.id(this);
        if (id.contains("littlebird")) return 4.0;
        if (id.contains("ch53") || id.contains("mh53")) return 12.0;
        if (id.contains("mi8") || id.contains("mi17")) return 10.5;
        return 7.5;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(renderCullPadding());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distanceSq) {
        double size = getBoundingBoxForCulling().getSize();
        if (Double.isNaN(size)) size = 1.0;
        size = size * 64.0 * getViewScale();
        return distanceSq < size * size;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("HeliEngineState", fcp$getEngineState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("HeliEngineState")) fcp$setEngineState(compound.getByte("HeliEngineState"));
    }
}
