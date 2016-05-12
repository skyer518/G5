package cn.com.lightech.led_g5g.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

/**
 * Created by 明 on 2016/3/8.
 */
public class ProgressUtil {


    private static ProgressDialog dialog = null;


    public static void showPogress(Context context, String text, boolean cancelable) {

        closeDialog();
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(text);
        dialog.setCancelable(false);
        dialog.show();

    }

    public static void closeDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
    }


}
