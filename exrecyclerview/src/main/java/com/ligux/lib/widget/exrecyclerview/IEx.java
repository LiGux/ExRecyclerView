package com.ligux.lib.widget.exrecyclerview;

import android.view.View;

/**
 * Version 1.0
 * <p/>
 * Date: 2016-03-31 18:27
 * Author: flzyup@ligux.com
 * <p/>
 * Copyright © 2010-2016 LiGux.com
 */
public interface IEx {
    void enableHeader(boolean enabled);
    void enableFooter(boolean enabled);
    void setLoadMore(int viewRes, ExRecyclerView.OnLoadMoreListener listener);
    void setLoadMore(View view, ExRecyclerView.OnLoadMoreListener listener);
}
