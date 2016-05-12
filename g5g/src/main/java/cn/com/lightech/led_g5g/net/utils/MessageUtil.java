package cn.com.lightech.led_g5g.net.utils;

import cn.com.lightech.led_g5g.gloabal.App;

import android.widget.Toast;

public class MessageUtil {

    public static void showToast(String message) {
        Toast.makeText(App.getInstance(), message, Toast.LENGTH_SHORT).show();
    }
}
