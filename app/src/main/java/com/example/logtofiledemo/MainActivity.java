package com.example.logtofiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kaer.logx.LogDumper;
import com.kaer.logx.LogX;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;

/**
 * 主窗体Activity
 *
 * @author huangfan
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 测试按钮 tag未hf
     */
    Button btnTest;

    /**
     * 测试按钮 tag未test
     */
    Button btnTest2;

    /**
     * 日志抓取对象
     */
    private LogDumper logDumper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTest = findViewById(R.id.btnTest);
        btnTest2 = findViewById(R.id.btnTest2);
        btnTest.setOnClickListener(this);
        btnTest2.setOnClickListener(this);


        //动态申请权限，需要的权限有读写外部存储权限
        PermissionX.init(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        logDumper = LogX.init(MainActivity.this).tags("hf","test").setLogFilePath(null).startDumper();
                        String logFilePath = logDumper.getLogFilePath();
                        Toast.makeText(MainActivity.this, "本地日志存储在此路径:" + logFilePath, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "没有相关权限，无法存储本地日志", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logDumper != null) {
            logDumper.close();
            logDumper = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnTest) {
            Log.d("hf", "测试");
        } else if (v.getId() == R.id.btnTest2) {
            Log.d("test", "测试");
        }
    }
}
