package com.ql.jcjr.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ql.jcjr.R;
import com.ql.jcjr.application.JcbApplication;
import com.ql.jcjr.base.BaseActivity;
import com.ql.jcjr.constant.AppConfig;
import com.ql.jcjr.constant.RequestURL;
import com.ql.jcjr.entity.MineFragmentEntity;
import com.ql.jcjr.entity.RiskWarningEntity;
import com.ql.jcjr.entity.UploadPicEntity;
import com.ql.jcjr.entity.UserData;
import com.ql.jcjr.http.HttpRequestManager;
import com.ql.jcjr.http.HttpSenderController;
import com.ql.jcjr.http.ParamsManager;
import com.ql.jcjr.http.ResponseEntity;
import com.ql.jcjr.http.SenderResultModel;
import com.ql.jcjr.net.GsonParser;
import com.ql.jcjr.utils.AppConfigCommon;
import com.ql.jcjr.utils.CommonUtils;
import com.ql.jcjr.utils.FileUtil;
import com.ql.jcjr.utils.ImageUtil;
import com.ql.jcjr.utils.LogUtil;
import com.ql.jcjr.utils.StringUtils;
import com.ql.jcjr.utils.ToastUtil;
import com.ql.jcjr.utils.UrlUtil;
import com.ql.jcjr.utils.crypt.DesUtil;
import com.ql.jcjr.view.ActionSheet;
import com.ql.jcjr.view.CommonToast;
import com.ql.jcjr.view.ImageTextHorizontalBarLess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends BaseActivity {

    public static final int PICK_CODE = 2;
    public static final int  SELECT_IMAGE = 3;
    public static final int CAPTURE_IMAGE = 4;
    public static final int  CROP_IMAGE = 5;
    private static final int CODE_CHANGE_LOGIN_PWD = 0;
    private static final int CODE_GESTURE = 1;
//    @ViewInject(R.id.ithb_avatar)
//    private ImageTextHorizontalBar mIthbAvatar;
    @ViewInject(R.id.ithb_bind_mobile)
    private ImageTextHorizontalBarLess mIthbMobile;
    @ViewInject(R.id.ithb_real_name)
    private ImageTextHorizontalBarLess mTthbRealName;

    @ViewInject(R.id.ithb_address)
    private ImageTextHorizontalBarLess getmTthbAddress;

    @ViewInject(R.id.ithb_bank)
    private ImageTextHorizontalBarLess mTthbBank;

    @ViewInject(R.id.ithb_account_security)
    private ImageTextHorizontalBarLess mSecurity;

    @ViewInject(R.id.ithb_user_icon)
    private ImageTextHorizontalBarLess ithb_user_icon;

    @ViewInject(R.id.ithb_about_us)
    private ImageTextHorizontalBarLess mTthbAboutUs;

    @ViewInject(R.id.ithb_test_danger)
    private ImageTextHorizontalBarLess ithb_test_danger;

    private Context mContext;
    private String mIsSetPay;
    private String mTakePhotoPath;
    private Bitmap mPhotoBitmap;

    private boolean hasShiMing = false;
    private boolean hasBindBank = false;
    private boolean hasSetTransPwd = false;
    private boolean needLoadInfo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ViewUtils.inject(this);
        mContext = this;
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(needLoadInfo){
            needLoadInfo = false;
            getMineFragmentData();
        }


    }

    private void getRiskWarning() {
        SenderResultModel resultModel = ParamsManager.getRisk();

        HttpRequestManager.httpRequestService(resultModel,
                new HttpSenderController.ViewSenderCallback() {

                    @Override
                    public void onSuccess(String responeJson) {
                        LogUtil.i("风险测评 " + responeJson);
                        RiskWarningEntity entity = GsonParser.getParsedObj(responeJson, RiskWarningEntity.class);
                        RiskWarningEntity.ResultBean resultBean = entity.getResult();
                        if(StringUtils.isBlank(resultBean.getType())||resultBean.getType()==null){
                            ithb_test_danger.setRightTitleText("未测评");
                            ithb_test_danger.setRightTitleColor(getResources().getColor(R.color.font_grey_three));
                            UserData.getInstance().setRiskWarning(false);
                        }else {
                            ithb_test_danger.setRightTitleText(resultBean.getType());
                            ithb_test_danger.setRightTitleColor(getResources().getColor(R.color.font_black));
                            UserData.getInstance().setRiskWarning(true);
                        }
                    }

                    @Override
                    public void onFailure(ResponseEntity entity) {
                        LogUtil.i("风险测评 " + entity.errorInfo);
                    }

                }, mContext);

    }

    private void init() {
        //关于
        mTthbAboutUs.setRightTitleText("v"+ CommonUtils.getAppVersionName());

//        mUserIconUrl = getIntent().getStringExtra("user_icon_url");
//        mIthbMobile.setRightTitleText(UserData.getInstance().getPhoneNumber());
//        getMineFragmentData();
    }

    public void getMineFragmentData() {
        SenderResultModel resultModel = ParamsManager.senderMineFragment();

        HttpRequestManager.httpRequestService(resultModel,
                new HttpSenderController.ViewSenderCallback() {

                    @Override
                    public void onSuccess(String responeJson) {
                        getRiskWarning();
                        LogUtil.i("我的信息 " + responeJson);
                        MineFragmentEntity entity = GsonParser.getParsedObj(responeJson, MineFragmentEntity.class);
                        MineFragmentEntity.ResultBean resultBean = entity.getResult();

                        //手机号
                        mIthbMobile.setRightTitleText(resultBean.getUsername());
                        //实名
                        if (StringUtils.isBlank(resultBean.getRealname())) {
                            hasShiMing = false;
                            mTthbRealName.setRightTitleText("未认证");
                            mTthbRealName.setRightTitleColor(getResources().getColor(R.color.font_grey_three));
                        } else {
                            hasShiMing = true;
                            mTthbRealName.setRightTitleText(resultBean.getRealname());
                            mTthbRealName.setRightIconVisibility(View.INVISIBLE);
                            mTthbRealName.setRightTitleColor(getResources().getColor(R.color.font_black));
                        }
                        //银行卡
                        if (StringUtils.isBlank(resultBean.getBank())) {
                            hasBindBank = false;
                            mTthbBank.setRightTitleText("未绑定");
                            mTthbBank.setRightTitleColor(getResources().getColor(R.color.font_grey_three));
                        } else {
                            hasBindBank = true;
                            mTthbBank.setRightTitleText(resultBean.getBank());
                            mTthbBank.setRightTitleColor(getResources().getColor(R.color.font_black));
                        }
                        //收货地址
                        if (resultBean.getIsbindaddress().equals("0")){
                            getmTthbAddress.setRightTitleText("未设置");
                            getmTthbAddress.setRightTitleColor(getResources().getColor(R.color.font_grey_three));
                        }else {
                            getmTthbAddress.setRightTitleText("修改");
                            getmTthbAddress.setRightTitleColor(getResources().getColor(R.color.font_black));
                        }

                        ithb_user_icon.setRightImage(resultBean.getHeadImgUrl());
                        UserData.getInstance().setRealName(resultBean.getRealname());
                        UserData.getInstance().setIsSetPay(resultBean.getIssetPay());
                    }

                    @Override
                    public void onFailure(ResponseEntity entity) {
                        LogUtil.i("我的信息失败 " + entity.errorInfo);
                        CommonToast.showHintDialog(mContext, entity.errorInfo);
                    }

                }, mContext);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CODE_CHANGE_LOGIN_PWD:
                finish();
                break;
            case CODE_GESTURE:
                finish();
                break;
            case CAPTURE_IMAGE:
                try {
                    if (imageFile != null&&resultCode==RESULT_OK) {
                        cropImage(imageUri);
                    } else {
                        ToastUtil.showToast(mContext, "未加载到图片");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case CROP_IMAGE:
                try {
                    if (imageFile != null&&resultCode==RESULT_OK) {
                            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            if (bmp != null) {
                                bmp = zoomBitmap(bmp, 320, 320);
                                if (bmp != null) {
                                    upload(bmp);
                                    bmp.recycle();
                                }
                            }
                    } else {
                        ToastUtil.showToast(mContext, "未加载到图片");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SELECT_IMAGE:
                try {
                    if (imageFile != null&&resultCode==RESULT_OK) {
                            if (imageUri != null) {
                                cropImage(data.getData());
                            }
                    } else {
                        ToastUtil.showToast(mContext, "未加载到图片");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @OnClick({
            R.id.btn_left, R.id.ithb_real_name, R.id.ithb_trans_psw, R.id.ithb_bank, R.id.tv_exit, R.id.ithb_about_us, R.id.ithb_feedback,
            R.id.ithb_account_security,R.id.ithb_app_condition,R.id.ithb_address,R.id.ithb_test_danger,R.id.ithb_user_icon
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.ithb_address:
                needLoadInfo = true;
                UrlUtil.showHtmlPage(mContext,"收货地址", RequestURL.Address_URL,true);
                break;
            case R.id.ithb_test_danger:
                needLoadInfo = true;
                UrlUtil.showHtmlPage(mContext,"风险测评", RequestURL.RISKTEST_URL,true);
                break;
            case R.id.ithb_real_name:
                if(!hasShiMing){
                    needLoadInfo = true;
                    Intent realNameIntent = new Intent(mContext, RealNameActivity.class);
                    startActivity(realNameIntent);
                }

                break;
            case R.id.ithb_user_icon: //头像


                showDialog();
                break;
            case R.id.ithb_bank:
                if(!hasBindBank){
                    needLoadInfo = true;
                }
                if(UserData.getInstance().getRealName().length()==0){
                    CommonToast.showShiMingDialog(mContext);
                }
                else{
                    Intent bankIntent = new Intent(mContext, BindBankCardActivity.class);
                    startActivity(bankIntent);
                }
                break;
            case R.id.ithb_account_security:
                Intent securityIntent = new Intent(mContext, AccountSecurityActivity.class);
                startActivityForResult(securityIntent,CODE_CHANGE_LOGIN_PWD);
                break;
            case R.id.tv_exit:
                showExitDialog();
                break;
            case R.id.ithb_about_us:
                Intent aboutUsIntent = new Intent(mContext, AboutUsActivity.class);
                startActivity(aboutUsIntent);
                break;

            case  R.id.ithb_feedback:
                Intent feedBackIntent = new Intent(mContext, FeedbackActivity.class);
                startActivity(feedBackIntent);
                break;
            case R.id.ithb_app_condition:
                Intent intent=new Intent(mContext,AppConditionActivity.class);
                startActivity(intent);
                break;
        }
    }
    public static String filePathMineUserInfo = "";
    private static File imageFile;
    private static Uri imageUri;
    /**
     * 选择头像
     */
    private void showDialog() {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.choice_face_dialog, null);
        Button btn1 = (Button) view.findViewById(R.id.btn1_dialog);
        Button btn2 = (Button) view.findViewById(R.id.btn2_dialog);
        Button btnCancel = (Button) view.findViewById(R.id.bt_cancel);

        final ActionSheet dialog = new ActionSheet(mContext, ActionSheet.GRAVITY_BOTTOM);
        dialog.setContentView(view);
        dialog.show();

        //拍照
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                        dialog.dismiss();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                filePathMineUserInfo = AppConfig.APP_PICTURE_FOLDER
                                        + "/"
                                        + System.currentTimeMillis()
                                        + AppConfig.JPG;
                                imageFile = new File(filePathMineUserInfo);
                                imageUri = Uri.fromFile(imageFile);
                                btnclick(imageUri, imageFile, mContext, AppConfig.APP_PICTURE_FOLDER);
                            }
                        });
                    }else {
                        insertDummyContactWrapper();
                    }
                }else {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            filePathMineUserInfo = AppConfig.APP_PICTURE_FOLDER
                                    + "/"
                                    + System.currentTimeMillis()
                                    + AppConfig.JPG;
                            imageFile = new File(filePathMineUserInfo);
                            imageUri = Uri.fromFile(imageFile);
                            btnclick(imageUri, imageFile, mContext, AppConfig.APP_PICTURE_FOLDER);
                        }
                    });
                }
            }
        });

        //从相册选择
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                filePathMineUserInfo = AppConfig.APP_PICTURE_FOLDER
                        + "/"
                        + System.currentTimeMillis()
                        + AppConfig.JPG;
                imageFile = new File(filePathMineUserInfo);
                imageUri = Uri.fromFile(imageFile);
                btnclickxc(imageFile, mContext, AppConfig.APP_PICTURE_FOLDER);
            }
        });

        //取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 点击按钮后调用相机拍照
     */

    public void btnclick(Uri imageUri, File imageFile, Context context, String wenJianJia) {
        // 如果初始化文件成功，则调用相机
        if (initImageFile(imageFile, wenJianJia)) {
            startTakePhoto(imageUri);
        } else {
            ToastUtil.showToast(mContext,"初始化文件失败，无法调用相机拍照！");
        }
    }

    private  void startTakePhoto(Uri imgUri) {
        try {
            Intent intent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            // 设置拍照后保存的图片存储在文件中
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            // 启动activity并获取返回数据
            startActivityForResult(intent, CAPTURE_IMAGE);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    /**
     * 初始化存储图片的文件
     *
     * @param imageFile
     * @return 初始化成功返回true，否则false
     */
    public  boolean initImageFile(File imageFile, String wenJianJia) {
        // 有SD卡时才初始化文件
        if (hasSDCard()) {
            if (initToCacheFiles(wenJianJia)) {
                if (!imageFile.exists()) {// 如果文件不存在，就创建文件
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否有SD卡
     *
     * @return 有SD卡返回true，否则false
     */
    public  boolean hasSDCard() {
        // 获取外部存储的状态
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // 有SD卡
            return true;
        }
        return false;
    }
    /**
     * 是否生成了次文件夹
     *
     * @param wenJianJia
     * @return
     */
    private boolean initToCacheFiles(String wenJianJia) {
        File cacheLocation = new File(wenJianJia);
        if (!cacheLocation.exists()) {
            cacheLocation.mkdirs();
        }
        return true;
    }

    private void upload(Bitmap bmp) {

        String base64Bitmap = ImageUtil.Bitmap2StrByBase64(bmp);

        SenderResultModel resultModel = ParamsManager.sendeHeaderImg(base64Bitmap);

        HttpRequestManager.httpRequestService(resultModel,
                new HttpSenderController.ViewSenderCallback() {

                    @Override
                    public void onSuccess(String responeJson) {
                        //{"RSPCOD":"00","RSPMSG":"上传成功","BINDID":"37651"}
                        LogUtil.i("上传图片成功 " + responeJson);
                        UploadPicEntity entity = GsonParser.getParsedObj(responeJson, UploadPicEntity.class);
                        UserData.getInstance().setUserIconUrl(entity.getResult().getHeadImgUrl());
                        ithb_user_icon.setRightImage(entity.getResult().getHeadImgUrl());
                        JcbApplication.needReloadMyInfo = true;
                    }

                    @Override
                    public void onFailure(ResponseEntity entity) {
                        LogUtil.i("上传图片失败 " + entity.errorInfo);
                        CommonToast.showHintDialog(mContext, entity.errorInfo);

                    }

                }, this);
    }
    private final String PSW_FILE_NAME = "wjthnfkghj";
    private void savePswString(String password) {
        String encrypt = "";
        try {
            byte[] encrypted =
                    DesUtil.encrypt(password.getBytes("utf-8"), AppConfigCommon.ENCRYPT_KEY.getBytes());
            encrypt = Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileUtil.writeObjectToDataFile(mContext, encrypt, PSW_FILE_NAME);
    }

    private void showExitDialog() {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.exit_dialog, null);
        Button btnExit = (Button) view.findViewById(R.id.btn_exit);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        final ActionSheet dialog = new ActionSheet(mContext, ActionSheet.GRAVITY_BOTTOM);
        dialog.setContentView(view);
        dialog.show();

        //退出
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserData.getInstance().setUSERID("");
                UserData.getInstance().setFingerPrint(false);
                UserData.getInstance().setIsOpenGesture(false);
                savePswString("");
                finish();
            }
        });

        //取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /***
     * 拍摄图片后通知手机系统相册进行扫描
     *
     * @param file
     */
    public static void initToseeDongTai(Context context, File file) {
        if (file != null) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.parse(file.getAbsolutePath()));
                JcbApplication.getInstance().sendBroadcast(intent);
        } else {
            ToastUtil.showToast(context, "由于手机权限原因，图片拍摄失败");
        }
    }

    //裁剪
    public void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, CROP_IMAGE);
    }

    public Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }


    /**
     * 调用系统相册

     */
    public  void selectImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, SELECT_IMAGE);
    }

    /**
     * 点击照片后选择系统相册
     *
     * @param imageFile
     * @param context
     * @param wenJianJia
     */

    public  void btnclickxc( File imageFile, Context context, String wenJianJia) {
        // 如果初始化文件成功，则调用相机
        if (initImageFile(imageFile, wenJianJia)) {
            selectImage();
        } else {
            ToastUtil.showToast(context, "无法加载图片");
        }
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    @TargetApi(Build.VERSION_CODES.M)
    private boolean insertDummyContactWrapper() {
        //提示用户需要手动开启的权限集合
        List<String> permissionsNeeded = new ArrayList<String>();

        //功能所需权限的集合
        final List<String> permissionsList = new ArrayList<String>();

        //若用户拒绝了该权限申请，则将该申请的提示添加到“用户需要手动开启的权限集合”中
//        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
//            permissionsNeeded.add("保存应用数据");
//        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
//            permissionsNeeded.add("读取应用数据");
//        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
//            permissionsNeeded.add("获取当前地理位置");
//        if (!addPermission(permissionsList, Manifest.permission.GET_ACCOUNTS))
//            permissionsNeeded.add("获取通讯录信息");
//        if (!addPermission(permissionsList, Manifest.permission.SYSTEM_ALERT_WINDOW))
//            permissionsNeeded.add("获取应用提示");
//        if(!addPermission(permissionsList,Manifest.permission.USE_FINGERPRINT))
//            permissionsNeeded.add("获取手机指纹信息");
        if(!addPermission(permissionsList,Manifest.permission.CAMERA))
            permissionsNeeded.add("获取手机相机信息");
//        if(!addPermission(permissionsList,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS))
//            permissionsNeeded.add("读取手机存储信息");


        //存在未配置的权限
        if (permissionsList.size() > 0) {

            //若用户赋之前拒绝过一部分权限，则需要提示用户开启其余权限并返回，否则该功能将无法执行
            if (permissionsNeeded.size() > 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                // Need Rationale
//                String message = "我们需要您授权下列权限：\n";
//                for (int i = 0; i < permissionsNeeded.size(); i++)
//                    message = message + "\n" + permissionsNeeded.get(i);
//
//                //弹出对话框，提示用户需要手动开启的权限
//                try {
//                    final CommonDialog.Builder dialog = new CommonDialog.Builder(this);
//                    dialog.setTitle(message);
//                    dialog.setMessageSize(R.dimen.f03_34);
//                    dialog.setButtonTextSize(R.dimen.f03_34);
//                    dialog.setPositiveButton("确定",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//
//                                }
//                            });
//                    dialog.create().show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }

            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }else {
            return true;
        }

    }
    //判断用户是否授予了所需权限
    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        //若配置了该权限，返回true
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            //若未配置该权限，将其添加到所需权限的集合，返回true
            permissionsList.add(permission);
            // 若用户勾选了“永不询问”复选框，并拒绝了权限，则返回false
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }

        return true;
    }
}
