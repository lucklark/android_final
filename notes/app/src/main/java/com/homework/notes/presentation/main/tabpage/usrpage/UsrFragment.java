package com.homework.notes.presentation.main.tabpage.usrpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannedString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
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
    private PieChart pChart;
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

        pChart = (PieChart) v.findViewById(R.id.chart2);
        initPieChart();
        return v;
    }

    public void getList() {
        Bundle bundle = getArguments();
        String notes_class = bundle.getString("note_class");

        items = new NotesDataSource(mContext.getApplicationContext()).getNotesOfClass(notes_class);
//        Log.d(TAG, "getDataForListView: "+items.get(0).total_review_time);
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
    private void initPieChart() {
        pChart.setUsePercentValues(true);
        pChart.getDescription().setEnabled(false);
        pChart.setExtraOffsets(5,10,5,5);
        pChart.setDragDecelerationFrictionCoef(0.95f);
        //旋转动画
        pChart.spin(1000, pChart.getRotationAngle(), pChart.getRotationAngle() + 360, Easing.EasingOption.EaseInCubic);
        //设置中间文件
        pChart.setCenterText(generateCenterSpannableText());

        pChart.setDrawHoleEnabled(true);
        pChart.setHoleColor(Color.WHITE);

        pChart.setTransparentCircleColor(Color.WHITE);
        pChart.setTransparentCircleAlpha(110);

        pChart.setHoleRadius(58f);
        pChart.setTransparentCircleRadius(61f);

        pChart.setDrawCenterText(true);

        pChart.setRotationAngle(0);
        // 触摸旋转
        pChart.setRotationEnabled(true);
        pChart.setHighlightPerTapEnabled(true);

        //变化监听
        pChart.setOnChartValueSelectedListener(this);

        //模拟数据
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for(NoteItems it : items) {
            entries.add(new PieEntry(Float.valueOf(it.total_review_time).floatValue(), it.title));
        }
//        entries.add(new PieEntry(40, "优秀"));
//        entries.add(new PieEntry(20, "满分"));
//        entries.add(new PieEntry(30, "及格"));
//        entries.add(new PieEntry(10, "不及格"));

        //设置数据
        setData(entries);

        pChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = pChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // 输入标签样式
        pChart.setEntryLabelColor(Color.BLACK);
        pChart.setEntryLabelTextSize(12f);
    }

    private SpannedString generateCenterSpannableText() {
        SpannedString s = new SpannedString("时间分布比");
        return s;
    }

    private void setData(ArrayList<PieEntry> entries)  {
        PieDataSet dataSet = new PieDataSet(entries, items.get(0).note_class);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        //数据的颜色
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        pChart.setData(data);
        pChart.highlightValues(null);
        //刷新
        pChart.invalidate();

    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
