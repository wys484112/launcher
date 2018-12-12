package com.example.launcher;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Activity;
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

public class AllAppList extends Activity implements LoaderManager.LoaderCallbacks<List<AppInfo>> {

    private GridView mAppsGrid;
    private AppAdapter mAdapter;
    static List<String> favorites = Arrays.asList("launcher");

    @Override
    public Loader<List<AppInfo>> onCreateLoader(int i, Bundle bundle) {
        return new AppLoader(this, LauncherAppState.getInstance().getIconCache());
    }

    @Override
    public void onLoadFinished(Loader<List<AppInfo>> loader, List<AppInfo> appInfos) {
        appInfos = extractFavorites(appInfos);
        mAdapter = new AppAdapter(this, appInfos);
        mAppsGrid.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<AppInfo>> loader) {
        mAppsGrid.setAdapter(null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        LauncherAppState.setApplicationContext(this);
        mAppsGrid = (GridView) findViewById(R.id.app_grid);
        mAppsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final AppInfo info = mAdapter.getItem(i);
                Log.d("wwww","info.intent=="+info.intent);
                startActivity(info.intent);
            }
        });
        if (savedInstanceState == null) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    private List<AppInfo> extractFavorites(List<AppInfo> infos) {
        List<AppInfo> favs = new ArrayList<>(favorites.size());
        for (String name : favorites) {
            for (AppInfo info : infos) {
                if (name.equals(info.title)) {
                    Log.d("wwww","title=="+info.title);
                    favs.add(info);
                    infos.remove(info);
                    break;
                }
            }
        }
//
//        mFavorites.removeAllViews();
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        for (AppInfo info : favs) {
//            View v = inflater.inflate(R.layout.row_app, mFavorites, false);
//            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
//            v.setLayoutParams(params);
//
//            ImageView image = (ImageView) v.findViewById(R.id.image);
//            TextView title = (TextView) v.findViewById(R.id.title);
//            image.setImageDrawable(new BitmapDrawable(getResources(), info.iconBitmap));
//            title.setText(info.title);
//
//            final Intent appIntent = info.intent;
//            v.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(appIntent);
//                }
//            });
//
//            mFavorites.addView(v);
//        }

        return infos;
    }

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
}
