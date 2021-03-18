package com.example.tasky;

import android.view.View;
import android.widget.PopupWindow;

public interface MethodTransfer {
    void delete_task(DB_Adapter helper, PopupWindow PW, String id, View popupViewInfo);

    int getOccurence(String str, String str2);

    String getString(String str, int i);

    void read_display_tasks(DB_Adapter helper, View view, int i);
}
