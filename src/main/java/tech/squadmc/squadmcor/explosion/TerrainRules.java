package tech.squadmc.squadmcor.explosion;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "squadmc")
public final class TerrainRules extends SimpleJsonResourceReloadListener {
    public record Settings(double radius, int oneBlockPer) {}
    public static volatile Settings current = new Settings(2, 8);

    private TerrainRules() { super(new Gson(), "sbw/explosions"); }

    @SubscribeEvent
    public static void register(AddReloadListenerEvent event) { event.addListener(new TerrainRules()); }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
        var json = files.get(new ResourceLocation("squadmc", "terrain")).getAsJsonObject();
        double radius = json.get("BlockRadius").getAsDouble();
        int divisor = json.get("OneBlockPer").getAsBigDecimal().intValueExact();
        if (!Double.isFinite(radius) || radius < 0 || divisor < 1)
            throw new IllegalArgumentException("Invalid terrain destruction settings");
        current = new Settings(radius, divisor);
    }
}
