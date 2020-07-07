package com.homework.notes.presentation.main.tabpage.statisticspage;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.homework.notes.persistence.datastructure.NoteItems;

import java.util.ArrayList;

public class XAxisValueFormatter implements IAxisValueFormatter {
    private ArrayList<NoteItems> items;
    public XAxisValueFormatter(ArrayList<NoteItems> items) {
        this.items = items;
    }
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int position = (int)value;
//        String [] s = new String[]{"s","b","d","e"};
        if(position >= items.size()) position  = 0;

        return items.get(position).title;
//        return s[position];
    }

}
