package com.example.jx.myapplication;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener {

    private EditText et_ip, et_light;
    private Button btn_begin;
    private TextView et_log;
    private TextView et_lightCatch;
    private Handler handler = null;
    private float lightCatch = 1;
    //传感器
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取传感器管理对象
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //创建属于主线程的handler
        handler = new Handler();

        et_ip = (EditText) findViewById(R.id.et_ip);
        et_light = (EditText) findViewById(R.id.et_light);
        btn_begin = (Button) findViewById(R.id.btn_begin);
        et_log = (TextView) findViewById(R.id.et_log);
        et_lightCatch = (TextView) findViewById(R.id.et_lightCatch);

        btn_begin.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            //设置配置项目不可编辑
            et_ip.setEnabled(false);
            et_light.setEnabled(false);
            btn_begin.setEnabled(false);

            handler.postDelayed(runnableUi, 10000);//每十秒执行一次runnable.
        }

    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            String ip = et_ip.getText().toString();
            String light = et_light.getText().toString();
            final String urlStr = "http://" + ip + "/android/light?" + "light=" + light + "&lightCatch=" + lightCatch;

            //发送传感器数据
            HttpUtils.doGetAsyn(urlStr, null);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String dateStr = formatter.format(curDate);
            String tips = lightCatch + " " + dateStr;
            //提示信息
            Toast.makeText(MainActivity.this, tips, Toast.LENGTH_SHORT).show();
            //更新界面
            et_log.append("\n");
            et_log.append(tips);

            handler.postDelayed(runnableUi, 10000);//每十秒执行一次runnable.
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        // 为光传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 取消监听
        mSensorManager.unregisterListener(this);
    }

    // 当传感器的值改变的时候回调该方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        // 获取传感器类型
        int type = event.sensor.getType();
        StringBuilder sb;
        switch (type) {

            case Sensor.TYPE_LIGHT:
                sb = new StringBuilder();
                sb.append("\n光传感器返回数据：");
                sb.append("\n当前光的强度为：");
                sb.append(values[0]);
                et_lightCatch.setText(sb.toString());
                lightCatch = values[0];
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
