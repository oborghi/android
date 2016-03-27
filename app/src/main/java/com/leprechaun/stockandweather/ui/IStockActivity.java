package com.leprechaun.stockandweather.ui;

import android.content.res.Resources;

public interface IStockActivity extends IBackgroundProcessActivity {
    StockFragment getFragment();
    Resources getResources();
    void showError();
}
