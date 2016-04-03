package com.leprechaun.stockandweather.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leprechaun.stockandweather.R;
import com.leprechaun.stockandweather.entity.Weather;
import com.leprechaun.stockandweather.entity.WeatherCurrentCondition;
import com.leprechaun.stockandweather.entity.WeatherPrevision;
import com.leprechaun.stockandweather.request.thread.RunnableWeatherData;
import com.leprechaun.stockandweather.ui.adapters.AdapterPrevisionList;
import com.leprechaun.stockandweather.ui.fragment.ProgressDialogFragment;
import com.leprechaun.stockandweather.ui.fragment.WeatherFragment;
import com.leprechaun.stockandweather.ui.interfaces.IWeatherActivity;
import com.leprechaun.stockandweather.ui.swipe.ISwipeActivity;
import com.leprechaun.stockandweather.ui.swipe.SwipeGestureDetector;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity implements IWeatherActivity, ISwipeActivity {

    private TextView textCity;
    private LinearLayout layoutWeatherInfo;
    private TextView labelTemperature;
    private TextView labelDescription;
    private TextView textHumidity;
    private TextView textPressure;
    private TextView textPressureStatus;
    private TextView textVisibility;
    private TextView textSunrise;
    private TextView textSunset;
    private TextView textWindDirection;
    private TextView textWindSpeed;
    private ImageView imageCurrentCondition;
    private ListView listPrevision;

    private WeatherFragment retainedFragment;
    private static WeatherActivity instance;
    private static volatile RunnableWeatherData runnableWeatherData;
    private GestureDetector gestureDetector;

    private static final Locale brazilLocale = new Locale("pt", "BR");

    private final String retained = "retainedWeather";
    private final String retainedProcess = "retainedWeatherProcess";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        instance = this;

        gestureDetector = new GestureDetector(
                new SwipeGestureDetector(this));

        textCity = (TextView) findViewById(R.id.labelCity);
        layoutWeatherInfo = (LinearLayout) findViewById(R.id.layoutWeatherInfo);
        labelTemperature = (TextView) findViewById(R.id.labelTempeature);
        labelDescription = (TextView) findViewById(R.id.labelDescription);
        textHumidity = (TextView) findViewById(R.id.textHumidity);
        textPressure = (TextView) findViewById(R.id.textPreassure);
        textPressureStatus = (TextView) findViewById(R.id.textPreassureStatus);
        textVisibility = (TextView) findViewById(R.id.textVisibility);
        textSunrise = (TextView) findViewById(R.id.textSunrise);
        textSunset = (TextView) findViewById(R.id.textSunset);
        textWindDirection = (TextView) findViewById(R.id.textWindDirection);
        textWindSpeed = (TextView) findViewById(R.id.textWindSpeed);
        imageCurrentCondition = (ImageView) findViewById(R.id.imageCurrentCondiction);
        listPrevision = (ListView) findViewById(R.id.listPrevision);

        retainedFragment = getRetainedFragment();

        if (retainedFragment == null) {
            retainedFragment = createRetainedFragment();
            setRetainedFragment(retainedFragment);
        } else {
            setLastWeatherUpdate(retainedFragment.getWeather());
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.menu_item_refresh:
                deleteRetainedFragment();
                retainedFragment = createRetainedFragment();
                setRetainedFragment(retainedFragment);
                return true;

            default:
                return false;
        }
    }

    public void deleteRetainedFragment() {
        FragmentManager fm = getFragmentManager();
        WeatherFragment fragment = getRetainedFragment();

        if(runnableWeatherData != null)
            runnableWeatherData.cancel();

        if(fragment != null)
            fm.beginTransaction().remove(fragment).commitAllowingStateLoss();

        retainedFragment = null;
    }

    public void setRetainedFragment(WeatherFragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(fragment, retained).commit();
    }

    public WeatherFragment getRetainedFragment() {
        return (WeatherFragment) getFragmentManager().findFragmentByTag(retained);
    }

    @Override
    public WeatherFragment getFragment() {
        return retainedFragment;
    }

    @Override
    public void showError()
    {
        closeProcessDialog();
        showToast(R.string.dialog_get_weather_info_error);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onPreExecute() {
        showProcessDialog();
    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {
        WeatherFragment fragment = getRetainedFragment();
        if (fragment != null) {
            setLastWeatherUpdate(fragment.getWeather());
        }
    }

    private void setLastWeatherUpdate(Weather lastWeatherUpdate) {

        if (lastWeatherUpdate != null) {

            WeatherCurrentCondition currentCondition = lastWeatherUpdate.getCurrentCondition();
           instance.textCity.setText(lastWeatherUpdate.getCity());

            if(currentCondition != null) {

                instance.labelTemperature.setText(String.format(brazilLocale, "%dºC", currentCondition.getTemperature()));
                instance.labelDescription.setText(currentCondition.getDescription());
                instance.textHumidity.setText(currentCondition.getHumidity());
                instance.textPressure.setText(currentCondition.getPressure());
                instance.textPressureStatus.setText(currentCondition.getPressureStatus());
                instance.textVisibility.setText(currentCondition.getVisibility());
                instance.textSunrise.setText(currentCondition.getSunrise());
                instance.textSunset.setText(currentCondition.getSunset());
                instance.textWindDirection.setText(currentCondition.getWindDirection());
                instance.textWindSpeed.setText(currentCondition.getWindSpeedy());

                if (currentCondition.getImageUrl() != null)
                    Picasso.with(instance).load(currentCondition.getImageUrl()).fit().into(instance.imageCurrentCondition);
                else
                    Picasso.with(instance).load(R.drawable.cannot_be_load).fit().into(instance.imageCurrentCondition);

                List<WeatherPrevision> previsions = lastWeatherUpdate.getPrevisions();

                if (previsions != null) {
                    if (previsions.size() > 0) {
                        AdapterPrevisionList adapter = new AdapterPrevisionList(instance, R.layout.item_list_prevision, previsions);
                        instance.listPrevision.setAdapter(adapter);
                    }
                }

                instance.layoutWeatherInfo.setVisibility(View.VISIBLE);
            }
            else
            {
                showError();
            }
        }
        else
        {
            showError();
        }

        closeProcessDialog();
    }

    public void showToast(final @StringRes int id)
    {
        Context context = getApplicationContext();
        CharSequence text = getString(id);
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void showProcessDialog() {
        FragmentManager fm = getFragmentManager();
        ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(R.string.dialog_wait_message, false, false);
        dialogFragment.show(fm, retainedProcess);


    }

    private ProgressDialogFragment getProcessDialog() {
        return (ProgressDialogFragment) getFragmentManager().findFragmentByTag(retainedProcess);
    }

    public void closeProcessDialog() {
        ProgressDialogFragment dialogFragment = getProcessDialog();

        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    private WeatherFragment createRetainedFragment() {

        if(runnableWeatherData != null)
            runnableWeatherData.cancel();

        runnableWeatherData = new RunnableWeatherData(this);
        retainedFragment = new WeatherFragment(runnableWeatherData);

        return retainedFragment;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.dispatchTouchEvent(event);
    }

    @Override
    public void onLeftSwipe() {

    }

    @Override
    public void onRightSwipe() {
        startActivity(new Intent(this, StockActivity.class));
    }
}
