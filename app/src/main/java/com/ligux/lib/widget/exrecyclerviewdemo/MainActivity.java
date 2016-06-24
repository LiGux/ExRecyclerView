package com.ligux.lib.widget.exrecyclerviewdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ligux.lib.widget.exrecyclerview.ExtendGridLayoutManager;
import com.ligux.lib.widget.exrecyclerview.ExtendRecyclerView;
import com.ligux.lib.widget.exrecyclerview.RefreshHeader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ExtendRecyclerView mList;
    private ExtendRecyclerView.AutoLoadAdapter mAdapter;
    private LayoutInflater mInflater;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInflater = LayoutInflater.from(this);
        mContext = this;

        mList = (ExtendRecyclerView) findViewById(R.id.list);
        final ArrayList<String> data = new ArrayList<>();
        data.add("刷新1");
        data.add("刷新2");
        data.add("刷新3");
        data.add("刷新4");
        data.add("刷新5");
        data.add("刷新6");
        data.add("刷新7");
        data.add("刷新8");
        data.add("刷新9");
        data.add("刷新10");
        data.add("刷新11");
        data.add("刷新12");
        data.add("刷新13");
        data.add("刷新14");

        final ArrayList<String> data2 = new ArrayList<>();
        data2.add("加载更多1");
        data2.add("加载更多2");
        data2.add("加载更多3");
        data2.add("加载更多4");

//        mList.enableFooter(true);
//        mList.enableHeader(true);
//        View view =
//        mList.setLoadMore();

        mAdapter = new ExtendRecyclerView.AutoLoadAdapter<String, Holder>(new ArrayList<String>()) {
            @Override
            public Holder onCreatePullToRefreshViewHolder(ViewGroup parent) {
                RefreshHeader view = new RefreshHeader(mContext);
                Holder holder = new Holder(view);
                return holder;
            }

            @Override
            public Holder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = mInflater.inflate(R.layout.header, null);
                Holder holder = new Holder(view);
                return holder;
            }

            @Override
            protected Holder onCreateFooterViewHolder(ViewGroup parent) {
                View view = mInflater.inflate(R.layout.recycle_view_footer, null);
                Holder holder = new Holder(view);
                return holder;
            }

            @Override
            protected Holder onCreateNormalViewHolder(ViewGroup parent) {
                View view = mInflater.inflate(R.layout.item_view, null);
                Holder holder = new Holder(view);
                holder.mText = (TextView) view.findViewById(R.id.text);
                return holder;
            }

            @Override
            protected Holder onCreateLoadMoreErrViewHolder(ViewGroup parent) {
                View view = mInflater.inflate(R.layout.recycleview_load_more_error, null);
                return new Holder(view);
            }

            @Override
            protected Holder onCreateLoadMoreViewHolder(ViewGroup parent) {
                View view = mInflater.inflate(R.layout.footer, null);
                Holder holder = new Holder(view);
                return holder;
            }

            @Override
            protected void onBindPullToRefreshView(Holder holder) {
            }

            @Override
            protected void onBindHeaderView(Holder holder) {
            }

            @Override
            protected void onBindFooterView(Holder holder) {
            }

            @Override
            protected void onBindLoadMoreView(Holder holder) {
            }

            @Override
            protected void onBindLoadMoreErrView(Holder holder) {
            }

            @Override
            protected void onBindNormalView(Holder holder, String data, int position) {
                holder.mText.setText(data);
            }
        };
        ExtendGridLayoutManager layoutManager = new ExtendGridLayoutManager(this, 1);
        mList.setLayoutManager(layoutManager);
        mList.setAdapter(mAdapter);
        layoutManager.setAdapter(mAdapter);

        mList.setLoadMoreListener(new ExtendRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {

                mList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addData(data2);
                        mList.notifyLoadMoreFinish(true);
                    }
                }, 1000);
            }

            @Override
            public void onRefresh(){
                mList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.clearData();
                        mAdapter.addData(data);
                        mList.notifyRefreshFinish(true);
                    }
                }, 1000);
            }
        });
        mList.setRefreshing(true);
    }

    private static class Holder extends RecyclerView.ViewHolder {

        public TextView mText;

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
