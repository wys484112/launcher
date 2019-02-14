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
import android.content.Loader;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.ShadowOverlayHelper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.launcher.loader.AppLoader;
import com.example.launcher.model.AppInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainAllAppsFragment extends BrowseFragment implements LoaderManager.LoaderCallbacks<List<AppInfo>> {
    private static final String TAG = "MainTvActivity";
    private static final boolean DEBUG = true;

    private ArrayObjectAdapter mRowsAdapter;

    private static final int NUM_COLS = 5;//一行显示5个应用

    static List<String> favorites = Arrays.asList(
            "com.example.launcher/com.example.launcher.MainTvActivity"
    );

    @Override
    public Loader<List<AppInfo>> onCreateLoader(int i, Bundle bundle) {
        if (DEBUG)
            Log.d(TAG, "onCreateLoader==");
        return new AppLoader(getActivity(), LauncherAppState.getInstance().getIconCache());
    }

    @Override
    public void onLoadFinished(Loader<List<AppInfo>> loader, List<AppInfo> appInfos) {
        if (DEBUG)
            Log.d(TAG, "onLoadFinished==");
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter() {
            @Override
            public boolean isUsingDefaultShadow() {
                return false;
            }

            @Override
            protected void onSelectLevelChanged(RowPresenter.ViewHolder holder) {
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
//            if (DEBUG)
//                Log.d(TAG, "i==" + i);

            if ((i + 1) == rows) {
//                if (DEBUG)
//                    Log.d(TAG, "add111==");

                for (int j = start; j < size; j++) {
//                    if (DEBUG)
//                        Log.d(TAG, "add111==");

                    mlistRowAdapter.add(appInfos.get(j));
                }
            } else {
//                if (DEBUG)
//                    Log.d(TAG, "add22==");
                for (int j = 0; j < NUM_COLS; j++) {
//                    if (DEBUG)
//                        Log.d(TAG, "add22==");
                    mlistRowAdapter.add(appInfos.get(start + j));
                }
            }
            mRowsAdapter.add(new ListRow(null, mlistRowAdapter));
        }
        setAdapter(mRowsAdapter);

    }

    @Override
    public void onLoaderReset(Loader<List<AppInfo>> loader) {
        if (DEBUG)
            Log.d(TAG, "onLoaderReset==");
        mRowsAdapter.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUIElements();
        setupEventListeners();


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LauncherAppState.setApplicationContext(getActivity());
        if (DEBUG)
            Log.d(TAG, "onCreate==");

        if (savedInstanceState == null) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (DEBUG)
            Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG)
            Log.d(TAG, "onStart==");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG)
            Log.d(TAG, "onPause==");

//        getLoaderManager().restartLoader(0, null, this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG)
            Log.d(TAG, "onResume==");


    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG)
            Log.d(TAG, "onStop==");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private void setupUIElements() {
        setHeadersState(HEADERS_DISABLED);
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));

    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
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
        }
    }
}
