package com.xcinfo.xc_blueteeth_android.main.device_class;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.toolbarbuilder.MyNavigationBuilder;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.activity.searchdevice.DividerItemDecoration;
import com.xcinfo.xc_blueteeth_android.main.monitor.activity.MonitoringDetailsActivity;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class DeviceClassActivity extends AppCompatActivity {
    public static final String MANNGER="mannger";
    public static final String STATE="state";

    RelativeLayout relativeLayout;//无数据时显示的图片
    LinearLayout linearLayout;//根布局
    List<Device> devices=new ArrayList<>();//通道数据
    RecyclerView recyclerView;
    DeviceClassAdapter adapter;
    String titleString="冷链汇监控系统";
    String manngerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_class_avtivity);

        ViewGroup v= (ViewGroup) DeviceClassActivity.this.getWindow().getDecorView();
        LinearLayout content= (LinearLayout) v.findViewById(R.id.activity_device_class_avtivity);
        inToolBar(content);
        initData();
        initView();

    }

    private void initView() {
        recyclerView= (RecyclerView) findViewById(R.id.device_class_recyclerview);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter=new DeviceClassAdapter(devices,this);
        adapter.setOnclickListener(new DeviceClassAdapter.DeviceAdapterListener() {
            @Override
            public void onClick(int position) {
                Intent intent=new Intent(DeviceClassActivity.this, MonitoringDetailsActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable(Constant.DEVICE_INFO_KEY,devices.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayout.VERTICAL,R.drawable.bluetoothdevice_decoration));

        linearLayout= (LinearLayout) findViewById(R.id.linealayout_information);
        relativeLayout= (RelativeLayout) findViewById(R.id.relativelayout_nodata);

        if (devices.size()==0){
            Log.d("@@@","数据为空");
            linearLayout.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        Intent intent=getIntent();
        int status=intent.getIntExtra(STATE,-1);
        manngerName=intent.getStringExtra(MANNGER);
        switch (status){
            case 0:
                devices= SqliteUtil.getInstance(this).getAllDeviceListByState(manngerName,status);
                break;
            case 1:
                devices= SqliteUtil.getInstance(this).getAllDeviceListByState(manngerName,status);
                break;
            case 2:
                devices= SqliteUtil.getInstance(this).getAllDeviceListByState(manngerName,status);
                break;
            case 3:
                devices= SqliteUtil.getInstance(this).getAllDeviceListByState(manngerName,status);
                break;
        }
        if (status==0){

//            for (Device device:devices){
//                Log.d("@@getBelongGroup:",":"+device.getBelongGroup());
//                Log.d("@@getBelongGroup:",":"+device.getBelongGroup());
//                Log.d("@@getDeviceName:",":"+device.getDeviceName());
//                Log.d("@@getToatlChannelCount:", ":"+String.valueOf(device.getToatlChannelCount()));
//                Log.d("@@getCHL1_ID:",":"+device.getCHL1_ID());
//                Log.d("@@getCHL1_type:",":"+device.getCHL1_type());
//                Log.d("@@getCHL1_unit:",":"+device.getCHL1_unit());
//                Log.d("@@getCHL1_unit:",":"+device.getCHL1_current());
//
//            }

        }

    }

    private void inToolBar(ViewGroup viewContent) {
        switch (getIntent().getIntExtra("status",-1)){
            case 0:
                titleString="正常设备";
                break;
            case 1:
                titleString="报警设备";
                break;
            case 2:
                titleString="异常设备";
                break;
            case 3:
                titleString="离线设备";
                break;
        }
        MyNavigationBuilder builder=new MyNavigationBuilder(this,viewContent);
        builder.settitleString(titleString).setLeftIconRes(R.drawable.back).
                setleftIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).setBackgroundColor(R.color.blue_main_ui)
              .onCreatAndBind(viewContent);
    }



    public static void actionStart(Context context,String mannger,int status){
        Intent intent=new Intent(context,DeviceClassActivity.class);
        intent.putExtra(MANNGER,mannger);
        intent.putExtra(STATE,status);
        context.startActivity(intent);

    }

}
