package tech.squadmc.squadmcor.client.repair;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;
import tech.squadmc.squadmcor.repair.VehicleRepair;
import tech.squadmc.squadmcor.repair.WheelLoss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * Вид поломанной ходовой. Выбитая гусеница исчезает целиком. У колёсной техники исчезает столько
 * колёс, сколько выбито, и чем больше колёс на борту, тем меньше крен от каждого. Сторона берётся по положению кости, а не по
 * имени: в моделях буквы L и R встречаются перепутанными.
 */
public final class DamageVisuals {
    private static final Pattern TRACK = Pattern.compile("track|gus", Pattern.CASE_INSENSITIVE);
    private static final Pattern WHEEL = Pattern.compile("wheel", Pattern.CASE_INSENSITIVE);
    private static final Pattern NOT_ROAD_WHEEL = Pattern.compile("steer|spare|zapask|rul", Pattern.CASE_INSENSITIVE);

    private static final float TRACK_ROLL = 3F;
    private static final float WHEEL_ROLL = 4.5F;
    private static final float WHEEL_PITCH = 1.5F;
    private static final float TILT_SPEED = 0.06F;

    private record Unit(String name, boolean left, float z) {}

    private static final class Layout {
        final List<Unit> tracks = new ArrayList<>();
        final List<Unit> leftWheels = new ArrayList<>();
        final List<Unit> rightWheels = new ArrayList<>();
        int boneCount;
        boolean anythingHidden;
    }

    private static final class Tilt {
        float roll, pitch, targetRoll, targetPitch;
    }

    private static final Map<GeoModel<?>, Layout> LAYOUTS = new WeakHashMap<>();
    private static final Map<VehicleEntity, Tilt> TILTS = new WeakHashMap<>();

    private DamageVisuals() {}

    /** Модель общая на всю технику типа, поэтому кости выставляются заново перед каждой отрисовкой. */
    public static void apply(GeoModel<?> model, VehicleEntity vehicle) {
        Collection<CoreGeoBone> bones = model.getAnimationProcessor().getRegisteredBones();
        Layout layout = LAYOUTS.get(model);
        if (layout == null || layout.boneCount != bones.size()) {
            layout = buildLayout(bones);
            LAYOUTS.put(model, layout);
        }

        boolean tracked = VehicleRepair.isTracked(vehicle);
        boolean trackLeft = tracked && vehicle.getLeftWheelDamaged();
        boolean trackRight = tracked && vehicle.getRightWheelDamaged();
        List<Unit> lostLeft = tracked ? List.of() : lostUnits(layout.leftWheels, vehicle, true);
        List<Unit> lostRight = tracked ? List.of() : lostUnits(layout.rightWheels, vehicle, false);

        boolean hide = ((trackLeft || trackRight) && !layout.tracks.isEmpty()) || !lostLeft.isEmpty() || !lostRight.isEmpty();
        if (hide || layout.anythingHidden) {
            for (Unit unit : layout.tracks) setHidden(model, unit, unit.left ? trackLeft : trackRight);
            for (Unit unit : layout.leftWheels) setHidden(model, unit, lostLeft.contains(unit));
            for (Unit unit : layout.rightWheels) setHidden(model, unit, lostRight.contains(unit));
            layout.anythingHidden = hide;
        }

        Tilt tilt = TILTS.computeIfAbsent(vehicle, v -> new Tilt());
        if (tracked) {
            tilt.targetRoll = ((trackRight ? 1F : 0F) - (trackLeft ? 1F : 0F)) * TRACK_ROLL;
            tilt.targetPitch = 0F;
        } else {
            tilt.targetRoll = (lostRight.size() * wheelShare(layout.rightWheels) - lostLeft.size() * wheelShare(layout.leftWheels)) * WHEEL_ROLL;
            tilt.targetPitch = pitchFor(lostLeft, layout.leftWheels) + pitchFor(lostRight, layout.rightWheels);
        }
    }

    /** Текущий крен в градусах: { вокруг продольной оси, вокруг поперечной }. Положительный крен — на правый борт. */
    public static float[] tilt(VehicleEntity vehicle) {
        Tilt tilt = TILTS.get(vehicle);
        if (tilt == null) return null;
        tilt.roll += (tilt.targetRoll - tilt.roll) * TILT_SPEED;
        tilt.pitch += (tilt.targetPitch - tilt.pitch) * TILT_SPEED;
        if (Math.abs(tilt.roll) < 0.01F && Math.abs(tilt.pitch) < 0.01F) return null;
        return new float[]{tilt.roll, tilt.pitch};
    }

    /** Чем больше колёс на борту, тем меньше крен от потери одного. */
    private static float wheelShare(List<Unit> side) {
        return side.size() <= 2 ? 1F : 2F / side.size();
    }

    /** Без переднего колеса нос опускается, без заднего поднимается. Спереди у моделей отрицательный Z. */
    private static float pitchFor(List<Unit> lost, List<Unit> side) {
        if (lost.isEmpty() || side.size() < 2) return 0F;
        float front = side.get(0).z;
        float back = side.get(side.size() - 1).z;
        if (back - front < 1F) return 0F;
        float sum = 0F;
        for (Unit unit : lost) sum += (back + front - 2F * unit.z) / (back - front);
        return sum * WHEEL_PITCH * wheelShare(side);
    }

    /** Колёса модели по маске сервера: обе нумерации идут спереди назад. */
    private static List<Unit> lostUnits(List<Unit> side, VehicleEntity vehicle, boolean left) {
        int mask = WheelLoss.mask(vehicle, left);
        if (side.isEmpty() || mask == 0) return List.of();
        int slots = WheelLoss.wheelsOnSide(vehicle, left);
        List<Unit> lost = new ArrayList<>();
        for (int i = 0; i < slots; i++) {
            if ((mask >> i & 1) == 0) continue;
            int index = slots == side.size() || slots < 2 ? Math.min(i, side.size() - 1)
                    : Math.round(i * (side.size() - 1F) / (slots - 1F));
            Unit unit = side.get(index);
            if (!lost.contains(unit)) lost.add(unit);
        }
        return lost;
    }

    private static void setHidden(GeoModel<?> model, Unit unit, boolean hidden) {
        CoreGeoBone bone = model.getAnimationProcessor().getBone(unit.name);
        if (bone != null) bone.setHidden(hidden);
    }

    private static Layout buildLayout(Collection<CoreGeoBone> bones) {
        Layout layout = new Layout();
        layout.boneCount = bones.size();
        for (CoreGeoBone bone : bones) {
            if (bone instanceof GeoBone geo && geo.getParent() == null) {
                collectTracks(geo, layout);
                collectWheels(geo, layout);
            }
        }
        layout.leftWheels.sort(Comparator.comparingDouble(Unit::z));
        layout.rightWheels.sort(Comparator.comparingDouble(Unit::z));
        return layout;
    }

    private static void collectTracks(GeoBone bone, Layout layout) {
        if (TRACK.matcher(bone.getName()).find()) {
            Boolean side = uniformSide(bone, TRACK);
            if (side != null) {
                layout.tracks.add(new Unit(bone.getName(), side, bone.getPivotZ()));
                return;
            }
        }
        for (GeoBone child : bone.getChildBones()) collectTracks(child, layout);
    }

    private static void collectWheels(GeoBone bone, Layout layout) {
        if (isRoadWheel(bone) && leafWheels(bone) <= 1) {
            Boolean side = uniformSide(bone, WHEEL);
            if (side != null) {
                (side ? layout.leftWheels : layout.rightWheels).add(new Unit(bone.getName(), side, leafZ(bone)));
            }
            return;
        }
        for (GeoBone child : bone.getChildBones()) collectWheels(child, layout);
    }

    private static boolean isRoadWheel(GeoBone bone) {
        String name = bone.getName();
        return WHEEL.matcher(name).find() && !NOT_ROAD_WHEEL.matcher(name).find();
    }

    private static int leafWheels(GeoBone bone) {
        int inChildren = 0;
        for (GeoBone child : bone.getChildBones()) inChildren += leafWheels(child);
        return inChildren == 0 && isRoadWheel(bone) ? 1 : inChildren;
    }

    private static float leafZ(GeoBone bone) {
        for (GeoBone child : bone.getChildBones()) {
            if (leafWheels(child) > 0) return leafZ(child);
        }
        return bone.getPivotZ();
    }

    /** true — левый борт, false — правый, null — кость охватывает оба борта или борт неясен. */
    private static Boolean uniformSide(GeoBone bone, Pattern pattern) {
        Boolean side = pattern.matcher(bone.getName()).find() ? sideOf(bone) : null;
        for (GeoBone child : bone.getChildBones()) {
            if (!containsMatch(child, pattern)) continue;
            Boolean childSide = uniformSide(child, pattern);
            if (childSide == null || (side != null && !side.equals(childSide))) return null;
            side = childSide;
        }
        return side;
    }

    private static boolean containsMatch(GeoBone bone, Pattern pattern) {
        if (pattern.matcher(bone.getName()).find()) return true;
        for (GeoBone child : bone.getChildBones()) {
            if (containsMatch(child, pattern)) return true;
        }
        return false;
    }

    /** GeckoLib меняет знак X при загрузке модели: левый борт (X > 0 в файле) имеет отрицательный pivotX. */
    private static Boolean sideOf(GeoBone bone) {
        float x = bone.getPivotX();
        if (Math.abs(x) >= 2F) return x < 0F;
        String name = bone.getName().toLowerCase(Locale.ROOT);
        if (name.contains("left") || name.matches(".*(wheel|track)(mov|rot|turn)?l.*")) return true;
        if (name.contains("right") || name.matches(".*(wheel|track)(mov|rot|turn)?r.*")) return false;
        return null;
    }
}
