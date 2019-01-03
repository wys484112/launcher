/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.launcher;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseGridView;
import android.support.v17.leanback.widget.FocusHighlightHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ItemBridgeAdapterShadowOverlayWrapper;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.ShadowOverlayHelper;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.launcher.loader.AppLoader;
import com.example.launcher.model.AppInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainAllAppsFragment extends BrowseFragment implements LoaderManager.LoaderCallbacks<List<AppInfo>>{
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;
    private ArrayObjectAdapter mRowsAdapter;

    private static final int NUM_COLS = 5;//一行显示5个应用

    static List<String> favorites = Arrays.asList("com.example.launcher/com.example.launcher.MainActivity",
            "com.android.tv.settings/com.android.tv.settings.MainSettings",
            "com.example.launcher/com.example.launcher.MainTvActivity",
            "com.xiaobaifile.tv/com.xiaobaifile.tv.view.StartupActivity"
    );
    @Override
    public Loader<List<AppInfo>> onCreateLoader(int i, Bundle bundle) {
        return new AppLoader(getActivity(), LauncherAppState.getInstance().getIconCache());
    }

    @Override
    public void onLoadFinished(Loader<List<AppInfo>> loader, List<AppInfo> appInfos) {
        appInfos = extractFavorites(appInfos);
        int size=appInfos.size();
        final int rows=size/NUM_COLS+1;

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter(){
            @Override
            public boolean isUsingDefaultShadow() {
//                return super.isUsingDefaultShadow();
                return false;

            }

            @Override
            protected ShadowOverlayHelper.Options createShadowOverlayOptions() {
                return super.createShadowOverlayOptions();
            }

            @Override
            protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
                super.initializeRowViewHolder(holder);

                final ViewHolder rowViewHolder = (ViewHolder) holder;
                Context context = holder.view.getContext();
                // set wrapper if needed
                rowViewHolder.getGridView().setNumRows(rows);
//                rowViewHolder.getGridView().setNumRows(rows);
            }
        });
        AppCardPresenter cardPresenter = new AppCardPresenter();
        Log.d("wwww", "rows=="+rows);
        Log.d("wwww", "size=="+size);

        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
        for(int i=0;i<size;i++){
            listRowAdapter.add(appInfos.get(i));
        }
//            mRowsAdapter.add(new ListRow(null, listRowAdapter));




        AppRowPresenter gridPresenter = new AppRowPresenter();
        ArrayObjectAdapter gridAdapter = new ArrayObjectAdapter(gridPresenter);
        for(int i=0;i<size;i++){
            gridAdapter.add(appInfos.get(i));
        }
        mRowsAdapter.add(new ListRow(null, gridAdapter));


//
//        int i;
//        for (i = 0; i < rows; i++) {
//
//            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
//            int start=(i)*NUM_COLS;
//            Log.d("wwww", "i==" + i);
//
//            if((i+1)==rows){
//                Log.d("wwww", "add111==");
//
//                for (int j = start; j < size; j++) {
//                    Log.d("wwww", "add111==");
//
//                    listRowAdapter.add(appInfos.get(j));
//                }
//            }else{
//                Log.d("wwww", "add22==");
//
//                for (int j = 0; j < NUM_COLS; j++) {
//                    Log.d("wwww", "add22==");
//
//                    listRowAdapter.add(appInfos.get(start+j));
//                }
//            }
//                HeaderItem header = new HeaderItem(0, "afsdf");
//
//            mRowsAdapter.add(new ListRow(null, listRowAdapter));
//
//        }




        setAdapter(mRowsAdapter);

    }

    @Override
    public void onLoaderReset(Loader<List<AppInfo>> loader) {
        mRowsAdapter.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareBackgroundManager();

        setupUIElements();

        setupEventListeners();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);


        LauncherAppState.setApplicationContext(getActivity());

        if (savedInstanceState == null) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().restartLoader(0, null, this);
        }

    }


//    /*
//    * 去掉Title显示
//    * */
//    @Override
//    public void installTitleView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
//        super.installTitleView(inflater, parent, savedInstanceState);
//    }
//
//    @Override
//    public View onInflateTitleView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
//        return null;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }
    private List<AppInfo> extractFavorites(List<AppInfo> infos) {
        List<AppInfo> favs = new ArrayList<>(favorites.size());
        for (String name : favorites) {
            for (AppInfo info : infos) {
                Log.d("wwww", "com==" + info.componentName.toString());
                if (name.contains(info.componentName.getClassName())) {
                    Log.d("wwww", "title==" + info.componentName);
                    favs.add(info);
                    infos.remove(info);
                    break;
                }
            }
        }
        return infos;
    }


    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
//        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        showTitle(false);
        setTitleView(null);
//        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_DISABLED);
//        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        // set search icon color
//        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));

    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                        .show();
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof AppInfo) {
                AppInfo appinfo = (AppInfo) item;
                Log.d(TAG, "Item: " + appinfo.intent);
                getActivity().startActivity(appinfo.intent);

            } else if (item instanceof String) {
                if (((String) item).contains(getString(R.string.error_fragment))) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {
            if (item instanceof Movie) {
                mBackgroundUri = ((Movie) item).getBackgroundImageUrl();
//                startBackgroundTimer();
            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(mBackgroundUri);
                }
            });
        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(
                    ContextCompat.getColor(getActivity(), R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            AppInfo appBean = (AppInfo) item;
            ((TextView) viewHolder.view).setText((String) appBean.title.toString());
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

}
