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

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
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
import android.util.AttributeSet;
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

public class MainAllAppsFragment extends BrowseFragment implements LoaderManager.LoaderCallbacks<List<AppInfo>> {
    private static final String TAG = "MainTvActivity";
    private static final boolean DEBUG = true;

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
    private ProgressDialog progressDialog;

    static List<String> favorites = Arrays.asList("com.example.launcher/com.example.launcher.MainActivity",
            "com.example.launcher/com.example.launcher.MainTvActivity",
            "com.example.launcher/com.example.launcher.MainTableLayoutTvActivity",
            "com.example.launcher/com.example.launcher.MainTableLayoutTvActivityxmlHorizontalScrollView",
            "com.example.launcher/com.example.launcher.MainTableLayoutTvActivityxml",
            "com.example.launcher/com.example.launcher.MainTvActivityTableLayoutStatusBar",
            "com.example.launcher/com.example.launcher.MainTvActivityTableLayout"

    );

    @Override
    public Loader<List<AppInfo>> onCreateLoader(int i, Bundle bundle) {
        if (DEBUG)
            Log.d(TAG, "onCreateLoader==");

        showProgressDialog();

        return new AppLoader(getActivity(), LauncherAppState.getInstance().getIconCache());
    }

    public void showProgressDialog() {
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.show();
    }

    public void dismissProgressDialog() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
    }

    @Override
    public void onLoadFinished(Loader<List<AppInfo>> loader, List<AppInfo> appInfos) {
        if (DEBUG)
            Log.d(TAG, "onLoadFinished==");

        dismissProgressDialog();


        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter() {
            @Override
            public boolean isUsingDefaultShadow() {
//                return super.isUsingDefaultShadow();
                return false;
            }

            @Override
            protected void onSelectLevelChanged(RowPresenter.ViewHolder holder) {
//                super.onSelectLevelChanged(holder);
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
//                rowViewHolder.getGridView().setNumRows(rows);
//                rowViewHolder.getGridView().setNumRows(rows);
            }
        });
        appInfos = extractFavorites(appInfos);
        int size = appInfos.size();
        final int rows = size / NUM_COLS + 1;
        int i;
        AppRowPresenter mgridPresenter = new AppRowPresenter();
        for (i = 0; i < rows; i++) {

            ArrayObjectAdapter mlistRowAdapter = new ArrayObjectAdapter(mgridPresenter);
            int start = (i) * NUM_COLS;
            if (DEBUG)
                Log.d(TAG, "i==" + i);

            if ((i + 1) == rows) {
                if (DEBUG)
                    Log.d(TAG, "add111==");

                for (int j = start; j < size; j++) {
                    if (DEBUG)
                        Log.d(TAG, "add111==");

                    mlistRowAdapter.add(appInfos.get(j));
                }
            } else {
                if (DEBUG)
                    Log.d(TAG, "add22==");

                for (int j = 0; j < NUM_COLS; j++) {
                    if (DEBUG)
                        Log.d(TAG, "add22==");

                    mlistRowAdapter.add(appInfos.get(start + j));
                }
            }
            HeaderItem header = new HeaderItem(0, "afsdf");

            mRowsAdapter.add(new ListRow(null, mlistRowAdapter));

        }


        setAdapter(mRowsAdapter);

    }

    public MainAllAppsFragment() {
        super();
        if (DEBUG)
            Log.d(TAG, "MainAllAppsFragment==");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (DEBUG)
            Log.d(TAG, "onActivityResult==");
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        if (DEBUG)
            Log.d(TAG, "onInflate==");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if (DEBUG)
            Log.d(TAG, "onAttachFragment==");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (DEBUG)
            Log.d(TAG, "onAttach==");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG)
            Log.d(TAG, "onStop==");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (DEBUG)
            Log.d(TAG, "onDetach==");
    }

    @Override
    public void onLoaderReset(Loader<List<AppInfo>> loader) {
        if (DEBUG)
            Log.d(TAG, "onLoaderReset==");

        dismissProgressDialog();
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (DEBUG)
            Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);


        LauncherAppState.setApplicationContext(getActivity());
        if (DEBUG)
            Log.d(TAG, "restartLoader==");

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
            if (DEBUG)
                Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
        dismissProgressDialog();
    }

    private List<AppInfo> extractFavorites(List<AppInfo> infos) {
        List<AppInfo> favs = new ArrayList<>(favorites.size());
        for (String name : favorites) {
            for (AppInfo info : infos) {
                if (DEBUG)
                    Log.d(TAG, "com==" + info.componentName.toString());
                if (name.contains(info.componentName.getClassName())) {
                    if (DEBUG)
                        Log.d(TAG, "title==" + info.componentName);
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
        if (!mBackgroundManager.isAttached()) {
            mBackgroundManager.attach(getActivity().getWindow());
        }

        mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
//        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
//        showTitle(true);
//        setTitleView(null);
//        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_DISABLED);
//        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));

    }

    private void setupEventListeners() {
//        setOnSearchClickedListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
//                        .show();
//            }
//        });

        //        titleview.java determine if show search view
//        private void updateSearchOrbViewVisiblity() {
//            int visibility = mHasSearchListener && (flags & SEARCH_VIEW_VISIBLE) == SEARCH_VIEW_VISIBLE
//                    ? View.VISIBLE : View.INVISIBLE;
//            mSearchOrbView.setVisibility(visibility);
//        }

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
                if (DEBUG)
                    Log.d(TAG, "Item: " + appinfo.intent);
                getActivity().startActivity(appinfo.intent);
//                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

//                getActivity().overridePendingTransition(R.anim.system_ui_start,R.anim.system_ui_close);


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
