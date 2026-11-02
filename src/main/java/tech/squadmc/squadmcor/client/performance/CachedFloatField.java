package tech.squadmc.squadmcor.client.performance;

import java.lang.reflect.Field;

/** Resolve legacy field aliases once per entity class, including missing fields. */
public final class CachedFloatField {
    private final String[] names;
    private record Lookup(Field field) {}
    private final ClassValue<Lookup> fields = new ClassValue<>() {
        @Override
        protected Lookup computeValue(Class<?> type) {
            for (Class<?> current = type; current != null && current != Object.class;
                 current = current.getSuperclass()) {
                for (String name : names) {
                    try {
                        Field field = current.getDeclaredField(name);
                        Class<?> valueType = field.getType();
                        if (valueType != float.class && valueType != int.class && valueType != short.class
                                && valueType != byte.class && valueType != char.class) return new Lookup(null);
                        if (!field.trySetAccessible()) return new Lookup(null);
                        return new Lookup(field);
                    } catch (NoSuchFieldException ignored) {
                        // Same alias/parent priority as the original model code.
                    } catch (RuntimeException ignored) {
                        return new Lookup(null);
                    }
                }
            }
            return new Lookup(null);
        }
    };

    public CachedFloatField(String... names) {
        this.names = names.clone();
    }

    public float get(Object instance) {
        Field field = fields.get(instance.getClass()).field();
        if (field == null) return 0.0F;
        try {
            return field.getFloat(instance);
        } catch (IllegalAccessException | IllegalArgumentException ignored) {
            return 0.0F;
        }
    }
}
