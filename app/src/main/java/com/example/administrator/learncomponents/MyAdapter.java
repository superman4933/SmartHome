package com.example.administrator.learncomponents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/5/10.
 */
class MyAdapter extends RecyclerView.Adapter {

    class NewviewHolder extends RecyclerView.ViewHolder {
        View root;
        private TextView textView_title,textView_content;
        public NewviewHolder(View root) {
            super(root);
            textView_title = (TextView) root.findViewById(R.id.textView_title);
            textView_content = (TextView) root.findViewById(R.id.textView_item);
        }

        public TextView getTextView_content() {
            return textView_content;
        }

        public TextView getTextView_title() {
            return textView_title;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout,null));
    }

private CellData[] data=new CellData[]{new CellData("标题1","数据1"),new CellData("标题2","数据2")};
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NewviewHolder newviewHolder = (NewviewHolder) holder;
        CellData cd = data[position];
        newviewHolder.getTextView_content().setText(cd.content);
        newviewHolder.getTextView_title().setText(cd.title);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }
}
