package com.example.logtofiledemo;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kaer.logx.LogDumper;
import com.kaer.logx.LogX;
import com.kaer.logx.LogXConfig;
import com.permissionx.guolindev.PermissionX;

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
     * 存储路径
     */
    TextView tvPath;

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
        tvPath= findViewById(R.id.tv_path);
        btnTest.setOnClickListener(this);
        btnTest2.setOnClickListener(this);

        //动态申请权限，需要的权限有读写外部存储权限
        PermissionX.init(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        LogXConfig config=new LogXConfig.Builder(this).FromConfig("Logx.properties").build();
                        //使用默认配置
                        logDumper = LogX.init(MainActivity.this).setConfig(config).startDumper();
                        String logFilePath = logDumper.getLogFilePath();
                        tvPath.setText(logFilePath);
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
