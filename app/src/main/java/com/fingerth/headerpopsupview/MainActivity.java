package com.fingerth.headerpopsupview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fingerth.commonadapter.recycleradapter.CommonRecyclerAdapter;
import com.fingerth.commonadapter.recycleradapter.Holder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.rv)
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(i + " . Hello world !");
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new CommonRecyclerAdapter<String>(this, list) {
            @Override
            public int itemViewType(int position) {
                if (position == 0) {
                    return 0x11;
                }
                return 0x00;
            }

            @Override
            public int setLayoutId(int i) {
                if (i == 0x11) {
                    return R.layout.item_header;
                }
                return R.layout.item_rv;
            }

            @Override
            public void onBind(Holder holder, int i, String s) {

            }

        });
        rv.post(new Runnable() {
            @Override
            public void run() {
                try {
                    rv.smoothScrollBy(0, rv.getChildAt(1).getTop());
                } catch (Exception e) {
                    e.printStackTrace();
                    //可能超角标，不去判断，直接捕获异常。
                }
            }
        });

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentDy;
            private int findFirstCompletelyVisibleItemPosition;
            private int findFirstVisibleItemPosition;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentDy = dy;
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();//获取LayoutManager
                //经过测试LinearLayoutManager和GridLayoutManager有以下的方法,这里只针对LinearLayoutManager
                if (manager instanceof LinearLayoutManager) {
                    //经测试第一个完整的可见的item位置，若为0则是最上方那个;在item超过屏幕高度的时候只有第一个item出现的时候为0 ，其他时候会是一个负的值
                    //此方法常用作判断是否能下拉刷新，来解决滑动冲突
                    findFirstCompletelyVisibleItemPosition = ((LinearLayoutManager) manager).findFirstCompletelyVisibleItemPosition();
                    //最后一个完整的可见的item位置
                    //int findLastCompletelyVisibleItemPosition = ((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                    //第一个可见的位置
                    findFirstVisibleItemPosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                    //最后一个可见的位置
                    //int findLastVisibleItemPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //newState == 0 ==  SCROLL_STATE_IDLE 時，The RecyclerView is not currently scrolling.
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (findFirstCompletelyVisibleItemPosition == 1 && findFirstVisibleItemPosition == 0) {
                        if (currentDy > 0) {
                            //if (recyclerView.getChildCount() > 2) {}
                            try {
                                recyclerView.smoothScrollBy(0, recyclerView.getChildAt(1).getTop());
                            } catch (Exception e) {
                                e.printStackTrace();
                                //可能超角标，不去判断，直接捕获异常。
                            }

                        } else {
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }
                }

            }
        });

    }

}
