package cn.com.lightech.led_g5g.entity;

/**
 * 灯的即时状态
 */
public class LampState {

    public byte white;
    public byte blue;
    public byte purple;
    public byte red;
    public byte green;
    public byte mode;// 这里把Schedule和WorkMode合并到一起了，看后面协议怎么修改
    public boolean lighting;
    public boolean moon;
    public boolean acclimation;

    public int Year;
    public int Month;
    public int Day;
    public int Hour;
    public int Minute;
    public boolean On;
    public boolean IsSwitch;
    public boolean IsFanSwitch;
    public int Power;

}
