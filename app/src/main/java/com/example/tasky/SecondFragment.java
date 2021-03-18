package com.example.tasky;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

public class SecondFragment extends Fragment {

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView((int) R.layout.activity_main);
        Intent myIntent = new Intent(getContext(), Task_Interface.class);
        SecondFragment.this.startActivityForResult(myIntent, 0);
    }
}
