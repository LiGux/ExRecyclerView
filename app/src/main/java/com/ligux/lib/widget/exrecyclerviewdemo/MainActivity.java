package com.ligux.lib.widget.exrecyclerviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ligux.lib.widget.exrecyclerview.ExGridLayoutManager;
import com.ligux.lib.widget.exrecyclerview.ExRecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ExRecyclerView mList;
    private ExRecyclerView.ExAdapter mAdapter;
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInflater = LayoutInflater.from(this);

        mList = (ExRecyclerView) findViewById(R.id.list);
        ArrayList<String> data = new ArrayList<>();
        data.add("hello");
        data.add("yonnie");
        data.add("kk");
        data.add("shanghai");
        data.add("guilin");
        data.add("beijing");
        data.add("chagnsha");
        data.add("yongzhou");
        data.add("xiamen");
        data.add("America");
        data.add("Europe");
        data.add("Asia");
        data.add("Africa");
        data.add("Ocean");

        mList.enableFooter(true);
        mList.enableHeader(true);
//        View view =
//        mList.setLoadMore();

        mAdapter = new ExRecyclerView.ExAdapter<String, Holder>(data) {
            @Override
            public Holder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = mInflater.inflate(R.layout.header, null);
                Holder holder = new Holder(view);
                return holder;
            }

            @Override
            protected Holder onCreateFooterViewHolder(ViewGroup parent) {
                View view = mInflater.inflate(R.layout.footer, null);
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
            protected Holder onCreateLoadMoreViewHolder(ViewGroup parent) {
                return null;
            }

            @Override
            protected void onBindHeaderView(Holder holder) {

            }

            @Override
            protected void onBindFooterView(Holder holder) {

            }

            @Override
            protected void onBindNormalView(Holder holder, String data, int position) {
                holder.mText.setText(data);
            }
        };
        ExGridLayoutManager layoutManager = new ExGridLayoutManager(this, 2, mAdapter);
        mList.setLayoutManager(layoutManager);
        mList.setAdapter(mAdapter);
    }

    private static class Holder extends RecyclerView.ViewHolder {

        public TextView mText;

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
