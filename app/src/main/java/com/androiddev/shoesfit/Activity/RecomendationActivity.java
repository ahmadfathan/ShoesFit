package com.androiddev.shoesfit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.androiddev.shoesfit.Databases.DBHelper;
import com.androiddev.shoesfit.Models.Filter;
import com.androiddev.shoesfit.Models.Shoes;
import com.androiddev.shoesfit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RecomendationActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private List<Shoes> shoesList;
    private Spinner sp_kebutuhan, sp_celana, sp_harga;
    private Button btn_cari;

    private int kebutuhan, celana, harga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendation);

        shoesList = new ArrayList<>();
        dbHelper = new DBHelper(this);

        sp_kebutuhan = findViewById(R.id.sp_kebutuhan);
        sp_celana = findViewById(R.id.sp_celana);
        sp_harga = findViewById(R.id.sp_harga);

        btn_cari = findViewById(R.id.btn_cari);

        btn_cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kebutuhan = sp_kebutuhan.getSelectedItemPosition() - 1;
                celana = sp_celana.getSelectedItemPosition() - 1;
                harga = sp_harga.getSelectedItemPosition() -1;

                if(kebutuhan != -1 && celana != -1 && harga != -1){
                    Intent intent = new Intent(RecomendationActivity.this, ShoesReviewActivity.class);
                    intent.putExtra("kebutuhan", kebutuhan);
                    intent.putExtra("celana", celana);
                    intent.putExtra("harga", harga);

                    startActivity(intent);
                }else{
                    Toast.makeText(RecomendationActivity.this, "Belum Lengkap", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(dbHelper.getAllOfShoes() == null) {
            try {
                JSONObject data = new JSONObject(loadJSONFromAsset());
                JSONArray sa = data.getJSONArray("Data");
                for (int i = 0; i < sa.length(); i++) {
                    JSONObject shoes = sa.getJSONObject(i);
                    dbHelper.addShoes(new Shoes(
                            0,
                            shoes.getString("Type"),
                            shoes.getString("Category"),
                            shoes.getString("Name"),
                            shoes.getString("Color"),
                            shoes.getInt("Price"),
                            shoes.getString("Image1"),
                            shoes.getString("Image2"),
                            shoes.getString("Image3"),
                            shoes.getString("Description"),
                            shoes.getString("Suits with")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        List<String> kebutuhanList = new ArrayList<>();
        kebutuhanList.add("-- Pilih Kebutuhan --");
        kebutuhanList.add("Mendaki");
        kebutuhanList.add("Olahraga");
        kebutuhanList.add("Rapat Formal");
        kebutuhanList.add("Pesta");

        ArrayAdapter<String> spa_kebutuhan = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kebutuhanList);
        spa_kebutuhan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);;
        sp_kebutuhan.setAdapter(spa_kebutuhan);

        List<String> celanaList = new ArrayList<>();
        celanaList.add("-- Pilih Celana --");
        celanaList.add("Navy");
        celanaList.add("Black");
        celanaList.add("Blue");
        celanaList.add("Light Grey");
        celanaList.add("Dark Grey");
        celanaList.add("White");
        celanaList.add("Tan");
        celanaList.add("Beige");
        celanaList.add("Brown");
        celanaList.add("Olive");
        celanaList.add("Jeans");

        ArrayAdapter<String> spa_celana = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, celanaList);
        spa_celana.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);;
        sp_celana.setAdapter(spa_celana);

        List<String> hargaList = new ArrayList<>();
        hargaList.add("-- Pilih Harga --");
        hargaList.add("di bawah Rp.200,000");
        hargaList.add("Rp.200,000 sampai Rp.300,000");
        hargaList.add("di atas Rp.300,000");

        ArrayAdapter<String> spa_harga = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hargaList);
        spa_harga.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);;
        sp_harga.setAdapter(spa_harga);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
