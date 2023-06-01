package com.climbsense.application;

import java.util.Map;

public class ChronometerEvent {
    private Map climb_map;

    public ChronometerEvent(Map climb_map) {
        this.climb_map = climb_map;
    }

    public Map getClimbMap() {
        return climb_map;
    }
}
