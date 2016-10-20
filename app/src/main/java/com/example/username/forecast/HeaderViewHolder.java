package com.example.username.forecast;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class HeaderViewHolder extends RecyclerView.ViewHolder{
    TextView cityView;
    TextView countryView;
    public HeaderViewHolder(View convertView) {
        super(convertView);
        cityView = (TextView) convertView.findViewById(R.id.text_city);
        countryView = (TextView) convertView.findViewById(R.id.text_country);

    }

    public void BindData(Location location) {
        cityView.setText(location.city);
        countryView.setText(location.country);
    }
}

