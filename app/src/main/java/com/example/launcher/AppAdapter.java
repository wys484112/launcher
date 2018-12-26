package com.example.launcher;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.launcher.loader.AppLoader;
import com.example.launcher.model.AppInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AppAdapter extends ArrayAdapter<AppInfo> {

    private LayoutInflater mLayoutInflater;
    private Resources mResources;

    public AppAdapter(Context context, List<AppInfo> objects) {
        super(context, 0, objects);
        mLayoutInflater = LayoutInflater.from(context);
        mResources = context.getResources();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mLayoutInflater.inflate(R.layout.grid_app, parent, false);
            holder = new ViewHolder();

            holder.image = (ImageView) v.findViewById(R.id.image);
            holder.title = (TextView) v.findViewById(R.id.title);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        final AppInfo info = getItem(position);

        holder.image.setImageDrawable(new BitmapDrawable(mResources, info.iconBitmap));
        holder.title.setText(info.title);

        return v;
    }

    class ViewHolder {
        ImageView image;
        TextView title;
    }
}
