package com.xcinfo.xc_blueteeth_android.main.monitor.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.xcinfo.xc_blueteeth_android.R;

import java.util.List;

import cn.refactor.lib.colordialog.PromptDialog;

/**
 * created by ：ycy on 2017/3/24.
 * email 1490258886@qq.com
 */

public class DialogUtils {

    public static void createSingleBtnDialog(Context context, String message, PromptDialog.OnPositiveListener onPositiveListener){
        PromptDialog promptDialog=new PromptDialog(context);
        promptDialog.setDialogType(PromptDialog.DIALOG_TYPE_INFO);
        promptDialog.setAnimationEnable(true);
        promptDialog.setContentText(message);
        promptDialog.setPositiveListener("确定",onPositiveListener);
        promptDialog.show();
    }
    public static void createDoubleBtnDialog(Context context,String title,String message,DialogInterface.OnClickListener dialogInterface){
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setIcon(R.drawable.note);
        dialog.setPositiveButton("确定", dialogInterface);
        dialog.setNegativeButton("取消",dialogInterface);
        dialog.create().show();
    }

    public static void createListDialog(Context context, String title, String[] name,int select,DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setSingleChoiceItems(name,select,onClickListener);
        builder.setPositiveButton("确定",onClickListener);
        builder.setNegativeButton("取消",onClickListener)
                .create().show();
    }
    public static void showCustomDia(Context context,float max,float min,DialogInterface.OnClickListener onClickListener)
    {
        AlertDialog.Builder customDia=new AlertDialog.Builder(context);
        final View viewDia= LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);
        final EditText editMax= (EditText) viewDia.findViewById(R.id.editTextmax);
        final EditText editMin= (EditText) viewDia.findViewById(R.id.editTextmin);
        if(max!=0.0){
            editMax.setText(""+max);
        }
        if(min!=0.0||min!=0){
            editMin.setText(""+min);
        }
        customDia.setTitle("设置报警值");
        customDia.setView(viewDia);
        customDia.setNegativeButton("返回",onClickListener);
        customDia.setPositiveButton("确定", onClickListener);
        customDia.create().show();
    }
}
