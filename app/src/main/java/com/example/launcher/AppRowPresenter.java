package com.example.launcher;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.launcher.model.AppInfo;
import com.example.launcher.ui.RowApp;
import com.example.launcher.ui.RowApp2;


/**
 * ImageCard Presenter
 *
 * @author jacky
 * @version v1.0
 * @since 16/7/16
 */
public class AppRowPresenter extends Presenter {

    private Context mContext;
    private int CARD_WIDTH = 600;
    private int CARD_HEIGHT = 300;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private Drawable mDefaultCardImage;

    private static void updateCardBackgroundColor(RowApp2 view, boolean selected) {
        int color = selected ?  sDefaultBackgroundColor  : sSelectedBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
//        view.setBackgroundColor(color);
        view.setBackgroundResource(R.drawable.appbg_tv);
//        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        sDefaultBackgroundColor =
                ContextCompat.getColor(parent.getContext(), R.color.default_background);
        sSelectedBackgroundColor =
                ContextCompat.getColor(parent.getContext(), R.color.selected_background);

        mDefaultCardImage = ContextCompat.getDrawable(parent.getContext(), R.drawable.movie);


        mContext = parent.getContext();
        RowApp2 cardView = new RowApp2(mContext) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

//        ViewGroup.LayoutParams lp = cardView.getLayoutParams();
//        lp.width = CARD_WIDTH;
//        lp.height = CARD_HEIGHT;
//        cardView.setLayoutParams(lp);
        cardView.setLayoutParams(new ViewGroup.LayoutParams(CARD_WIDTH, CARD_HEIGHT));

//        cardView.setLayoutParams(new ViewGroup.LayoutParams(CARD_WIDTH, CARD_HEIGHT));
        cardView.setFocusable(true);
        cardView.setOrientation(LinearLayout.VERTICAL);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
//        cardView.getImageview().setVisibility(View.GONE);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        RowApp2 cardView = (RowApp2) viewHolder.view;
        AppInfo appBean = (AppInfo) item;
//        cardView.getImageview().setImageDrawable(new BitmapDrawable(mContext.getResources(), appBean.iconBitmap));
        cardView.setText(appBean.title);
        cardView.setImageDrawable(new BitmapDrawable(mContext.getResources(), appBean.iconBitmap));
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        RowApp cardView = (RowApp) viewHolder.view;
//        cardView.getImageview().setImageDrawable(new BitmapDrawable(mContext.getResources(), appBean.iconBitmap));
//        cardView.getTextView().setText(appBean.title);
    }
}
