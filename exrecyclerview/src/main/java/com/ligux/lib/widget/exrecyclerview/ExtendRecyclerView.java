package com.ligux.lib.widget.exrecyclerview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.util.List;

public class ExtendRecyclerView extends RecyclerView {
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_PULL_TO_REFRESH = 1;
    public final static int TYPE_HEADER = 2;//头部--支持头部增加一个headerView
    public final static int TYPE_FOOTER = 3;//底部--往往是loading_more
    public final static int TYPE_LOADMORE = 4;
    public final static int TYPE_LOADMORE_ERROR = 5;

    private boolean mIsLoadMoreEn = false;//是否允许加载更多
    private boolean mIsLoadMoreErrEn = false;
    private boolean mIsFooterEn = false;
    private AutoLoadAdapter mAutoLoadAdapter;

    private boolean mIsLoadingMore;
    private int mLoadMorePosition;
    private LoadMoreListener mListener;

    //刷新
    private float mLastY = -1;
    private static final float DRAG_RATE = 3;
    private boolean mPullRefreshEnabled = true;
    //private RefreshHeader mTouchDwnRefreshHeader = null;
    private RefreshHeader mSavedRefreshHeader;
    //private boolean mBuildSuccessFlag = false;

    public ExtendRecyclerView(Context context) {
        super(context);
        init();
    }

    public ExtendRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExtendRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化-添加滚动监听
     * <p/>
     * 回调加载更多方法，前提是
     * <pre>
     *    1、有监听并且支持加载更多：null != mListener && mIsLoadMoreEn
     *    2、目前没有在加载，正在上拉（dy>0），当前最后一条可见的view是否是当前数据列表的最好一条--及加载更多
     * </pre>
     */
    private void init() {
        super.addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (null != mListener && !mIsLoadingMore && dy > 0) {
                    if (mIsLoadMoreErrEn) {
                        mIsLoadMoreErrEn = false;
                        mIsLoadMoreEn = true;
                    }
                    if (mIsLoadMoreEn) {
                        int lastVisiblePosition = getLastVisiblePosition();
                        if (lastVisiblePosition + 1 == getAdapter().getItemCount()) {
                            setLoadingMore(true);
                            mLoadMorePosition = lastVisiblePosition;
                            mListener.onLoadMore();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        RefreshHeader refreshHeader = (RefreshHeader) getLayoutManager().findViewByPosition(0);

        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();

                if (mPullRefreshEnabled && refreshHeader != null) {
                    refreshHeader.onMove(deltaY / DRAG_RATE);
                    if (refreshHeader.getVisibleHeight() > refreshHeader.getMinVisiableHeight() && refreshHeader.getState() < RefreshHeader.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1;
                if (mPullRefreshEnabled && refreshHeader != null) {
                    if (refreshHeader.releaseAction()) {
                        mSavedRefreshHeader = refreshHeader;
                        if (mListener != null) {
                            mListener.onRefresh();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setLoadMoreListener(LoadMoreListener listener) {
        mListener = listener;
    }

    public interface LoadMoreListener {
        void onLoadMore();

        void onRefresh();
    }

//    @Override
//    @Deprecated
//    public void setAdapter(Adapter adapter) {
//        throw new IllegalArgumentException("Please use setAdapter(AutoLoadAdapter adapter) instead!");
//    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAutoLoadAdapter = (AutoLoadAdapter) adapter;
        super.setAdapter(mAutoLoadAdapter);
        reset();
    }

    private int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    private void reset() {
//        mIsLoadMoreEn = true;
//        mAutoLoadAdapter.setLoadMoreEn(true);
//        mIsFooterEn = false;
//        mAutoLoadAdapter.setFooterEn(false);
//        mIsLoadMoreErrEn = false;
//        mAutoLoadAdapter.setLoadMoreErrEn(false);
//        setLoadingMore(false);

        mPullRefreshEnabled = mAutoLoadAdapter.getPullToRefreshEn();
        mIsLoadMoreEn = mAutoLoadAdapter.getLoadMoreEn();
        mIsFooterEn = mAutoLoadAdapter.getFooterEn();
        mIsLoadMoreErrEn = mAutoLoadAdapter.getLoadMoreErrEn();
        setLoadingMore(false);
    }

    private void setAutoLoadMoreEnable(boolean autoLoadMore) {
        mIsLoadMoreEn = autoLoadMore;
        mAutoLoadAdapter.setLoadMoreEn(mIsLoadMoreEn);
        mIsFooterEn = !mIsLoadMoreEn;
        mAutoLoadAdapter.setFooterEn(mIsFooterEn);
        mIsLoadMoreErrEn = false;
        mAutoLoadAdapter.setLoadMoreErrEn(false);
    }

    private void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
    }

    /**
     * 通知更多的数据已经加载
     * 每次加载完成之后添加了Data数据，用notifyItemRemoved来刷新列表展示，
     * 而不是用notifyDataSetChanged来刷新列表
     */
    public void notifyLoadMoreFinish(boolean hasMore) {
        setAutoLoadMoreEnable(hasMore);
        mAutoLoadAdapter.notifyItemRemoved(mLoadMorePosition);
        mIsLoadingMore = false;
    }

    public void notifyRefreshFinish(boolean hasMore) {
        if (mSavedRefreshHeader != null)
            mSavedRefreshHeader.refreshComplete();
        setAutoLoadMoreEnable(hasMore);
        mAutoLoadAdapter.notifyDataSetChanged();
        mIsLoadingMore = false;
    }

    public void notifyLoadMoreError() {
        mIsLoadMoreEn = false;
        mAutoLoadAdapter.setLoadMoreEn(false);
        mIsFooterEn = false;
        mAutoLoadAdapter.setFooterEn(false);
        mIsLoadMoreErrEn = true;
        mAutoLoadAdapter.setLoadMoreErrEn(true);
        mAutoLoadAdapter.notifyItemRemoved(mLoadMorePosition);
        mIsLoadingMore = false;
    }

    public void notifyRefreshError() {
        if (mSavedRefreshHeader != null)
            mSavedRefreshHeader.refreshFail();
    }

//    private void setBuildPullToRefreshFlag(boolean bool) {
//        mBuildSuccessFlag = bool;
//    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing && mPullRefreshEnabled && mListener != null) {
            scrollToPosition(0);
            //mBuildSuccessFlag = false;

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    RefreshHeader mRefreshHeader = (RefreshHeader) getLayoutManager().findViewByPosition(0);
                    if (mRefreshHeader != null) {
                        //setBuildPullToRefreshFlag(true);
                        //LogUtils.e("setRefreshing", "mRefreshHeader != null");
                        mSavedRefreshHeader = mRefreshHeader;
                        mRefreshHeader.setState(RefreshHeader.STATE_REFRESHING);
//                        mRefreshHeader.onMove(mRefreshHeader.getOriginalHeight());
                        mRefreshHeader.smoothScrollTo(mRefreshHeader.getOriginalHeight());
                        mListener.onRefresh();
                    } else {
                        mListener.onRefresh();
                    }
                }
            }, 100);
        }
    }

    public static abstract class AutoLoadAdapter<DT, VH extends ViewHolder> extends Adapter<VH> {

        private boolean mHasPullToRefreshView = true;
        private boolean mIsPullToRefreshEnable;
        private boolean mIsHeaderEnable;
        private boolean mIsFooterEn;
        private boolean mIsLoadMoreEn;
        private boolean mIsLoadMoreErrEn;
        private List<DT> mDataset;

        public AutoLoadAdapter(@NonNull List<DT> Dataset) {
            mIsHeaderEnable = false;
            mDataset = Dataset;
            reset();
        }

        public void addData(List<DT> dataIn) {
            if (dataIn != null)
                mDataset.addAll(dataIn);
        }

        public void clearData() {
            if (mDataset != null)
                mDataset.clear();
        }

        public boolean IsEmpty() {
            return !(mDataset != null && mDataset.size() > 0);
        }


        @Override
        public int getItemViewType(int position) {
            int headerPosition = 0;
            int footerPosition = getItemCount() - 1;

            if (mHasPullToRefreshView) {
                if (headerPosition == position)
                    return TYPE_PULL_TO_REFRESH;
                else if (headerPosition + 1 == position && mIsHeaderEnable)
                    return TYPE_HEADER;
            } else {
                if (headerPosition == position && mIsHeaderEnable) {
                    return TYPE_HEADER;
                }
            }

            if (footerPosition == position) {
                if (mIsFooterEn)
                    return TYPE_FOOTER;
                else if (mIsLoadMoreEn)
                    return TYPE_LOADMORE;
                else if (mIsLoadMoreErrEn)
                    return TYPE_LOADMORE_ERROR;
            }
            return TYPE_NORMAL;
        }

        @Override
        public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_PULL_TO_REFRESH) {
                return onCreatePullToRefreshViewHolder(parent);
            } else if (viewType == TYPE_HEADER) {
                return onCreateHeaderViewHolder(parent);
            } else if (viewType == TYPE_FOOTER) {
                return onCreateFooterViewHolder(parent);
            } else if (viewType == TYPE_LOADMORE) {
                return onCreateLoadMoreViewHolder(parent);
            } else if (viewType == TYPE_LOADMORE_ERROR) {
                return onCreateLoadMoreErrViewHolder(parent);
            } else { // type normal
                return onCreateNormalViewHolder(parent);
            }
        }

        protected abstract VH onCreatePullToRefreshViewHolder(ViewGroup parent);

        protected abstract VH onCreateHeaderViewHolder(ViewGroup parent);

        protected abstract VH onCreateFooterViewHolder(ViewGroup parent);

        protected abstract VH onCreateNormalViewHolder(ViewGroup parent);

        protected abstract VH onCreateLoadMoreViewHolder(ViewGroup parent);

        protected abstract VH onCreateLoadMoreErrViewHolder(ViewGroup parent);


        @Override
        public void onBindViewHolder(VH holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType == TYPE_PULL_TO_REFRESH) {
                onBindHeaderView(holder);
            } else if (viewType == TYPE_HEADER) {
                onBindHeaderView(holder);
            } else if (viewType == TYPE_FOOTER) {
                onBindFooterView(holder);
            } else if (viewType == TYPE_LOADMORE) {
                onBindLoadMoreView(holder);
            } else if (viewType == TYPE_LOADMORE_ERROR) {
                onBindLoadMoreErrView(holder);
            } else { // type normal
                int realPosition = position - (mIsHeaderEnable ? 1 : 0) - (mHasPullToRefreshView ? 1 : 0);
                onBindNormalView(holder, mDataset.get(realPosition), realPosition);
            }
        }

        protected abstract void onBindPullToRefreshView(VH holder);

        protected abstract void onBindHeaderView(VH holder);

        protected abstract void onBindFooterView(VH holder);

        protected abstract void onBindNormalView(VH holder, DT data, final int position);

        protected abstract void onBindLoadMoreView(VH holder);

        protected abstract void onBindLoadMoreErrView(VH holder);

        /**
         * 需要计算上加载更多和添加的头部俩个
         *
         * @return
         */
        @Override
        public int getItemCount() {
            int count = (mDataset == null ? -1 : mDataset.size());//mInternalAdapter.getItemCount();
            if (count <= 0) {//没有数据时不显示
                mIsFooterEn = false;
                mIsLoadMoreEn = false;
                mIsLoadMoreErrEn = false;
            }
            if (mHasPullToRefreshView) count++;
            if (mIsLoadMoreEn) count++;
            if (mIsLoadMoreErrEn) count++;
            if (mIsHeaderEnable) count++;
            if (mIsFooterEn) count++;
            return count;
        }

        //默认配置
        private void reset() {
            setPullToRefreshEn(true);
            setHeaderEnable(false);
            setFooterEn(false);
            setLoadMoreEn(true);
            setLoadMoreErrEn(false);
        }

        public void setPullToRefreshEn(boolean enable) {
            mIsPullToRefreshEnable = enable;
        }

        public void setHeaderEnable(boolean enable) {
            mIsHeaderEnable = enable;
        }

        public void setFooterEn(boolean enable) {
            mIsFooterEn = enable;
        }

        public void setLoadMoreEn(boolean enable) {
            mIsLoadMoreEn = enable;
        }

        public void setLoadMoreErrEn(boolean enable) {
            mIsLoadMoreErrEn = enable;
        }

        public boolean getPullToRefreshEn() {
            return mIsPullToRefreshEnable;
        }

        public boolean getLoadMoreEn() {
            return mIsLoadMoreEn;
        }

        public boolean getLoadMoreErrEn() {
            return mIsLoadMoreErrEn;
        }

        public boolean getFooterEn() {
            return mIsFooterEn;
        }
    }
}