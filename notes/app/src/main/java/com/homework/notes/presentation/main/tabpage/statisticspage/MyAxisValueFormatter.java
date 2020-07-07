package com.homework.notes.presentation.main.tabpage.statisticspage;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class MyAxisValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Integer va = (int)value;
        if(value > 60) return (int)va/60 + "min";
        else return  va +"s";
    }
}
