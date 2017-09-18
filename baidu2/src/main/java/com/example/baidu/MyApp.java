package com.example.baidu;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by 小亚 on 2017/9/1.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        // 注册一个key验证是否正确的广播所者,我们在开发APP时,经常有一些验证,比如网络等,具体流程如下
        //创建一个BroadcastReceiver的内部类,
        BroadcastReceiver receiver = new BroadcastReceiver() {
            //复写其必须有的方法onReceive,第一个参数上下文,第二个参数是传过来的intent.
            public void onReceive(Context context, Intent intent) {
                //参数intent.getAction得到发送过来的Intent里的action动作
                String action = intent.getAction();
                //创建一个null的字符串.
                String msg = "";
                //进行检查,if判断字符串是否相同,action.equals(),直接用SDKInitializer.SDK_BROADTCAST_ACTION_ST...就可以了
                if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                    msg = "Key验证成功！";
                }//判断SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR :验证失败
                else if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                    msg = "Key验证失败！";
                }//判断SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR :网络错误
                else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                    msg = "网络错误";
                }
                //弹吐司,内容就是字符串.
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        };
        //代码进行广播的注册配置,也可以在清单配置文件里进行配置
        IntentFilter filter = new IntentFilter();
        //添加过滤条件,IntentFilter对象.addAction();内容就是SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_....
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        //进行广播注册,1.要注册广播类对象 2配置文件IntentFilter对象.
        registerReceiver(receiver, filter);

        // 初始化百度地图SDK
        SDKInitializer.initialize(getApplicationContext());
    }
}