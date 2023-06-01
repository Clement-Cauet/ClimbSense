package com.climbsense.application.ui.fragment;

import static android.text.format.DateUtils.formatElapsedTime;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.climbsense.application.ChronometerEvent;
import com.climbsense.application.ChronometerService;
import com.climbsense.application.R;
import com.climbsense.application.database.DatabaseAccess;
import com.climbsense.application.databinding.FragmentClimbBinding;
import com.climbsense.application.function.InstanceFunction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ClimbFragment extends Fragment {

    private FragmentClimbBinding binding;

    private DatabaseAccess databaseAccess;
    private InstanceFunction instanceFunction;
    private CountDownTimer countDownTimer;

    private LinearLayout fragment_climb;
    private TextView chrono_text, height_text, acceleration_text, bpm_text, temperature_text, humidity_text;
    private Button start_climb_button, stop_climb_button;
    private Chronometer chronometer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentClimbBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.databaseAccess = databaseAccess.getInstance(getActivity());
        this.instanceFunction = InstanceFunction.getInstance();

        fragment_climb      = root.findViewById(R.id.fragment_climb);
        chrono_text         = root.findViewById(R.id.chrono_text);
        height_text         = root.findViewById(R.id.height_text);
        acceleration_text   = root.findViewById(R.id.acceleration_text);
        bpm_text            = root.findViewById(R.id.bpm_text);
        temperature_text    = root.findViewById(R.id.temperature_text);
        humidity_text       = root.findViewById(R.id.humidity_text);
        start_climb_button  = root.findViewById(R.id.start_climb_button);
        stop_climb_button   = root.findViewById(R.id.stop_climb_button);

        if (instanceFunction.getChronometer() != null) {
            chrono_text.setText(formatElapsedTime((long) instanceFunction.getClimbInfo().get("time")));
            height_text.setText(String.valueOf(instanceFunction.getClimbInfo().get("height")));
            bpm_text.setText(String.valueOf(instanceFunction.getClimbInfo().get("bpm")));
            if (!instanceFunction.getChronometer()) {
                start_climb_button.setVisibility(View.VISIBLE);
                stop_climb_button.setVisibility(View.GONE);
            } else {
                start_climb_button.setVisibility(View.GONE);
                stop_climb_button.setVisibility(View.VISIBLE);
            }
        }

        start_climb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.addClimb();

                Intent serviceIntent = new Intent(getActivity(), ChronometerService.class);
                serviceIntent.putExtra("startChronometer", true);
                getActivity().startService(serviceIntent);

                instanceFunction.setChronometer(true);

                start_climb_button.setVisibility(View.GONE);
                stop_climb_button.setVisibility(View.VISIBLE);
            }
        });

        stop_climb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.updateClimb();

                getActivity().stopService(new Intent(getActivity(), ChronometerService.class));

                instanceFunction.setChronometer(false);

                chrono_text.setText(R.string.chronometer_text);
                height_text.setText(R.string.height_text);
                acceleration_text.setText(R.string.acceleration_text);
                bpm_text.setText(R.string.bpm_text);
                temperature_text.setText(R.string.temperature_text);
                humidity_text.setText(R.string.humidity_text);

                start_climb_button.setVisibility(View.VISIBLE);
                stop_climb_button.setVisibility(View.GONE);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onChronometerEvent(ChronometerEvent event) {
        Map climb_map = event.getClimbMap();
        chrono_text.setText(formatElapsedTime((long) climb_map.get("time")));
        height_text.setText(String.valueOf(climb_map.get("height")));
        acceleration_text.setText(String.valueOf(climb_map.get("acceleration")));
        bpm_text.setText(String.valueOf(climb_map.get("bpm")));
        temperature_text.setText(String.valueOf(climb_map.get("temperature")));
        humidity_text.setText(String.valueOf(climb_map.get("humidity")));
    }

    private String formatElapsedTime(long elapsedMillis) {
        int seconds = (int) (elapsedMillis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        int days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        String time;
        if (days > 0) {
            if (days > 1) {
                time = String.format("%02d " + getContext().getString(R.string.day) + "s %02d:%02d:%02d", days, hours, minutes, seconds);
            } else {
                time = String.format("%02d" + getContext().getString(R.string.day) + "%02d:%02d:%02d", days, hours, minutes, seconds);
            }
        } else {
            time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return time;
    }

}
