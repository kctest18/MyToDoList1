package com.example.kevin.mytodolist1;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ColorAdapter extends BaseAdapter
{
    private LayoutInflater myLayoutInflater;
    private ArrayList<ColorItem> colors;

    public ColorAdapter(Context context, ArrayList<ColorItem> list)
    {
        this.colors=list;
        myLayoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Object getItem(int position) {
        return colors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return colors.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=myLayoutInflater.inflate(R.layout.color_item, null);
        int color;
        ColorItem colorItem=(ColorItem)getItem(position);
        Log.d("getView()", "colorItem.getName()="+colorItem.getName());
        Log.d("getView()", "colorItem.getCode()="+colorItem.getCode());
        TextView txtColorName=v.findViewById(R.id.txtColorName);
        txtColorName.setText(colorItem.getName());
        ImageView imgColor=v.findViewById(R.id.imgColor);
//        color=Integer.parseInt(colorItem.getCode(),16);
        imgColor.setBackgroundColor(Color.parseColor(colorItem.getCode()));
        return v;
    }
}
