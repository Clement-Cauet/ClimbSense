package com.climbsense.application.function;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.climbsense.application.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CalendarFunction {

    private Calendar calendar;

    public CalendarFunction() {
        calendar = Calendar.getInstance();
    }

    public Date getDate() {
        return calendar.getTime();
    }

    public DatePickerDialog selectDate(Context context, TextView textView) {
        int year    = calendar.get(Calendar.YEAR);
        int month   = calendar.get(Calendar.MONTH);
        int day     = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog popup_calendar = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(calendar.YEAR, year);
                calendar.set(calendar.MONTH, month);
                calendar.set(calendar.DAY_OF_MONTH, day);

                textView.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.getTime()));
            }
        }, year, month, day);

        return popup_calendar;
    }

    public String durationDisplay(Context context, Date start, Date end) {
        long durationMillis = calculateTimeDifference(start, end);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis) % 24;
        long days = TimeUnit.MILLISECONDS.toDays(durationMillis);

        StringBuilder durationBuilder = new StringBuilder();

        if (days > 0) {
            durationBuilder.append(days);
            durationBuilder.append(" " + context.getString(R.string.day));
            if (days > 1) {
                durationBuilder.append("s");
            }
            durationBuilder.append(", ");
        }

        durationBuilder.append(String.format("%02d", hours));
        durationBuilder.append(":");
        durationBuilder.append(String.format("%02d", minutes));
        durationBuilder.append(":");
        durationBuilder.append(String.format("%02d", seconds));

        return durationBuilder.toString();
    }

    private long calculateTimeDifference(Date start, Date end) {
        long startMillis = start.getTime();
        long endMillis = end.getTime();
        return endMillis - startMillis;
    }
}
