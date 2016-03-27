package com.leprechaun.stockandweather.ui;

import android.annotation.SuppressLint;

import com.leprechaun.stockandweather.entity.Stock;

import java.util.List;

public class StockFragment extends RunnableFragment {

    private List<Stock> stockList;

    public StockFragment()
    {
        super(null);
    }

    @SuppressLint("ValidFragment")
    public StockFragment(Runnable action) {
        super(action);
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }
}
