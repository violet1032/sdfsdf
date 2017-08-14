package com.xcinfo.xc_blueteeth_android.main.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BlueToothCommuicationService;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.DialogUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.StatusColorUtils;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.fragment.HomeFragment;
import com.xcinfo.xc_blueteeth_android.main.fragment.MoniterFragment;
import com.xcinfo.xc_blueteeth_android.main.fragment.SettingFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabHost.OnTabChangeListener {
    private List<TabItem> tabItemList;
    FragmentTabHost mFragmentTabHost;
    boolean isBind;

    public static BlueToothCommuicationService.BluetoothBinder getMyBinder() {
        return myBinder;
    }

    private static BlueToothCommuicationService.BluetoothBinder myBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
                isBind=false;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (BlueToothCommuicationService.BluetoothBinder) service;
            isBind=true;

            Log.d("@@Mainactivity","binded");
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DialogInterface.OnClickListener onClickListener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        };
        DialogInterface.OnClickListener onClickListener2=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        };

        DialogUtil.exitApp(this,onClickListener,onClickListener2
               );
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBar();
        initTabData();
        initTabHost();
        if (myBinder==null)
        bindBluetoothService();

    }

    protected void setStatusBar() {
        StatusColorUtils.setColor(this, getResources().getColor(R.color.blue_main_ui));
    }


    @Override
    protected void onDestroy() {
        Intent bindIntent = new Intent(this, BlueToothCommuicationService.class);
        stopService(bindIntent);
        myBinder=null;
        super.onDestroy();
    }

    private void bindBluetoothService() {

        Intent bindIntent = new Intent(this, BlueToothCommuicationService.class);
        startService(bindIntent);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
    }


    private void initTabData() {
        Log.d("@@","initData");
        tabItemList=new ArrayList<TabItem>();
        //添加监控
        tabItemList.add(new TabItem(R.drawable.moniter_normal,R.drawable.moniter_press,R.string.minoter, MoniterFragment.class));
        //添加主页
        tabItemList.add(new TabItem(R.drawable.home_normal,R.drawable.home_press,R.string.home, HomeFragment.class));
        //添加设置
        tabItemList.add(new TabItem(R.drawable.set_normal,R.drawable.set_press,R.string.setting, SettingFragment.class));

    }

    private void initTabHost() {
        Log.d("@@","initTabhost");
        //获取FragmentTabHost
        mFragmentTabHost= (FragmentTabHost) findViewById(android.R.id.tabhost);
        //绑定TabHost()
        mFragmentTabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);
        //去掉分割线
        mFragmentTabHost.getTabWidget().setDividerDrawable(null);
        for(int i=0;i<tabItemList.size();i++){
            TabItem tabItem=tabItemList.get(i);
            //绑定Fragment(将Fragment添加到FragmentTabHost组件上面)
            //newTabSpec:代表Tab名字
            //setIndicator:图片(采用布局文件--Tab到样式我们自己做)
            TabHost.TabSpec tabSpec=mFragmentTabHost.newTabSpec(tabItem.getTitleString()).setIndicator(tabItem.getView());
            //添加Fragment
            //tabSpec:选项卡
            //tabItem.getFragmentClass():具体的Fragment
            //tabItem.getBundle():给我们的具体的Fragment传参数
            mFragmentTabHost.addTab(tabSpec,tabItem.getFragmentClass(),tabItem.getBundle());
            //给我们的Tab按钮设置背景
//            mFragmentTabHost.getTabWidget()
//                    .getChildAt(i)
//                    .setBackgroundColor(getResources().getColor(R.color.main_bottom_bg));
            //监听点击Tab
            mFragmentTabHost.setOnTabChangedListener(this);
            //默认选中第一个Tab
            if (i == 1){
                tabItem.setChecked(true);
            }

        }
        mFragmentTabHost.setCurrentTab(1);


    }

    @Override
    public void onTabChanged(String tabId) {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        AccelerateInterpolator accelerateInterpolator=new AccelerateInterpolator();
        operatingAnim.setInterpolator(accelerateInterpolator);
        operatingAnim.setDuration(250);
        operatingAnim.setFillAfter(true);
        //重置Tab样式
        for (int i = 0;i < tabItemList.size();i++){
            TabItem tabItem = tabItemList.get(i);
            if (tabId.equals(tabItem.getTitleString())){
                //选中设置为选中壮体啊
                mFragmentTabHost.getTabWidget().getChildAt(i).setAnimation(operatingAnim);

                tabItem.setChecked(true);
            }else {
                //没有选择Tab样式设置为正常
                tabItem.setChecked(false);
            }
        }
    }




    class TabItem {
        private int imageNormal;//正常情况下显示的图片
        private int imagePressed;//选中后的图片

        //tab的名字
        private int title;
        private String titleString;
        private Class<? extends Fragment> fragmentClass;

        private View view;
        private ImageView imageView;
        private TextView textView;
        private Bundle bundle;

        public TabItem(int imageNormal,int imagePressed,int title,Class<? extends Fragment> fragmentClass){
            this.imageNormal=imageNormal;
            this.imagePressed=imagePressed;
            this.title=title;
            this.fragmentClass=fragmentClass;
        }



        public Class <? extends Fragment> getFragmentClass(){
            return fragmentClass;
        }

        public int getTitle(){
            return title;
        }

        public String getTitleString(){
            if (title==0){
                return "";

            }if (TextUtils.isEmpty(titleString)){
                titleString=getString(title);
            }
            return titleString;
        }

        public Bundle getBundle(){
            if (bundle == null){
                bundle = new Bundle();
            }
            bundle.putString("title",getTitleString());
            return bundle;
        }

        //还需要提供一个切换Tab方法---改变Tab样式
        public void setChecked(boolean isChecked){
            if (imageView!=null){
                if (isChecked){
                    imageView.setImageResource(imagePressed);
                }else {
                    imageView.setImageResource(imageNormal);
                }
            }if (textView!=null&&title!=0){
                if (isChecked){
                    textView.setTextColor(getResources().getColor(R.color.main_bottom_text_select));
                }else {
                    textView.setTextColor(getResources().getColor(R.color.main_bottom_text_normal));
                }
            }
        }

        public View getView(){
            if (this.view==null){
                this.view=getLayoutInflater().inflate(R.layout.view_tab_indicator,null);
                this.imageView= (ImageView) this.view.findViewById(R.id.iv_tab);
                this.textView= (TextView) this.view.findViewById(R.id.tv_tab);
                //判断资源是否存在,不再我就因此
                if (this.title == 0){
                    this.textView.setVisibility(View.GONE);
                }else {
                    this.textView.setVisibility(View.VISIBLE);
                    this.textView.setText(getTitleString());
                }
                //绑定图片默认资源
                this.imageView.setImageResource(imageNormal);
            }
            return this.view;
        }

    }
}
