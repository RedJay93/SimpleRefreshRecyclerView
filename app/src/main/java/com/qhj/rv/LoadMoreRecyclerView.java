package com.qhj.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * 写这段代码的时候，只有上帝和我知道它是干嘛的
 * 现在，只有上帝知道
 * Created by Coder·Qin on 2017/10/13.
 */
public class LoadMoreRecyclerView extends RecyclerView {
    private boolean isLoadMore;
    private OnRecyclerViewRefreshListener listener;
    private View footerView;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount  = recyclerView.getChildCount();
                int totalItemCount  = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (dy>0){
                    if ((firstVisibleItemPosition + visibleItemCount) == totalItemCount){
                        View endItem = recyclerView.getChildAt(visibleItemCount - 1);
                        if (endItem!=null&&endItem.getBottom()<=recyclerView.getHeight()){
                            //此处会有一个分割线高度的误差，endItem.getBottom()为view底部到父容器顶部的距离，加上分割线之后会导致滑到底部后endItem.getBottom()<recyclerView.getHeight()
                            if (listener!=null&&!isLoadMore){
                                smoothScrollToPosition(totalItemCount);
                                getAdapter().notifyItemChanged(totalItemCount-1);
                                listener.onLoadMore();
                                isLoadMore=true;
                            }
                        }
                    }
                }
            }
        });
    }

    public void setLoadMoreListener(OnRecyclerViewRefreshListener listener){
        this.listener=listener;
    }

    public void finishLoadMore(){
        isLoadMore=false;
        if (footerView!=null){
            removeView(footerView);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        RefreshRecyclerViewSuperAdapter superAdapter = new RefreshRecyclerViewSuperAdapter(adapter);
        super.setAdapter(superAdapter);
    }

    public class RefreshRecyclerViewSuperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private RecyclerView.Adapter dataAdapter;
        private final int TYPE_FOOTER=1;

        public RefreshRecyclerViewSuperAdapter(RecyclerView.Adapter dataAdapter) {
            this.dataAdapter = dataAdapter;
        }

        @Override
        public int getItemViewType(int position) {
            if (isLoadMore&&position==getItemCount()-1){
                return TYPE_FOOTER;
            }
            return dataAdapter.getItemViewType(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            if (viewType==TYPE_FOOTER){
                footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_refresh_recyclerview, parent, false);
                holder= new FooterViewHolder(footerView);
            }else {
                holder=dataAdapter.onCreateViewHolder(parent,viewType);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = getItemViewType(position);
            if (itemViewType != TYPE_FOOTER) {
                dataAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            if (dataAdapter.getItemCount() != 0) {
                int count = dataAdapter.getItemCount();
                if (isLoadMore){
                    count++;
                }
                return count;
            }
            return 0;
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder{

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
