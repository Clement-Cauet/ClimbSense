package com.climbsense.application.function;

import android.widget.Chronometer;
import android.widget.ImageButton;

import androidx.navigation.NavController;

import java.util.Map;

public class InstanceFunction {

    public static InstanceFunction instanceFunction;

    public NavController navController;
    private ImageButton imageButton;

    private String idParameterChart;
    private Map climbInfo;
    private Boolean chronometer;

    //Return instance instanceTask class
    public static synchronized InstanceFunction getInstance() {
        if (instanceFunction == null)
            instanceFunction = new InstanceFunction();
        return instanceFunction;
    }

    //Constructor instanceTask class
    public InstanceFunction() {
        //this.instanceFunction = instanceFunction.getInstance();
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public NavController getNavController() {
        return navController;
    }

    public void setParameterChart(String id) {
        this.idParameterChart = id;
    }

    public String getIdParameterChart() {
        return idParameterChart;
    }

    public void setImageUriAward(ImageButton imageButton) {
        this.imageButton = imageButton;
    }

    public ImageButton getImageUriAward() {
        return imageButton;
    }

    public void setChronometer(Boolean chronometer) {
        this.chronometer = chronometer;
    }

    public Boolean getChronometer() {
        return this.chronometer;
    }

    public void setClimbInfo(Map climbInfo) {
        this.climbInfo = climbInfo;
    }

    public Map getClimbInfo() {
        return this.climbInfo;
    }

}
