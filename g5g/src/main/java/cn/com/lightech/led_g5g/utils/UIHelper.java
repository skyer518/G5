package cn.com.lightech.led_g5g.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.util.Log;
import android.view.KeyEvent;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.entity.Device;
import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.gloabal.App;
import cn.com.lightech.led_g5g.gloabal.LocalPhoneParms;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.view.console.impl.ConnectDialogView;
import cn.com.lightech.led_g5g.view.console.impl.UpdateLedDialog;

public class UIHelper implements OnDismissListener {

    public static final String tag = UIHelper.class.getName();

    private static UIHelper instance;
    private static Dialog dialog = null;

    public static UIHelper getInstance() {
        if (instance == null)
            instance = new UIHelper();
        return instance;
    }

    private UIHelper() {

    }

    public void closeConnectDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    public void showConnectDialog(final Context context, boolean canCancel, DeviceGroup group) {
        if (dialog != null) {
            dialog.show();
            return;
        }

        ConnectDialogView view = new ConnectDialogView(
                context, group);

        dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(view);
        dialog.show();
        dialog.getWindow().setLayout(LocalPhoneParms.getPhoneWidth() / 4 * 3,
                LocalPhoneParms.getPhoneHeight() / 4 * 3);
        dialog.setCancelable(false);
        dialog.setOnDismissListener(this);

    }

    public static void showUpdateLedDialog(final Context context, final Device device) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage("确定要升级此灯？")
                .setPositiveButton(R.string.device_wifi_dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ConnectionsManager.getInstance().priorityConnect(device.getIp(), 8080);
                        UpdateLedDialog updateLedDialog = new UpdateLedDialog(context);
                        updateLedDialog.setCancelable(false);
                        updateLedDialog.show();
                        updateLedDialog.setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                ConnectionsManager.getInstance().clearPriorityConnections();
                            }
                        });
                    }
                }).setNegativeButton(R.string.device_wifi_dialog_button_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).create();
        alertDialog.show();

    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        closeConnectDialog();
        Log.d(tag, "onDismiss: " + dialog.getClass().getName());
    }
}
