package tech.squadmc.squadmcor.command;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import tech.squadmc.squadmcor.network.ModNetworking;
import tech.squadmc.squadmcor.network.SquadReloadPacket;
import tech.squadmc.squadmcor.squadmc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class SquadCommand {

    private static final Logger LOGGER = LogUtils.getLogger();

    private SquadCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("squadmc")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reload").executes(SquadCommand::reload)));
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        int copied = syncDevResources();
        source.sendSuccess(() -> Component.literal("[SquadMC] Перезагрузка конфигов..."), true);

        server.reloadResources(server.getPackRepository().getSelectedIds()).whenComplete((result, error) -> server.execute(() -> {
            if (error != null) {
                LOGGER.error("SquadMC reload failed", error);
                source.sendFailure(Component.literal("[SquadMC] Ошибка перезагрузки: " + error.getMessage()));
                return;
            }
            for (ServerLevel level : server.getAllLevels()) {
                for (Entity entity : level.getAllEntities()) {
                    if (entity instanceof VehicleEntity vehicle) vehicle.data().update();
                }
            }
            ModNetworking.CHANNEL.send(PacketDistributor.ALL.noArg(), new SquadReloadPacket());
            source.sendSuccess(() -> Component.literal("[SquadMC] Готово. Обновлено файлов: " + copied), true);
        }));
        return 1;
    }

    /**
     * A dev run reads resources from the build folder, so edits in src/main/resources stay invisible
     * to any reload until they are copied there. Does nothing in a built mod.
     *
     * @return number of files copied
     */
    private static int syncDevResources() {
        if (FMLEnvironment.production) return 0;
        Path project = FMLPaths.GAMEDIR.get().toAbsolutePath().normalize().getParent();
        if (project == null) return 0;
        Path source = project.resolve("src/main/resources");
        if (!Files.isDirectory(source)) return 0;

        int copied = 0;
        // Gradle and IDE runs each keep their own output folder.
        for (String output : List.of("build/resources/main", "out/production/resources", "bin/main")) {
            Path target = project.resolve(output);
            if (Files.isDirectory(target)) copied += copyChanged(source, target);
        }
        return copied;
    }

    private static int copyChanged(Path source, Path target) {
        int copied = 0;
        try (Stream<Path> walk = Files.walk(source)) {
            List<Path> files = walk.filter(Files::isRegularFile).toList();
            for (Path file : files) {
                String relative = source.relativize(file).toString();
                // Forge fills mods.toml and pack.mcmeta from gradle.properties when building.
                if (relative.endsWith("mods.toml") || relative.endsWith("pack.mcmeta")) continue;
                Path destination = target.resolve(relative);
                if (Files.exists(destination)
                        && Files.getLastModifiedTime(destination).compareTo(Files.getLastModifiedTime(file)) >= 0) continue;
                Files.createDirectories(destination.getParent());
                Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
                copied++;
            }
        } catch (IOException e) {
            LOGGER.error("Could not copy SquadMC resources to {}", target, e);
        }
        return copied;
    }
}
