//package cn.com.lightech.led_g5w.presenter.responsibility;
//
//import android.os.Handler;
//
//import cn.com.lightech.led_g5w.R;
//import cn.com.lightech.led_g5w.entity.DeviceType;
//import cn.com.lightech.led_g5w.gloabal.CmdBuilder;
//import cn.com.lightech.led_g5w.net.ConnectManager;
//import cn.com.lightech.led_g5w.net.entity.CmdType;
//import cn.com.lightech.led_g5w.net.entity.Request;
//import cn.com.lightech.led_g5w.net.entity.Response;
//import cn.com.lightech.led_g5w.utils.ProgressUtil;
//
///**
// * Created by alek on 2016/5/17.
// */
//public class DutyGetDeviceType extends DutyHandler {
//
//
//    public static final int RESULT_OK = 1;
//    public static final int RESULT_TIMEOUT = 0;
//    public static final int RESULT_UNKONW_TYPE = -1;
//
//    public DutyGetDeviceType(DutyHandler successor) {
//        super(successor);
//    }
//
//    public DutyGetDeviceType() {
//    }
//
//    @Override
//    public void handleRequest(final RequestEntity request) {
//        ProgressUtil.showPogress(request.mContext, request.mContext.getString(R.string.device_wifi_save_data_2_led), false);
//
//        final LedTask task = new LedTask() {
//            @Override
//            public void sendCommond(ConnectManager connectManager) {
//                Request cmd = new Request();
//                cmd.setCmdType(CmdType.QueryType);
//                connectManager.SendToLed(CmdBuilder.Build(cmd));
//            }
//
//            @Override
//            public boolean onReceive(Response response, ConnectManager connectManager) {
//                super.onReceive(response, connectManager);
//                if (response.getCmdType() == CmdType.QueryType) {
//                    if (response.IsOK()) {
//                        request.deviceType = response.getDeviceType();
//                        if (request.deviceType != DeviceType.Unknown)
//                            over(RESULT_OK);
//                        else {
//                            over(RESULT_UNKONW_TYPE);
//                        }
//                    } else {
//                        over(RESULT_TIMEOUT);
//                    }
//                }
//                return true;
//            }
//
//
//            @Override
//            public void onOver(int success) {
//                switch (success) {
//                    case RESULT_TIMEOUT:
//                        request.handler.sendEmptyMessage(RequestEntity.WHAT_GET_DEVICE_TYPE_FAILED);
//                        break;
//                    case RESULT_OK:
//                        handNext(request);
//                        break;
//                    case RESULT_UNKONW_TYPE:
//                        request.handler.sendEmptyMessage(RequestEntity.WHAT_GET_DEVICE_TYPE_FAILED);
//                        break;
//                }
//
//            }
//        };
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!task.isOver)
//                    task.over(RESULT_TIMEOUT);
//            }
//        }, 3000);
//    }
//}
