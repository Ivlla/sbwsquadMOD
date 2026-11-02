package tech.squadmc.squadmcor.fx;

import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.squadmc.squadmcor.squadmc;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Загружает наборы эффектов и OBJ-модели. Обновляется по F3+T. КЛИЕНТ. */
@OnlyIn(Dist.CLIENT)
public final class MtsFxLibrary implements ResourceManagerReloadListener {
    private static final Logger LOG = LogManager.getLogger("squadmc-fx");
    private static final Gson GSON = new Gson();

    private static final Map<MtsFxPreset, MtsFxDefs.Preset> PRESETS = new EnumMap<>(MtsFxPreset.class);
    private static final Map<String, float[][]> MODELS = new HashMap<>();

    public static MtsFxDefs.Preset get(MtsFxPreset preset) {
        return PRESETS.get(preset);
    }

    /** Грани модели: для каждой — 4 вершины по {x, y, z, u, v}, всего 20 чисел. */
    public static float[][] model(String path) {
        return MODELS.get(path);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        PRESETS.clear();
        MODELS.clear();
        for (MtsFxPreset preset : MtsFxPreset.values()) {
            ResourceLocation id = new ResourceLocation(squadmc.MODID, "mts_fx/" + preset.file + ".json");
            try (BufferedReader reader = manager.openAsReader(id)) {
                MtsFxDefs.Preset parsed = GSON.fromJson(reader, MtsFxDefs.Preset.class);
                PRESETS.put(preset, parsed);
                for (MtsFxDefs.ParticleDef def : parsed.particles) {
                    if (def.model != null && !MODELS.containsKey(def.model)) {
                        MODELS.put(def.model, loadObj(manager, def.model));
                    }
                }
            } catch (Exception e) {
                LOG.error("Не удалось загрузить эффект {}", id, e);
            }
        }
    }

    private static float[][] loadObj(ResourceManager manager, String path) throws Exception {
        List<float[]> positions = new ArrayList<>();
        List<float[]> uvs = new ArrayList<>();
        List<float[]> faces = new ArrayList<>();
        try (BufferedReader reader = manager.openAsReader(new ResourceLocation(squadmc.MODID, path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                switch (parts[0]) {
                    case "v" -> positions.add(new float[]{
                            Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])});
                    // Как в ModelParserOBJ из MTS: V считается от низа текстуры.
                    case "vt" -> uvs.add(new float[]{Float.parseFloat(parts[1]), 1 - Float.parseFloat(parts[2])});
                    case "f" -> {
                        // Треугольник превращаем в четырёхугольник, повторяя последнюю вершину.
                        int count = parts.length - 1;
                        float[] face = new float[20];
                        for (int i = 0; i < 4; i++) {
                            String[] index = parts[1 + Math.min(i, count - 1)].split("/");
                            float[] position = positions.get(Integer.parseInt(index[0]) - 1);
                            float[] uv = uvs.get(Integer.parseInt(index[1]) - 1);
                            System.arraycopy(position, 0, face, i * 5, 3);
                            System.arraycopy(uv, 0, face, i * 5 + 3, 2);
                        }
                        faces.add(face);
                    }
                    default -> { }
                }
            }
        }
        return faces.toArray(new float[0][]);
    }
}
