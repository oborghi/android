package com.leprechaun.stockandweather.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;

public abstract class RunnableFragment extends Fragment {
    private Runnable action;
    private Thread thread;

    protected RunnableFragment()
    {
        super();
    }

    protected RunnableFragment(Runnable action) {
        super();
        this.action = action;

        if(action != null)
            this.thread = new Thread(action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if(thread != null && thread.getState() == Thread.State.NEW) {
            action.run();
        }
    }

    @Override
    public void onDestroy() {
        if (this.thread != null) {
            if (thread.isAlive()) {
                thread.interrupt();
                thread = null;
            }
        }

        super.onDestroy();
    }
}
