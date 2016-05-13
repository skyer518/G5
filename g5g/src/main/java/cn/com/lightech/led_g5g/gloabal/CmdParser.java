package cn.com.lightech.led_g5g.gloabal;

import java.util.Arrays;
import java.util.List;

import cn.com.lightech.led_g5g.entity.DataType;
import cn.com.lightech.led_g5g.entity.data.CurveData;
import cn.com.lightech.led_g5g.entity.CurvePoint;
import cn.com.lightech.led_g5g.entity.DeviceType;
import cn.com.lightech.led_g5g.entity.data.FlashData;
import cn.com.lightech.led_g5g.entity.data.ManualData;
import cn.com.lightech.led_g5g.entity.data.MoonData;
import cn.com.lightech.led_g5g.entity.TimeBucket;
import cn.com.lightech.led_g5g.net.entity.CmdType;
import cn.com.lightech.led_g5g.entity.LampChannel;
import cn.com.lightech.led_g5g.entity.LampState;
import cn.com.lightech.led_g5g.net.entity.ReplyErrorCode;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.utils.Logger;
import cn.com.lightech.led_g5g.utils.TimeUtil;


/*命令解析器*/
public class CmdParser {

    private static Logger logger = Logger.getLogger(CmdParser.class);

    public static Response Parse(byte[] content) {

        Response result = new Response();
        result.setCmdType(CmdType.Unknow);

        // TODO:测试用！正式去掉这里
        result.setByteArray(content);

        // 基本长度检测
        if (content == null || content.length < 5) {
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
        if (content[2] == (byte) 0xf1 && content.length == 64) {
            if (!valideSize(content, result)) return result;
            result = ParseQueryGroup(content);
            ReplyErrorCode replyCode = result.getReplyCode();
            result.setByteArray(content);

            result.setCmdType(CmdType.QueryGroup);
            result.setReplyCode(replyCode);
            logger.i("recv data，len:%d,cmdType:%s,errorcode:%s", content.length,
                    CmdType.QueryGroup.toString(), replyCode.toString());

        } else {
            // 数据包长度检测
            if (!valideDataLength(content)) {
                result.setReplyCode(ReplyErrorCode.DataLengthError);
                logger.e("包长度不对，len:%d", content.length);
                return result;
            }
            if (!valideSize(content, result)) return result;


            ReplyErrorCode replyCode = ReplyErrorCode.UnKnow;
            int rspCmd = content[3] & 0xff;// 命令
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
                case QueryGroup:// 查询组号命令
                    result = ParseQueryGroup(content);
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

        }


        return result;
    }

    private static boolean valideSize(byte[] content, Response result) {

        int dataLen = content[2] & 0xff; // byte [-127~128]，表示长度时要转换
        if (dataLen == 0xf1) {
            dataLen = 60;
        }
        // 校验和检测
        int nValideSize = content[dataLen + 3] & 0xff;
        int acSize = 0;
        for (int i = 0; i < 3 + dataLen; i++) {
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
        if (content == null || content.length < 5) {
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
            logger.e("包长度不对，len:%1d; dataLen:%2d,data:[%3s]", content.length, content[2] & 0xff, Arrays.toString(content));
            return true;
        }
        return false;
    }

    private static boolean valideDataLength(byte[] content) {
        int dataLen = content[2] & 0xff;
        if (dataLen == 0xf1 && content.length == 64)
            return true;
        if (content.length < dataLen + 4) {
            return false;
        }
        return true;
    }

    // 通用解析
    private static ReplyErrorCode ParseNormal(byte[] content) {
        // 一般0x55 (接收成功) 0xff（接收出错，重发）
        if (content != null && content.length > 4 && content[4] == 0x55)
            return ReplyErrorCode.OK;
        return ReplyErrorCode.LogicError;
    }

    /**
     * 下载曲线数据到单片机 (响应)
     */
    private static Response ParseSendDataToLED(byte[] content) {
        Response result = new Response();
        if (content.length < 7) {
            result.setReplyCode(ReplyErrorCode.LogicError);
            return result;
        }
        int nPos = 4;
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

        int nPos = 4;
        byte ErroNo = content[nPos];
        if (ErroNo != 0xff) {
            LampState ls = new LampState();

            int year1 = content[nPos++] & 0xff;
            int year2 = content[nPos++] & 0xff;
            ls.Year = (year1 << 8) + year2;
            ls.Month = content[nPos++];
            ls.Day = content[nPos++];
            ls.Hour = content[nPos++];
            ls.Minute = content[nPos++];
            ls.IsSwitch = content[nPos++] == 0 ? false : true;
            ls.mode = content[nPos++];
            ls.lighting = content[nPos++] == 0 ? false : true;
            ls.moon = content[nPos++] == 0 ? false : true;
            ls.acclimation = content[nPos++] == 0 ? false : true;

            ls.IsFanSwitch = content[nPos++] == 0 ? false : true;
            ls.Power = content[nPos++];

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
        if (content.length < 7) {
            result.setReplyCode(ReplyErrorCode.LogicError);
            return result;
        }
        int nPos = 4;
        byte id1 = content[nPos];
        byte id2 = content[nPos + 1];
        byte[] ids = new byte[]{id1, id2};
        result.setPackageId(ids);
        int dataLength = content[nPos + 2] & 0xff;
        int startIndex = nPos + 3;

        DataType dataType = DataType.valueOfIds(ids[0], ids[1]);
        switch (dataType) {
            case Curve:
                if (dataLength != 0x7c) {
                    // 数据包长度不对
                    result.setReplyCode(ReplyErrorCode.DataLengthError);
                    logger.e("Auto数据包内长度不足：%d,应为0x7c", dataLength);
                    return result;
                }
                CurveData node = new CurveData(ids[0], ids[1]);
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
//                if (scheduleMode == DataType.) {
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

                FlashData flashNode = new FlashData();
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
                MoonData moonData = new MoonData();
                moonData.setLastFullMoonDay(content[startIndex++]);
                moonData.setTime(new TimeBucket(content[startIndex++], content[startIndex++], content[startIndex++], content[startIndex++]));
                result.setDataNode(moonData);
                break;
            case Instant:
                if (dataLength != 0x09) {
                    // 数据包长度不对
                    result.setReplyCode(ReplyErrorCode.DataLengthError);
                    logger.e(" Instant 数据包内长度不足：%d,应为0x09", dataLength);
                    return result;
                }
                ManualData manualData = new ManualData();
                LampChannel channel = new LampChannel();
                channel.setPurple(content[startIndex++]);
                channel.setBlue(content[startIndex++]);
                channel.setWhite(content[startIndex++]);
                channel.setGreen(content[startIndex++]);
                channel.setRed(content[startIndex++]);
                manualData.setChannel(channel);
                result.setDataNode(manualData);
                break;
            case Timing:
                if (dataLength != 0x34) {
                    // 数据包长度不对
                    result.setReplyCode(ReplyErrorCode.DataLengthError);
                    logger.e("AutoTiming数据包内长度不足：%d,应为0x34", dataLength);
                    return result;
                }

                CurveData curveData = new CurveData(ids[0], ids[1]);
                List<CurvePoint> points1 = curveData.getPoints();
                int index = 0;
                for (int i = 0; i < Constants.HOUR_NUM; i++) {
                    CurvePoint point = new CurvePoint();
                    point.setTime(content[startIndex++] & 0xff, (content[startIndex++] & 0xff) / 10);
                    if (TimeUtil.isVali(point.getTime())) { // 无效时间点放弃
                        points1.add(point);
                        index++;
                    }

                }
                result.setDataNode(curveData);
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
        if (content.length < 7) {
            result.setReplyCode(ReplyErrorCode.LogicError);
            return result;
        }
        int startIndex = 4;
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
    private static Response ParseQueryGroup(byte[] content) {
        Response result = new Response();
        if (content.length < 7) {
            result.setReplyCode(ReplyErrorCode.LogicError);
            return result;
        }
        int startIndex = 3;
        int device = content[startIndex++];
        int num = content[startIndex++];
        byte[] mac = new byte[]{content[startIndex++],
                content[startIndex++],
                content[startIndex++],
                content[startIndex++],
                content[startIndex++],
                content[startIndex++]};

        result.setDeviceType(DeviceType.parseInt(device));
        result.setGroupNum(num);
        result.setMac(mac);
        result.setReplyCode(ReplyErrorCode.OK);
        return result;
    }

}
