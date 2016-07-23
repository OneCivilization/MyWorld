package com.onecivilization.Optimize.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onecivilization.Optimize.Model.Record;
import com.onecivilization.Optimize.R;
import com.onecivilization.Optimize.Util.AppManager;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Created by CGZ on 2016/7/23.
 */
public class RecordsCalendar extends LinearLayout {

    private GridLayout gridLayout;
    private Button[] buttons;
    private ImageButton previous;
    private ImageButton next;
    private TextView title;
    private int currentYear;
    private int currentMonth;
    private int thisYear;
    private int thisMonth;
    private int today;
    private int todayLocation;
    private int firstDayLocation;
    private int monthLength;
    private int dayColor;
    private int todayColor;
    private int backgroundColor;
    private int trueColor;
    private int falseColor;
    private String[] monthNames = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();
    private LinkedList<Record> records = new LinkedList<>();
    private int punishment;

    public RecordsCalendar(Context context) {
        this(context, null);
    }

    public RecordsCalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordsCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RecordsCalendar, defStyleAttr, 0);
        try {
            dayColor = typedArray.getColor(R.styleable.RecordsCalendar_dayColor, 0xff527ecb);
            todayColor = typedArray.getColor(R.styleable.RecordsCalendar_todayColor, 0xff0046c6);
            backgroundColor = typedArray.getColor(R.styleable.RecordsCalendar_backgroundColor, 0xffd0e1ed);
            trueColor = typedArray.getColor(R.styleable.RecordsCalendar_trueColor, 0x7f1dbd0c);
            falseColor = typedArray.getColor(R.styleable.RecordsCalendar_falseColor, 0x50ff0000);
        } finally {
            typedArray.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.records_calendar, this);
        gridLayout = (GridLayout) findViewById(R.id.grid_layout);
        previous = (ImageButton) findViewById(R.id.previous);
        next = (ImageButton) findViewById(R.id.next);
        title = (TextView) findViewById(R.id.title);
        buttons = new Button[49];
        int i;
        for (i = 0; i < 49; i++) {
            buttons[i] = (Button) gridLayout.getChildAt(i);
            buttons[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            buttons[i].setTextColor(dayColor);
            buttons[i].setBackgroundColor(backgroundColor);
        }
        String[] weekdayNames = new DateFormatSymbols(AppManager.LOCALE).getShortWeekdays();
        for (i = 0; i < 7; i++) {
            buttons[i].setText(weekdayNames[i + 1]);
            buttons[i].setTextColor(0xff454545);
            buttons[i].setBackgroundColor(0x00000000);
        }
        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMonth != 0) {
                    setTime(currentYear, currentMonth - 1);
                } else {
                    setTime(currentYear - 1, 11);
                }
            }
        });
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMonth != 11) {
                    setTime(currentYear, currentMonth + 1);
                } else {
                    setTime(currentYear + 1, 0);
                }
            }
        });
        GregorianCalendar calendar = new GregorianCalendar();
        thisYear = calendar.get(Calendar.YEAR);
        thisMonth = calendar.get(Calendar.MONTH);
        today = calendar.get(Calendar.DAY_OF_MONTH);
        setTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }

    public void setTime(int year, int month) {
        if (todayLocation != 0) {
            buttons[todayLocation].setTextColor(dayColor);
        }
        for (int i = 7; i < 49; i++) {
            buttons[i].setBackgroundColor(0xffd0e1ed);
        }
        currentYear = year;
        currentMonth = month;
        GregorianCalendar calendar = new GregorianCalendar(year, month, 1);
        if (AppManager.LOCALE.equals(Locale.CHINESE)) {
            title.setText(new SimpleDateFormat("yyyy年MM月").format(calendar.getTime()));
        } else {
            title.setText(monthNames[calendar.get(Calendar.MONTH)] + "  " + calendar.get(Calendar.YEAR));
        }
        firstDayLocation = calendar.get(Calendar.DAY_OF_WEEK) + 6;
        monthLength = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int i;
        for (i = 7; i < firstDayLocation; i++) {
            buttons[i].setText("");
        }
        for (; i < firstDayLocation + monthLength; i++) {
            buttons[i].setText(String.valueOf(i - firstDayLocation + 1));
            if (thisYear == currentYear && thisMonth == currentMonth && (i - firstDayLocation + 1) == today) {
                buttons[i].setTextColor(todayColor);
                todayLocation = i;
            }
        }
        for (; i < 49; i++) {
            buttons[i].setText("");
        }
        showRecords();
    }

    public void setRecords(LinkedList<Record> records, int punishment) {
        this.records = records;
        this.punishment = punishment;
        showRecords();
    }

    public void showRecords() {
        GregorianCalendar calendar = new GregorianCalendar(currentYear, currentMonth, 1);
        long earliestTimeThisMonth = calendar.getTimeInMillis();
        calendar.set(Calendar.MONTH, currentMonth + 1);
        long latestTimeThisMonth = calendar.getTimeInMillis();
        ListIterator<Record> iterator = records.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().time >= earliestTimeThisMonth) {
                iterator.previous();
                break;
            }
        }
        Record record;
        int[] progress = new int[31];
        while (iterator.hasNext()) {
            record = iterator.next();
            if (record.time < latestTimeThisMonth) {
                calendar.setTimeInMillis(record.time);
                if (record.tag) {
                    progress[calendar.get(Calendar.DAY_OF_MONTH) - 1]++;
                } else {
                    progress[calendar.get(Calendar.DAY_OF_MONTH) - 1] -= punishment;
                }
            } else {
                break;
            }
        }
        for (int i = 0; i < 31; i++) {
            if (progress[i] > 0) {
                buttons[firstDayLocation + i].setBackgroundColor(trueColor + 0x10000000 * (progress[i] - 1));
            } else if (progress[i] < 0) {
                buttons[firstDayLocation + i].setBackgroundColor(falseColor - 0x10000000 * (progress[i] + 1));
            }
        }
    }

}
