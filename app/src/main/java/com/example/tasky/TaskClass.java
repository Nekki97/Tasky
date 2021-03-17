package com.example.tasky;

import java.util.ArrayList;

public class TaskClass {
    private static int lastContactId = 0;
    private final String mID;
    private final String mName;
    private final String mHardDL;
    private final String mStartDate;

    public TaskClass(String ID, String name, String harddl, String start_date) {
        this.mID = ID;
        this.mName = name;
        this.mHardDL = harddl;
        this.mStartDate = start_date;
    }

    public String getID() {
        return this.mID;
    }

    public String getName() {
        return this.mName;
    }

    public String getStartDate() { return this.mStartDate; }

    public String getHardDL() { return this.mHardDL; }

    public static ArrayList<TaskClass> createTaskList(int numTasks) {
        ArrayList<TaskClass> tasks_1 = new ArrayList<>();
        for (int i = 1; i <= numTasks; i++) {
            int i2 = lastContactId + 1;
            lastContactId = i2;
            tasks_1.add(new TaskClass(Integer.toString(i2), "Example Task", "Example HardDL", "Example Start Date"));
        }
        return tasks_1;
    }
}
