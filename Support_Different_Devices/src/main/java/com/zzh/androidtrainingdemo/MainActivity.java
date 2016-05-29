package com.zzh.androidtrainingdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String Result;

    private String[] permissions = {Manifest.permission.CAMERA,Manifest.permission.READ_CONTACTS};
    private final static int PERMISSION_REQUEST_CODE = 0;

    private String[] permissionDeniedForver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String资源的使用，支持不同的语言
        String temp = getResources().getString(R.string.HelloWorld);
        if (temp != null) {
            Log.d(TAG, temp);
        }

        //键值对的读写
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String tag = "com.zzh.sharedPreferenes";
//                SharedPreferences sharedPreferences =
//                        getSharedPreferences(tag, Context.MODE_PRIVATE);

                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("feeling", "hard-working");
                editor.commit(); //容易忽略
                String result = sharedPreferences.getString("feeling", "");
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(TAG, "result = " + s);
            }
        }.execute();

        //文件操作

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                //写文件
                String fileName = "textfile";
                File file = new File(getFilesDir(), fileName);
                FileOutputStream fileOutputStream;
                BufferedWriter bufferedWriter = null;
                try {
                    fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                    bufferedWriter = new BufferedWriter
                            (new OutputStreamWriter(fileOutputStream));
                    bufferedWriter.write("nihao to file");
                    bufferedWriter.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //读文件
                FileInputStream fileInputStream;
                String result = null;
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = null;
                try {
                    fileInputStream = openFileInput(fileName);
                    bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    while ((result = bufferedReader.readLine()) != null) {
                        stringBuilder.append(result);
                    }
                    result = stringBuilder.toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(TAG, s);
            }
        }.execute();


        //与其他App交互
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:13981010266");
                Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
                Intent chooser = Intent.createChooser(callIntent, "打电话");
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });



        //权限申请
        findViewById(R.id.requestPermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    //执行需要权限的功能
                    doPermissionNeedTasks();
                    return;
                }else{
                    requestMutilePermission(permissions);
                }


            }
        });
    }

    private void doPermissionNeedTasks() {
        Log.d(TAG,"做需要权限的工作");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE){
            if (checkPermissionsGranted(grantResults)){
                //获取到所有权限
                doPermissionNeedTasks();
            }else{
                final List<String> permissionDenied = new ArrayList();
                final List<String> permissionDeniedForever = new ArrayList();
                permissionDenied.clear();
                permissionDeniedForever.clear();
                for (String permission : permissions){
                   if(checkPermissionDeniedForever(permission)){
                       permissionDeniedForever.add(permission);
                   }else{
                      permissionDenied.add(permission);
                   }
                }
                this.permissionDeniedForver = ListToString(permissionDeniedForever);
                //dealwithPermissionForever(permissionDeniedForever);
                dealwithAllPermissionDenied(permissionDenied,permissionDeniedForever);
            }
        }

    }


    private void dealwithAllPermissionDenied(final List<String> permissionDenied,final List<String> permissionDeniedForever){

        if (permissionDenied.size() > 0){
            //处理普通拒绝，告诉用户，让他自己选择
            new AlertDialog.Builder(this)
                    .setTitle("权限")
                    .setMessage("刚刚您拒绝了我们的权限申请，如果您需要该功能，请给予我们权限")
                    .setPositiveButton("需要", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,ListToString(permissionDenied),PERMISSION_REQUEST_CODE);
                            dialog.dismiss();
                            dealwithPermissionForever(permissionDeniedForever);
                        }

                    })
                    .setNegativeButton("不需要", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG,"用户不需要权限");
                            dialog.dismiss();
                            dealwithPermissionForever(permissionDeniedForever);
                        }
                    }).show();
        }

    }

    private void dealwithPermissionForever(List<String> permissionDeniedForever){
        if (permissionDeniedForever.size() > 0){
            //处理永久拒绝权限
            new AlertDialog.Builder(this)
                    .setTitle("权限")
                    .setMessage("刚刚您永久地拒绝了我们的权限申请，如果您需要该功能，将跳转到设置界面获取权限")
                    .setPositiveButton("需要", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri); startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("不需要", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG,"用户不需要权限");
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE){
            boolean Result = true;

            for (String permission:permissions){
                if (ActivityCompat.checkSelfPermission(this, permission)
                        == PackageManager.PERMISSION_DENIED) {
                    Result = false;
                }
            }
            if (!Result){
                new AlertDialog.Builder(this)
                        .setTitle("权限")
                        .setMessage("刚刚您又没给我们的权限申请，如果您需要该功能，将跳转到设置界面获取权限")
                        .setPositiveButton("需要", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("不需要", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG,"用户不需要权限");
                                dialog.dismiss();
                            }
                        }).show();
            }else{
                doPermissionNeedTasks();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermissionDeniedForever(String Permission){
        boolean showRationale = shouldShowRequestPermissionRationale(Permission);
        if (!showRationale) return true;
        return false;
    }


    private boolean checkPermissionsGranted(int[] grantResults){
        for (int i :grantResults){
            if (i == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }

        return true;
    }

    private void requestMutilePermission(final String [] permissions) {
        boolean result = false;
        for (String permission : permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){
                result = true;
                break;
            }
        }
        if (result) {
            //曾经拒绝过权限申请，显示提示信息
            new AlertDialog.Builder(this)
                    .setTitle("权限")
                    .setMessage("app需要权限才能工作")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //申请权限
                            ActivityCompat.requestPermissions(MainActivity.this,permissions,PERMISSION_REQUEST_CODE);
                        }
                    }).show();
        }else{
            //申请权限
            ActivityCompat.requestPermissions(MainActivity.this,permissions,PERMISSION_REQUEST_CODE);
        }
    }

    private String[] ListToString(List<String> list){
        String[] strings = new String[list.size()];
        int i = 0;
        for (String string :list){
            strings[i] = string;
            i++;
        }
        return strings;
    }

}
