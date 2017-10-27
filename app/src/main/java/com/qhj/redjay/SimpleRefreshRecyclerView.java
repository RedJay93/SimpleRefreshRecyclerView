package com.qhj.redjay;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * 写这段代码的时候，只有上帝和我知道它是干嘛的
 * 现在，只有上帝知道
 * Created by Coder·Qin on 2017/10/12.
 */
public class SimpleRefreshRecyclerView extends ViewGroup {
    private Context context;
    private View childAt;
    private int width;
    private int height;
    private SwipeRefreshLayout swipe;
    private LoadMoreRecyclerView rv;
    private OnRecyclerViewRefreshListener listener;

    public SimpleRefreshRecyclerView(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public SimpleRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public SimpleRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.simple_refresh_recyclerview,this,true);
        rv = (LoadMoreRecyclerView) findViewById(R.id.rv_simple_refresh);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light
        );
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (listener!=null){
                    listener.onRefresh();
                }else {
                    swipe.setRefreshing(false);
                    Toast.makeText(context, "尚未设置监听方法", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public LoadMoreRecyclerView getRecyclerView(){
        return rv;
    }

    public void setOnRefreshListener(OnRecyclerViewRefreshListener listener){
        this.listener=listener;
        rv.setLoadMoreListener(listener);
    }

    public void finishRefresh(){
        swipe.setRefreshing(false);
    }

    public void finishLoadMore(){
        rv.finishLoadMore();
    }

    public void notifyData(){
        rv.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childAt = getChildAt(0);
        measureChild(childAt,widthMeasureSpec,heightMeasureSpec);
        width = childAt.getMeasuredWidth();
        height = childAt.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        childAt.layout(0,0,width,height);
    }
}
