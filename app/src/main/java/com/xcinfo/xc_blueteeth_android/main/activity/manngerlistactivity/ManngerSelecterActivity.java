package com.xcinfo.xc_blueteeth_android.main.activity.manngerlistactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.utils.GetScreenParameter;
import com.xcinfo.xc_blueteeth_android.main.bean.DeviceManager;
import com.xcinfo.xc_blueteeth_android.main.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class ManngerSelecterActivity extends Activity {
    RelativeLayout rootView;
    RecyclerView recyclerViewMannger;
    ManngerListAdapter adapter;
    List<String> manngerList;

    public static final String MANNGERLIST="manngerList";
    public static final String DATABUNDEL="dataBundel";
    public static final int CHOOSEMANNGER=0;
    String chosedMannger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除顶部actionbar
        setContentView(R.layout.activity_mannger_selecter);


        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra(ManngerSelecterActivity.DATABUNDEL);
        manngerList=bundle.getStringArrayList(ManngerSelecterActivity.MANNGERLIST);
        initView();
    }

    private void initView() {
        rootView= (RelativeLayout) findViewById(R.id.activity_mannger_selecter);
        recyclerViewMannger= (RecyclerView) findViewById(R.id.recyclerview_manngerlist);

        //设置宽度
        ViewGroup.LayoutParams layoutParams=rootView.getLayoutParams();
        layoutParams.width= (int) (GetScreenParameter.getScreenWidth(this)*0.7);
        rootView.setLayoutParams(layoutParams);

        adapter=new ManngerListAdapter(manngerList,this);
        adapter.setClickListener(new ManngerListAdapter.clickListener() {
            @Override
            public void onClick(String mannger) {
                Intent intent=new Intent();
                intent.putExtra(HomeFragment.RESULT,mannger);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        recyclerViewMannger.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMannger.setAdapter(adapter);

    }



}
