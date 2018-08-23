package com.bakikocak.challenge_wafer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import adapter.CountriesAdapter;
import model.Country;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    ArrayList<Country> countriesList = new ArrayList<Country>();
    private CountriesAdapter countriesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        dummyData();

    }

    private void initializeViews() {
        listView = (ListView) findViewById(R.id.lv_main);
        countriesAdapter = new CountriesAdapter(MainActivity.this, countriesList, mTouchListener);
        listView.setAdapter(countriesAdapter);
    }

    private void dummyData() {
        countriesList.add(new Country("Turkey", "Turkish Lira", "Turkish"));
        countriesList.add(new Country("Spain", "Euro", "Spanish"));
        countriesList.add(new Country("Germany", "Euro", "German"));
        countriesList.add(new Country("USA", "US Dollar", "English"));
        countriesList.add(new Country("Brazil", "Brazilian real", "Portugues"));
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };
}
