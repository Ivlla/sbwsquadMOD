package tech.squadmc.squadmcor.heli.entity.vehicle.Bmp.Bmp2;

import com.atsuishio.superbwarfare.data.gun.GunData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import tech.squadmc.squadmcor.entity.SquadBaseVehicleEntity;
import tech.squadmc.squadmcor.entity.TDADummyProjectile;
import tech.squadmc.squadmcor.heli.entity.vehicle.VehicleSkins;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.SquadWeaponFire;
import tech.squadmc.squadmcor.init.ModEntities;

import javax.annotation.Nullable;

/** BMP-2M and BMP-2MD: the mod's smoke grenades and engine smoke generator, as on the BMP-2. */
public abstract class Bmp2mVehicleBase extends SquadBaseVehicleEntity {

    private static final float[] GRENADE_ANGLES = {-35F, -12F, 12F, 35F};
    private static final float GRENADE_SPEED = 0.6F;
    private static final float GRENADE_INACCURACY = 0.5F;
    private static final float GRENADE_PITCH = -10F;

    protected Bmp2mVehicleBase(EntityType<? extends Bmp2mVehicleBase> type, Level level) {
        super(type, level);
    }

    public ResourceLocation getCurrentTexture() {
        return VehicleSkins.currentTexture(this);
    }

    /** SBW's own turret sound stays silent where the Squad loop plays. */
    @Override
    public void turretTurnSound(float diffX, float diffY, float pitch) {
        if (SquadWeaponFire.TURRETS.contains(EngineControl.id(this))) return;
        super.turretTurnSound(diffX, diffY, pitch);
    }

    @Override
    public void playShootSound3p(@Nullable LivingEntity living, @Nullable GunData data, @Nullable Vec3 pos) {
        super.playShootSound3p(living, data, pos);
        SquadWeaponFire.onShoot(this, living, data);
    }

    @Override
    public float getEngineSoundVolume() {
        return EngineControl.usesSquadSound(this) ? 0f : super.getEngineSoundVolume();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> tech.squadmc.squadmcor.client.ClientAccess.sendSmokePulse(this));
        } else {
            updateSmokeGenerator(false);
        }
    }

    @Override
    public void spawnAdditionalGrenades(TDADummyProjectile original) {
        float baseAngle = getYRot() - getTurretYRot();
        float pitch = -GRENADE_PITCH * (float) Math.PI / 180F;
        double vy = Math.sin(pitch), horizontal = Math.cos(pitch);
        for (int i = 0; i < GRENADE_ANGLES.length; i++) {
            float yaw = -(baseAngle + GRENADE_ANGLES[i]) * (float) Math.PI / 180F;
            double vx = Math.sin(yaw) * horizontal, vz = Math.cos(yaw) * horizontal;
            if (i == 0) {
                original.shoot(vx, vy, vz, GRENADE_SPEED, GRENADE_INACCURACY);
                continue;
            }
            TDADummyProjectile grenade = new TDADummyProjectile(ModEntities.TDA_DUMMY.get(), level());
            grenade.setAdditional(true);
            grenade.setPos(original.getX(), original.getY(), original.getZ());
            grenade.setOwner(original.getOwner());
            grenade.shoot(vx, vy, vz, GRENADE_SPEED, GRENADE_INACCURACY);
            level().addFreshEntity(grenade);
        }
    }

    @Override
    public boolean hasDecoy() {
        return false;
    }

    @Override
    public boolean hasSmokeGenerator() {
        return true;
    }

    @Override
    public Vec3 getSmokeGeneratorOffset() {
        return new Vec3(0.0, 0.6, 0.0);
    }
}
