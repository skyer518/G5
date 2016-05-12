package cn.com.lightech.led_g5g.view.Other.impl;

import android.app.Activity;


public class MainActivity extends Activity {

//    @Bind(cn.com.u2be.led_g5g.R.id.tv_info)
//    TextView tvInfo;
//    @Bind(cn.com.u2be.led_g5g.R.id.btn_groupBroadcast)
//    Button btnGroupBroadcast;
//    @Bind(cn.com.u2be.led_g5g.R.id.btn_singleBroadcast)
//    Button btnSingleBroadcast;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(cn.com.u2be.led_g5g.R.layout.activity_main);
//        ButterKnife.bind(this);
//
//        final UDPManager manager = new UDPManager();
//        btnGroupBroadcast.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tvInfo.setText("");
//                manager.SendAndRecvied(new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        tvInfo.setText(tvInfo.getText() + msg.obj.toString());
//                    }
//                });
//            }
//        });
//
//        btnSingleBroadcast.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String ip = tvInfo.getText().toString();
//                tvInfo.setText("");
//                manager.send(ip, 988, new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        tvInfo.setText(tvInfo.getText() + msg.obj.toString());
//                    }
//                });
//            }
//        });
//
//
//    }
}
