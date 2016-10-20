package com.example.username.forecast;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by username on 10/4/2016.
 */


public class ForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> _items;
    private Context _context;
    public static final int ITEM_TYPE_NORMAL = 0;
    public static final int ITEM_TYPE_HEADER = 1;
    public static final int ITEM_TYPE_CONDITION = 2;

    public ForecastAdapter(Context context, List<Object> objects) {

        _items = objects;
        _context = context;
    }

//    @NonNull
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//
//        if (convertView == null) {
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            convertView = inflater.inflate(R.layout.row_layout, parent, false);
//        }
//        TextView textMain = (TextView) convertView.findViewById(R.id.text_main);
//        TextView textDate = (TextView) convertView.findViewById(R.id.text_date);
//        TextView textDay = (TextView) convertView.findViewById(R.id.text_day);
//        TextView textLow = (TextView) convertView.findViewById(R.id.text_low);
//        TextView textHigh = (TextView) convertView.findViewById(R.id.text_high);
//
//        Forecast item = _items.get(position);
//
//        textMain.setText(item.getText());
//        textDate.setText(item.getDate());
//        textDay.setText(item.getDay());
//        textLow.setText(String.format("%s°", item.getLow()));
//        textHigh.setText(String.format("%s°", item.getHigh()));
//
//        return convertView;
//    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            View normalView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, null);
            return new ForecastHolder(normalView);
        }
        else if (viewType == ITEM_TYPE_HEADER) {
            View headView = LayoutInflater.from(getContext()).inflate(R.layout.header_layout, parent, false);
            return new HeaderViewHolder(headView);
        }
        else if (viewType == ITEM_TYPE_CONDITION) {
            View condView = LayoutInflater.from(getContext()).inflate(R.layout.condition_layout, parent, false);
            return new ConditionViewHolder(condView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_TYPE_NORMAL) {
            ((ForecastHolder)holder).BindData((Forecast)_items.get(position));
        } else if (itemType == ITEM_TYPE_HEADER) {
            ((HeaderViewHolder)holder).BindData((Location)_items.get(position));
        }
        else if (itemType == ITEM_TYPE_CONDITION) {
            ((ConditionViewHolder)holder).BindData((Condition) _items.get(position));
        }
    }



    @Override
    public int getItemViewType(int position) {
        if (_items.get(position) instanceof Forecast) {
            return ITEM_TYPE_NORMAL;
        } else if (_items.get(position) instanceof Location){
            return ITEM_TYPE_HEADER;
        }
        else if (_items.get(position) instanceof Condition){
            return ITEM_TYPE_CONDITION;
        }
        throw new InvalidParameterException("there is no view for type "+ _items.get(position).getClass().toString());
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    public Context getContext() {
        return _context;
    }
}

