package tech.squadmc.squadmcor.entity.empl;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.FormatTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TowEntity extends GeoVehicleEntity {

    private static final EntityDataAccessor<Boolean> LOADED = SynchedEntityData.defineId(TowEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> RELOAD_COOLDOWN = SynchedEntityData.defineId(TowEntity.class, EntityDataSerializers.INT);

    public TowEntity(EntityType<? extends TowEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOADED, false);
        this.entityData.define(RELOAD_COOLDOWN, 0);
    }

    public boolean isLoaded() {
        return this.entityData.get(LOADED);
    }

    public void setLoaded(boolean loaded) {
        this.entityData.set(LOADED, loaded);
    }

    public int getReloadCooldown() {
        return this.entityData.get(RELOAD_COOLDOWN);
    }

    public void setReloadCooldown(int cooldown) {
        this.entityData.set(RELOAD_COOLDOWN, cooldown);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("State", this.isLoaded());
        compound.putInt("ReloadCoolDown", this.getReloadCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setLoaded(compound.getBoolean("State"));
        this.setReloadCooldown(compound.getInt("ReloadCoolDown"));
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        var gunData = this.getGunData(0);
        if (gunData == null) {
            return InteractionResult.SUCCESS;
        }

        int coolDown = 240;
        ItemStack stack = player.getItemInHand(hand);

        if (gunData.hasEnoughAmmoToShoot(player)) {
            this.setLoaded(true);
            return super.interact(player, hand);
        }

        if (!this.isLoaded()) {
            if (!gunData.selectedAmmoConsumer().isAmmoItem(stack)) {
                return super.interact(player, hand);
            }

            Level level = this.level();
            if (level instanceof ServerLevel serverLevel && this.getReloadCooldown() == 0) {

                this.modifyGunData(0, data -> {
                    data.reloadAmmo(player);
                });

                this.setLoaded(true);
                serverLevel.playSound(
                        null,
                        this.blockPosition(),
                        ModSounds.TYPE_63_RELOAD.get(),
                        SoundSource.PLAYERS,
                        1.0F,
                        this.random.nextFloat() * 0.1F + 0.9F
                );
            } else {
                double remaining = (double) (coolDown - this.getReloadCooldown()) / 20.0;
                double total = (double) coolDown / 20.0;

                String message = FormatTool.format1DZ(remaining) + " / " + FormatTool.format1DZ(total);
                player.displayClientMessage(Component.literal(message), true);
            }
        } else {
            this.setLoaded(false);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        int cooldown = this.getReloadCooldown();
        if (cooldown > 0) {
            this.setReloadCooldown(cooldown - 1);
        }

    }


    @Override
    public void vehicleShoot(@Nullable LivingEntity living, @Nullable UUID uuid, @Nullable Vec3 targetPos) {
        super.vehicleShoot(living, uuid, targetPos);

        int coolDown = 240;
        this.setReloadCooldown(coolDown);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.4F) * damage);
    }
}
