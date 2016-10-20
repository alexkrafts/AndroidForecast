package com.example.username.forecast;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ConditionViewHolder extends RecyclerView.ViewHolder{
    TextView temperView;
    TextView weatherView;
    public ConditionViewHolder(View convertView) {
        super(convertView);
        temperView = (TextView) convertView.findViewById(R.id.text_temper);
        weatherView = (TextView) convertView.findViewById(R.id.text_weather);

    }

    public void BindData(Condition condition) {
        temperView.setText(String.format("%s°", condition.temp));
        weatherView.setText(condition.text);
    }
}
