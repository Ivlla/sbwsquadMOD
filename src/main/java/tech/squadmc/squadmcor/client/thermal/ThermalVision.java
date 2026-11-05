package tech.squadmc.squadmcor.client.thermal;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.squadmc;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Тепловизор. Холодный мир рисуется серой лайтмапой, а тёплые сущности попадают в столбец лайтмапы
 * без синего канала, где уровень света равен нагреву. Шейдер отличает их по этому признаку и строит
 * картинку прицела. Картинка зависит от прибора конкретной техники, см. {@link ThermalSight}.
 */
public final class ThermalVision {
    /** Прогрев работающего двигателя примерно за 20 секунд. */
    private static final float WARM_UP_PER_TICK = 1F / 400F;
    /** Остывание заглушённого двигателя примерно за 90 секунд. */
    private static final float COOL_DOWN_PER_TICK = 1F / 1800F;
    private static final float RUNNING_HEAT = 0.95F;
    private static final float LIT_ITEM_HEAT = 0.9F;

    public static final KeyMapping TOGGLE = new KeyMapping("key.squadmc.thermal", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.squadmc_heli");

    private static final Map<VehicleEntity, Float> VEHICLE_HEAT = new WeakHashMap<>();
    @Nullable
    private static ThermalSight active;

    private ThermalVision() {}

    public static boolean isActive() {
        return active != null;
    }

    /** Нагрев от 0 до 1. */
    public static float heatOf(Entity entity) {
        if (entity instanceof VehicleEntity vehicle) return VEHICLE_HEAT.getOrDefault(vehicle, 0F);
        if (entity.isOnFire() || entity instanceof Projectile) return 1F;
        if (entity instanceof ItemEntity item && emitsLight(item.getItem())) return LIT_ITEM_HEAT;
        if (entity instanceof ItemFrame frame && emitsLight(frame.getItem())) return LIT_ITEM_HEAT;
        if (entity instanceof LivingEntity living) return living.getMobType() == MobType.UNDEAD ? 0.2F : 0.85F;
        return 0F;
    }

    private static boolean emitsLight(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock().defaultBlockState().getLightEmission() > 0;
    }

    private static boolean engineRunning(VehicleEntity vehicle) {
        if (vehicle.isWreck()) return true;
        if (EngineControl.appliesTo(vehicle)) return EngineControl.state(vehicle) == EngineControl.RUNNING;
        return Math.abs(vehicle.getPower()) > 0.01F;
    }

    private static void updateHeat(VehicleEntity vehicle) {
        boolean running = engineRunning(vehicle);
        Float known = VEHICLE_HEAT.get(vehicle);
        // Машина, которую клиент видит впервые, уже могла прогреться.
        float heat = known == null ? (running ? RUNNING_HEAT : 0F) : known;
        heat = running ? Math.min(RUNNING_HEAT, heat + WARM_UP_PER_TICK) : Math.max(0F, heat - COOL_DOWN_PER_TICK);
        VEHICLE_HEAT.put(vehicle, heat);
    }

    @Nullable
    private static ThermalSight sightFor(Player player) {
        if (!(player.getVehicle() instanceof VehicleEntity vehicle)) return null;
        ThermalSight sight = ThermalSight.of(EntityType.getKey(vehicle.getType()).getPath());
        if (sight == null) return null;
        int seat = vehicle.getSeatIndex(player);
        boolean helicopter = EngineControl.isHelicopter(vehicle);
        return (helicopter ? seat <= 1 : seat == vehicle.getTurretControllerIndex()) ? sight : null;
    }

    private static void play(Minecraft mc, String sound) {
        mc.getSoundManager().play(SimpleSoundInstance.forUI(
                SoundEvent.createVariableRangeEvent(new ResourceLocation(squadmc.MODID, sound)), 1F));
    }

    private static void setActive(Minecraft mc, @Nullable ThermalSight sight) {
        if (active == sight) return;
        if (sight != null) mc.gameRenderer.loadEffect(sight.effect());
        else mc.gameRenderer.shutdownEffect();
        active = sight;
    }

    @Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        private Registration() {}

        @SubscribeEvent
        public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE);
        }
    }

    @Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
    public static final class Events {
        private Events() {}

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) {
                active = null;
                return;
            }
            if (!mc.isPaused()) {
                for (Entity entity : mc.level.entitiesForRendering()) {
                    if (entity instanceof VehicleEntity vehicle) updateHeat(vehicle);
                }
            }

            ThermalSight available = sightFor(mc.player);
            while (TOGGLE.consumeClick()) {
                if (active != null) {
                    setActive(mc, null);
                    play(mc, "thermal.flir_off");
                } else if (available != null) {
                    setActive(mc, available);
                    play(mc, "thermal.flir_on");
                }
            }
            if (active != null && active != available) setActive(mc, null);
        }

        @SubscribeEvent
        public static void onRenderGui(RenderGuiEvent.Post event) {
            if (active == null) return;
            Minecraft mc = Minecraft.getInstance();
            GuiGraphics graphics = event.getGuiGraphics();
            String label = active.label();
            graphics.drawString(mc.font, label, mc.getWindow().getGuiScaledWidth() - mc.font.width(label) - 6, 6, 0xFFFFFF, true);
        }
    }
}
