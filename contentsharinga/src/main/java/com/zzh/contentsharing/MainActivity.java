package com.zzh.contentsharing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    String fileName = "sharedImage.png";
    String filePath ;
    Intent ResultIntent = new Intent();
    File requesrtFile ;
    Uri fileUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePath  = getFilesDir().toString() +"/images" +"/sharedImage.png";
        requesrtFile = new File(filePath);
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                File file = new File(filePath);

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
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                try{
                    fileUrl = FileProvider.getUriForFile
                            (MainActivity.this,"com.zzh.contentsharing.fileprovider",requesrtFile);
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }

                if (fileUrl != null){
                    ResultIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    ResultIntent.setDataAndType(fileUrl,getContentResolver().getType(fileUrl));
                    MainActivity.this.setResult(RESULT_OK,ResultIntent);
                }



            }
        }.execute();


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}
