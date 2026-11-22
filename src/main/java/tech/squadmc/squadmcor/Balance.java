package tech.squadmc.squadmcor;

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
import java.util.HashMap;

@Mod.EventBusSubscriber(modid = "squadmc")
public final class Balance extends SimpleJsonResourceReloadListener {
    private static volatile Map<String, Map<String, Float>> profiles = Map.of();

    private Balance() { super(new Gson(), "balance"); }

    @SubscribeEvent
    public static void register(AddReloadListenerEvent event) { event.addListener(new Balance()); }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
        Map<String, Map<String, Float>> next = new HashMap<>();
        files.forEach((id, json) -> {
            if (!id.getNamespace().equals("squadmc")) return;
            Map<String, Float> fields = new HashMap<>();
            json.getAsJsonObject().entrySet().forEach(entry -> {
                float value = entry.getValue().getAsFloat();
                if (!Float.isFinite(value) || value < 0) throw new IllegalArgumentException(id + "/" + entry.getKey());
                fields.put(entry.getKey(), value);
            });
            next.put(id.getPath(), Map.copyOf(fields));
        });
        profiles = Map.copyOf(next);
    }

    public static float value(String profile, String key, float fallback) {
        return profiles.getOrDefault(profile, Map.of()).getOrDefault(key, fallback);
    }
}
