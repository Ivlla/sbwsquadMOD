package tech.squadmc.squadmcor.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControlled;
import tech.squadmc.squadmcor.smoke.VehicleSmokeCloudEntity;
import tech.squadmc.squadmcor.smoke.VehicleSmokeSystem;

public abstract class SquadBaseVehicleEntity extends GeoVehicleEntity implements EngineControlled, tech.squadmc.squadmcor.repair.WheelLoss.Holder {

    private static final EntityDataAccessor<Byte> ENGINE_STATE =
            SynchedEntityData.defineId(SquadBaseVehicleEntity.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Boolean> SMOKE_GENERATOR =
            SynchedEntityData.defineId(SquadBaseVehicleEntity.class, EntityDataSerializers.BOOLEAN);

    // --- Р”С‹РјРѕРІР°СЏ СЃРёСЃС‚РµРјР°: Р”Р“ + РґС‹РјРѕРіРµРЅРµСЂР°С‚РѕСЂ (РїРѕСЂС‚ СЃРёСЃС‚РµРјС‹ aasgranate) ---
    private int smokeVolleyCooldown = 0;
    private int smokeCharges = VehicleSmokeSystem.DEFAULT_CHARGES;
    @javax.annotation.Nullable
    private VehicleSmokeCloudEntity exhaustSmoke; // РЅРµ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ РґС‹РјРѕРіРµРЅРµСЂР°С‚РѕСЂРѕРј СЃ РІРµСЂСЃРёРё РЅР° С€Р»РµР№С„Р°С…, РѕСЃС‚Р°РІР»РµРЅРѕ РґР»СЏ СЃРѕРІРјРµСЃС‚РёРјРѕСЃС‚Рё

    /**
     * "Р—Р°РїР°СЃ" С‚РёРєРѕРІ, РЅР° РїСЂРѕС‚СЏР¶РµРЅРёРё РєРѕС‚РѕСЂС‹С… РіРµРЅРµСЂР°С‚РѕСЂ СЃС‡РёС‚Р°РµС‚СЃСЏ РІРєР»СЋС‡С‘РЅРЅС‹Рј
     * РїРѕСЃР»Рµ РїРѕСЃР»РµРґРЅРµРіРѕ Р·Р°РјРµС‡РµРЅРЅРѕРіРѕ РёРјРїСѓР»СЊСЃР° СЃС‚СЂРµР»СЊР±С‹. РќСѓР¶РµРЅ, РїРѕС‚РѕРјСѓ С‡С‚Рѕ
     * getShootAnimationTimer(...) РїСЂРё Р°РІС‚Рѕ-РѕРіРЅРµ РЎРђРњ РјРёРіР°РµС‚ 0/>0 РјРµР¶РґСѓ
     * РІС‹СЃС‚СЂРµР»Р°РјРё (РЅР°РїСЂРёРјРµСЂ, RPM 600 + ShootAnimationTime 1 РґР°С‘С‚ >0 С‚РѕР»СЊРєРѕ
     * 1 С‚РёРє РёР· 2) вЂ” Р±РµР· Р·Р°РїР°СЃР° РіРµРЅРµСЂР°С‚РѕСЂ РіР°СЃ Р±С‹ РєР°Р¶РґС‹Р№ РІС‚РѕСЂРѕР№ С‚РёРє.
     */
    private int smokeGraceTicks = 0;
    private static final int SMOKE_GRACE_TICKS = 6;

    private static final EntityDataAccessor<Byte> WHEELS_LOST =
            SynchedEntityData.defineId(SquadBaseVehicleEntity.class, EntityDataSerializers.BYTE);

    @Override
    public byte squadmc$getWheelsLost() {
        return this.entityData.get(WHEELS_LOST);
    }

    @Override
    public void squadmc$setWheelsLost(byte packed) {
        this.entityData.set(WHEELS_LOST, packed);
    }

    public SquadBaseVehicleEntity(EntityType<? extends SquadBaseVehicleEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WHEELS_LOST, (byte) 0);
        this.entityData.define(ENGINE_STATE, EngineControl.OFF);
        this.entityData.define(SMOKE_GENERATOR, false);
    }

    @Override
    public byte fcp$getEngineState() {
        return this.entityData.get(ENGINE_STATE);
    }

    @Override
    public void fcp$setEngineState(byte state) {
        this.entityData.set(ENGINE_STATE, state);
    }

    public boolean isEngineStarted() {
        return fcp$getEngineState() == EngineControl.RUNNING;
    }

    public void setEngineStarted(boolean started) {
        fcp$setEngineState(started ? EngineControl.RUNNING : EngineControl.OFF);
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

    // --- Р”С‹РјРѕРІР°СЏ СЃРёСЃС‚РµРјР°: accessors ---
    public boolean isSmokeGeneratorOn() { return this.entityData.get(SMOKE_GENERATOR); }
    public void setSmokeGeneratorOn(boolean on) { this.entityData.set(SMOKE_GENERATOR, on); }
    public int getSmokeVolleyCooldown() { return this.smokeVolleyCooldown; }
    public void setSmokeVolleyCooldown(int v) { this.smokeVolleyCooldown = v; }
    public int getSmokeCharges() { return this.smokeCharges; }
    public void setSmokeCharges(int v) { this.smokeCharges = v; }
    @javax.annotation.Nullable public VehicleSmokeCloudEntity getExhaustSmoke() { return this.exhaustSmoke; }
    public void setExhaustSmoke(@javax.annotation.Nullable VehicleSmokeCloudEntity e) { this.exhaustSmoke = e; }

    /**
     * РћР±РЅРѕРІРёС‚СЊ СЃРѕСЃС‚РѕСЏРЅРёРµ РґС‹РјРѕРіРµРЅРµСЂР°С‚РѕСЂР° Рё, РµСЃР»Рё РѕРЅ СЃРµР№С‡Р°СЃ РґРѕР»Р¶РµРЅ РґС‹РјРёС‚СЊ,
     * С‚СѓС‚ Р¶Рµ (РІ СЌС‚РѕРј Р¶Рµ С‚РёРєРµ) РїРѕРїС‹С‚Р°С‚СЊСЃСЏ РѕСЃС‚Р°РІРёС‚СЊ РѕС‡РµСЂРµРґРЅРѕР№ РєР»СѓР±РѕРє С€Р»РµР№С„Р°.
     * Р’С‹Р·С‹РІР°С‚СЊ РёР· tick() РєРѕРЅРєСЂРµС‚РЅРѕР№ С‚РµС…РЅРёРєРё РџРћРЎР›Р• super.tick(), РїРµСЂРµРґР°РІР°СЏ
     * "Р¶РёРІРѕР№" РёРјРїСѓР»СЊСЃ СЃС‚СЂРµР»СЊР±С‹ Р·Р° СЌС‚РѕС‚ С‚РёРє (РЅР°РїСЂРёРјРµСЂ,
     * getShootAnimationTimer(...) > 0).
     *
     * Р’РђР–РќРћ: firingPulse вЂ” СЌС‚Рѕ РёРјРµРЅРЅРѕ РёРјРїСѓР»СЊСЃ, РѕРЅ СЃР°Рј РјРёРіР°РµС‚ 0/>0 РјРµР¶РґСѓ
     * РІС‹СЃС‚СЂРµР»Р°РјРё РїСЂРё Р°РІС‚Рѕ-РѕРіРЅРµ (С‚Р°Рє СѓР¶Рµ СЂР°Р±РѕС‚Р°РµС‚ РІ РґСЂСѓРіРёС… РјР°С€РёРЅР°С…). Р§С‚РѕР±С‹
     * РіРµРЅРµСЂР°С‚РѕСЂ РЅРµ РіР°СЃ РІ РїР°СѓР·Р°С… РјРµР¶РґСѓ РёРјРїСѓР»СЊСЃР°РјРё, РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ "Р·Р°РїР°СЃ"
     * SMOKE_GRACE_TICKS: РїРѕРєР° РѕРЅ РЅРµ РёСЃСЃСЏРє, РіРµРЅРµСЂР°С‚РѕСЂ СЃС‡РёС‚Р°РµС‚СЃСЏ РІРєР»СЋС‡С‘РЅРЅС‹Рј
     * РґР°Р¶Рµ РµСЃР»Рё РІ СЌС‚РѕС‚ РєРѕРЅРєСЂРµС‚РЅС‹Р№ С‚РёРє РёРјРїСѓР»СЊСЃР° РЅРµ Р±С‹Р»Рѕ.
     */
    protected void updateSmokeGenerator(boolean firingPulse) {
        if (firingPulse) {
            this.smokeGraceTicks = SMOKE_GRACE_TICKS;
        } else if (this.smokeGraceTicks > 0) {
            this.smokeGraceTicks--;
        }

        boolean on = this.smokeGraceTicks > 0;
        this.setSmokeGeneratorOn(on);
        if (!this.level().isClientSide && hasSmokeGenerator() && on) {
            VehicleSmokeSystem.tickGenerator(this);
        }
    }

    /**
     * РЎРµСЂРІРµСЂРЅС‹Р№ "РїСѓР»СЊСЃ" РѕС‚ РєР»РёРµРЅС‚Р° (СЃРј. SmokeGeneratorPulsePacket): РєР»РёРµРЅС‚
     * РІРёРґРёС‚ getShootAnimationTimer(seat0, weapon0) > 0 (СЃРµСЂРІРµСЂ СЌС‚РѕРіРѕ РЅРµ
     * РІРёРґРёС‚ вЂ” СЌС‚Рѕ РєР»РёРµРЅС‚СЃРєР°СЏ Р°РЅРёРјР°С†РёСЏ РІС‹СЃС‚СЂРµР»Р°) Рё СЂР°Р· РІ С‚РёРє С€Р»С‘С‚ СЌС‚РѕС‚
     * РїСѓР»СЊСЃ, С‡С‚РѕР±С‹ РїСЂРѕРґР»РёС‚СЊ grace-РїРµСЂРёРѕРґ. Р›РѕРіРёРєР° РёРґРµРЅС‚РёС‡РЅР°
     * updateSmokeGenerator(true), РЅРѕ РЅРµ С‚СЂРѕРіР°РµС‚ VehicleSmokeSystem вЂ”
     * С„Р°РєС‚РёС‡РµСЃРєРёР№ С‚РёРє РіРµРЅРµСЂР°С‚РѕСЂР° (Рё РµРіРѕ "on"-СЃС‚Р°С‚СѓСЃ) РїРѕ-РїСЂРµР¶РЅРµРјСѓ РґРµР»Р°РµС‚
     * updateSmokeGenerator(false), РІС‹Р·С‹РІР°РµРјС‹Р№ РёР· tick() РєР°Р¶РґС‹Р№ СЃРµСЂРІРµСЂРЅС‹Р№
     * С‚РёРє, РєРѕС‚РѕСЂС‹Р№ РїСЂРѕСЃС‚Рѕ Р§РРўРђР•Рў СѓР¶Рµ РїСЂРѕРґР»С‘РЅРЅС‹Р№ smokeGraceTicks.
     */
    public void pulseSmokeGenerator() {
        this.smokeGraceTicks = SMOKE_GRACE_TICKS;
    }

    /** Р•СЃС‚СЊ Р»Рё Сѓ РјР°С€РёРЅС‹ РґС‹РјРѕРІС‹Рµ РіСЂР°РЅР°С‚РѕРјС‘С‚С‹. */
    public boolean hasSmokeLauncher() { return false; }
    /** Р›РѕРєР°Р»СЊРЅС‹Рµ offset'С‹ С‚РѕС‡РµРє РєСЂРµРїР»РµРЅРёСЏ Р”Р“ (СѓС‡РёС‚С‹РІР°РµС‚СЃСЏ yaw РєРѕСЂРїСѓСЃР°). */
    public Vec3[] getSmokeLauncherPoints() { return new Vec3[0]; }
    /** Р•СЃС‚СЊ Р»Рё РґС‹РјРѕРіРµРЅРµСЂР°С‚РѕСЂ (РїРѕСЃС‚РѕСЏРЅРЅС‹Р№ РґС‹Рј РёР· РІС‹С…Р»РѕРїР°, РїРѕРєР° РІРєР»СЋС‡С‘РЅ). */
    public boolean hasSmokeGenerator() { return false; }
    /** Р›РѕРєР°Р»СЊРЅС‹Р№ offset С‚РѕС‡РєРё РІС‹РїСѓСЃРєР° РґС‹РјР° РіРµРЅРµСЂР°С‚РѕСЂР°. */
    public Vec3 getSmokeGeneratorOffset() { return new Vec3(0.0, 1.0, -2.0); }


    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("WheelsLost", squadmc$getWheelsLost());
        compound.putBoolean("EngineStarted", this.isEngineStarted());
        compound.putInt("SmokeCharges", this.smokeCharges);
        compound.putBoolean("SmokeGenerator", this.isSmokeGeneratorOn());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        squadmc$setWheelsLost(compound.getByte("WheelsLost"));
        if (compound.contains("EngineStarted")) {
            this.setEngineStarted(compound.getBoolean("EngineStarted"));
        }
        if (compound.contains("SmokeCharges")) this.smokeCharges = compound.getInt("SmokeCharges");
        if (compound.contains("SmokeGenerator")) this.setSmokeGeneratorOn(compound.getBoolean("SmokeGenerator"));
        // РћР±Р»Р°РєРѕ РіРµРЅРµСЂР°С‚РѕСЂР° РЅРµ СЃРѕС…СЂР°РЅСЏРµРј: РєР»СѓР±РєРё С€Р»РµР№С„Р° СЃР°РјРё РїРѕ СЃРµР±Рµ РІСЂРµРјРµРЅРЅС‹Рµ СЃСѓС‰РЅРѕСЃС‚Рё.
    }

    // =========================================================================
    // 1. РџРћР’РћР РћРў Р‘РђРЁРќР РЎ РљР›РђР’РРђРўРЈР Р« (A / D)
    // =========================================================================
    public boolean hasKeyboardTurretTraverse() {
        return false;
    }

    public float getTurretTraverseSpeed() {
        return 2.2F;
    }

    // =========================================================================
    // 2. Р”Р«РњРћР’Р«Р• Р“Р РђРќРђРўР« (TDA)
    // =========================================================================
    public void spawnAdditionalGrenades(TDADummyProjectile original) {
    }

    // =========================================================================
    // 3. Р‘Р›РћРљРР РћР’РљРђ Р”Р’РР–Р•РќРРЇ РџР Р Р’Р«РљР›Р®Р§Р•РќРќРћРњ Р”Р’РР“РђРўР•Р›Р•
    // =========================================================================
    /**
     * Заглушённая машина катится по инерции и останавливается трением. Вода не ограничена:
     * плавучесть SBW работает и без двигателя, поэтому плавающая техника не тонет.
     * На суше работает стояночный тормоз, чтобы не сползать со склонов.
     */
    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            if (this.smokeVolleyCooldown > 0) this.smokeVolleyCooldown--;
        }

        super.tick();
        EngineControl.tick(this);
        tech.squadmc.squadmcor.repair.WheelLoss.tick(this);

        if (fcp$getEngineState() != EngineControl.RUNNING && this.onGround() && !this.isInWater()) {
            Vec3 delta = this.getDeltaMovement();
            double horizontal = delta.horizontalDistanceSqr();
            super.setDeltaMovement(horizontal < 0.0004 ? new Vec3(0.0, delta.y, 0.0) : new Vec3(delta.x * 0.97, delta.y, delta.z * 0.97));
        }
    }
}
