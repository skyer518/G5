package cn.com.lightech.led_g5g.gloabal;

import java.util.Calendar;
import java.util.List;

import cn.com.lightech.led_g5g.entity.data.CurveData;
import cn.com.lightech.led_g5g.entity.CurvePoint;
import cn.com.lightech.led_g5g.entity.DataNode;
import cn.com.lightech.led_g5g.entity.data.FlashData;
import cn.com.lightech.led_g5g.entity.data.ManualData;
import cn.com.lightech.led_g5g.entity.data.MoonData;
import cn.com.lightech.led_g5g.entity.TimeBucket;
import cn.com.lightech.led_g5g.entity.LampChannel;
import cn.com.lightech.led_g5g.entity.LampState;
import cn.com.lightech.led_g5g.entity.data.UpdateData;
import cn.com.lightech.led_g5g.net.entity.Request;


/**
 * 命令构建器
 */
public class CmdBuilder {

    private static final int ADDTION_LENGTH = 8;// 用来计算数据包内长度

    /**
     * 构建发送给LED的命令
     *
     * @param request 命令参数的封装
     * @return 包数据byte[]
     * @throws IllegalArgumentException
     */
    public static byte[] Build(Request request) throws IllegalArgumentException {
        switch (request.getCmdType()) {
            case CheckReady:
                return CmdBuilder.CreateCheckReadyCmd();
            case QueryGroup:
                return CmdBuilder.CreateQueryGroupCmd();
            case SetGroup:
                return CmdBuilder.CreateSetGroupCmd(request);
            case GetVersion:
                return null;
            case OnOff:
                return CmdBuilder.CreateSetOnOffCmd(request.getBoolVal());
            case PreViewCurve:
                return CmdBuilder.CreatePreviewCurveCmd(request.getIntVal());
            case PreviewMode:
                return CmdBuilder.CreatePreviewCmd(request.getLampState());
            case QueryState:
                return CmdBuilder.CreateQueryStateCmd();
            case RecvDataFromLED:
                return CmdBuilder.CreateRecvDataFromLEDCmd(request.getByteArray());
            case SendDataToLED:
                return CmdBuilder.CreateDataToLedCmd(request);
            case SetState:
                return CmdBuilder.CreateSetStateCmd(request.getLampState());
            case StopPreview:
                return CmdBuilder.CreateStopPreviewCmd();
            case SyncTime:
                return CmdBuilder.CreateSyncTimeCmd();
            case ValidateData:
                return CmdBuilder.CreateValidateDataCmd(request.getByteArray());
            case ConfirmLed:
                return CmdBuilder.CreateConfirmLedDataCmd(request.getIntVal());
            case FindLed:
                return "HLK".getBytes();
            default:
                throw new IllegalArgumentException("god bless you");
        }
    }


    /**
     * 灯具确认
     *
     * @param groupNo
     * @return
     */
    private static byte[] CreateConfirmLedDataCmd(int groupNo) {
        // TODO   ConfirmLed
        byte[] cmd = new byte[11];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) 0x03;// package length
        cmd[startIndex++] = (byte) 0x1D;// cmd
        cmd[startIndex++] = (byte) 0xA6;// deviceType
        cmd[startIndex++] = (byte) groupNo; //group number
        cmd[startIndex++] = Sum(cmd, 0, startIndex - 1); //check sum
        return cmd;
    }

    /**
     * 时间同步
     */
    private static byte[] CreateSyncTimeCmd() {

        Calendar calendar = Calendar.getInstance();

        byte[] cmd = new byte[16];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = 0x08;// 数据包长度
        cmd[startIndex++] = 0x01;// 命令
        int year = calendar.get(Calendar.YEAR);
        int year1 = (year & 0xFF00) >> 8;
        int year2 = (year & 0xFF);
        cmd[startIndex++] = (byte) year1;
        cmd[startIndex++] = (byte) year2;
        cmd[startIndex++] = (byte) (calendar.get(Calendar.MONTH) + 1);
        cmd[startIndex++] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
        cmd[startIndex++] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        cmd[startIndex++] = (byte) calendar.get(Calendar.MINUTE);
        cmd[startIndex++] = (byte) calendar.get(Calendar.SECOND);
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);

        return cmd;
    }

    /**
     * 查询灯的状态
     */
    private static byte[] CreateQueryStateCmd() {
        byte[] cmd = new byte[9];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = 0x01;// 长度
        cmd[startIndex++] = 0x03;// 命令
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);
        return cmd;
    }

    /**
     * 开关灯
     */
    private static byte[] CreateSetOnOffCmd(boolean on) {
        byte[] cmd = new byte[10];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = 0x02;// 长度
        cmd[startIndex++] = 0x06;// 命令
        cmd[startIndex++] = (byte) (on ? 1 : 0);// ID
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);
        return cmd;
    }

    /**
     * 预览曲线
     *
     * @param speed (0: normal(时间点/3秒) 1:x2(时间点/2秒) 2: x3(时间点/1秒))
     * @return
     */
    private static byte[] CreatePreviewCurveCmd(int speed) {
        byte[] cmd = new byte[10];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = 0x02;
        cmd[startIndex++] = 0x08; // 预览命令
        cmd[startIndex++] = (byte) speed; // 预览速度
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);
        return cmd;
    }

    /**
     * 预览模式，只是预览即时状态，主要用于即时模式
     */
    private static byte[] CreatePreviewCmd(LampState ls) {
        if (ls == null)
            throw new IllegalArgumentException("missing lampstate argument");
        byte[] cmd = new byte[14];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = 0x06;
        cmd[startIndex++] = 0x07; // 模式预览，各个通道的预览也是这样，
        /* 这里通道调整下顺序 */
        cmd[startIndex++] = ls.purple;
        cmd[startIndex++] = ls.blue;
        cmd[startIndex++] = ls.white;
        cmd[startIndex++] = ls.green;
        cmd[startIndex++] = ls.red;
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);
        return cmd;
    }

    /**
     * 下载曲线数据到单片机
     */
    private static byte[] CreateDataToLedCmd(Request request) {
        UpdateData updateData = request.getUpdateData();
        if (updateData == null) {
            DataNode modelDate = request.getData();
            switch (modelDate.getDataType()) {
                case Instant:
                    return createManualDataToLedCmd(request);
                case Curve:
                    return createAutoDataToLedCmd(request);
                case Moon:
                    return createMoonDataToLedCmd(request);
                case Timing:
                    return createAutoTimingDataToLedCmd(request);
                case Flash:
                    return creatFlashDataToLedCmd(request);
                default:
                    break;
            }
        } else {
            if (updateData.getId2() == (byte) 0x80)
                return createUpdateLedCmd0x80(request);
            else
                return createUpdateLedCmd(request);
        }

        return null;
    }


    private static byte[] createManualDataToLedCmd(Request request) {
        ManualData effectMode = (ManualData) request.getData();
        int length = 21;
        byte[] cmd = new byte[length];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[startIndex++] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[startIndex++] = effectMode.getId1(); // 包ID1
        cmd[startIndex++] = effectMode.getId2();// 包ID2
        cmd[startIndex++] = (byte) (cmd.length - 12); // 包长度

        LampChannel lc = effectMode.getChannel();
        cmd[startIndex++] = (byte) (lc != null ? lc.getPurple() : 0);
        cmd[startIndex++] = (byte) (lc != null ? lc.getBlue() : 0);
        cmd[startIndex++] = (byte) (lc != null ? lc.getWhite() : 0);
        cmd[startIndex++] = (byte) (lc != null ? lc.getGreen() : 0);
        cmd[startIndex++] = (byte) (lc != null ? lc.getRed() : 0);

        long unixTime = effectMode.getUnixTime();
        cmd[startIndex++] = (byte) ((unixTime & 0xFF000000) >> 24);
        cmd[startIndex++] = (byte) ((unixTime & 0x00FF0000) >> 16);
        cmd[startIndex++] = (byte) ((unixTime & 0x0000FF00) >> 8);
        cmd[startIndex++] = (byte) (unixTime & 0x000000FF);

        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;

    }


    private static byte[] createMoonDataToLedCmd(Request request) {
        MoonData moonData = (MoonData) request.getData();
        int length = 21;
        byte[] cmd = new byte[length];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[startIndex++] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[startIndex++] = moonData.getId1(); // 包ID1
        cmd[startIndex++] = moonData.getId2();// 包ID2
        cmd[startIndex++] = (byte) (cmd.length - 12); // 包长度

        TimeBucket time = moonData.getTime();
        cmd[startIndex++] = (byte) moonData.getLastFullMoonDay();
        cmd[startIndex++] = (byte) (time != null ? time.getStartHour() : 0);
        cmd[startIndex++] = (byte) (time != null ? time.getStartMinute() : 0);
        cmd[startIndex++] = (byte) (time != null ? time.getEndHour() : 0);
        cmd[startIndex++] = (byte) (time != null ? time.getEndMinute() : 0);

        long unixTime = moonData.getUnixTime();
        cmd[startIndex++] = (byte) ((unixTime & 0xFF000000) >> 24);
        cmd[startIndex++] = (byte) ((unixTime & 0x00FF0000) >> 16);
        cmd[startIndex++] = (byte) ((unixTime & 0x0000FF00) >> 8);
        cmd[startIndex++] = (byte) (unixTime & 0x000000FF);

        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;

    }

    /**
     * 发送时间曲线到led
     */
    private static byte[] createAutoTimingDataToLedCmd(Request request) {
        CurveData modelData = (CurveData) request.getData();
        int length = 64;

        byte[] cmd = new byte[length];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[startIndex++] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[startIndex++] = modelData.getId1(); // 包ID1
        cmd[startIndex++] = modelData.getId2();// 包ID2
        cmd[startIndex++] = (byte) (cmd.length - 12); // 包长度

        // 时间曲线
        List<CurvePoint> points = modelData.getPoints();
        for (int i = 0; i < Constants.HOUR_NUM; i++) {
            if (i >= points.size()) {
                cmd[startIndex++] = (byte) 0xff;
                cmd[startIndex++] = (byte) 0xff;
            } else {
                CurvePoint point = points.get(i);
                cmd[startIndex++] = (byte) point.getHour();
                cmd[startIndex++] = (byte) (point.getMinute() * 10);
            }
        }
        // 如果是 preview 没有包识别码
//        if (!modelData.isPreview()) {
        long unixTime = modelData.getUnixTime();
        cmd[startIndex++] = (byte) ((unixTime & 0xFF000000) >> 24);
        cmd[startIndex++] = (byte) ((unixTime & 0x00FF0000) >> 16);
        cmd[startIndex++] = (byte) ((unixTime & 0x0000FF00) >> 8);
        cmd[startIndex++] = (byte) (unixTime & 0x000000FF);
//        }

        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;
    }

    /**
     * 发送闪电设置到led
     */
    private static byte[] creatFlashDataToLedCmd(Request request) {
        FlashData dataNode = (FlashData) request.getData();
        int length = 28;
        byte[] cmd = new byte[length];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[startIndex++] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[startIndex++] = dataNode.getId1(); // 包ID1
        cmd[startIndex++] = dataNode.getId2();// 包ID2
        cmd[startIndex++] = (byte) (cmd.length - 12); // 包长度

        TimeBucket time1 = dataNode.getTime1();
        TimeBucket time2 = dataNode.getTime2();
        TimeBucket time3 = dataNode.getTime3();

        cmd[startIndex++] = (byte) (time1 != null ? time1.getStartHour() : 0);
        cmd[startIndex++] = (byte) (time1 != null ? time1.getStartMinute() : 0);
        cmd[startIndex++] = (byte) (time1 != null ? time1.getEndHour() : 0);
        cmd[startIndex++] = (byte) (time1 != null ? time1.getEndMinute() : 0);

        cmd[startIndex++] = (byte) (time2 != null ? time2.getStartHour() : 0);
        cmd[startIndex++] = (byte) (time2 != null ? time2.getStartMinute() : 0);
        cmd[startIndex++] = (byte) (time2 != null ? time2.getEndHour() : 0);
        cmd[startIndex++] = (byte) (time2 != null ? time2.getEndMinute() : 0);

        cmd[startIndex++] = (byte) (time3 != null ? time3.getStartHour() : 0);
        cmd[startIndex++] = (byte) (time3 != null ? time3.getStartMinute() : 0);
        cmd[startIndex++] = (byte) (time3 != null ? time3.getEndHour() : 0);
        cmd[startIndex++] = (byte) (time3 != null ? time3.getEndMinute() : 0);


        long unixTime = dataNode.getUnixTime();
        cmd[startIndex++] = (byte) ((unixTime & 0xFF000000) >> 24);
        cmd[startIndex++] = (byte) ((unixTime & 0x00FF0000) >> 16);
        cmd[startIndex++] = (byte) ((unixTime & 0x0000FF00) >> 8);
        cmd[startIndex++] = (byte) (unixTime & 0x000000FF);

        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;
    }

    /**
     * 发送模式数据到led
     */
    private static byte[] createAutoDataToLedCmd(Request request) {
        CurveData dataNode = (CurveData) request.getData();
        int length = 136;
        byte[] cmd = new byte[length];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[startIndex++] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[startIndex++] = dataNode.getId1(); // 包ID1
        cmd[startIndex++] = dataNode.getId2();// 包ID2
        cmd[startIndex++] = (byte) (cmd.length - 12); // 包长度

        List<CurvePoint> points = dataNode.getPoints();
        if (points == null)
            return null;
        for (int i = 0; i < Constants.HOUR_NUM; i++) {
            LampChannel lc = null;
            if (i < points.size()) {
                lc = points.get(i).getChannel();
            }
            cmd[startIndex++] = (byte) (lc != null ? lc.getPurple() : 0);
            cmd[startIndex++] = (byte) (lc != null ? lc.getBlue() : 0);
            cmd[startIndex++] = (byte) (lc != null ? lc.getWhite() : 0);
            cmd[startIndex++] = (byte) (lc != null ? lc.getGreen() : 0);
            cmd[startIndex++] = (byte) (lc != null ? lc.getRed() : 0);
        }
        // 如果是 preview 没有包识别码
//        if (!dataNode.isPreview()) {
        long unixTime = dataNode.getUnixTime();
        cmd[startIndex++] = (byte) ((unixTime & 0xFF000000) >> 24);
        cmd[startIndex++] = (byte) ((unixTime & 0x00FF0000) >> 16);
        cmd[startIndex++] = (byte) ((unixTime & 0x0000FF00) >> 8);
        cmd[startIndex++] = (byte) (unixTime & 0x000000FF);

//        }

        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;
    }

    /**
     * 发送模式数据到led
     */
    private static byte[] createUpdateLedCmd(Request request) {
        UpdateData updateData = request.getUpdateData();
        int length = 140;
        byte[] cmd = new byte[length];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[startIndex++] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[startIndex++] = updateData.getId1(); // 包ID1
        cmd[startIndex++] = updateData.getId2();// 包ID2
        cmd[startIndex++] = (byte) (0x80); // 包长度

        byte[] data = updateData.getData();
        for (int i = 0; i < data.length; i++) {
            cmd[startIndex++] = data[i];
        }
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;
    }

    /**
     * 发送模式数据到led
     */
    private static byte[] createUpdateLedCmd0x80(Request request) {
        UpdateData updateData = request.getUpdateData();
        int length = 16;
        byte[] cmd = new byte[length];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[startIndex++] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[startIndex++] = updateData.getId1(); // 包ID1
        cmd[startIndex++] = updateData.getId2();// 包ID2
        cmd[startIndex++] = (byte) (0x04); // 包长度

        byte[] data = updateData.getData();
        for (int i = 0; i < data.length; i++) {
            cmd[startIndex++] = data[i];
        }
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;
    }


    /**
     * 从LED获取曲线数据
     *
     * @param pkgId 模式
     * @return
     */
    private static byte[] CreateRecvDataFromLEDCmd(byte[] pkgId) {

        if (pkgId == null || pkgId.length != 2)
            return null;
        byte[] cmd = new byte[11];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = 0x03;// 命令数据长度
        cmd[startIndex++] = (byte) 0x21; // 命令 下载曲线数据到单片机
        cmd[startIndex++] = pkgId[0]; // 包ID1
        cmd[startIndex++] = pkgId[1];// 包ID2
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;
    }

    /**
     * 停止预览
     */
    private static byte[] CreateStopPreviewCmd() {
        byte[] cmd = new byte[9];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = 0x01;
        cmd[startIndex++] = 0x09;
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);// (byte) 0x94;
        return cmd;
    }


    /**
     * 检查灯是否就绪
     */
    private static byte[] CreateCheckReadyCmd() {
        byte[] cmd = new byte[9];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = 0x01;
        cmd[startIndex++] = 0x1C;
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);
        return cmd;
    }


    /**
     * 查询组号
     */
    private static byte[] CreateQueryGroupCmd() {
        byte[] cmd = new byte[8];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) 0x1A;
        cmd[startIndex++] = Sum(cmd, 0, 2);
        return cmd;

    }

    /**
     * 设置组号
     */
    private static byte[] CreateSetGroupCmd(Request request) {
        byte[] cmd = new byte[20];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = 0x0c;
        cmd[startIndex++] = 0x1B;
        cmd[startIndex++] = (byte) request.getIntVal();//组号
        final byte[] mac = request.getByteArray();//mac
        cmd[startIndex++] = mac[0];
        cmd[startIndex++] = mac[1];
        cmd[startIndex++] = mac[2];
        cmd[startIndex++] = mac[3];
        cmd[startIndex++] = mac[4];
        cmd[startIndex++] = mac[5];
        cmd[startIndex++] = (byte) 0x00;// temp 暂时没有用
        cmd[startIndex++] = (byte) 0x00;// temp 暂时没有用
        cmd[startIndex++] = (byte) 0x00;// temp 暂时没有用
        cmd[startIndex++] = (byte) 0x00;// temp 暂时没有用
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);
        return cmd;
    }


    /*
     * 查询曲线数据有效性
     */
    private static byte[] CreateValidateDataCmd(byte[] pkgId) {

        if (pkgId == null || pkgId.length != 2)
            return null;
        byte[] cmd = new byte[11];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) (cmd.length - ADDTION_LENGTH);
        cmd[startIndex++] = (byte) 0x22;
        cmd[startIndex++] = pkgId[0];
        cmd[startIndex++] = pkgId[1];
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);
        return cmd;
    }


    /**
     * 设置灯的状态
     */
    private static byte[] CreateSetStateCmd(LampState ls) {
        if (ls == null)
            return null;
        byte[] cmd = new byte[13];
        int startIndex = 0;
        cmd[startIndex++] = 0x34;
        cmd[startIndex++] = 0x56;

        cmd[startIndex++] = Const.getInstance().getUUID()[0];
        cmd[startIndex++] = Const.getInstance().getUUID()[1];
        cmd[startIndex++] = Const.getInstance().getUUID()[2];
        cmd[startIndex++] = Const.getInstance().getUUID()[3];
        cmd[startIndex++] = (byte) (cmd.length - ADDTION_LENGTH);// 长度
        cmd[startIndex++] = 0x04;// 命令
        cmd[startIndex++] = (byte) (ls.On ? 0x1 : 0);
        cmd[startIndex++] = ls.mode;
        cmd[startIndex++] = (byte) (ls.lighting ? 0x1 : 0);
        cmd[startIndex++] = (byte) (ls.acclimation ? 0x1 : 0);
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1);

        return cmd;
    }


    /**
     * 计算包校验码
     */
    private static byte Sum(byte[] cmd, int startIndex, int endIndex) {
        if (startIndex < 0 || startIndex > endIndex)
            return 0;
        int sum = 0;
        for (int i = startIndex; i <= endIndex && i < cmd.length; i++) {
            sum += (cmd[i] & 0xff);
        }

        return (byte) (sum & 0xff);
    }

}
