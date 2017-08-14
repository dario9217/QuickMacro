package zhou.example.com.quickmacro;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class SaveService extends Service {
    private WindowManager wm;
    private View sore;
    private Handler handler = new Handler();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("sore", "脚本录制服务已开启");
        showTip();
    }


    /**
     * 显示脚本录制对话框
     */
    public void showTip() {
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // WindowManager可以向屏幕添加一个控件, 需要知道控件如何摆放和一些其他属性 所以需要LayoutParams
        // 参见Toast中的TN源码
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.MATCH_PARENT; // 高
        params.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽
        params.format = PixelFormat.TRANSLUCENT; // 半透明
        // 需要权限android.permission.SYSTEM_ALERT_WINDOW
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// TYPE_PRIORITY_PHONE:优先于通话界面
        // WindowManager.LayoutParams.TYPE_TOAST;
        // //Toast类型

        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON // 屏幕常量
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不可获取焦点
        // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE; //不可摸

        sore = View.inflate(SaveService.this, R.layout.tip, null);

        // 添加View到窗口上
        wm.addView(sore, params);
        sore.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(SaveService.this, SaveService.class);
                Intent intent1 = new Intent(SaveService.this.getApplicationContext(), RecordingService.class);
                stopService(intent);
                stopService(intent1);
                wm.removeView(sore);
                return true;
            }
        });
        sore.setOnTouchListener(new View.OnTouchListener() {
            private int downX;
            private int downY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("test", "down");
                        downX = (int) event.getRawX();
                        downY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("test", "move");
                        break;
                    case MotionEvent.ACTION_UP:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        int dx = moveX - downX;
                        int dy = moveY - downY;
                        //取绝对值
                        int x = Math.abs(dx);
                        int y = Math.abs(dy);
                        if (x < 10 && y < 10) {
                            String tap = SPutils.getString(SaveService.this.getApplicationContext(), Contant.RECORD);
                            tap = tap + "input tap " + moveX + " " + moveY +
                                    "\n sleep 2 \n";
                            SPutils.putString(SaveService.this.getApplicationContext(), Contant.RECORD, tap);
                        } else {
                            String swap = SPutils.getString(SaveService.this.getApplicationContext(), Contant.RECORD);
                            swap = swap + "input swipe " + downX + " " + downY + " " + moveX + " " + moveY + " " + "  \n sleep 2 \n ";
                            SPutils.putString(SaveService.this.getApplicationContext(), Contant.RECORD, swap);
                        }
                        wm.removeView(sore);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                wm.addView(sore, params);
                            }
                        }, 1000);
                        Log.i("sore", "x=" + moveX);
                        Log.i("sore", "y=" + moveY);
                        Log.i("sore", "dx=" + dx);
                        Log.i("sore", "dy=" + dy);
                        Log.i("test", "up");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

}
