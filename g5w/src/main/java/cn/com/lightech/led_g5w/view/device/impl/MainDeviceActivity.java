package cn.com.lightech.led_g5w.view.device.impl;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.adapter.ExpControllableDeviceAdapter;
import cn.com.lightech.led_g5w.presenter.MainPresenter;
import cn.com.lightech.led_g5w.utils.ImageUtil;
import cn.com.lightech.led_g5w.view.AppBaseTabNavgationActivity;
import cn.com.lightech.led_g5w.view.console.impl.AutoFragment;
import cn.com.lightech.led_g5w.view.console.impl.ManualFragment;
import cn.com.lightech.led_g5w.view.device.IMainDeviceView;
import cn.com.lightech.led_g5w.wedgit.CustViewPager;
import cn.com.lightech.led_g5w.wedgit.RoundImageView;

/**
 * Created by 明 on 2016/3/4.
 */
public class MainDeviceActivity extends AppBaseTabNavgationActivity implements IMainDeviceView, View.OnLongClickListener {

    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 360;
    private static int output_Y = 360;
    ExpControllableDeviceAdapter deviceAdapter;

    @Bind(R.id.container)
    CustViewPager mViewPager;
    @Bind(R.id.iv_custPic)
    RoundImageView ivCustPic;
    @Bind(R.id.ll_device_activity)
    LinearLayout llDeviceActivity;

    MainPresenter mainDevicePresenter;
    private DeviceLEDFragment defaultFragment;
    private Fragment currentFragment;
    private PopupMenu menu;
    private String[] deviceType;

    public MainDeviceActivity() {
        super();
        mainDevicePresenter = new MainPresenter(this, this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        deviceType = getResources().getStringArray(R.array.array_device_type);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        ActionBar supportActionBar = getActionBar();
        supportActionBar.setTitle(getString(R.string.device_device_title));

        setmViewPager(mViewPager);
        setmSectionsPagerAdapter(new SectionsPagerAdapter4Device(getFragmentManager()));
        //gotoDeviceGroupFragment();
        ivCustPic.setOnLongClickListener(this);
        registerForContextMenu(ivCustPic);
    }

    @Override
    protected void loadData() {
        super.loadData();
        //将图片显示到ImageView中
        Bitmap bm = ImageUtil.readBitmapFormDirectoryPictures(IMAGE_FILE_NAME);
        if (bm != null) {
            ivCustPic.setImageBitmap(bm);
        }

    }


    void choicePhoto() {
        Intent intentFromGallery = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 设置文件类型
//        intentFromGallery.setType("image/*");
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);


//        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            ivCustPic.setImageBitmap(photo);
            try {
                ImageUtil.saveBitmapToFile(photo, IMAGE_FILE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(intent.getData());
                break;

            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            IMAGE_FILE_NAME);
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }

                break;

            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(intent);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }


    void addNewDevice() {
        Intent intent = new Intent();
        intent.setClass(this, AddDeviceActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_btn_help:
                mainDevicePresenter.showHelp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            super.onBackPressed();
        }

    }


    /**
     * 返回按钮事件，弹出确认框
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                AlertDialog isExit = new AlertDialog.Builder(this).create();
                // 设置对话框标题
                // isExit.setTitle(getResources()
                // .getString(R.string.tips_dialog_title));
                // 设置对话框消息
                isExit.setMessage(getResources().getString(
                        R.string.app_exit_application_msg));
                // 添加选择按钮并注册监听
                isExit.setButton(DialogInterface.BUTTON_POSITIVE, getResources()
                        .getString(R.string.app_exit_confirm_button_text), listener);
                isExit.setButton(DialogInterface.BUTTON_NEGATIVE, getResources()
                        .getString(R.string.app_exit_cancel_button_text), listener);
                // 显示对话框
                isExit.show();

                break;
            case KeyEvent.KEYCODE_MENU:
                super.openOptionsMenu();
                break;

        }
        super.onKeyDown(keyCode, event);
        return true;

    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onLongClick(View v) {
        if (menu == null) {
            menu = new PopupMenu(this, v);
            menu.getMenuInflater().inflate(R.menu.menu_main_userphoto, menu.getMenu());
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_menu_choice)
                        choicePhoto();
                    return false;
                }
            });
        }
        menu.show();
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter4Device extends FragmentPagerAdapter {

        public SectionsPagerAdapter4Device(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return DeviceLEDFragment.newInstance("", "");
                case 1:
                    return DeviceSprayFragment.newInstance("", "");
            }
            return AutoFragment.newInstance("", "");
        }

        @Override
        public int getCount() {
            return deviceType.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return deviceType[position];
        }
    }


//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        if (v.getId() == R.id.iv_custPic) {
//            menu.add(0, 0, Menu.NONE, "修改");
//        }
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        if (item.getItemId() == 0) {
//            choicePhoto();
//        }
//        return super.onContextItemSelected(item);
//    }
}
