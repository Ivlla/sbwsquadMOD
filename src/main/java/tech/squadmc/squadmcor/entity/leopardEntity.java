package tech.squadmc.squadmcor.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tech.squadmc.squadmcor.init.ModEntities;

public class leopardEntity extends SquadBaseVehicleEntity {

    public leopardEntity(EntityType<? extends leopardEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.3F) * damage);
    }

    @Override
    public void tick() {

        if (this.level().isClientSide) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () ->
                    tech.squadmc.squadmcor.client.ClientAccess.handleLeopardClientTick(this)
            );
        }

        super.tick();

        // Р С›Р В±Р Р…Р С•Р Р†Р В»Р ВµР Р…Р С‘Р Вµ Р В·Р Р†РЎС“Р С”Р С•Р Р† Р Р…Р В° Р С”Р В»Р С‘Р ВµР Р…РЎвЂљР Вµ
        if (this.level().isClientSide) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () ->
                    tech.squadmc.squadmcor.client.sound.leopardSoundManager.updateSounds(this)
            );
        }

        // Дымогенератор (VehicleSmokeSystem): клиент шлёт пульс, сервер тикает генератор
        if (this.level().isClientSide) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () ->
                    tech.squadmc.squadmcor.client.ClientAccess.sendSmokePulse(this));
        } else {
            this.updateSmokeGenerator(false);
        }
    }

    @Override
    public boolean hasSmokeGenerator() { return true; }

    @Override
    public Vec3 getSmokeGeneratorOffset() { return new Vec3(0.0, 0.75, 0.0); }

    public void spawnAdditionalGrenades(TDADummyProjectile original) {
        float baseAngle = this.getYRot() - this.getTurretAngle();
        float pitch = -10.0F;

        float f = -pitch * ((float)Math.PI / 180F);
        float f1 = (float)Math.sin(f);
        float f2 = (float)Math.cos(f);

        float[] angles = new float[]{-45.0F, -15.0F, 15.0F, 45.0F};
        float speed = 0.65F;
        float inaccuracy = 0.4F;

        float yaw0 = baseAngle + angles[0];
        float f3_0 = -yaw0 * ((float)Math.PI / 180F);
        double vx0 = Math.sin(f3_0) * f2;
        double vy0 = f1;
        double vz0 = Math.cos(f3_0) * f2;
        original.shoot(vx0, vy0, vz0, speed, inaccuracy);

        for (int i = 1; i < angles.length; i++) {
            float yaw = baseAngle + angles[i];
            float f3 = -yaw * ((float)Math.PI / 180F);
            double vx = Math.sin(f3) * f2;
            double vy = f1;
            double vz = Math.cos(f3) * f2;

            TDADummyProjectile grenade = new TDADummyProjectile(ModEntities.TDA_DUMMY.get(), this.level());
            grenade.setAdditional(true);
            grenade.setPos(original.getX(), original.getY(), original.getZ());
            grenade.setOwner(original.getOwner());
            grenade.shoot(vx, vy, vz, speed, inaccuracy);
            this.level().addFreshEntity(grenade);
        }
    }

    private float getTurretAngle() {
        try {
            java.lang.reflect.Field field = com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity.class.getDeclaredField("turretYRot");
            field.setAccessible(true);
            return field.getFloat(this);
        } catch (Exception e) {
            return this.getYRot();
        }
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }
}

