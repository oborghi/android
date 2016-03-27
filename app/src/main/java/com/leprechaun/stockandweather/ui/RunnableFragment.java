package com.leprechaun.stockandweather.ui;

import android.app.Fragment;
import android.os.Bundle;

public abstract class RunnableFragment extends Fragment {
    private Runnable action;
    private Thread thread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if(thread != null && thread.getState() == Thread.State.NEW) {
            action.run();
        }
    }

    protected RunnableFragment(Runnable action) {
        super();
        this.action = action;

        if(action != null)
            this.thread = new Thread(action);
    }
}
