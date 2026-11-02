package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModParticleTypes;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.squadmc.squadmcor.repair.VehicleRepair.Part;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Ремонтная горелка: зажать ПКМ, глядя на часть техники. Чинит ту часть, на которую направлена
 * (башню, гусеницу или колесо, двигатель). Корпус лечит только до половины.
 */
public class RepairTorchItem extends Item implements GeoItem {
    private static final double REACH = 5.0;
    /** Часть чинится с нуля за 8 секунд. */
    private static final float PART_PER_TICK = 1F / 160F;
    /** Корпус: как у Repair Tool SBW, 0.25% за тик. */
    private static final float HULL_PER_TICK = 0.0025F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RepairTorchItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getVehicle() != null) return InteractionResultHolder.pass(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!(level instanceof ServerLevel server) || !(entity instanceof ServerPlayer player)) return;
        Target target = findTarget(player);
        if (target == null) return;

        VehicleEntity vehicle = target.vehicle;
        if (vehicle.isWreck()) return;
        if (isEnemy(vehicle, player)) {
            message(player, Component.translatable("message.squadmc.repair.enemy"));
            return;
        }

        Part part = chooseRepair(vehicle, target.obbPart);
        boolean worked;
        if (part != null) {
            VehicleRepair.repairPart(vehicle, part, PART_PER_TICK);
            message(player, Component.translatable("message.squadmc.repair.part", part.displayName(vehicle),
                    Math.round(100F * part.health(vehicle) / part.max(vehicle))));
            worked = true;
        } else {
            float heal = VehicleRepair.torchHullHeal(vehicle, vehicle.getMaxHealth() * HULL_PER_TICK);
            worked = heal > 0F;
            if (worked) {
                vehicle.heal(heal);
                message(player, Component.translatable("message.squadmc.repair.body",
                        Math.round(100F * vehicle.getHealth() / vehicle.getMaxHealth())));
            } else if (vehicle.getHealth() < vehicle.getMaxHealth()) {
                message(player, Component.translatable("message.squadmc.repair.body_limit"));
            } else {
                message(player, Component.translatable("message.squadmc.repair.full"));
            }
        }
        if (worked) effects(server, target.hit, remaining);
    }

    /**
     * Часть под прицелом, если она повреждена. Если у модели техники нет хитбокса такой части,
     * до повреждённой всё равно можно добраться, наведясь на корпус.
     */
    @Nullable
    private static Part chooseRepair(VehicleEntity vehicle, @Nullable OBB.Part looked) {
        Part part = looked == null ? null : Part.of(looked);
        if (part != null) return part.needsRepair(vehicle) ? part : null;
        for (Part candidate : Part.values()) {
            if (candidate.needsRepair(vehicle) && !hasObb(vehicle, candidate)) return candidate;
        }
        return null;
    }

    private static boolean hasObb(VehicleEntity vehicle, Part part) {
        for (OBB obb : vehicle.getOBBs()) {
            if (Part.of(obb.part) == part) return true;
        }
        return false;
    }

    private static boolean isEnemy(VehicleEntity vehicle, Player player) {
        String team = AasStations.vehicleTeam(vehicle);
        return !team.isEmpty() && player.getTeam() != null && !team.equalsIgnoreCase(player.getTeam().getName());
    }

    private record Target(VehicleEntity vehicle, @Nullable OBB.Part obbPart, Vec3 hit) {}

    @Nullable
    private static Target findTarget(Player player) {
        Vec3 from = player.getEyePosition();
        Vec3 to = from.add(player.getViewVector(1F).scale(REACH));
        Target best = null;
        double bestDistance = Double.MAX_VALUE;
        for (VehicleEntity vehicle : player.level().getEntitiesOfClass(VehicleEntity.class, player.getBoundingBox().inflate(REACH + 12.0))) {
            if (vehicle.getOBBs().isEmpty()) {
                var clip = vehicle.getBoundingBox().clip(from, to);
                if (clip.isPresent() && from.distanceToSqr(clip.get()) < bestDistance) {
                    bestDistance = from.distanceToSqr(clip.get());
                    best = new Target(vehicle, null, clip.get());
                }
                continue;
            }
            for (OBB obb : vehicle.getOBBs()) {
                if (obb.part == OBB.Part.COLLISION || obb.part == OBB.Part.INTERACTIVE) continue;
                Vec3 hit = OBB.Companion.rayIntersect(obb, from, to);
                if (hit == null) continue;
                double distance = from.distanceToSqr(hit);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = new Target(vehicle, obb.part, hit);
                }
            }
        }
        return best;
    }

    private static void message(Player player, Component text) {
        player.displayClientMessage(text, true);
    }

    private static void effects(ServerLevel level, Vec3 pos, int tick) {
        level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 1, 0.05, 0.05, 0.05, 0.01);
        level.sendParticles(ModParticleTypes.FIRE_STAR.get(), pos.x, pos.y, pos.z, 2, 0.05, 0.05, 0.05, 0.08);
        if (tick % 10 == 0) {
            level.playSound(null, pos.x, pos.y, pos.z, ModSounds.REPAIRING.get(), SoundSource.PLAYERS, 0.8F, 1F);
        }
    }

    /** С горелкой в руке ПКМ по технике чинит, а не сажает в неё. */
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = tech.squadmc.squadmcor.squadmc.MODID)
    public static final class Interaction {
        private Interaction() {}

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void onInteractAt(net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific event) {
            if (event.getTarget() instanceof VehicleEntity && event.getItemStack().getItem() instanceof RepairTorchItem) {
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.PASS);
            }
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void onInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
            if (event.getTarget() instanceof VehicleEntity && event.getItemStack().getItem() instanceof RepairTorchItem) {
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.PASS);
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new tech.squadmc.squadmcor.client.repair.RepairTorchRenderer();
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
