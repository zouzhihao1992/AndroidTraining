package com.zzh.androidtrainingdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String Result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String资源的使用，支持不同的语言
        String temp = getResources().getString(R.string.HelloWorld);
        if (temp != null){
            Log.d(TAG,temp);
        }

        //键值对的读写
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {
                String tag = "com.zzh.sharedPreferenes";
//                SharedPreferences sharedPreferences =
//                        getSharedPreferences(tag, Context.MODE_PRIVATE);

                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("feeling","hard-working");
                editor.commit(); //容易忽略
                String result = sharedPreferences.getString("feeling","");
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(TAG,"result = "+s);
            }
        }.execute();

        //文件操作

        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {

                //写文件
                String fileName = "textfile";
                File file = new File(getFilesDir(),fileName);
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
                }finally {
                    if (bufferedWriter != null){
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
                    while ((result = bufferedReader.readLine()) != null){
                        stringBuilder.append(result);
                    }
                    result = stringBuilder.toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (bufferedReader != null){
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
                Log.d(TAG,s);
            }
        }.execute();

        //与其他App交互


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:13981010266");
                Intent callIntent = new Intent(Intent.ACTION_DIAL,uri);
                Intent chooser =  Intent.createChooser(callIntent,"打电话");
                if (callIntent.resolveActivity(getPackageManager())!=null){
                    startActivity(chooser);
                }
            }
        });





























    }
}
