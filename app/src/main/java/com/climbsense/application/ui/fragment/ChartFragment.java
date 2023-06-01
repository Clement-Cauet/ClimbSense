package com.climbsense.application.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.climbsense.application.R;
import com.climbsense.application.database.DatabaseAccess;
import com.climbsense.application.databinding.FragmentChartBinding;
import com.climbsense.application.function.CalendarFunction;
import com.climbsense.application.function.ConversionFunction;
import com.climbsense.application.function.InstanceFunction;
import com.climbsense.application.function.UsersFunction;
import com.climbsense.application.ui.other.PopupAlertDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChartFragment extends Fragment {

    private FragmentChartBinding binding;

    private DatabaseAccess databaseAccess;
    private InstanceFunction instanceFunction;
    private CalendarFunction calendarFunction;
    private ConversionFunction conversionFunction;
    private UsersFunction usersFunction;
    private PopupAlertDialog popupAlertDialog;

    private LinearLayout fragment_chart, chart_layout;
    private TextView date_start_edit, date_end_edit, duration_edit;
    private EditText climb_name_edit;
    private Button return_button, download_button, share_button;
    private ImageButton save_name_button;
    private LineChart height_chart, acceleration_chart, temperature_chart, bpm_chart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fragment_chart = root.findViewById(R.id.fragment_chart);
        chart_layout = root.findViewById(R.id.chart_layout);
        return_button = root.findViewById(R.id.return_button);
        climb_name_edit = root.findViewById(R.id.climb_name_edit);
        save_name_button = root.findViewById(R.id.save_name_button);
        date_start_edit = root.findViewById(R.id.date_start_edit);
        date_end_edit = root.findViewById(R.id.date_end_edit);
        duration_edit = root.findViewById(R.id.duration_edit);
        height_chart = root.findViewById(R.id.height_chart);
        acceleration_chart = root.findViewById(R.id.acceleration_chart);
        temperature_chart = root.findViewById(R.id.temperature_chart);
        bpm_chart = root.findViewById(R.id.bpm_chart);
        download_button = root.findViewById(R.id.download_button);
        share_button = root.findViewById(R.id.share_button);

        this.databaseAccess     = DatabaseAccess.getInstance(getActivity());
        this.instanceFunction   = instanceFunction.getInstance();
        this.calendarFunction   = new CalendarFunction();
        this.conversionFunction = new ConversionFunction();
        this.usersFunction      = new UsersFunction(getActivity(), getContext(), chart_layout);
        this.popupAlertDialog   = new PopupAlertDialog(getActivity(), getContext());

        databaseAccess.selectClimbs(Filter.equalTo(FieldPath.documentId(), instanceFunction.getIdParameterChart()), null)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                climb_name_edit.setText(document.getString("name"));
                                try {
                                    date_start_edit.setText(conversionFunction.convertDBFormatToDateTimeDisplay(document.getString("date_start")));
                                    date_end_edit.setText(conversionFunction.convertDBFormatToDateTimeDisplay(document.getString("date_end")));
                                    duration_edit.setText(calendarFunction.durationDisplay(getContext(), conversionFunction.convertDBFormatToDate(document.getString("date_start")), conversionFunction.convertDBFormatToDate(document.getString("date_end"))));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                });

        save_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String collection = "/users/" + databaseAccess.getFirebaseAuth().getUid() + "/climbs/";
                Map<String, Object> map = new HashMap<>();
                map.put("name", climb_name_edit.getText().toString());
                databaseAccess.updateClimb(Filter.equalTo(FieldPath.documentId(), instanceFunction.getIdParameterChart()), map);
            }
        });

        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    usersFunction.climbDownloadPDF();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersFunction.climbShare();
            }
        });

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instanceFunction.getNavController().popBackStack();
            }
        });

        setHeight_chart(height_chart);
        setAcceleration_chart(acceleration_chart);
        setTemperature_chart(temperature_chart);
        setBpm_chart(bpm_chart);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setHeight_chart(LineChart lineChart) {

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            entries.add(new Entry(i, (int) (Math.random() * 200) + 1));
        }

        LineDataSet dataSet = new LineDataSet(entries, getContext().getString(R.string.chart_height));
        Random rnd = new Random();
        dataSet.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.invalidate();

    }

    private void setAcceleration_chart(LineChart lineChart) {

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            entries.add(new Entry(i, (int) (Math.random() * 15) + 1));
        }

        LineDataSet dataSet = new LineDataSet(entries, getContext().getString(R.string.chart_acceleration));
        Random rnd = new Random();
        dataSet.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.invalidate();
    }

    private void setTemperature_chart(LineChart lineChart) {

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            entries.add(new Entry(i, (int) (Math.random() * (40 - 20)) + 20));
        }

        ArrayList<Entry> entries2 = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            entries2.add(new Entry(i, (int) (Math.random() * 100) + 1));
        }

        Random rnd = new Random();

        LineDataSet dataSet = new LineDataSet(entries, getContext().getString(R.string.chart_temperature));
        dataSet.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        dataSet.setDrawValues(false);

        LineDataSet dataSet2 = new LineDataSet(entries2, getContext().getString(R.string.chart_humidity));
        dataSet2.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        dataSet2.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineData.addDataSet(dataSet2);

        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.invalidate();
    }

    private void setBpm_chart(LineChart lineChart) {

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            entries.add(new Entry(i, (int) (Math.random() * (160 - 90)) + 90));
        }

        LineDataSet dataSet = new LineDataSet(entries, getContext().getString(R.string.chart_bpm));
        Random rnd = new Random();
        dataSet.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.invalidate();
    }

}
