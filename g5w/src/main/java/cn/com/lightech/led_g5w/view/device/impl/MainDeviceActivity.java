package cn.com.lightech.led_g5w.view.device.impl;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.gloabal.Const;
import cn.com.lightech.led_g5w.net.utils.Logger;
import cn.com.lightech.led_g5w.presenter.MainPresenter;
import cn.com.lightech.led_g5w.utils.ImageUtil;
import cn.com.lightech.led_g5w.view.AppBaseTabNavgationActivity;
import cn.com.lightech.led_g5w.view.device.IMainDeviceView;
import cn.com.lightech.led_g5w.wedgit.CustViewPager;
import cn.com.lightech.led_g5w.wedgit.RoundImageView;

/**
 * Created by 明 on 2016/3/4.
 */
public class MainDeviceActivity extends AppBaseTabNavgationActivity implements IMainDeviceView, View.OnLongClickListener {
    private Logger logger = Logger.getLogger(MainDeviceActivity.class);
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_g5w_head_image.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 360;
    private static int output_Y = 360;

    @Bind(R.id.container)
    CustViewPager mViewPager;
    @Bind(R.id.iv_custPic)
    RoundImageView ivCustPic;

    MainPresenter mainDevicePresenter;

    private DeviceLEDFragment ledFragment = DeviceLEDFragment.newInstance("", "");
    private DeviceSprayFragment sprayFragment = DeviceSprayFragment.newInstance("", "");
    private PopupMenu menu;
    private String[] deviceType;
    private MenuItem scanMenu;
    private boolean isScanning;

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
        checkPremision();
        loadUUID();
        //将图片显示到ImageView中
        Bitmap bm = ImageUtil.readBitmapFormDirectoryPictures(IMAGE_FILE_NAME);
        if (bm != null) {
            ivCustPic.setImageBitmap(bm);
        }

    }

    private void loadUUID() {
        byte[] uuid = new byte[4];
        final SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        int uuid1 = sharedPreferences.getInt("UUID1", -1);
        int uuid2 = sharedPreferences.getInt("UUID2", -1);
        int uuid3 = sharedPreferences.getInt("UUID3", -1);
        int uuid4 = sharedPreferences.getInt("UUID4", -1);

        if (uuid1 == -1 && uuid2 == -1 && uuid3 == -1 && uuid4 == -1) {
            uuid = createUUIDByRandom();
            saveUUID(uuid);
        } else {
            uuid[0] = (byte) uuid1;
            uuid[1] = (byte) uuid2;
            uuid[2] = (byte) uuid3;
            uuid[3] = (byte) uuid4;
        }


        Const.getInstance().setUUID(uuid);


    }

    private void saveUUID(byte[] uuid) {
        final SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("UUID1", uuid[0]);
        edit.putInt("UUID2", uuid[1]);
        edit.putInt("UUID3", uuid[2]);
        edit.putInt("UUID4", uuid[3]);
    }

    private byte[] createUUIDByRandom() {
        byte[] uuid = new byte[4];
        for (int i = 0; i < 4; i++) {
            Random random = new Random();
            Integer next = random.nextInt();
            uuid[i] = next.byteValue();
        }
        return uuid;
    }

    public void checkPremision() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, 0xAb);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0xAb) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                }
            }
            if (!isAllGranted) {
                finish();
            }
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
        getMenuInflater().inflate(R.menu.menu_actionbar_device_main, menu);
        scanMenu = menu.findItem(R.id.action_btn_device_scanning);
        scanLoading(isScanning);

        return false;
        // return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_btn_help:
                mainDevicePresenter.showHelp();
                break;
            case R.id.action_btn_device_scanning:
                mainDevicePresenter.scanDevice();
                //scanLoading(true);
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

    @Override
    public void scanLoading(boolean isScanning) {
        this.isScanning = isScanning;
        if (scanMenu != null) {
            if (isScanning) {
                scanMenu.setActionView(R.layout.actionbar_indeterminate_progress);
                scanMenu.setEnabled(false);
            } else {
                scanMenu.setActionView(null);
                scanMenu.setEnabled(true);
            }
        }
    }

    @Override
    public void refresh() {
        ledFragment.setDevices(mainDevicePresenter.getDeviceGroups());
        sprayFragment.setDevices(mainDevicePresenter.getDevices());
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.i("DeviceLEDFragment", "onResume");
        mainDevicePresenter.start();
    }


    @Override
    public void onStop() {
        super.onStop();
        mainDevicePresenter.stop();
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
                    return ledFragment; //DeviceLEDFragment.newInstance("", "");
                case 1:
                    return sprayFragment;//DeviceSprayFragment.newInstance("", "");
            }
            return ledFragment;
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
