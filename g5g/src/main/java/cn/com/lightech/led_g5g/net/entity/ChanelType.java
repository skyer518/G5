package cn.com.lightech.led_g5g.net.entity;

/**
 * Created by 明 on 2016/3/30.
 */
public enum ChanelType {
    Bule(0), White(1), PurPle(2), Green(3), Red(4);

    private final int index;

    private ChanelType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }


}
