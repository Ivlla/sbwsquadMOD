package tech.squadmc.squadmcor.item;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.item.gun.launcher.JavelinItem;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.SoundTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import tech.squadmc.squadmcor.entity.projectile.SpikeMissileEntity;
import tech.squadmc.squadmcor.init.SpikeContent;
import tech.squadmc.squadmcor.spike.SpikeControl;
import tech.squadmc.squadmcor.network.SpikeNetwork;

import java.util.UUID;

/** Inherits Javelin's renderer/animations, but never its seeker or missile. */
public final class SpikeItem extends JavelinItem {
    @Override
    public boolean useSpecialFireProcedure(GunData data) {
        return true; // The server's continuous-LMB timer owns firing; no seeker.
    }

    @Override
    public void shoot(ShootParameters parameters) {
        // Ignore the ordinary SBW ShootMessage: it must not bypass the 40-tick timer.
    }

    /** Called only after SpikeControl consumes a completed server-side charge. */
    public void fireCharged(ServerPlayer player, UUID launcherId) {
        if (launcherId == null || player.getMainHandItem().getItem() != this || !SpikeControl.isScoped(player)) return;
        GunData data = GunData.from(player.getMainHandItem());
        if (!canShoot(data, player)) return;
        ShootParameters parameters = new ShootParameters(player, player, player.serverLevel(),
                player.getEyePosition(), player.getLookAngle(), data, 0, true, null, null);
        SpikeMissileEntity missile = new SpikeMissileEntity(SpikeContent.SPIKE_MISSILE.get(), player.serverLevel());
        missile.initializeControl(player, launcherId);
        // Spawn at the eye so a muzzle offset cannot teleport the missile through a wall.
        missile.setPos(player.getEyePosition());
        Vec3 direction = player.getLookAngle().add(0, SpikeMissileEntity.LAUNCH_LIFT, 0).normalize();
        missile.shoot(direction.x, direction.y, direction.z, 1.5F, 0);

        if (!player.serverLevel().addFreshEntity(missile)) return;
        beforeShoot(parameters);
        SoundTool.playLocalSound(player, ModSounds.JAVELIN_FIRE_1P.get(), 2.0F, 1.0F);
        SoundTool.playDistantSound(player.serverLevel(), ModSounds.JAVELIN_FIRE_3P.get(),
                player.position(), 4.0F, 1.0F, player);
        SoundTool.playDistantSound(player.serverLevel(), ModSounds.JAVELIN_FAR.get(),
                player.position(), 10.0F, 1.0F, player);
        afterShoot(parameters); // One round, standard reload and ammo accounting.
        data.save();
        SpikeNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SpikeNetwork.ShotFired(player.getInventory().selected, launcherId));
    }
}
