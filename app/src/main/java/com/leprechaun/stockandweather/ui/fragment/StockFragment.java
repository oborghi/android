package com.leprechaun.stockandweather.ui.fragment;

import android.annotation.SuppressLint;

import com.leprechaun.stockandweather.entity.Stock;
import com.leprechaun.stockandweather.request.thread.RunnableStockData;

import java.util.List;

public class StockFragment extends RunnableFragment {

    private List<Stock> stockList;

    public StockFragment()
    {
        super();
    }

    @SuppressLint("ValidFragment")
    public StockFragment(RunnableStockData action) {
        super(action);
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }
}
