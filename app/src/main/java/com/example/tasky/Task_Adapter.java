package com.example.tasky;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Task_Adapter extends RecyclerView.Adapter<Task_Adapter.ViewHolder> {
    public Context mContext;
    public MethodTransfer mMethodTransfer;
    private final List<Object> mTasks;
    int typeid;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.basic_task_name);
        }
    }

    public Task_Adapter(List<Object> tasks, Context context, MethodTransfer methodTransfer) {
        mTasks = tasks;
        mContext = context;
        mMethodTransfer = methodTransfer;
    }

    @NonNull
    @Override
    public Task_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Object task = mTasks.get(position);
        TextView textView = holder.nameTextView;
        if (typeid == 1) {
            textView.setText(((TaskClass) task).getName());
        }
        holder.itemView.setOnLongClickListener(new MyOnLongClickListener(task));
    }

    public int getItemCount() {
        return mTasks.size();
    }


    public class MyOnLongClickListener implements View.OnLongClickListener {
        String mStartDate;
        String mID;
        String mName;
        String mHardDL;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View popupViewInfo = inflater.inflate(R.layout.popup_info_new, null);
        DB_Adapter helper = new DB_Adapter(mContext);

        public MyOnLongClickListener(Object task) {
            TaskClass task_1 = (TaskClass) task;
            this.mID = task_1.getID();
            this.mName = task_1.getName();
            this.mStartDate = task_1.getStartDate();
            this.mHardDL = task_1.getHardDL();
        }

        public boolean onLongClick(View v) {
            PopupWindow pw = new PopupWindow(popupViewInfo, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            pw.setOutsideTouchable(false);
            pw.showAtLocation(v, Gravity.CENTER, 0, 0);

            ((TextView) popupViewInfo.findViewById(R.id.task_name)).setText(mName);
            ((TextView) popupViewInfo.findViewById(R.id.task_start_date)).setText(mStartDate);
            ((TextView) popupViewInfo.findViewById(R.id.task_harddl)).setText(mHardDL);

            View Bback = popupViewInfo.findViewById(R.id.back_button);
            Bback.setOnClickListener(new BackOnClickListener(pw));

            View Bdelete = popupViewInfo.findViewById(R.id.del_button);
            Bdelete.setOnClickListener(new DeleteOnClickListener(pw));
            return true;
        }

        public class BackOnClickListener implements View.OnClickListener {
            PopupWindow mPW;
            public BackOnClickListener(PopupWindow pw)  {this.mPW = pw; }
            @Override
            public void onClick(View v) {
                mPW.dismiss();
                mMethodTransfer.read_display_tasks(helper, popupViewInfo, R.id.loadingPanelDelete);
            }
        }

        public class DeleteOnClickListener implements View.OnClickListener {
            String del_ID = MyOnLongClickListener.this.mID;
            PopupWindow mPW;
            public DeleteOnClickListener(PopupWindow pw)  {this.mPW = pw; }
            @Override
            public void onClick(View v) {
                popupViewInfo.findViewById(R.id.loadingPanelDelete).setVisibility(View.VISIBLE);
                mMethodTransfer.delete_task(helper, mPW, del_ID, popupViewInfo);
            }
        }

    }
}