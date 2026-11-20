// Based on Frontline Combat Pack track animation conventions; path fitted to the M113 track geometry.
package tech.squadmc.squadmcor.heli.client.model.M113;

import tech.squadmc.squadmcor.heli.client.model.Util.FCPTrackPath;

public final class M113TrackPaths {
    private static final int TRACK_COUNT = 70;
    private static final int ANIMATION_LENGTH = 100;
    private static final int KEYFRAME_COUNT = TRACK_COUNT + 1;
    private static final float PATH_LENGTH = 160.73f;

    // Rounded-rectangle loop matching the M113 road-wheel run. The final frame closes the loop.
    private static final float[][] M113_KEYFRAMES = createKeyframes();

    public static final FCPTrackPath M113 =
            new FCPTrackPath(TRACK_COUNT, ANIMATION_LENGTH, M113_KEYFRAMES);

    private M113TrackPaths() {
    }

    private static float[][] createKeyframes() {
        final double radius = 7.5;
        final double straightLength = (PATH_LENGTH - 2.0 * Math.PI * radius) / 2.0;
        final double halfStraight = straightLength / 2.0;
        final double centerY = 8.0;
        final double totalLength = 2.0 * straightLength + 2.0 * Math.PI * radius;
        float[][] frames = new float[KEYFRAME_COUNT][3];

        for (int i = 0; i < KEYFRAME_COUNT; i++) {
            double distance = totalLength * i / TRACK_COUNT;
            double y;
            double z;
            double tangentY;
            double tangentZ;

            if (distance <= straightLength) {
                y = centerY + radius;
                z = -halfStraight + distance;
                tangentY = 0.0;
                tangentZ = 1.0;
            } else if (distance <= straightLength + Math.PI * radius) {
                double arc = distance - straightLength;
                double angle = Math.PI / 2.0 - arc / radius;
                y = centerY + radius * Math.sin(angle);
                z = halfStraight + radius * Math.cos(angle);
                tangentY = -Math.cos(angle);
                tangentZ = Math.sin(angle);
            } else if (distance <= 2.0 * straightLength + Math.PI * radius) {
                double run = distance - straightLength - Math.PI * radius;
                y = centerY - radius;
                z = halfStraight - run;
                tangentY = 0.0;
                tangentZ = -1.0;
            } else {
                double arc = distance - 2.0 * straightLength - Math.PI * radius;
                double angle = -Math.PI / 2.0 - arc / radius;
                y = centerY + radius * Math.sin(angle);
                z = -halfStraight + radius * Math.cos(angle);
                tangentY = -Math.cos(angle);
                tangentZ = Math.sin(angle);
            }

            float rotation = (float) Math.toDegrees(Math.atan2(-tangentY, tangentZ));
            frames[i][0] = rotation;
            frames[i][1] = (float) y;
            frames[i][2] = (float) z;
        }

        // Avoid a tiny floating-point seam between the first and closing keyframe.
        frames[KEYFRAME_COUNT - 1] = frames[0].clone();
        return frames;
    }
}
