package com.saveetha.myjoints.joint;

public enum JointPosition {

    HEAD(110, 110),
    NECK(110, 110),
    LEFT_SHOULDER(110, 110),
    RIGHT_SHOULDER(110, 110),
    LEFT_ELBOW(38, 38),
    RIGHT_ELBOW(38, 38),
    LEFT_WRIST(34, 34),
    RIGHT_WRIST(34, 34),
    LEFT_HIP(44, 44),
    RIGHT_HIP(44, 44),
    LEFT_KNEE(42, 42),
    RIGHT_KNEE(42, 42),
    LEFT_ANKLE(36, 36),
    RIGHT_ANKLE(36, 36);

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
