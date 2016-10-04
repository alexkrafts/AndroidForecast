package com.example.username.forecast;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by username on 10/4/2016.
 */

public class ForecastAdapter extends ArrayAdapter<Forecast> {
    private List<Forecast> _items;
    private Context _context;

    public ForecastAdapter(Context context, List<Forecast> objects) {
        super(context, R.layout.row_layout, objects);
        _items = objects;
        _context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_layout, parent, false);
        }
        TextView textMain = (TextView) convertView.findViewById(R.id.text_main);
        TextView textDate = (TextView) convertView.findViewById(R.id.text_date);
        TextView textDay = (TextView) convertView.findViewById(R.id.text_day);

        Forecast item = _items.get(position);

        textMain.setText(item.getText());
        textDate.setText(item.getDate());
        textDay.setText(item.getDay());

        return convertView;
    }
}
