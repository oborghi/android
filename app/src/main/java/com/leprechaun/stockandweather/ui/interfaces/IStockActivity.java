package com.leprechaun.stockandweather.ui.interfaces;

import android.content.res.Resources;

import com.leprechaun.stockandweather.ui.fragment.StockFragment;

public interface IStockActivity extends IBackgroundProcessActivity {
    StockFragment getFragment();
    Resources getResources();
    void showError();
}
