package com.ligux.lib.widget.exrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

/**
 * Version 1.0
 * <p>
 * Date: 2016-04-01 10:13
 * Author: flzyup@ligux.com
 * <p>
 * Copyright © 2010-2016 LiGux.com
 */

/**
 * Extend GridLayoutManager
 */
public class ExGridLayoutManager extends GridLayoutManager {
    private ExRecyclerView.ExAdapter mAdapter;

    private SpanSizeLookup mSpanSizeLookup = new SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            int type = mAdapter.getItemViewType(position);
            if (type == ExRecyclerView.ExAdapter.VIEW_TYPE.HEADER.value() ||
                    type == ExRecyclerView.ExAdapter.VIEW_TYPE.FOOTER.value() ||
                    type == ExRecyclerView.ExAdapter.VIEW_TYPE.LOAD_MORE.value()) {
                return getSpanCount();
            } else {
                return 1;
            }
        }
    };

    //    public ExGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public ExGridLayoutManager(Context context, int spanCount, @NonNull ExRecyclerView.ExAdapter adapter) {
        super(context, spanCount);
        mAdapter = adapter;
        setSpanSizeLookup(mSpanSizeLookup);
    }

    public ExGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout, @NonNull ExRecyclerView.ExAdapter adapter) {
        super(context, spanCount, orientation, reverseLayout);
        mAdapter = adapter;
        setSpanSizeLookup(mSpanSizeLookup);
    }
}
