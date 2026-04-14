package com.happysg.biomechanical.content.cogolem;

public enum GolemCommand {
    STAY,
    WANDER,
    FOLLOW,
    STATION;

    public GolemCommand cycle(){
        return values()[(this.ordinal() + 1) % values().length];
    }
}
