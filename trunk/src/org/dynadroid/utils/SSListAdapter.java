package org.dynadroid.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;


public abstract class SSListAdapter extends ArrayAdapter {

    private int layout;
    private List objects;
    private boolean reuseViews = true;

    public SSListAdapter(Context context, int layout, List objects) {
        this(context, layout, objects, true);
    }

    public SSListAdapter(Context context, int layout, List objects, boolean reuseViews) {
        super(context, layout, objects);
        this.objects = objects;
        this.layout = layout;
        this.reuseViews = reuseViews;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        //Utils.debug("***convertView = "+ convertView + " at "+position);
        View v = convertView;
        if (v == null || !reuseViews) {
            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(layout, null);
        }
        if (position < objects.size()) {
            Object o = objects.get(position);
            bind(v,o, position);
        }
        return v;
    }

    @Override
    public boolean isEnabled(int position) {
        return isEnabled(objects.get(position));
    }

    public boolean isEnabled(Object data) {
       return !(data instanceof String);
    }

    public void clear(){
        objects.clear();
    }
    public abstract void bind(View view, Object data, int position);
}
