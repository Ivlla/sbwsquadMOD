package tech.squadmc.squadmcor.fx;

import java.util.List;

/** Формат файлов mts_fx/*.json. Имена полей те же, что в JSONParticle и JSONSound из MTS. */
public final class MtsFxDefs {
    private MtsFxDefs() {}

    public static class Preset {
        public List<ParticleDef> particles;
        public List<SoundDef> sounds;
    }

    /** Условие visibility: эффект включается, если значение переменной лежит в [min, max]. */
    public static class Cond {
        public String variable;
        public double min, max;
    }

    public static class ParticleDef {
        public String type, spawningOrientation, renderingOrientation;
        public boolean isBright, isBlended, stopsOnGround, ignoreCollision, useBlockColor;
        public boolean getBlockPropertiesFromGround, randomTexture, randomColor;
        public float daytimeReductionFactor;
        public int quantity, duration, durationRandomness, movementDuration;
        public float transparency, toTransparency;
        public int fadeInTransparencyTime, fadeOutTransparencyTime;
        public float scale, toScale;
        public int fadeInScaleTime, fadeOutScaleTime;
        public float hitboxSize;
        public List<String> textures;
        public List<Integer> textureDelays, colorDelays;
        public String color, toColor, model;
        public List<String> colorList;
        public double[] spreadRandomness, pos, rot, rotationRandomness;
        public double[] initialVelocity, movementVelocity, terminalVelocity;
        public List<Cond> conds;

        public boolean isBreak() {
            return "BREAK".equals(type);
        }
    }

    public static class SoundDef {
        public List<String> variations;
        public List<Cond> conds;
        public double minDistance, minDistanceVolume;
        public double middleDistance, middleDistanceVolume;
        public double maxDistance, maxDistanceVolume;
    }
}
