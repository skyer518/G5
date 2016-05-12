package cn.com.lightech.led_g5w.gloabal;

import android.app.Application;

public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    
    /**
     * 获取对应id的string资源中的字符串
     * @param id  string资源id
     * @return
     */
    protected static String getResString(int id) {
    	return instance.getResources().getString(id);
    }
    
}
