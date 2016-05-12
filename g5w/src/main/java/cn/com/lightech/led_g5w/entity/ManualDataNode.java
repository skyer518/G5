package cn.com.lightech.led_g5w.entity;

/**
 * Created by 明 on 2016/3/15.
 */
public class ManualDataNode extends DataNode {

    public ManualDataNode() {
        super(Mode.Manual);
    }


    public LampChannel getChannel() {
        return channel;
    }

    public void setChannel(LampChannel channel) {
        this.channel = channel;
    }

    private LampChannel channel;


}
