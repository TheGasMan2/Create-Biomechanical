package com.happysg.createbiomechanical.content.cogolem;

public enum GolemCommands {
    STAY,
    WANDER,
    FOLLOW,
    STATION;

    public GolemCommands cycle(){
        return values()[(this.ordinal() + 1) % values().length];
    }
}
