package tech.squadmc.squadmcor.spike;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.item.SpikeItem;
import tech.squadmc.squadmcor.squadmc;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** Server-thread-only state, separate for every player and aiming session. */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class SpikeControl {
    public static final String LAUNCHER_ID = "SquadSpikeLauncherId";
    private static final Map<ServerPlayer, SpikeControlSession> STATES = new WeakHashMap<>();
    private static final Map<ServerPlayer, SpikeTrigger> TRIGGERS = new WeakHashMap<>();

    private SpikeControl() {}

    public static void update(ServerPlayer player, boolean scoped, boolean fireHeld, int slot) {
        SpikeControlSession state = STATES.computeIfAbsent(player, key -> new SpikeControlSession());
        boolean valid = scoped && slot >= 0 && slot < 9
                && slot == player.getInventory().selected && canHoldControl(player);
        // Even an off/on pair received in one server tick breaks the old link.
        state.update(valid, slot, player.tickCount);
        UUID launcher = launcherId(player.getMainHandItem(), valid && fireHeld);
        TRIGGERS.computeIfAbsent(player, key -> new SpikeTrigger()).update(
                fireHeld, valid && canFire(player), slot, launcher, player.tickCount);
    }

    private static UUID launcherId(ItemStack stack, boolean create) {
        if (!(stack.getItem() instanceof SpikeItem)) return null;
        if (create && !stack.getOrCreateTag().hasUUID(LAUNCHER_ID)) {
            stack.getOrCreateTag().putUUID(LAUNCHER_ID, UUID.randomUUID());
        }
        return stack.hasTag() && stack.getTag().hasUUID(LAUNCHER_ID)
                ? stack.getTag().getUUID(LAUNCHER_ID) : null;
    }

    private static boolean canFire(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        return stack.getItem() instanceof SpikeItem item && item.canShoot(GunData.from(stack), player);
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        SpikeTrigger trigger = TRIGGERS.get(player);
        if (trigger == null) return;
        UUID launcher = launcherId(player.getMainHandItem(), false);
        if (trigger.poll(isScoped(player) && canFire(player), player.getInventory().selected,
                launcher, player.tickCount) && player.getMainHandItem().getItem() instanceof SpikeItem item) {
            item.fireCharged(player, launcher);
        }
    }

    private static boolean canHoldControl(ServerPlayer player) {
        if (!player.isAlive() || player.isSpectator()
                || !(player.getMainHandItem().getItem() instanceof SpikeItem)) return false;
        if (player.getVehicle() instanceof VehicleEntity vehicle && vehicle.banHand(player)) return false;
        return !GunData.from(player.getMainHandItem()).reloading();
    }

    public static boolean isScoped(ServerPlayer player) {
        SpikeControlSession state = STATES.get(player);
        return state != null && state.isActive(player.getInventory().selected, player.tickCount)
                && canHoldControl(player);
    }

    public static long revision(ServerPlayer player) {
        SpikeControlSession state = STATES.get(player);
        return state == null ? -1 : state.revision();
    }

    public static boolean canGuide(ServerPlayer player, long revision, int slot, UUID launcherId) {
        if (!isScoped(player) || revision(player) != revision || player.getInventory().selected != slot) {
            return false;
        }
        ItemStack held = player.getMainHandItem();
        return launcherId != null && held.hasTag() && held.getTag().hasUUID(LAUNCHER_ID)
                && launcherId.equals(held.getTag().getUUID(LAUNCHER_ID));
    }
}
