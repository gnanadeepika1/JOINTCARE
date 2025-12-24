package com.saveetha.myjoints.joint;

public enum JointPosition {

    HEAD(110, 110),
    NECK(110, 110),
    LEFT_SHOULDER(110, 110),
    RIGHT_SHOULDER(110, 110),
    LEFT_ELBOW(92, 92),
    RIGHT_ELBOW(92, 92),
    LEFT_WRIST(90, 90),
    RIGHT_WRIST(90, 90),
    LEFT_HAND_thumb_1(52, 52),
    LEFT_HAND_thumb_2(52, 52),
    RIGHT_HAND_thumb_1(52, 52),
    RIGHT_HAND_thumb_2(52, 52),
    LEFT_HAND_INDEX_1(54, 54),
    LEFT_HAND_INDEX_2(54, 54),
    LEFT_HAND_INDEX_3(40, 40),
    LEFT_HAND_MIDDLE_1(54, 54),
    LEFT_HAND_MIDDLE_2(49, 49),
    LEFT_HAND_MIDDLE_3(40, 40),
    LEFT_HAND_RING_1(54, 54),
    LEFT_HAND_RING_2(49, 49),
    LEFT_HAND_RING_3(40, 40),
    LEFT_HAND_LITTLE_1(54, 54),
    LEFT_HAND_LITTLE_2(49, 49),
    LEFT_HAND_LITTLE_3(40, 40),
    RIGHT_HAND_INDEX_1(54, 54),
    RIGHT_HAND_INDEX_2(49, 49),
    RIGHT_HAND_INDEX_3(40, 40),
    RIGHT_HAND_MIDDLE_1(54, 54),
    RIGHT_HAND_MIDDLE_2(49, 49),
    RIGHT_HAND_MIDDLE_3(40, 40),
    RIGHT_HAND_RING_1(54, 54),
    RIGHT_HAND_RING_2(49, 49),
    RIGHT_HAND_RING_3(40, 40),
    RIGHT_HAND_LITTLE_1(54, 54),
    RIGHT_HAND_LITTLE_2(49, 49),
    RIGHT_HAND_LITTLE_3(40, 40),
    LEFT_LEG_INDEX_1(40, 40),
    LEFT_LEG_THUMB_1(40, 40),
    LEFT_LEG_THUMB_2(40, 40),
    LEFT_LEG_MIDDLE_1(40, 40),
    LEFT_LEG_RING_1(40, 40),
    LEFT_LEG_LITTLE_1(38, 38),
    RIGHT_LEG_THUMB_1(38, 38),
    RIGHT_LEG_THUMB_2(38, 38),
    RIGHT_LEG_INDEX_1(38, 38),
    RIGHT_LEG_MIDDLE_1(38, 38),

    RIGHT_LEG_RING_1(38, 38),
    RIGHT_LEG_LITTLE_1(38, 38),
    LEFT_HIP(140, 140),
    RIGHT_HIP(140, 140),
    LEFT_KNEE(92, 92),
    RIGHT_KNEE(92, 92),
    LEFT_ANKLE(75, 75),
    RIGHT_ANKLE(75, 75);

    private final int width;
    private final int height;

    JointPosition(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
