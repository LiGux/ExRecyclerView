package com.ligux.lib.widget.exrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

/**
 * Created by Administrator on 2016/4/20.
 */


/**
 * Extend GridLayoutManager
 */
public class ExtendGridLayoutManager extends GridLayoutManager {
    private ExtendRecyclerView.AutoLoadAdapter mAdapter;

    private SpanSizeLookup mSpanSizeLookup = new SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            int type = mAdapter.getItemViewType(position);
            if (type == ExtendRecyclerView.TYPE_PULL_TO_REFRESH ||
                    type == ExtendRecyclerView.TYPE_HEADER ||
                    type == ExtendRecyclerView.TYPE_FOOTER ||
                    type == ExtendRecyclerView.TYPE_LOADMORE ||
                    type == ExtendRecyclerView.TYPE_LOADMORE_ERROR) {
                return getSpanCount();
            } else {
                return 1;
            }
        }
    };

    //    public ExGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
    public void setAdapter(@NonNull ExtendRecyclerView.AutoLoadAdapter adapter){
        mAdapter = adapter;
    }

    public ExtendGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        setSpanSizeLookup(mSpanSizeLookup);
    }

    public ExtendGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        //mAdapter = adapter;
        setSpanSizeLookup(mSpanSizeLookup);
    }
}

