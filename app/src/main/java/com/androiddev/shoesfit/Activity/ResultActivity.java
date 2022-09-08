package com.androiddev.shoesfit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androiddev.shoesfit.R;

public class ResultActivity extends AppCompatActivity {

    private Button btn_rekomendasi;
    private TextView tv_result;
    private RadioGroup rg_jenis_kelamin;
    private double w, h;

    int jenisKelamin = 0;

    double[]    EU_H = {35, 35.5, 36, 37, 37.5, 38, 38.5, 39, 40, 41, 42, 43, 44, 45, 46.5, 48.5};
    double[][]  UK_H = {
            {3      , 2.5},
            {3.5    , 3},
            {4      , 3.5},
            {4.5    , 4},
            {5      , 4.5},
            {5.5    , 5},
            {6      , 5.5},
            {6.5    , 6},
            {7      , 6.5},
            {7.5    , 7},
            {8      , 7.5},
            {8.5    , 8},
            {10     , 9.5},
            {11     , 10.5},
            {12     , 11.5},
            {13.5   , 13}
    };

    double[][]  US_H = {
            {3.5  , 5},
            {4    , 5.5},
            {4.5  , 6},
            {5    , 6.5},
            {5.5  , 7},
            {6    , 7.5},
            {6.5  , 8},
            {7    , 8.5},
            {7.5  , 9},
            {8    , 9.5},
            {8.5  , 10},
            {9    , 10.5},
            {10.5 , 12},
            {11.5 , 13},
            {12.5 , 14},
            {14   , 15.5}
    };

    double[]    EU_W = {35.5, 36, 37, 37.5, 38, 38.5, 39, 40, 41, 42, 43, 44, 45, 46.5, 48.5};
    double[]    CM_W = {8, 8.1, 8.3, 8.5, 8.6, 8.8, 8.89, 9.2, 9.52, 10, 10.16, 10.48, 10.95, 10.95, 11.8};
    double[]    CM_H = {22.8, 23.1, 23.5, 23.8, 24.1, 24.5, 24.8, 25.1, 25.4, 25.7, 26, 26.7, 27.3, 27.9, 28.6, 29.2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        btn_rekomendasi = findViewById(R.id.btn_hasil_rekomendasi);

        btn_rekomendasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, RecomendationActivity.class);
                startActivity(intent);
            }
        });
        tv_result = findViewById(R.id.tv_result);
        rg_jenis_kelamin = findViewById(R.id.rg_jenis_kelamin);

        rg_jenis_kelamin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_pria:
                        jenisKelamin = 0;
                        break;
                    case R.id.rb_wanita:
                        jenisKelamin = 1;
                        break;
                }
                calculate(w, h);
            }
        });

        if(getIntent().hasExtra("width") && getIntent().hasExtra("width")){
            w = getIntent().getDoubleExtra("width", 0);
            h = getIntent().getDoubleExtra("height", 0);

            calculate(w, h);

        }else{
            finish();
        }
    }
    
    private String formatResult(double w, double h, double EU, double UK, double US, double CM){
        return String.format("Ukuran Kaki\n%.1f mm x %.1f mm\n\nEU  : %.1f \nUK  : %.1f \nUS  : %.1f \nCM  : %.2f", h, w, EU, UK, US, CM);
    }

    //Ukuran Kaki\n250mm x 100 mm\n\nEU  : 43\nUK  : 12\nUS  : 21\nCM  : 43

    private void calculate(double w, double h){
        double _EU = 0, _UK = 0, _US = 0, _CM = 0;

        int k = 0, l = 0;

        for(int i = 0 ; i < EU_H.length; i++){
            if(i == 0){
                if(h/10 <= CM_H[i]){
                    k = i;
                }
            }else{
                if(h/10 >= CM_H[i-1] && h/10 <= CM_H[i]){
                    if(Math.abs(h - CM_H[i]) >=  Math.abs(h - CM_H[i-1])){
                        k = i;
                    }else{
                        k = i-1;
                    }
                }
            }
        }

        for(int i = 0 ; i < EU_W.length; i++){
            if(i == 0){
                if(w/10 <= CM_W[i]){
                    l = i;
                }
            }else{
                if(w/10 >= CM_W[i-1] && w/10 <= CM_W[i]){
                    if(Math.abs(w - CM_W[i]) >=  Math.abs(w - CM_W[i-1])){
                        l = i;
                    }else{
                        l = i-1;
                    }
                }
            }
        }

        double realSize = (EU_H[k] > EU_W[l])? EU_H[k]:EU_W[l];

        for(int i = 0 ; i < EU_H.length; i++){
            if(EU_H[i] == realSize){
                _EU = EU_H[i];
                _UK = UK_H[i][jenisKelamin];
                _US = US_H[i][jenisKelamin];
                _CM = CM_H[i];
            }
        }

        tv_result.setText(formatResult(w,h,_EU,_UK,_US, _CM));
    }
}
