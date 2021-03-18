package com.example.tasky;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Task_Interface extends AppCompatActivity implements MethodTransfer {
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
        read_display_tasks(helper, popupViewInfo, R.id.loadingPanelDelete);
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

    public void read_display_tasks(DB_Adapter helper, View view, int loading_id) {
        String data = helper.getData();
        String[] lines = data.split(" \n");

        tasks = new ArrayList<>();
        for (String line : lines) {
            String[] properties = line.split(" {3}");
            tasks.add(new TaskClass(properties[0], properties[1], properties[2], properties[3]));
        }

        RecyclerView rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setAdapter(new Task_Adapter(tasks, this, this));
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.addItemDecoration(new DividerItemDecoration(this, 1));
        view.findViewById(loading_id).setVisibility(View.GONE);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.fabAddTask).setOnClickListener(this.add_listener);

        DB_Adapter helper = new DB_Adapter(this);
        // helper.insertData("Test", "5", "22/09/2021");
        // helper.insertData("Test2", "7", "22/10/2021");

        read_display_tasks(helper, findViewById(R.id.activity_main), R.id.loadingPanel);
    }

    @SuppressLint({"InflateParams", "ResourceType"})
    private final View.OnClickListener add_listener = v -> {
        FloatingActionButton fab = findViewById(R.id.fabAddTask);
        fab.setVisibility(View.GONE);

        View popupViewAdd = ((LayoutInflater) getLayoutInflater()).inflate(R.layout.popup_add_new, null);

        PopupWindow popupWindowAdd = new PopupWindow(popupViewAdd, -1, -2);
        popupWindowAdd.showAtLocation(popupViewAdd, 48, 10, 10);
        popupWindowAdd.setFocusable(false);
        popupWindowAdd.setOutsideTouchable(false);

        EditText name_text = popupViewAdd.findViewById(R.id.task_name_new);
        EditText harddl_text = popupViewAdd.findViewById(R.id.task_harddl_new);

        /*
        name_text.setShowSoftInputOnFocus(true);
        harddl_text.setShowSoftInputOnFocus(true);

        name_text.clearFocus();
        harddl_text.clearFocus();

        showSoftKeyboard(name_text);
        showSoftKeyboard(harddl_text);
         */

        Button Bstartdate = popupViewAdd.findViewById(R.id.start_date_button);
        Bstartdate.setOnClickListener(new DateOnClickListener(Bstartdate));

        View Bcancel = popupViewAdd.findViewById(R.id.cancel_button);
        Bcancel.setOnClickListener(new CancelOnClickListener(popupWindowAdd, fab));

        View Bdone = popupViewAdd.findViewById(R.id.done_button);
        Bdone.setOnClickListener(new DoneOnClickListener(popupWindowAdd, fab));
    };

    public class DateOnClickListener implements View.OnClickListener {
        Button mButton;
        public DateOnClickListener(Button button)  {this.mButton = button;}
        @Override
        public void onClick(View v) {
            InputMethodManager imm =(InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            @SuppressLint("InflateParams")
            View popupViewDate = ((LayoutInflater) getLayoutInflater()).inflate(R.layout.date_picker, null);

            PopupWindow popupWindowDate = new PopupWindow(popupViewDate, -1, -2);
            popupWindowDate.showAtLocation(popupViewDate, 48, 10, 10);
            popupWindowDate.setFocusable(false);
            popupWindowDate.setOutsideTouchable(false);

            DatePicker date_picker = popupViewDate.findViewById(R.id.start_date_picker);
            Button BdateSet = popupViewDate.findViewById(R.id.start_date_set);

            String date_on_button = (String) mButton.getText();
            if (!date_on_button.equals("")) {
                date_picker.updateDate(Integer.parseInt(date_on_button.substring(6)),
                                Integer.parseInt(date_on_button.substring(3,5))-1,
                                       Integer.parseInt(date_on_button.substring(0,2)));
            }

            BdateSet.setOnClickListener(view -> {

                String month = ""+(date_picker.getMonth()+1);
                String day = ""+(date_picker.getDayOfMonth());
                if(date_picker.getMonth() < 10){ month = "0" + month; }
                if(date_picker.getDayOfMonth() < 10){day  = "0" + day ; }

                StringBuilder text = new StringBuilder(day);
                text.append("/").append(month).append("/").append(date_picker.getYear());
                mButton.setText(text);

                date_picker.setVisibility(View.GONE);
                BdateSet.setVisibility(View.GONE);

                popupWindowDate.dismiss();
            });
        }
    }

    public static class CancelOnClickListener implements View.OnClickListener {
        PopupWindow mPW;
        View mFab;
        public CancelOnClickListener(PopupWindow pw, View fab)  {this.mPW = pw; this.mFab=fab;}
        @Override
        public void onClick(View v) {
            mFab.setVisibility(View.VISIBLE);
            mPW.dismiss();
        }
    }

    public class DoneOnClickListener implements View.OnClickListener {
        PopupWindow mPW;
        View mFab;
        DB_Adapter helper = new DB_Adapter(Task_Interface.this);
        public DoneOnClickListener(PopupWindow pw, View fab)  {this.mPW = pw; this.mFab=fab;}
        @Override
        public void onClick(View v) {
            View popupView = mPW.getContentView();

            String name = ((EditText) popupView.findViewById(R.id.task_name_new)).getText().toString();
            String harddl = ((EditText) popupView.findViewById(R.id.task_harddl_new)).getText().toString();
            String date = ((Button) popupView.findViewById(R.id.start_date_button)).getText().toString();

            helper.insertData(name, harddl, date);

            mFab.setVisibility(View.VISIBLE);
            mPW.dismiss();
            read_display_tasks(helper, findViewById(R.id.activity_main), R.id.loadingPanel);
        }
    }

    public void showSoftKeyboard(View view) {
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static void hideSoftKeyboard(final View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}