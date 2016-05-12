package cn.com.lightech.led_g5g.entity.data;

import cn.com.lightech.led_g5g.entity.DataNode;
import cn.com.lightech.led_g5g.entity.LampChannel;
import cn.com.lightech.led_g5g.entity.PackageId;

/**
 * Created by 明 on 2016/3/15.
 */
public class ManualData extends DataNode {

    public ManualData() {
        super(PackageId.Instant);
    }


    public LampChannel getChannel() {
        return channel;
    }

    public void setChannel(LampChannel channel) {
        this.channel = channel;
    }

    private LampChannel channel;


}
