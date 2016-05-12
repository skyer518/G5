package cn.com.u2be.xbase.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 明 on 2016/3/10.
 */
public abstract class BaseFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initVariables(savedInstanceState);
        View view = initView(inflater, container);
        loadData();
        return view;
    }

    protected abstract void initVariables(Bundle savedInstanceState);

    protected abstract View initView(LayoutInflater inflater, ViewGroup container);

    protected abstract void loadData();


}
