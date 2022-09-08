package com.androiddev.shoesfit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiddev.shoesfit.model.Shoes;
import com.androiddev.shoesfit.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ShoesAdapter extends ArrayAdapter {
    private List<Shoes> shoesList;

    public ShoesAdapter(Context context, int resourceId, List<Shoes> shoesList) {
        super(context, resourceId, shoesList.toArray());
        this.shoesList = shoesList;
    }

    @Override
    public int getCount(){
        return super.getCount();
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.shoes_item, null);

        ImageView iv = view.findViewById(R.id.iv_image1);
        TextView tv_title = view.findViewById(R.id.tv_name);
        TextView tv_price = view.findViewById(R.id.tv_harga);

        Glide.with(view)
                .load(shoesList.get(position).getImage1())
                .error(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);

        tv_title.setText(shoesList.get(position).getName());
        tv_price.setText("Rp. " + shoesList.get(position).getPrice());
        return view;
    }
}
