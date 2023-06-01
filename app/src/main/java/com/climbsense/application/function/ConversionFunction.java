package com.climbsense.application.function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class ConversionFunction {

    public ConversionFunction() {

    }

    public String convertDBFormatToDateTimeDisplay(String date) throws ParseException {
        Date convert_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(date);
        SimpleDateFormat date_format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        return date_format.format(convert_date);
    }

    public String convertDBFormatToDateDisplay(String date) throws ParseException {
        Date convert_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(date);
        SimpleDateFormat date_format = new SimpleDateFormat("dd MMM yyyy");
        return date_format.format(convert_date);
    }

    public String convertDateDisplayToDBFormat(String date) throws ParseException {
        Date convert_date = new SimpleDateFormat("dd MMM yyyy").parse(date);
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        return date_format.format(convert_date);
    }

    public String convertDateToDBFormat(Date date) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        return date_format.format(date);
    }

    public Date convertDBFormatToDate(String date) throws ParseException {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        return date_format.parse(date);
    }

}
