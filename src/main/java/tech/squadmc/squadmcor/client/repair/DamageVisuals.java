package tech.squadmc.squadmcor.client.repair;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;
import tech.squadmc.squadmcor.repair.VehicleRepair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * Вид поломанной ходовой. Выбитая гусеница исчезает целиком. У колёсной техники исчезает одно колесо
 * борта, и чем больше колёс на борту, тем меньше крен. Сторона берётся по положению кости, а не по
 * имени: в моделях буквы L и R встречаются перепутанными.
 */
public final class DamageVisuals {
    private static final Pattern TRACK = Pattern.compile("track|gusen", Pattern.CASE_INSENSITIVE);
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

        boolean left = vehicle.getLeftWheelDamaged();
        boolean right = vehicle.getRightWheelDamaged();
        boolean tracked = VehicleRepair.isTracked(vehicle);
        Unit lostLeft = !tracked && left ? pick(layout.leftWheels, vehicle, 1) : null;
        Unit lostRight = !tracked && right ? pick(layout.rightWheels, vehicle, 2) : null;

        boolean hide = (tracked && (left || right) && !layout.tracks.isEmpty()) || lostLeft != null || lostRight != null;
        if (hide || layout.anythingHidden) {
            for (Unit unit : layout.tracks) {
                setHidden(model, unit, tracked && (unit.left ? left : right));
            }
            for (Unit unit : layout.leftWheels) setHidden(model, unit, unit == lostLeft);
            for (Unit unit : layout.rightWheels) setHidden(model, unit, unit == lostRight);
            layout.anythingHidden = hide;
        }

        Tilt tilt = TILTS.computeIfAbsent(vehicle, v -> new Tilt());
        float rollSize = tracked ? TRACK_ROLL : WHEEL_ROLL;
        tilt.targetRoll = (right ? wheelShare(tracked, layout.rightWheels) * rollSize : 0F)
                - (left ? wheelShare(tracked, layout.leftWheels) * rollSize : 0F);
        tilt.targetPitch = pitchFor(lostLeft, layout.leftWheels) + pitchFor(lostRight, layout.rightWheels);
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

    private static float wheelShare(boolean tracked, List<Unit> side) {
        return tracked || side.size() <= 2 ? 1F : 2F / side.size();
    }

    /** Без переднего колеса нос опускается, без заднего поднимается. Спереди у моделей отрицательный Z. */
    private static float pitchFor(Unit lost, List<Unit> side) {
        if (lost == null || side.size() < 2) return 0F;
        float front = side.get(0).z;
        float back = side.get(side.size() - 1).z;
        if (back - front < 1F) return 0F;
        float position = (back + front - 2F * lost.z) / (back - front);
        return position * WHEEL_PITCH * wheelShare(false, side);
    }

    private static Unit pick(List<Unit> side, VehicleEntity vehicle, int salt) {
        if (side.isEmpty()) return null;
        return side.get(Math.floorMod(vehicle.getUUID().hashCode() * 31 + salt, side.size()));
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
