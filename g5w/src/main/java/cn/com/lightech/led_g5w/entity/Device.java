package cn.com.lightech.led_g5w.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by 明 on 2016/3/4.
 */
public class Device implements Serializable {

    private int groupNumber;
    private int number;
    private String ip;
    private DeviceType type;


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    private String mac;

    public Device() {
    }

    public Device(int groupNumber, int number, String ip) {
        this.groupNumber = groupNumber;
        this.number = number;
        this.ip = ip;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public DeviceType getType() {
        return type;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    //    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(number);
//        dest.writeString(ip);
//    }
//
//
//    public static final Creator<Device> CREATOR = new Creator<Device>() {
//        @Override
//        public Device createFromParcel(Parcel source) {
//            Device device = new Device();
//            device.number = source.readInt();
//            device.ip = source.readString();
//            return device;
//        }
//
//        @Override
//        public Device[] newArray(int size) {
//            return new Device[0];
//        }
//    };

}
