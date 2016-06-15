package cn.com.lightech.led_g5g.entity;

/**
 * 灯的即时状态
 */
public class LampState {


    private byte white;
    private byte blue;
    private byte purple;
    private byte red;
    private byte green;
    private byte mode;// 这里把Schedule和WorkMode合并到一起了，看后面协议怎么修改
    private boolean lighting;
    private boolean moon;
    private boolean acclimation;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private boolean on;
    private boolean isSwitch;
    private boolean isFanSwitch;
    private int power;


    public LampState() {

    }

    private byte validChannelValue(byte value) {
        if (value > 100) {
            value = 100;
        }
        if (value < 0) {
            value = 0;
        }
        return value;
    }

    public void setWhite(byte white) {
        this.white = validChannelValue(white);
    }

    public void setBlue(byte blue) {
        this.blue = validChannelValue(blue);
    }

    public void setPurple(byte purple) {
        this.purple = validChannelValue(purple);
    }

    public void setRed(byte red) {
        this.red = validChannelValue(red);
    }

    public void setGreen(byte green) {
        this.green = validChannelValue(green);
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public void setLighting(boolean lighting) {
        this.lighting = lighting;
    }

    public void setMoon(boolean moon) {
        this.moon = moon;
    }

    public void setAcclimation(boolean acclimation) {
        this.acclimation = acclimation;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        if (hour > 24) {
            hour = 24;
        }
        if (hour < 0) {
            hour = 0;
        }
        this.hour = hour;
    }

    public void setMinute(int minute) {
        if (minute > 60) {
            minute = 60;
        }
        if (minute < 0) {
            minute = 0;
        }
        this.minute = minute;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public void setSwitch(boolean aSwitch) {
        isSwitch = aSwitch;
    }

    public void setFanSwitch(boolean fanSwitch) {
        isFanSwitch = fanSwitch;
    }

    public void setPower(int power) {
        this.power = power;
    }


    public byte getWhite() {
        return white;
    }

    public byte getBlue() {
        return blue;
    }

    public byte getPurple() {
        return purple;
    }

    public byte getRed() {
        return red;
    }

    public byte getGreen() {
        return green;
    }

    public byte getMode() {
        return mode;
    }

    public boolean isLighting() {
        return lighting;
    }

    public boolean isMoon() {
        return moon;
    }

    public boolean isAcclimation() {
        return acclimation;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isOn() {
        return on;
    }

    public boolean isSwitch() {
        return isSwitch;
    }

    public boolean isFanSwitch() {
        return isFanSwitch;
    }

    public int getPower() {
        return power;
    }
}
