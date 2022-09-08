package com.androiddev.shoesfit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.androiddev.shoesfit.adapter.ShoesAdapter;
import com.androiddev.shoesfit.db.DBHelper;
import com.androiddev.shoesfit.model.Filter;
import com.androiddev.shoesfit.model.Shoes;
import com.androiddev.shoesfit.R;

import java.util.ArrayList;
import java.util.List;

public class ShoesReviewActivity extends BaseActivity {

    private int kebutuhan, celana, harga;
    private Filter filter;
    private List<Shoes> shoesList = new ArrayList<>();
    private DBHelper dbHelper;
    private ShoesAdapter adapter;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoes_review);

        gridView = findViewById(R.id.gridview);

        dbHelper = new DBHelper(this);

        if(getIntent().hasExtra("kebutuhan") &&
            getIntent().hasExtra("celana") &&
                getIntent().hasExtra("harga")
        ){
            kebutuhan = getIntent().getIntExtra("kebutuhan", -1);
            celana = getIntent().getIntExtra("celana", -1);
            harga = getIntent().getIntExtra("harga", -1);

            filter = new Filter(
                    Filter.KEBUTUHAN.valueOf(kebutuhan),
                    Filter.CELANA.valueOf(celana),
                    Filter.HARGA.valueOf(harga)
                    );


            //Toast.makeText(this, "" + filter.getHarga().getValue(), Toast.LENGTH_SHORT).show();
            shoesList = dbHelper.getListOfShoes(filter);



            if(shoesList != null){
                //Toast.makeText(this, "" + shoesList.size(), Toast.LENGTH_SHORT).show();
                adapter = new ShoesAdapter(this, R.layout.shoes_item, shoesList);
                gridView.setAdapter(adapter);
            }else{
                Toast.makeText(this, "Tidak ada sepatu yang sesuai", Toast.LENGTH_SHORT).show();
            }
        }else{
            finish();
        }
    }
}
