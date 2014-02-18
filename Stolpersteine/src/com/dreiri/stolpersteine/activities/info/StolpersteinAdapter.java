package com.dreiri.stolpersteine.activities.info;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dreiri.stolpersteine.R;
import com.dreiri.stolpersteine.api.model.Stolperstein;

public class StolpersteinAdapter extends BaseAdapter {
    
    private LayoutInflater inflater;
    private ArrayList<Stolperstein> stolpersteine;
    private ViewHolder viewHolder;
    
    private class ViewHolder {
        TextView textViewName;
        TextView textViewAddress;
    }
    
    public StolpersteinAdapter(Context context, ArrayList<Stolperstein> stolpersteine) {
        this.inflater = LayoutInflater.from(context);
        this.stolpersteine = stolpersteine;
    }

    @Override
    public int getCount() {
        return stolpersteine.size();
    }

    @Override
    public Object getItem(int position) {
        return stolpersteine.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_info, null);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.textViewAddress = (TextView) convertView.findViewById(R.id.textViewAddress);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Stolperstein stolperstein = stolpersteine.get(position);
        viewHolder.textViewName.setText(stolperstein.getPerson().getNameAsString());
        viewHolder.textViewAddress.setText(stolperstein.getLocation().getAddressAsString());
        return convertView;
    }

}
