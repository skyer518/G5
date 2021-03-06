package cn.com.lightech.led_g5w.gloabal;

import java.util.List;

import cn.com.lightech.led_g5w.entity.AutoDataNode;
import cn.com.lightech.led_g5w.entity.CurvePoint;
import cn.com.lightech.led_g5w.entity.DeviceType;
import cn.com.lightech.led_g5w.entity.FlashDataNode;
import cn.com.lightech.led_g5w.entity.ManualDataNode;
import cn.com.lightech.led_g5w.entity.Mode;
import cn.com.lightech.led_g5w.entity.MoonDataNode;
import cn.com.lightech.led_g5w.entity.PackageId;
import cn.com.lightech.led_g5w.entity.TimeBucket;
import cn.com.lightech.led_g5w.net.entity.CmdType;
import cn.com.lightech.led_g5w.entity.LampChannel;
import cn.com.lightech.led_g5w.entity.LampState;
import cn.com.lightech.led_g5w.net.entity.ReplyErrorCode;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.net.utils.Logger;
import cn.com.lightech.led_g5w.utils.TimeUtil;
import cn.com.lightech.led_g5w.view.spray.Timing;
import cn.com.lightech.led_g5w.view.spray.entity.WaveNode;


/*命令解析器*/
public class CmdParser {

    private static Logger logger = Logger.getLogger(CmdParser.class);

    public static Response Parse(byte[] content) {

        Response result = new Response();
        result.setCmdType(CmdType.Unknow);

        // TODO:测试用！正式去掉这里
        result.setByteArray(content);

        // 基本长度检测
        if (content == null || content.length < 9) {
            result.setReplyCode(ReplyErrorCode.NotEnoughData);
            logger.e("返回数据长度不足，len:%d", content == null ? 0 : content.length);
            return result;
        }

        // 包头检测
        if (content[0] != 0x65 || content[1] != 0x43) {
            result.setReplyCode(ReplyErrorCode.HeaderError);
            logger.e("包头不对，d1:%d,d2:%d", content[0], content[1]);
            return result;
        }

        // 设备标识符检测
        if (content[2] != Const.getInstance().getUUID()[0]
                || content[3] != Const.getInstance().getUUID()[1]
                || content[4] != Const.getInstance().getUUID()[2]
                || content[5] != Const.getInstance().getUUID()[3]) {

            result.setReplyCode(ReplyErrorCode.NotThisDeviceResponse);
            logger.e("不是我的命令，d1:%d,d2:%d,d3:%d,d4:%d", content[2], content[3], content[4], content[5]);
            return result;
        }
        // 数据包长度检测
        if (!valideDataLength(content)) {
            result.setReplyCode(ReplyErrorCode.DataLengthError);
            logger.e("包长度不对，len:%d", content.length);
            return result;
        }
        if (!valideSize(content, result)) return result;


        ReplyErrorCode replyCode = ReplyErrorCode.UnKnow;
        int rspCmd = content[7] & 0xff;// 命令
        CmdType cmdType = CmdType.Parse(rspCmd);

        switch (cmdType) {
            case CheckReady:
                replyCode = ParseNormal(content);
                break;
            case SyncTime: // 时间同步
                replyCode = ParseNormal(content);
                break;
            case QueryState: // 查询灯的状态
                result = ParseQueryState(content);
                replyCode = result.getReplyCode();
                break;
            case SetState: // 设置灯的状态
                replyCode = ParseNormal(content);
                break;
            case OnOff: // 开关灯
                replyCode = ParseNormal(content);
                break;
            case PreviewMode: // 预览模式
                replyCode = ParseNormal(content);
                break;
            case PreViewCurve: // 预览曲线
                replyCode = ParseNormal(content);
                break;
            case StopPreview: // 结束预览命令
                replyCode = ParseNormal(content);
                break;
            case AttachSub:// 关联从灯命令
                replyCode = ParseNormal(content);
                break;
            case QueryGroup0x1A:// 查询组号命令
                result = ParseQueryGroup0x1A(content);
                replyCode = result.getReplyCode();
                break;
            case SetGroup:// 设置组号命令
                replyCode = ParseNormal(content);
                break;
            case SendDataToLED: // 下载曲线数据到单片机
                result = ParseSendDataToLED(content);
                replyCode = result.getReplyCode();
                break;
            case RecvDataFromLED: // 上传曲线数据到平板端
                result = ParseRecvDataFromLED(content);
                replyCode = result.getReplyCode();
                break;
            case ValidateData: // 查询曲线数据有效性
                result = ParseValidateData(content);
                replyCode = result.getReplyCode();
                break;

            case IDFormatError:
                replyCode = ReplyErrorCode.IDFormatError;
                logger.e("ID格式错误");
                break;
            case ValidateSumFailed:
                replyCode = ReplyErrorCode.ValidateSumFailed;
                logger.e("LED数据包校验和失败");
                break;
            default:
                break;

        }

        // TODO:测试用！正式去掉这里
        result.setByteArray(content);

        result.setCmdType(cmdType);
        result.setReplyCode(replyCode);
        logger.i("recv data，len:%d,cmdType:%s,errorcode:%s", content.length,
                cmdType.toString(), replyCode.toString());


        return result;
    }


    private static boolean valideSize(byte[] content, Response result) {

        int dataLen = content[6] & 0xff; // byte [-127~128]，表示长度时要转换

        // 校验和检测
        int nValideSize = content[dataLen + 7] & 0xff;
        int acSize = 0;
        for (int i = 0; i < 7 + dataLen; i++) {
            acSize += (content[i] & 0xff);
        }
        if ((acSize & 0xff) != nValideSize) {
            result.setReplyCode(ReplyErrorCode.ValidateCodeError);
            logger.e("校验和检测失败");
            return false;

        }
        return true;
    }

    /**
     * 判断数据包是否还需要更多数据
     */
    public static boolean needMoreData(byte[] content) {
        // 基本长度检测
        if (content == null || content.length < 9) {
            logger.e("返回数据长度不足，len:%d", content == null ? 0 : content.length);
            return true;
        }

        // 包头检测
        if (content[0] != 0x65 || content[1] != 0x43) {
            logger.e("包头不对，d1:%d,d2:%d", content[0], content[1]);
            return true;
        }

        // 数据包长度检测
        if (!valideDataLength(content)) {
            logger.e("pid1: " + content[8] + " ; pid2: " + content[9]);
            logger.e("包长度不对，len:%1d; dataLen:%2d", content.length, content[6] & 0xff);
            return true;
        }

        return false;
    }

    private static boolean valideDataLength(byte[] content) {
        int dataLen = content[6] & 0xff;
        if (content.length < dataLen + 4) {
            return false;
        }
        return true;
    }

    // 通用解析
    private static ReplyErrorCode ParseNormal(byte[] content) {
        // 一般0x55 (接收成功) 0xff（接收出错，重发）
        if (content != null && content.length > 8 && content[8] == 0x55)
            return ReplyErrorCode.OK;
        return ReplyErrorCode.LogicError;
    }

    /**
     * 下载曲线数据到单片机 (响应)
     */
    private static Response ParseSendDataToLED(byte[] content) {
        Response result = new Response();
        if (content.length < 11) {
            result.setReplyCode(ReplyErrorCode.LogicError);
            return result;
        }
        int nPos = 8;
        byte id1 = content[nPos];
        byte id2 = content[nPos + 1];
        byte ErroNo = content[nPos + 2];
        if (ErroNo == 0x55) {
            result.setPackageId(new byte[]{id1, id2});
            result.setReplyCode(ReplyErrorCode.OK);
            return result;
        }
        result.setReplyCode(ReplyErrorCode.LogicError);
        return result;
    }

    /**
     * 查询灯的状态
     */
    private static Response ParseQueryState(byte[] content) {
        Response result = new Response();

        int nPos = 8;
        byte ErroNo = content[nPos];
        if (ErroNo != 0xff) {
            LampState ls = new LampState();

            int year1 = content[nPos++] & 0xff;
            int year2 = content[nPos++] & 0xff;
            ls.setYear((year1 << 8) + year2);
            ls.setMonth(content[nPos++] & 0xff);
            ls.setDay(content[nPos++] & 0xff);
            ls.setHour(content[nPos++] & 0xff);
            ls.setMinute(content[nPos++] & 0xff);
            ls.setSwitch(content[nPos++] == 0 ? false : true);
            ls.setMode(content[nPos++]);
            ls.setLighting(content[nPos++] == 0 ? false : true);
            ls.setMoon(content[nPos++] == 0 ? false : true);
            ls.setAcclimation(content[nPos++] == 0 ? false : true);

            ls.setFanSwitch(content[nPos++] == 0 ? false : true);
            ls.setPower(content[nPos++]);

            result.setLampState(ls);
            result.setReplyCode(ReplyErrorCode.OK);
            return result;
        } else {
            result.setReplyCode(ReplyErrorCode.LogicError);
            return result;
        }

    }

    /**
     * 下载曲线数据到单片机
     */
    private static Response ParseRecvDataFromLED(byte[] content) {
        Response result = new Response();
        if (content.length < 11) {
            result.setReplyCode(ReplyErrorCode.LogicError);
            return result;
        }
        int nPos = 8;
        byte id1 = content[nPos];
        byte id2 = content[nPos + 1];
        byte[] ids = new byte[]{id1, id2};
        result.setPackageId(ids);
        int dataLength = content[nPos + 2] & 0xff;
        int startIndex = nPos + 3;
        if (ids[0] == 0x02) {
            if (dataLength != 0x10) {
                // 数据包长度不对
                result.setReplyCode(ReplyErrorCode.DataLengthError);
                logger.e("数据包内长度不足：%d,应为0x19", dataLength);
                return result;
            }

            WaveNode waveNode = new WaveNode();
            waveNode.setFunction(content[startIndex++]);
            waveNode.setEffect(content[startIndex++]);
            waveNode.setPulseS(content[startIndex++]);
            waveNode.setPulseMs(content[startIndex++]);
            waveNode.setPower(content[startIndex++]);
            waveNode.setChannel(content[startIndex++]);

            byte feed = content[startIndex++];
            waveNode.setFeed(feed == 0x00 ? false : true);
            byte autoWave = content[startIndex++];
            waveNode.setAutoWave(autoWave == 0x00 ? false : true);
            byte dayOrNight = content[startIndex++];
            waveNode.setDayOrNight(dayOrNight == 0x00 ? false : true);

            waveNode.setDaysAgo(content[startIndex++]);
            waveNode.setTime(new Timing(content[startIndex++],
                    content[startIndex++]));

            waveNode.setM1(content[startIndex++]);
            waveNode.setM2(content[startIndex++]);
            waveNode.setM3(content[startIndex++]);
            waveNode.setM4(content[startIndex++]);
            result.setWaveNode(waveNode);

        } else {
            Mode scheduleMode = PackageId.getMode(ids);
            switch (scheduleMode) {
                case Auto:
                    if (dataLength != 0x7c) {
                        // 数据包长度不对
                        result.setReplyCode(ReplyErrorCode.DataLengthError);
                        logger.e("Auto数据包内长度不足：%d,应为0x7c", dataLength);
                        return result;
                    }
                    AutoDataNode node = new AutoDataNode();
                    List<CurvePoint> points = node.getPoints();
                    for (int i = 0; i < Constants.HOUR_NUM; i++) {
                        LampChannel lc = new LampChannel();

                        lc.setPurple(content[startIndex++]);
                        lc.setBlue(content[startIndex++]);
                        lc.setWhite(content[startIndex++]);
                        lc.setGreen(content[startIndex++]);
                        lc.setRed(content[startIndex++]);
                        points.add(new CurvePoint(lc));

                    }
                    result.setDataNode(node);
//                if (scheduleMode == Mode.) {
//                    return result;
//                }
                    break;
                case Flash:
                    if (dataLength != 0x10) {
                        // 数据包长度不对
                        result.setReplyCode(ReplyErrorCode.DataLengthError);
                        logger.e("Flash 数据包内长度不足：%d,应为0x10", dataLength);
                        return result;
                    }

                    FlashDataNode flashNode = new FlashDataNode();
                    flashNode.setTime1(new TimeBucket(content[startIndex++], content[startIndex++], content[startIndex++], content[startIndex++]));
                    flashNode.setTime2(new TimeBucket(content[startIndex++], content[startIndex++], content[startIndex++], content[startIndex++]));
                    flashNode.setTime3(new TimeBucket(content[startIndex++], content[startIndex++], content[startIndex++], content[startIndex++]));

                    result.setDataNode(flashNode);
                    break;
                case Moon:
                    if (dataLength != 0x09) {
                        // 数据包长度不对
                        result.setReplyCode(ReplyErrorCode.DataLengthError);
                        logger.e("Moon 数据包内长度不足：%d,应为0x09", dataLength);
                        return result;
                    }
                    MoonDataNode moonDataNode = new MoonDataNode();
                    moonDataNode.setLastFullMoonDay(content[startIndex++]);
                    moonDataNode.setTime(new TimeBucket(content[startIndex++], content[startIndex++], content[startIndex++], content[startIndex++]));
                    result.setDataNode(moonDataNode);
                    break;
                case Manual:
                    if (dataLength != 0x09) {
                        // 数据包长度不对
                        result.setReplyCode(ReplyErrorCode.DataLengthError);
                        logger.e(" Manual 数据包内长度不足：%d,应为0x09", dataLength);
                        return result;
                    }
                    ManualDataNode manualDataNode = new ManualDataNode();
                    LampChannel channel = new LampChannel();
                    channel.setPurple(content[startIndex++]);
                    channel.setBlue(content[startIndex++]);
                    channel.setWhite(content[startIndex++]);
                    channel.setGreen(content[startIndex++]);
                    channel.setRed(content[startIndex++]);
                    manualDataNode.setChannel(channel);
                    result.setDataNode(manualDataNode);
                    break;
                case AutoTiming:
                    if (dataLength != 0x34) {
                        // 数据包长度不对
                        result.setReplyCode(ReplyErrorCode.DataLengthError);
                        logger.e("AutoTiming数据包内长度不足：%d,应为0x34", dataLength);
                        return result;
                    }

                    AutoDataNode autoDataNode = new AutoDataNode();
                    List<CurvePoint> points1 = autoDataNode.getPoints();
                    int index = 0;
                    for (int i = 0; i < Constants.HOUR_NUM; i++) {
                        CurvePoint point = new CurvePoint();
                        point.setTime(content[startIndex++] & 0xff, (content[startIndex++] & 0xff) / 10);
                        if (TimeUtil.isVali(point.getTime())) { // 无效时间点放弃
                            points1.add(point);
                            index++;
                        }

                    }
                    result.setDataNode(autoDataNode);
                    break;
                default:
                    break;
            }


            // 包标识码、时间截
            long data1 = content[startIndex++] & 0xff;
            long data2 = content[startIndex++] & 0xff;
            long data3 = content[startIndex++] & 0xff;
            long data4 = content[startIndex] & 0xff;
            long unixTIme = (data1 << 24) + (data2 << 16) + (data3 << 8) + data4;

            result.setUnixTime(unixTIme);
        }
        result.setReplyCode(ReplyErrorCode.OK);

        return result;

        /*
         * TODO:出错的情况？ result.setReplyCode(ReplyErrorCode.LogicError); return
		 * result;
		 */
    }

    /* 查询曲线数据有效性 */
    private static Response ParseValidateData(byte[] content) {
        Response result = new Response();
        if (content.length < 11) {
            result.setReplyCode(ReplyErrorCode.LogicError);
            return result;
        }
        int startIndex = 8;
        byte id1 = content[startIndex++];
        byte id2 = content[startIndex++];

        long data1 = content[startIndex++] & 0xff;
        long data2 = content[startIndex++] & 0xff;
        long data3 = content[startIndex++] & 0xff;
        long data4 = content[startIndex] & 0xff;
        long unixTIme = (data1 << 24) + (data2 << 16) + (data3 << 8) + data4;
        result.setUnixTime(unixTIme);
        result.setPackageId(new byte[]{id1, id2});
        result.setReplyCode(ReplyErrorCode.OK);
        return result;
    }



    /* 查询曲线数据有效性 */
    private static Response ParseQueryGroup0x1A(byte[] content) {
        Response result = new Response();
        if (content.length < 11) {
            result.setReplyCode(ReplyErrorCode.LogicError);
            return result;
        }
        //
        int startIndex = 8;
        int device = content[startIndex++] & 0xff;
        int groupNum = content[startIndex++];
        byte[] mac = new byte[]{content[startIndex++],
                content[startIndex++],
                content[startIndex++],
                content[startIndex++],
                content[startIndex++],
                content[startIndex++]};


        int unUse00 = content[startIndex++] & 0xff;
        int unUse01 = content[startIndex++] & 0xff;
        int unUse02 = content[startIndex++] & 0xff;
        int unUse03 = content[startIndex++] & 0xff;
        int unUse04 = content[startIndex++] & 0xff;
        int unUse05 = content[startIndex++] & 0xff;
        int unUse06 = content[startIndex++] & 0xff;
        int unUse07 = content[startIndex++] & 0xff;
        int unUse08 = content[startIndex++] & 0xff;
        int unUse09 = content[startIndex++] & 0xff;

        result.setDeviceType(DeviceType.parseInt(device));
        result.setGroupNum(groupNum);
        result.setMac(mac);
        result.setReplyCode(ReplyErrorCode.OK);
        return result;
    }

}
