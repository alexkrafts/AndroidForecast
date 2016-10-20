package com.example.username.forecast;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by username on 10/18/2016.
 */
public class ForecastHolder extends RecyclerView.ViewHolder {
    TextView textMain;
    TextView textDate;
    TextView textDay;
    TextView textLow;
    TextView textHigh;
    public ForecastHolder(View convertView) {
        super(convertView);
        textMain = (TextView) convertView.findViewById(R.id.text_main);
        textDate = (TextView) convertView.findViewById(R.id.text_date);
        textDay = (TextView) convertView.findViewById(R.id.text_day);
        textLow = (TextView) convertView.findViewById(R.id.text_low);
        textHigh = (TextView) convertView.findViewById(R.id.text_high);
    }
    public void BindData(Forecast item) {
        textMain.setText(item.getText());
        textDate.setText(item.getDate());
        textDay.setText(item.getDay());
        textLow.setText(String.format("%s°", item.getLow()));
        textHigh.setText(String.format("%s°", item.getHigh()));
    }
}

