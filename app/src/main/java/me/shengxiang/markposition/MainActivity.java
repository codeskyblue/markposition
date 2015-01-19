package me.shengxiang.markposition;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {

    private String LOG_TAG = "MarkPoint";
    private String MP_POSITION = "MP_POSITION";
    private BroadcastReceiver mReceiver = null;
    private WindowManager wm=null;
    private WindowManager.LayoutParams wmParams=null;
    private MyFloatView myFV=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //创建悬浮窗口
        createView();

        if(mReceiver == null){
            IntentFilter filter = new IntentFilter(MP_POSITION);
            mReceiver = new MpReceiver();
            registerReceiver(mReceiver, filter);
        }

        Button closeService = (Button)findViewById(R.id.close_service);
        closeService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MpReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, intent.getAction());
            if (intent.getAction().equals(MP_POSITION)) {
                String msg = intent.getStringExtra("msg");
                Log.i(LOG_TAG, msg);

                wmParams = ((MyApplication)getApplication()).getMywmParams();
                wmParams.x = 500;
                wmParams.y = 700;
                String[] parts = msg.split(",");
                if (parts.length != 2 ){
                    return;
                }
                wmParams.x = Integer.parseInt(parts[0]);
                wmParams.y = Integer.parseInt(parts[1]);
                wm = (WindowManager)getApplicationContext().getSystemService("window");
                wm.updateViewLayout(myFV, wmParams);
            }
        }
    }

    private void createView(){
        myFV=new MyFloatView(getApplicationContext());
        myFV.setImageResource(R.drawable.icon);
        //获取WindowManager
        wm = (WindowManager)getApplicationContext().getSystemService("window");
        //设置LayoutParams(全局变量）相关参数
        wmParams = ((MyApplication)getApplication()).getMywmParams();

        /**
         *以下都是WindowManager.LayoutParams的相关属性
         * 具体用途可参考SDK文档
         */
        wmParams.type = 2002;
        wmParams.type= WindowManager.LayoutParams.TYPE_PHONE;   //设置window type
        wmParams.format= PixelFormat.RGBA_8888;   //设置图片格式，效果为背景透明

        //设置Window flag
        wmParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 下面的flags属性的效果形同“锁定”。
         * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL
                               | LayoutParams.FLAG_NOT_FOCUSABLE
                               | LayoutParams.FLAG_NOT_TOUCHABLE;
        */


        wmParams.gravity= Gravity.LEFT|Gravity.TOP;   //调整悬浮窗口至左上角
        //以屏幕左上角为原点，设置x、y初始值
        wmParams.x=0;
        wmParams.y=0;

        //设置悬浮窗口长宽数据
        wmParams.width=100;
        wmParams.height=100;

        //显示myFloatView图像
        wm.addView(myFV, wmParams);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //在程序退出(Activity销毁）时销毁悬浮窗口
        wm.removeView(myFV);
    }
}
