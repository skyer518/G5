package cn.com.lightech.led_g5w.gloabal;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cn.com.lightech.led_g5w.entity.AutoDataNode;
import cn.com.lightech.led_g5w.entity.CurvePoint;
import cn.com.lightech.led_g5w.entity.DataNode;
import cn.com.lightech.led_g5w.entity.FlashDataNode;
import cn.com.lightech.led_g5w.entity.ManualDataNode;
import cn.com.lightech.led_g5w.entity.MoonDataNode;
import cn.com.lightech.led_g5w.entity.TimeBucket;
import cn.com.lightech.led_g5w.entity.LampChannel;
import cn.com.lightech.led_g5w.entity.LampState;
import cn.com.lightech.led_g5w.entity.UpdataNode;
import cn.com.lightech.led_g5w.net.entity.Request;


/**
 * 命令构建器
 */
public class CmdBuilder {

    private static final int ADDTION_LENGTH = 4;// 用来计算数据包内长度

    /**
     * 构建发送给LED的命令
     *
     * @param request 命令参数的封装
     * @return 包数据byte[]
     * @throws IllegalArgumentException
     */
    public static byte[] Build(Request request) throws IllegalArgumentException {
        switch (request.getCmdType()) {
            case AttachSub:
                return CmdBuilder.CreateAttachSubCmd();
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

        byte[] cmd = new byte[6];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) 0xff;
        cmd[3] = (byte) 0xA5;
        cmd[4] = (byte) groupNo;
        cmd[5] = Sum(cmd, 0, 4);
        return cmd;
    }

    /**
     * 时间同步
     */
    private static byte[] CreateSyncTimeCmd() {

        Calendar calendar = Calendar.getInstance();

        byte[] cmd = new byte[12];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x08;// 数据包长度
        cmd[3] = 0x01;// 命令
        int year = calendar.get(Calendar.YEAR);
        int year1 = (year & 0xFF00) >> 8;
        int year2 = (year & 0xFF);
        cmd[4] = (byte) year1;
        cmd[5] = (byte) year2;
        cmd[6] = (byte) (calendar.get(Calendar.MONTH) + 1);
        cmd[7] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
        cmd[8] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        cmd[9] = (byte) calendar.get(Calendar.MINUTE);
        cmd[10] = (byte) calendar.get(Calendar.SECOND);
        cmd[11] = Sum(cmd, 0, 10);

        return cmd;
    }

    /**
     * 查询灯的状态
     */
    private static byte[] CreateQueryStateCmd() {
        byte[] cmd = new byte[5];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x01;
        cmd[3] = 0x03;
        cmd[4] = Sum(cmd, 0, 3);
        return cmd;
    }

    /**
     * 开关灯
     */
    private static byte[] CreateSetOnOffCmd(boolean on) {
        byte[] cmd = new byte[6];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x02;// 长度
        cmd[3] = 0x06;// 命令
        cmd[4] = (byte) (on ? 1 : 0);// ID
        cmd[5] = Sum(cmd, 0, 4);
        return cmd;
    }

    /**
     * 预览曲线
     *
     * @param speed (0: normal(时间点/3秒) 1:x2(时间点/2秒) 2: x3(时间点/1秒))
     * @return
     */
    private static byte[] CreatePreviewCurveCmd(int speed) {
        byte[] cmd = new byte[6];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x02;
        cmd[3] = 0x08; // 预览命令
        cmd[4] = (byte) speed; // 预览速度
        cmd[5] = Sum(cmd, 0, 4);
        return cmd;
    }

    /**
     * 预览模式，只是预览即时状态，主要用于即时模式
     */
    private static byte[] CreatePreviewCmd(LampState ls) {
        if (ls == null)
            throw new IllegalArgumentException("missing lampstate argument");
        byte[] cmd = new byte[10];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x06;
        cmd[3] = 0x07; // 模式预览，各个通道的预览也是这样，
        /* 这里通道调整下顺序 */
        cmd[4] = ls.purple;
        cmd[5] = ls.blue;
        cmd[6] = ls.white;
        cmd[7] = ls.green;
        cmd[8] = ls.red;
        cmd[9] = Sum(cmd, 0, 8);
        return cmd;
    }

    /**
     * 下载曲线数据到单片机
     */
    private static byte[] CreateDataToLedCmd(Request request) {
        UpdataNode updataNode = request.getUpdataNode();
        if (updataNode == null) {
            DataNode modelDate = request.getModelDate();
            switch (modelDate.getScheduleMode()) {
                case Manual:
                    return createManualDataToLedCmd(request);
                case Auto:
                    return createAutoDataToLedCmd(request);
                case Moon:
                    return createMoonDataToLedCmd(request);
                case AutoTiming:
                    return createAutoTimingDataToLedCmd(request);
                case Flash:
                    return creatFlashDataToLedCmd(request);
                default:
                    break;
            }
        } else {
            if (updataNode.getID2() == (byte) 0x80)
                return createCheckUpdataLedCmd(request);
            else
                return createUpdataLedCmd(request);
        }

        return null;
    }


    private static byte[] createManualDataToLedCmd(Request request) {
        ManualDataNode effectMode = (ManualDataNode) request.getModelDate();
        int length = 17;
        byte[] cmd = new byte[length];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[3] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[4] = effectMode.getID1(); // 包ID1
        cmd[5] = effectMode.getID2();// 包ID2
        cmd[6] = (byte) (cmd.length - 8); // 包长度
        int startIndex = 7;
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
        MoonDataNode moonDataNode = (MoonDataNode) request.getModelDate();
        int length = 17;
        byte[] cmd = new byte[length];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[3] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[4] = moonDataNode.getID1(); // 包ID1
        cmd[5] = moonDataNode.getID2();// 包ID2
        cmd[6] = (byte) (cmd.length - 8); // 包长度
        int startIndex = 7;
        TimeBucket time = moonDataNode.getTime();
        cmd[startIndex++] = (byte) moonDataNode.getLastFullMoonDay();
        cmd[startIndex++] = (byte) (time != null ? time.getStartHour() : 0);
        cmd[startIndex++] = (byte) (time != null ? time.getStartMinute() : 0);
        cmd[startIndex++] = (byte) (time != null ? time.getEndHour() : 0);
        cmd[startIndex++] = (byte) (time != null ? time.getEndMinute() : 0);

        long unixTime = moonDataNode.getUnixTime();
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
        AutoDataNode modelData = (AutoDataNode) request.getModelDate();
        int length = 60;
        if (modelData.isPreview()) {
            length = 56;
        }
        byte[] cmd = new byte[length];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[3] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[4] = modelData.getID1(); // 包ID1
        cmd[5] = modelData.getID2();// 包ID2
        cmd[6] = (byte) (cmd.length - 8); // 包长度
        int startIndex = 7;
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
        if (!modelData.isPreview()) {
            long unixTime = modelData.getUnixTime();
            cmd[startIndex++] = (byte) ((unixTime & 0xFF000000) >> 24);
            cmd[startIndex++] = (byte) ((unixTime & 0x00FF0000) >> 16);
            cmd[startIndex++] = (byte) ((unixTime & 0x0000FF00) >> 8);
            cmd[startIndex++] = (byte) (unixTime & 0x000000FF);
        }

        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;
    }

    /**
     * 发送闪电设置到led
     */
    private static byte[] creatFlashDataToLedCmd(Request request) {
        FlashDataNode dataNode = (FlashDataNode) request.getModelDate();
        int length = 24;
        byte[] cmd = new byte[length];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[3] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[4] = dataNode.getID1(); // 包ID1
        cmd[5] = dataNode.getID2();// 包ID2
        cmd[6] = (byte) (cmd.length - 8); // 包长度
        int startIndex = 7;

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
        AutoDataNode dataNode = (AutoDataNode) request.getModelDate();
        int length = 132;
        if (dataNode.isPreview()) {
            length = 128;
        }
        byte[] cmd = new byte[length];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[3] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[4] = dataNode.getID1(); // 包ID1
        cmd[5] = dataNode.getID2();// 包ID2
        cmd[6] = (byte) (cmd.length - 8); // 包长度
        int startIndex = 7;

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
        if (!dataNode.isPreview()) {
            long unixTime = dataNode.getUnixTime();
            cmd[startIndex++] = (byte) ((unixTime & 0xFF000000) >> 24);
            cmd[startIndex++] = (byte) ((unixTime & 0x00FF0000) >> 16);
            cmd[startIndex++] = (byte) ((unixTime & 0x0000FF00) >> 8);
            cmd[startIndex++] = (byte) (unixTime & 0x000000FF);

        }

        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;
    }

    /**
     * 发送模式数据到led
     */
    private static byte[] createUpdataLedCmd(Request request) {
        UpdataNode updataNode = request.getUpdataNode();
        int length = 136;
        byte[] cmd = new byte[length];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[3] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[4] = updataNode.getID1(); // 包ID1
        cmd[5] = updataNode.getID2();// 包ID2
        cmd[6] = (byte) (0x80); // 包长度
        int startIndex = 7;
        byte[] data = updataNode.getData();
        for (int i = 0; i < data.length; i++) {
            cmd[startIndex++] = data[i];
        }
        cmd[startIndex] = Sum(cmd, 0, startIndex - 1); // 校验和
        return cmd;
    }

    /**
     * 发送模式数据到led
     */
    private static byte[] createCheckUpdataLedCmd(Request request) {
        UpdataNode updataNode = request.getUpdataNode();
        int length = 12;
        byte[] cmd = new byte[length];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) (cmd.length - ADDTION_LENGTH);// 0x7e;// 命令数据长度
        cmd[3] = (byte) 0x20; // 命令 下载曲线数据到单片机
        cmd[4] = updataNode.getID1(); // 包ID1
        cmd[5] = updataNode.getID2();// 包ID2
        cmd[6] = (byte) (0x04); // 包长度
        int startIndex = 7;
        byte[] data = updataNode.getData();
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
        byte[] cmd = new byte[7];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x03;// 命令数据长度
        cmd[3] = (byte) 0x21; // 命令 下载曲线数据到单片机
        cmd[4] = pkgId[0]; // 包ID1
        cmd[5] = pkgId[1];// 包ID2
        cmd[6] = Sum(cmd, 0, 5); // 校验和
        return cmd;
    }

    /**
     * 停止预览
     */
    private static byte[] CreateStopPreviewCmd() {
        byte[] cmd = new byte[5];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x01;
        cmd[3] = 0x09;
        cmd[4] = Sum(cmd, 0, 3);// (byte) 0x94;
        return cmd;
    }

    /**
     * 查询SN
     */
    private static byte[] CreateQuerySNCmd() {
        byte[] cmd = new byte[5];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x01;
        cmd[3] = (byte) 0xf7;
        cmd[4] = (byte) 0x82;
        return cmd;
    }

    /**
     * 检查灯是否就绪
     */
    private static byte[] CreateCheckReadyCmd() {
        byte[] cmd = new byte[5];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x01;
        cmd[3] = 0x1C;
        cmd[4] = Sum(cmd, 0, 3);
        return cmd;
    }

    /**
     * 从灯建立与当前主灯关联命令 AttachSub
     */
    private static byte[] CreateAttachSubCmd() {
        byte[] cmd = new byte[5];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x01;
        cmd[3] = 0x10;
        cmd[4] = Sum(cmd, 0, 3);// 0x9B;
        return cmd;
    }

    /**
     * 查询组号
     */
    private static byte[] CreateQueryGroupCmd() {
//        byte[] cmd = new byte[4];
//        cmd[0] = 0x34;
//        cmd[1] = 0x56;
//        cmd[2] = (byte) 0xF1;
//        cmd[3] = Sum(cmd, 0, 2);
//        return cmd;
        byte[] cmd = new byte[5];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x01;
        cmd[3] = 0x1A;
        cmd[4] = Sum(cmd, 0, 3);
        return cmd;
    }

    /**
     * 设置组号
     */
    private static byte[] CreateSetGroupCmd(Request request) {
        byte[] cmd = new byte[14];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x0A;
        cmd[3] = 0x1B;
        cmd[4] = (byte) request.getIntVal();
        final byte[] mac = request.getByteArray();
        cmd[5] = (byte) mac[0];
        cmd[6] = (byte) mac[1];
        cmd[7] = (byte) mac[2];
        cmd[8] = (byte) mac[3];
        cmd[9] = (byte) mac[4];
        cmd[10] = (byte) mac[5];
        cmd[11] = (byte) 0x11;
        cmd[12] = (byte) 0x11;
        cmd[13] = Sum(cmd, 0, 12);
        return cmd;
    }

    /**
     * 检查硬件
     */
    private static byte[] CreateCheckHardWareCmd() {
        byte[] cmd = new byte[5];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x01;
        cmd[3] = (byte) 0xf4;
        cmd[4] = Sum(cmd, 0, 3);// 0x7F;
        return cmd;
    }

    /*
     * 查询曲线数据有效性
     */
    private static byte[] CreateValidateDataCmd(byte[] pkgId) {

        if (pkgId == null || pkgId.length != 2)
            return null;
        byte[] cmd = new byte[7];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) (cmd.length - ADDTION_LENGTH);
        cmd[3] = (byte) 0x22;
        cmd[4] = pkgId[0];
        cmd[5] = pkgId[1];
        cmd[6] = Sum(cmd, 0, 5);
        return cmd;
    }

    /*
     * 查询Storm
     */
    private static byte[] CreateQueryStormCmd() {

        byte[] cmd = new byte[5];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = 0x01;
        cmd[3] = 0x0A;
        cmd[4] = Sum(cmd, 0, 3);
        return cmd;
    }

    /**
     * 设置灯的状态
     */
    private static byte[] CreateSetStateCmd(LampState ls) {
        if (ls == null)
            return null;
        byte[] cmd = new byte[10];
        cmd[0] = 0x34;
        cmd[1] = 0x56;
        cmd[2] = (byte) (cmd.length - ADDTION_LENGTH);// 长度
        cmd[3] = 0x04;// 命令
        cmd[4] = (byte) (ls.On ? 0x1 : 0);
        cmd[5] = ls.mode;
        cmd[6] = (byte) (ls.lighting ? 0x1 : 0);
        cmd[7] = (byte) (ls.moon ? 0x1 : 0);
        cmd[8] = (byte) (ls.acclimation ? 0x1 : 0);
        cmd[9] = Sum(cmd, 0, 8);

        return cmd;
    }

    /**
     * 更新硬件
     */
    private static byte[] CreateUpdateHardwareCmd() {

        byte[] cmd = new byte[5];
        /*
         * cmd[0]=0x34; cmd[1]=0x56; cmd[2]=0x01; cmd[3]=0x0A; cmd[4]=(byte)
		 * ((cmd[0]+cmd[1]+cmd[2]+cmd[3])&0xff);
		 */
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
