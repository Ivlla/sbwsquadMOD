package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import tech.squadmc.squadmcor.squadmc;

/**
 * Полный ремонт у станций AAS. AAS остаётся необязательным: блоки распознаются по ID, код мода не
 * подключается. Мейн в AAS не имеет команды, поэтому чинит всех; станция чинит только свою команду
 * и только полностью построенная.
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class AasStations {
    private static final ResourceLocation MAIN = new ResourceLocation("aas", "main_supply_be");
    private static final ResourceLocation STATION = new ResourceLocation("aas", "vehicle_station_be");
    private static final double MAIN_RADIUS = 15.0;
    private static final double STATION_RADIUS = 10.0;
    /** За проверку раз в секунду: полный ремонт примерно за 30 секунд, как перезарядка на мейне AAS. */
    private static final float REPAIR_PER_SECOND = 1F / 30F;

    private AasStations() {}

    /** Команда техники: метка AAS, иначе команда водителя. Пустая строка, если её нет. */
    public static String vehicleTeam(Entity vehicle) {
        String tagged = vehicle.getPersistentData().getString("AAS_VehicleTeam").trim();
        if (!tagged.isEmpty()) return tagged;
        Entity driver = vehicle.getFirstPassenger();
        return driver != null && driver.getTeam() != null ? driver.getTeam().getName() : "";
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        if (level.getGameTime() % 20 != 0 || !ModList.get().isLoaded("aas")) return;

        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof VehicleEntity vehicle) || vehicle.isWreck()) continue;
            if (VehicleRepair.isFullyRepaired(vehicle) || !nearServiceBlock(level, vehicle)) continue;
            VehicleRepair.repairAll(vehicle, REPAIR_PER_SECOND);
            if (vehicle.getFirstPassenger() instanceof Player driver) {
                driver.displayClientMessage(Component.translatable("message.squadmc.repair.station",
                        Math.round(100F * vehicle.getHealth() / vehicle.getMaxHealth())), true);
            }
        }
    }

    private static boolean nearServiceBlock(ServerLevel level, VehicleEntity vehicle) {
        int chunkX = vehicle.blockPosition().getX() >> 4;
        int chunkZ = vehicle.blockPosition().getZ() >> 4;
        int reach = ((int) MAIN_RADIUS >> 4) + 1;
        for (int x = chunkX - reach; x <= chunkX + reach; x++) {
            for (int z = chunkZ - reach; z <= chunkZ + reach; z++) {
                if (!level.hasChunk(x, z)) continue;
                LevelChunk chunk = level.getChunk(x, z);
                for (BlockEntity block : chunk.getBlockEntities().values()) {
                    if (services(block, vehicle)) return true;
                }
            }
        }
        return false;
    }

    private static boolean services(BlockEntity block, VehicleEntity vehicle) {
        ResourceLocation id = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(block.getType());
        if (MAIN.equals(id)) return inRange(block.getBlockPos(), vehicle, MAIN_RADIUS);
        if (!STATION.equals(id) || !inRange(block.getBlockPos(), vehicle, STATION_RADIUS)) return false;
        if (!isConstructed(block.getBlockState())) return false;
        CompoundTag tag = block.saveWithoutMetadata();
        String owner = tag.getString("TeamOwner");
        String team = vehicleTeam(vehicle);
        return !owner.isEmpty() && !owner.equalsIgnoreCase("NEUTRAL") && owner.equalsIgnoreCase(team);
    }

    private static boolean inRange(BlockPos pos, Entity vehicle, double radius) {
        return vehicle.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= radius * radius;
    }

    private static boolean isConstructed(BlockState state) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty("constructed");
        return property != null && Boolean.TRUE.equals(state.getValue(property));
    }
}
