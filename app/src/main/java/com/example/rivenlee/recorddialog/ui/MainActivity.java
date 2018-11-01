package com.example.rivenlee.recorddialog.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.rivenlee.recorddialog.R;

public class MainActivity extends AppCompatActivity {

    private boolean isRecordDialogShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void click(View view){
        if(isRecordDialogShow){
            return;
        }
        RecordDialogFragment dialogFragment = RecordDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), "dialog");
        isRecordDialogShow = true;
        dialogFragment.setOnCancelListener(new RecordDialogFragment.OnCancelInterface() {
            @Override
            public void onCancel() {
                isRecordDialogShow = false;
            }
        });
    }
}
