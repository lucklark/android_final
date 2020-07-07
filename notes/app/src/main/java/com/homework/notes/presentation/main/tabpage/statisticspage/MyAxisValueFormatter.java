package com.homework.notes.presentation.main.tabpage.statisticspage;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class MyAxisValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int temp = (int)value;
        return temp/60+1 + " min";
    }
}
