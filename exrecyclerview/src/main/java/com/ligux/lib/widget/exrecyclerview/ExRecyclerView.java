package com.ligux.lib.widget.exrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Version 1.0
 * <p>
 * Date: 2016-03-31 17:15
 * Author: flzyup@ligux.com
 * <p>
 * Copyright © 2010-2016 LiGux.com
 */

/**
 * Extended RecyclerView which support the following features:
 * 1. load more
 * 2. header
 * 3. footer
 */
public class ExRecyclerView extends RecyclerView implements IEx {
    private LayoutInflater mInflater;
    private ExAdapter mAdapter;

    private View mLoadMoreView;
    private OnLoadMoreListener mLoadMoreListener;
    private boolean mEnableHeader, mEnableFooter;

    public ExRecyclerView(Context context) {
        this(context, null);
    }

    public ExRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Set adapter for the {@link ExRecyclerView}
     *
     * @param adapter
     */
    public void setAdapter(ExAdapter adapter) {
        super.setAdapter(adapter);
        adapter.enableHeader(mEnableHeader)
                .enableFooter(mEnableFooter)
                .setLoadMoreView(mLoadMoreView);
    }

    @Override
    @Deprecated
    public void setAdapter(Adapter adapter) {
        throw new IllegalArgumentException("Please use setAdapter(ExAdapter adapter) instead!");
    }


    private void checkAdapter() {
        if (mAdapter != null) {
            throw new IllegalArgumentException("addHeader(), addFooter(), setLoadMore() method should be called before setAdapter()");
        }
    }

    @Override
    public void enableHeader(boolean enabled) {
        checkAdapter();
        mEnableHeader = enabled;
    }

    @Override
    public void enableFooter(boolean enabled) {
        checkAdapter();
        mEnableFooter = enabled;
    }

    @Override
    public void setLoadMore(int viewRes, OnLoadMoreListener listener) {
        checkAdapter();
        View view = mInflater.inflate(viewRes, null);
        setLoadMore(view, listener);
    }

    @Override
    public void setLoadMore(View view, OnLoadMoreListener listener) {
        checkAdapter();
        mLoadMoreView = view;
        mLoadMoreListener = listener;
    }

    /**
     * Extended adapter
     *
     * @param <VH>
     */
    public static abstract class ExAdapter<DT, VH extends ViewHolder> extends Adapter<VH> {
        private static final int ID_HEADER = Integer.MIN_VALUE;
        private static final int ID_FOOTER = ID_HEADER + 1;
        private static final int ID_LOAD_MORE = ID_FOOTER + 1;

//        private LayoutManager mLayoutManager;
        private int mPreOffset = 0, mPostOffset = 0;
        private View mLoadMoreView;
        private boolean mEnableHeader;
        private boolean mEnableFooter;
        private boolean mEnableLoadMore;
        private List<DT> mData;

        public ExAdapter(/*LayoutManager layoutManager, */@NonNull List<DT> data) {
            super();
//            if (!(layoutManager instanceof ExGridLayoutManager) ||
//                    !(layoutManager instanceof LinearLayoutManager)) {
//                //TODO: add StaggedGridLayoutManager
//                throw new IllegalArgumentException("LayoutManager instance not illegal");
//            }
//            mLayoutManager = layoutManager;
            mData = data;
        }

        private ExAdapter enableHeader(boolean enabled) {
            mEnableHeader = enabled;
            if (mEnableHeader) {
                mPreOffset++;
            }
            return this;
        }

        private ExAdapter enableFooter(boolean enabled) {
            mEnableFooter = enabled;
            if (mEnableFooter) {
                mPostOffset++;
            }
            return this;
        }

        private ExAdapter setLoadMoreView(View view) {
            mEnableLoadMore = mLoadMoreView != null;
            if (mEnableLoadMore) {
                mLoadMoreView = view;
                mPostOffset++;
            }
            return this;
        }

        @Override
        public long getItemId(int position) {
            int viewType = getItemViewType(position);
            if (viewType == VIEW_TYPE.NORMAL.value()) {
                return position - mPreOffset - mPostOffset;
            } else if (viewType == VIEW_TYPE.HEADER.value()) {
                return ID_HEADER;
            } else if (viewType == VIEW_TYPE.FOOTER.value()) {
                return ID_FOOTER;
            } else if (viewType == VIEW_TYPE.LOAD_MORE.value()) {
                return ID_LOAD_MORE;
            }
            throw new IllegalArgumentException(String.format("can't recognize this viewType = %d", viewType));
        }

        @Override
        public int getItemCount() {
            return mData.size() + mPreOffset + mPostOffset;
        }


        @Override
        public int getItemViewType(int position) {
            if (position == 0 && mEnableHeader) {
                return VIEW_TYPE.HEADER.value();
            } else if (position == getItemCount() - 1) {
                if (mEnableLoadMore) {
                    return VIEW_TYPE.LOAD_MORE.value();
                } else if (mEnableFooter) {
                    return VIEW_TYPE.FOOTER.value();
                }
            } else if (position == getItemCount() - 2) {
                if (mEnableFooter && mEnableLoadMore) {
                    return VIEW_TYPE.FOOTER.value();
                }
            }

            return VIEW_TYPE.NORMAL.value();
        }



        @Override
        public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE.NORMAL.value()) {
                return onCreateNormalViewHolder(parent);
            } else if (viewType == VIEW_TYPE.HEADER.value()) {
                return onCreateHeaderViewHolder(parent);
            } else if (viewType == VIEW_TYPE.FOOTER.value()) {
                return onCreateFooterViewHolder(parent);
            } else if (viewType == VIEW_TYPE.LOAD_MORE.value()) {
                return onCreateLoadMoreViewHolder(parent);
            }
            throw new IllegalArgumentException("viewType not recognized!");
        }

        @Override
        public final void onBindViewHolder(VH holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType == VIEW_TYPE.NORMAL.value()) {
                int realPosition = position - mPreOffset;
                Log.d("TAG", "real position:" + realPosition +" position: " + position + " pre offset:" + mPreOffset + " post offset: " + mPostOffset);
                onBindNormalView(holder, mData.get(realPosition), realPosition);
            } else if (viewType == VIEW_TYPE.HEADER.value()) {
                onBindHeaderView(holder);
            } else if (viewType == VIEW_TYPE.FOOTER.value()) {
                onBindFooterView(holder);
            }
        }

        protected abstract VH onCreateHeaderViewHolder(ViewGroup parent);

        protected abstract VH onCreateFooterViewHolder(ViewGroup parent);

        protected abstract VH onCreateNormalViewHolder(ViewGroup parent);

        protected abstract VH onCreateLoadMoreViewHolder(ViewGroup parent);

        protected abstract void onBindHeaderView(VH holder);

        protected abstract void onBindFooterView(VH holder);

        protected abstract void onBindNormalView(VH holder, DT data, int position);

        /**
         * This enum indicate the item type
         */
        public enum VIEW_TYPE {
            NORMAL, // normal data item
            HEADER, // header
            FOOTER,  // footer
            LOAD_MORE; // load more

            public int value() {
                if (this == NORMAL) {
                    return 0;
                } else if (this == HEADER) {
                    return 1;
                } else if (this == FOOTER) {
                    return 2;
                } else if (this == LOAD_MORE) {
                    return 3;
                } else {
                    return -1;
                }
            }
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
