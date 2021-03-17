package com.example.tasky;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Task_Interface extends AppCompatActivity implements MethodTransfer {
    FloatingActionButton fab;
    int typeid = 1;
    static ArrayList<Object> tasks = new ArrayList<>();

    public String getString(String unfilteredStr, int position) {
        String value = "";
        boolean cond = true;
        int old_index = -1;
        int iter = 0;
        while (cond) {
            if (!BuildConfig.DEBUG || (!TextUtils.isEmpty(unfilteredStr) && unfilteredStr != null)) {
                int comma_index = unfilteredStr.indexOf(",", old_index + 1);
                value = unfilteredStr.substring(old_index + 1, comma_index);
                iter++;
                if (iter == position) {
                    cond = false;
                }
                old_index = comma_index;
            } else {
                throw new AssertionError("Assertion failed 1");
            }
        }
        return value;
    }

    public void delete_task(DB_Adapter helper, PopupWindow PW, String id, View popupViewInfo) {
        helper.delete(id);
        PW.dismiss();
        read_display_tasks(helper, typeid, popupViewInfo, R.id.loadingPanelDelete);
    }

    public int getOccurence(String stringToTest, String letterToCount) {
        if (!BuildConfig.DEBUG || (!TextUtils.isEmpty(stringToTest) && stringToTest != null)) {
            int positionOfLetter = stringToTest.indexOf(letterToCount);
            int countNumberOfLetters = 0;
            while (positionOfLetter != -1) {
                countNumberOfLetters++;
                positionOfLetter = stringToTest.indexOf(letterToCount, positionOfLetter + 1);
            }
            return countNumberOfLetters;
        }
        throw new AssertionError("Assertion failed 2");
    }

    public void read_display_tasks(DB_Adapter helper, int typeid2, View view, int loading_id) {
        String data = helper.getData();
        String[] lines = data.split(" \n");

        tasks = new ArrayList<>();
        if (typeid2 == 1) {
            for (String line : lines) {
                String[] properties = line.split(" {3}");
                tasks.add(new TaskClass(properties[0], properties[1], properties[2], properties[3]));
            }
        }

        RecyclerView rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setAdapter(new Task_Adapter(tasks, this, this, typeid2));
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.addItemDecoration(new DividerItemDecoration(this, 1));
        view.findViewById(loading_id).setVisibility(View.GONE);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typeid = getIntent().getIntExtra("typeid", 0);

        findViewById(R.id.fabAddTask).setOnClickListener(this.add_listener);

        DB_Adapter helper = new DB_Adapter(this);
        // helper.insertData("Test", "5", "22/09/2021");
        // helper.insertData("Test2", "7", "22/10/2021");

        read_display_tasks(helper, typeid, findViewById(R.id.activity_main), R.id.loadingPanel);
    }

    @SuppressLint("InflateParams")
    private final View.OnClickListener add_listener = v -> {
        View popupViewAdd = null;
        if (typeid == 1) {
            popupViewAdd = getLayoutInflater().inflate(R.layout.popup_add_new, null);
        }
        PopupWindow popupWindowAdd = new PopupWindow(popupViewAdd, -1, -2);
        popupWindowAdd.showAtLocation(v, 48, 10, 10);
        findViewById(R.id.fabAddTask).setVisibility(View.GONE);
        popupWindowAdd.setFocusable(true);
        popupWindowAdd.setOutsideTouchable(false);

        assert popupViewAdd != null;
        Button Bstartdate = popupViewAdd.findViewById(R.id.start_date_button);
        Bstartdate.setOnClickListener(new DateOnClickListener(Bstartdate));

        View Bcancel = popupViewAdd.findViewById(R.id.cancel_button);
        Bcancel.setOnClickListener(new CancelOnClickListener(popupWindowAdd));
    };

    public class DateOnClickListener implements View.OnClickListener {
        Button mButton;
        public DateOnClickListener(Button button)  {this.mButton = button;}
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            setContentView(R.layout.popup_add_new);

            DatePicker date_picker = findViewById(R.id.start_date_picker);
            date_picker.setVisibility(View.VISIBLE);

            Button BdateSet = findViewById(R.id.start_date_set);
            BdateSet.setVisibility(View.VISIBLE);

            BdateSet.setOnClickListener(view -> {
                mButton.setHint(date_picker.getDayOfMonth()+"/"+date_picker.getMonth()+"/"+date_picker.getYear());

                date_picker.setVisibility(View.GONE);
                BdateSet.setVisibility(View.GONE);
            });
        }
    }

    public class CancelOnClickListener implements View.OnClickListener {
        PopupWindow mPW;
        public CancelOnClickListener(PopupWindow pw)  {this.mPW = pw;}
        @Override
        public void onClick(View v) {
            setContentView(R.layout.activity_main);
            findViewById(R.id.fabAddTask).setVisibility(View.VISIBLE);
            mPW.dismiss();
            findViewById(R.id.fabAddTask).setOnClickListener(Task_Interface.this.add_listener);
            read_display_tasks(new DB_Adapter(Task_Interface.this), typeid, findViewById(R.id.activity_main), R.id.loadingPanel);
        }
    }
}