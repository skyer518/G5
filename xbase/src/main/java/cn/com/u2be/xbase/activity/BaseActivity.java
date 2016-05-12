package cn.com.u2be.xbase.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by 明 on 2016/2/26.
 */
public abstract class BaseActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables(savedInstanceState);
        initView();
        loadData();
    }


    protected abstract void initVariables(Bundle savedInstanceState);

    protected abstract void initView();

    protected abstract void loadData();


}
