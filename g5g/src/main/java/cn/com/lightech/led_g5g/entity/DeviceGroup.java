package cn.com.lightech.led_g5g.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 明 on 2016/3/4.
 */
public class DeviceGroup implements Serializable {

    private int number;

    private List<Device> devices = new ArrayList<>(0);

    public DeviceGroup(int number) {
        setNumber(number);
    }

    public DeviceGroup() {
        setNumber(0);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        if (number > 254)
            number = 254;
        if (number < 0)
            number = 0;
        this.number = number;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void addDevice(Device device) {
        for (int i = 0; i < devices.size(); i++) {
            Device currentDevice = devices.get(i);
            if (device.getIp().equals(currentDevice.getIp())) {
                return;
            }
        }
        devices.add(device);
    }

    public String getDisplayNumber() {
        return new Integer(number + 1).toString();
    }


//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(number);
//        dest.writeList(devices);
//    }
//
//
//    public static final Creator<DeviceGroup> CREATOR = new Creator<DeviceGroup>() {
//        @Override
//        public DeviceGroup createFromParcel(Parcel source) {
//            DeviceGroup group = new DeviceGroup();
//            group.number = source.readInt();
//            source.readList(group.getDevices(), DeviceGroup.class.getClassLoader());
//            return group;
//        }
//
//        @Override
//        public DeviceGroup[] newArray(int size) {
//            return new DeviceGroup[0];
//        }
//    };

}
