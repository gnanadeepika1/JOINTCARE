package com.saveetha.myjoints.joint;

public class JointPoint {
    public String name;
    public float x;   // normalized 0–1
    public float y;   // normalized 0–1
    public boolean selected;
    public JointPosition position;

    public JointPoint(JointPosition position, String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.position = position;
    }

}
