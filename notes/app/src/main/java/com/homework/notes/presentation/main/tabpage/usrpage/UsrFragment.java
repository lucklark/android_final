package com.homework.notes.presentation.main.tabpage.usrpage;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.homework.notes.R;

import com.homework.notes.persistence.ClassDataSource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.homework.notes.persistence.NotesDataSource;
import com.homework.notes.persistence.datastructure.NoteItems;


public class UsrFragment extends Fragment implements OnChartValueSelectedListener {
    private static final String TAG = "UsrFragment";
    private Context mContext;
    private ArrayList<NoteItems> items;
    private BarChart mChart;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    //另写新的构造函数，使class_name能够传入该fragment
    public static UsrFragment NewInstance(String note_class) {
        UsrFragment usrFragment = new UsrFragment();
        Bundle bundle = new Bundle();
        bundle.putString("note_class", note_class);
        usrFragment.setArguments(bundle);
        return usrFragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.usr_layout,container,false);

        getList();
        mChart = (BarChart) v.findViewById(R.id.chart1);
        initBarChart();




        return v;
    }

    public void getList() {
        Bundle bundle = getArguments();
        String notes_class = bundle.getString("note_class");

        items = new NotesDataSource(mContext.getApplicationContext()).getNotesOfClass(notes_class);
        Log.d(TAG, "getDataForListView: "+items.get(0).total_review_time);
    }

    //定义条形表
    public void initBarChart() {

        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(true);
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        //自定义坐标轴适配器，配置在X轴，xAxis.setValueFormatter(xAxisFormatter);
        IAxisValueFormatter xAxisFormatter = new XAxisValueFormatter(items);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(xAxisFormatter);


        //自定义坐标轴适配器，配置在Y轴。leftAxis.setValueFormatter(custom);
        IAxisValueFormatter custom = new MyAxisValueFormatter();



        //获取到图形左边的Y轴
        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.addLimitLine(limitLine);

        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        //获取到图形右边的Y轴，并设置为不显示
        mChart.getAxisRight().setEnabled(false);

        //图例设置
        Legend legend = mChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);

        setBarChartData();
    }

    private void setBarChartData() {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        int i = 0;
//        yVals1.add(new BarEntry(0, 4));
//        yVals1.add(new BarEntry(1, 2));
//        yVals1.add(new BarEntry(2, 6));
//        yVals1.add(new BarEntry(3, 1));
        for(NoteItems it : items) {
            yVals1.add(new BarEntry(i, Float.valueOf(it.total_review_time).floatValue()));
            i++;
        }

        BarDataSet set1;
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "class:"+items.get(0).note_class);
            set1.setDrawIcons(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);

            data.setBarWidth(0.9f);

            mChart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.d(TAG, "1");
    }

    @Override
    public void onNothingSelected() {
        Log.d(TAG, "2");
    }
}
