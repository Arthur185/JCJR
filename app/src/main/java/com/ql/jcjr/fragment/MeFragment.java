package com.ql.jcjr.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ql.jcjr.R;
import com.ql.jcjr.activity.AutoBidActivityNew;
import com.ql.jcjr.activity.BidHistoryActivity;
import com.ql.jcjr.activity.CapitalRecordActivity;
import com.ql.jcjr.activity.CapitalStatisticsActivity;
import com.ql.jcjr.activity.ContactUsActivity;
import com.ql.jcjr.activity.LoginActivity;
import com.ql.jcjr.activity.LoginActivityCheck;
import com.ql.jcjr.activity.MainActivity;
import com.ql.jcjr.activity.MsgHomeActivity;
import com.ql.jcjr.activity.MyRedPacketsActivity;
import com.ql.jcjr.activity.RechargeActivity;
import com.ql.jcjr.activity.SettingActivity;
import com.ql.jcjr.activity.WithdrawalsActivity;
import com.ql.jcjr.adapter.ShortCutAdapter;
import com.ql.jcjr.application.JcbApplication;
import com.ql.jcjr.base.BaseFragment;
import com.ql.jcjr.constant.Global;
import com.ql.jcjr.constant.RequestURL;
import com.ql.jcjr.entity.CheckBankEntity;
import com.ql.jcjr.entity.MineFragmentEntity;
import com.ql.jcjr.entity.MsgHomeInfoEntity;
import com.ql.jcjr.entity.UserData;
import com.ql.jcjr.http.HttpRequestManager;
import com.ql.jcjr.http.HttpSenderController;
import com.ql.jcjr.http.ParamsManager;
import com.ql.jcjr.http.ResponseEntity;
import com.ql.jcjr.http.SenderResultModel;
import com.ql.jcjr.net.GsonParser;
import com.ql.jcjr.utils.GlideUtil;
import com.ql.jcjr.utils.LogUtil;
import com.ql.jcjr.utils.SharedPreferencesUtils;
import com.ql.jcjr.utils.StringUtils;
import com.ql.jcjr.utils.ToastUtil;
import com.ql.jcjr.utils.UrlUtil;
import com.ql.jcjr.view.CircleImageView;
import com.ql.jcjr.view.CommonToast;
import com.ql.jcjr.view.ImageTextHorizontalBarLess;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liuchao on 2016/9/23.
 */
public class MeFragment extends BaseFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @ViewInject(R.id.iv_wdzc)
    private ImageView mIvWdzc;

    @ViewInject(R.id.tv_total_num)
    private TextView mTvTotalNum;
    @ViewInject(R.id.tv_balance)
    private TextView mTvBalance;
    @ViewInject(R.id.tv_accumulated_income)
    private TextView mTvAccumulatedIncome;
    @ViewInject(R.id.tv_phone_number)
    private TextView mTvPhoneNum;
    @ViewInject(R.id.civ_user_icon)
    private CircleImageView mUserIcon;
    @ViewInject(R.id.btn_login)
    private Button mTvLogin;
    @ViewInject(R.id.ll_me_login)
    private LinearLayout mLlMeLogin;
    @ViewInject(R.id.ll_me_info)
    private LinearLayout mLlMeInfo;
    @ViewInject(R.id.ll_me_operate)
    private LinearLayout mLlMeOperate;
    @ViewInject(R.id.tv_level)
    private TextView tv_level;
    @ViewInject(R.id.tv_vipj)
    private TextView tv_vipj;
    @ViewInject(R.id.ithb_me_wdhb)
    private ImageTextHorizontalBarLess ithb_me_wdhb;
    @ViewInject(R.id.btn_notice)
    private ImageView btn_notice;
    @ViewInject(R.id.rl_nologin)
    private RelativeLayout rl_nologin;
    @ViewInject(R.id.sv_me)
    private ScrollView sv_me;

    private String myTotalMoney;
    private String myBalanceMoney;
    private String myIncomeMoney;

    private Context mContext;
    private ShortCutAdapter shortCutAdapter;
    private String mRealName;
    private String mUserIconUrl;
    private String mTel;
    private String mIsSetPay;
    private int i=0;

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        init();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if(isVisible) {
            isLoginOrNot();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(JcbApplication.needReloadMyInfo){
            JcbApplication.needReloadMyInfo = false;
            isLoginOrNot();
        }
    }

    @Override
    protected int getContentView() {
        LogUtil.i("MeFragment getContentView");
        return R.layout.fragment_me;
    }

    @Override
    protected void initView(View view) {
        LogUtil.i("MeFragment initView");
        ViewUtils.inject(this, view);
        mContext = getActivity();
        SharedPreferencesUtils.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void init() {
        mTvTotalNum.setTypeface(JcbApplication.getPingFangBoldTypeFace());
        mTvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserData.getInstance().getPhoneNumber().equals("")) {
                    Intent intent = new Intent(mContext, LoginActivityCheck.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                    startActivity(intent);
                }
                Map<String, String> datas = new HashMap<String, String>();
                MobclickAgent.onEventValue(mContext, "click_mine_register", datas, 1);
            }
        });
        tv_vipj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("main_index",1);
                startActivity(intent);
            }
        });
        tv_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    if (StringUtils.isNotBlank(mRealName)) {
                        UrlUtil.showHtmlPage(mContext,"会员中心", RequestURL.VIP_DETAIL_URL,true);
//                    } else {
////                    CommonToast.showHintDialog(mContext, "您还未实名认证！");
//                        CommonToast.showShiMingDialog(mContext);
//                    }
            }
        });

    }

    private void isLoginOrNot() {
        sv_me.fullScroll(ScrollView.FOCUS_UP);
        if (UserData.getInstance().isLogin()) {
            rl_nologin.setVisibility(View.GONE);
            mTvPhoneNum.setVisibility(View.VISIBLE);

            getMineFragmentData();

        } else {

            rl_nologin.setVisibility(View.VISIBLE);

            mTvTotalNum.setText("- -");
            mTvBalance.setText("——");
            mTvAccumulatedIncome.setText("——");
            mTvPhoneNum.setVisibility(View.GONE);
            tv_vipj.setText("立即投资，获取收益");
            tv_vipj.setVisibility(View.VISIBLE);
            tv_level.setVisibility(View.GONE);
            GlideUtil.displayPic(mContext, "", R.drawable.default_user_icon, mUserIcon);
            ithb_me_wdhb.setRightTitleText("");
        }
    }


    public void getMsgInfo() {
        SenderResultModel resultModel = ParamsManager.getMsgCenterInfo();
        resultModel.isShowLoadding = false;
        HttpRequestManager.httpRequestService(resultModel,
                new HttpSenderController.ViewSenderCallback() {

                    @Override
                    public void onSuccess(String responeJson) {
                        LogUtil.i("获取消息中心信息 " + responeJson);
                        MsgHomeInfoEntity entity = GsonParser.getParsedObj(responeJson, MsgHomeInfoEntity.class);
                        MsgHomeInfoEntity.ResultBean result = entity.getResult();

                       int msgNum = Integer.parseInt(result.getMessage().getNum());
                       int actNum = Integer.parseInt(result.getActive().getNum());
                       int noticeNum = Integer.parseInt(result.getGonggao().getNum());

                        //获取历史中的数据
                        int historyMsgNum = UserData.getInstance().getMsgNum();
                        int historyActNum = UserData.getInstance().getActNum();
                        int historyNoticeNum = UserData.getInstance().getNoticeNum();

                        //红点提示
                        if(msgNum >0 && msgNum>historyMsgNum || actNum >0 && actNum>historyActNum || noticeNum >0 && noticeNum>historyNoticeNum){
                            btn_notice.setImageResource(R.drawable.ic_have_notice);
                        }else {
                            btn_notice.setImageResource(R.drawable.ic_notice);
                        }

                    }

                    @Override
                    public void onFailure(ResponseEntity entity) {
                        LogUtil.i("获取消息中心信息 " + entity.errorInfo);
                    }

                }, mContext);
    }
    public void getMineFragmentData() {
        SenderResultModel resultModel = ParamsManager.senderMineFragment();

        HttpRequestManager.httpRequestService(resultModel,
                new HttpSenderController.ViewSenderCallback() {

                    @Override
                    public void onSuccess(String responeJson) {
                        getMsgInfo();
                        LogUtil.i("我的页面 " + responeJson);
                        MineFragmentEntity entity = GsonParser.getParsedObj(responeJson, MineFragmentEntity.class);
                        MineFragmentEntity.ResultBean resultBean = entity.getResult();

                        ithb_me_wdhb.setRightTitleText(resultBean.getCashcount() + "张未使用");

                        myTotalMoney = StringUtils.formatMoney(resultBean.getTotal());
                        myBalanceMoney = StringUtils.formatMoney(resultBean.getUse_money());
                        myIncomeMoney = StringUtils.formatMoney(resultBean.getCollection_interest1());

                        hideMyMoney(UserData.getInstance().getHideMyMoney());

                        mUserIconUrl = resultBean.getHeadImgUrl();

                        UserData.getInstance().setUserIconUrl(mUserIconUrl);
                        GlideUtil.displayPic(mContext, mUserIconUrl, R.drawable.default_user_icon, mUserIcon);

                        initVIP(resultBean);

                        mTel = resultBean.getUsername();
                        if (StringUtils.isBlank(resultBean.getRealname())) {
                            mRealName = "";
                            mTvPhoneNum.setText("未实名");
                        } else {
                            mRealName = resultBean.getRealname();
                            mTvPhoneNum.setText(resultBean.getRealname());
                        }
                        UserData.getInstance().setRealName(mRealName);

                        UserData.getInstance().setIsSetPay(resultBean.getIssetPay());
                        UserData.getInstance().setUserName(resultBean.getUsername());

                        if (i==0) {
                            i++;
                            if (StringUtils.isBlank(UserData.getInstance().getRealName())) {
                                //去实名
                                ToastUtil.showRealNameDialog(mContext);
                            }else {
                                //去绑卡
                                checkBank();
                            }
                        }
                    }

                    @Override
                    public void onFailure(ResponseEntity entity) {
                        LogUtil.i("我的页面失败 " + entity.errorInfo);
                        CommonToast.showHintDialog(mContext, entity.errorInfo);
                    }

                }, mContext);
    }

    private void initVIP(MineFragmentEntity.ResultBean resultBean) {
        if (resultBean.getRank().equals("-1")){
            tv_vipj.setText("立即投资，获取收益");
            tv_vipj.setVisibility(View.VISIBLE);
            tv_level.setVisibility(View.GONE);
        }else {
            tv_vipj.setVisibility(View.GONE);
            tv_level.setVisibility(View.VISIBLE);
            if (resultBean.getRank().equals("0")) {
                tv_level.setText(resultBean.getRankname());
                tv_level.setBackgroundResource(R.drawable.vip0);
            } else if (resultBean.getRank().equals("1")) {
                tv_level.setText(resultBean.getRankname());
                tv_level.setBackgroundResource(R.drawable.vip1);
            } else if (resultBean.getRank().equals("2")) {
                tv_level.setText(resultBean.getRankname());
                tv_level.setBackgroundResource(R.drawable.vip2);
            } else if (resultBean.getRank().equals("3")) {
                tv_level.setText(resultBean.getRankname());
                tv_level.setBackgroundResource(R.drawable.vip3);
            } else if (resultBean.getRank().equals("4")) {
                tv_level.setText(resultBean.getRankname());
                tv_level.setBackgroundResource(R.drawable.vip4);
            } else if (resultBean.getRank().equals("5")) {
                tv_level.setText(resultBean.getRankname());
                tv_level.setBackgroundResource(R.drawable.vip5);
            } else if (resultBean.getRank().equals("6")) {
                tv_level.setText(resultBean.getRankname());
                tv_level.setBackgroundResource(R.drawable.vip6);
            }
        }
    }

    private void hideMyMoney(boolean isHide){
        if(isHide){
            mTvTotalNum.setText("******");
            mTvBalance.setText("******");
            mTvAccumulatedIncome.setText("******");
            mIvWdzc.setImageResource(R.drawable.show_psw_normal);
        }
        else{
            mTvTotalNum.setText(myTotalMoney);
            mTvBalance.setText(myBalanceMoney);
            mTvAccumulatedIncome.setText(myIncomeMoney);
            mIvWdzc.setImageResource(R.drawable.show_psw_pressed);
        }
    }

    @OnClick({R.id.civ_user_icon, R.id.iv_wdzc, R.id.tv_withdrawals, R.id.tv_recharge, R.id.btn_notice, R.id.ithb_me_wdhb, R.id.ithb_me_zjjl,
            R.id.ithb_me_zdtb, R.id.ithb_me_wdtz, R.id.ithb_me_yqyl, R.id.ithb_mesetting,R.id.ll_me_info,R.id.btn_to_login,R.id.btn_to_logincheck,
            R.id.iv_kf})
    public void onClick(View v) {
        Intent intent = new Intent();

        switch (v.getId()) {
            case R.id.iv_wdzc:
                boolean isHide = UserData.getInstance().getHideMyMoney();
                if(isHide){
                    UserData.getInstance().setHideMyMoney(false);
                }
                else{
                    UserData.getInstance().setHideMyMoney(true);
                }
                hideMyMoney(!isHide);
                break;

            case R.id.tv_withdrawals:
                if (!UserData.getInstance().isLogin()) {
                    if (UserData.getInstance().getPhoneNumber().equals("")) {
                        intent = new Intent(mContext, LoginActivityCheck.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                        startActivity(intent);
                    }
                    return;
                }

                //提现
                if (StringUtils.isNotBlank(mRealName)) {
                    intent.setClass(mContext, WithdrawalsActivity.class);
                    intent.putExtra("account_balance", myBalanceMoney);
                    startActivity(intent);
                } else {
                    CommonToast.showShiMingDialog(mContext);
                }
                break;

            case R.id.tv_recharge:
                if (!UserData.getInstance().isLogin()) {
                    if (UserData.getInstance().getPhoneNumber().equals("")) {
                       intent = new Intent(mContext, LoginActivityCheck.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                        startActivity(intent);
                    }
                    return;
                }

                //充值
                if (StringUtils.isNotBlank(mRealName)) {
                    intent.setClass(mContext, RechargeActivity.class);
                    startActivity(intent);
                } else {
                    CommonToast.showShiMingDialog(mContext);
                }
                break;
            case R.id.btn_notice:
                if (!UserData.getInstance().isLogin()) {
                    if (UserData.getInstance().getPhoneNumber().equals("")) {
                        intent = new Intent(mContext, LoginActivityCheck.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                        startActivity(intent);
                    }
                    return;
                }
                intent.setClass(mContext, MsgHomeActivity.class);
                startActivity(intent);
                break;

            case R.id.ithb_me_wdhb:
                if (!UserData.getInstance().isLogin()) {
                    if (UserData.getInstance().getPhoneNumber().equals("")) {
                        intent = new Intent(mContext, LoginActivityCheck.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                        startActivity(intent);
                    }
                    return;
                }
                //我的红包
                intent.setClass(mContext, MyRedPacketsActivity.class);
                intent.putExtra("use_type", MyRedPacketFragment.TYPE_MY_HB);
                startActivity(intent);
                break;

            case R.id.ithb_me_zjjl:
                if (!UserData.getInstance().isLogin()) {
                    if (UserData.getInstance().getPhoneNumber().equals("")) {
                        intent = new Intent(mContext, LoginActivityCheck.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                        startActivity(intent);
                    }
                    return;
                }
                //交易记录
                intent.setClass(mContext, CapitalRecordActivity.class);
                startActivity(intent);
                break;

            case R.id.ithb_me_zdtb:
                if (!UserData.getInstance().isLogin()) {
                    if (UserData.getInstance().getPhoneNumber().equals("")) {
                        intent = new Intent(mContext, LoginActivityCheck.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                        startActivity(intent);
                    }
                    return;
                }
                //自动投标
                intent.setClass(mContext, AutoBidActivityNew.class);
                startActivity(intent);
                break;

            case R.id.ithb_me_wdtz:
                if (!UserData.getInstance().isLogin()) {
                    if (UserData.getInstance().getPhoneNumber().equals("")) {
                        intent = new Intent(mContext, LoginActivityCheck.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                        startActivity(intent);
                    }
                    return;
                }
                //投资记录
                intent.setClass(mContext, BidHistoryActivity.class);
                startActivity(intent);
                break;

            case R.id.ithb_me_yqyl:
                if (!UserData.getInstance().isLogin()) {
                    if (UserData.getInstance().getPhoneNumber().equals("")) {
                        intent = new Intent(mContext, LoginActivityCheck.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                        startActivity(intent);
                    }
                    return;
                }

                //邀请有礼
                UrlUtil.showHtmlPage(mContext,"邀请有礼", RequestURL.YQYL_URL + UserData.getInstance().getUSERID(),true);
                break;

            case R.id.civ_user_icon:
            case R.id.ithb_mesetting:
                if (!UserData.getInstance().isLogin()) {
                    if (UserData.getInstance().getPhoneNumber().equals("")) {
                        intent = new Intent(mContext, LoginActivityCheck.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                        startActivity(intent);
                    }
                    return;
                }
                //设置
                intent.setClass(mContext, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_me_info:
                intent.setClass(mContext,CapitalStatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_to_login:
                Map<String, String> data = new HashMap<String, String>();
                MobclickAgent.onEventValue(mContext, "mine_daily_sign", data, 1);
                if (UserData.getInstance().getPhoneNumber().equals("")) {
                    intent = new Intent(mContext, LoginActivityCheck.class);
                    startActivity(intent);
                }else {
                    intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra("phone_num", UserData.getInstance().getPhoneNumber());
                    startActivity(intent);
                }
                break;
            case R.id.btn_to_logincheck:
                Map<String, String> data1 = new HashMap<String, String>();
                MobclickAgent.onEventValue(mContext, "mine_daily_register", data1, 1);
                intent = new Intent(mContext, LoginActivityCheck.class);
                startActivity(intent);
                break;
            case R.id.iv_kf:
                intent.setClass(mContext, ContactUsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SharedPreferencesUtils.KEY_USER_ID)
                || key.equals(SharedPreferencesUtils.KEY_USER_ICON_URL)
                || key.equals(SharedPreferencesUtils.KEY_REAL_NAME)) {

            if (isAdded()) {
                isLoginOrNot();
            }
        }

    }

    private void checkBank() {
        SenderResultModel resultModel = ParamsManager.senderCheckBank();
        resultModel.isShowLoadding=false;
        HttpRequestManager.httpRequestService(resultModel,
                new HttpSenderController.ViewSenderCallback() {

                    @Override
                    public void onSuccess(String responeJson) {
                        LogUtil.i("是否绑定银行卡 " + responeJson);
                        CheckBankEntity entity = GsonParser.getParsedObj(responeJson, CheckBankEntity.class);
                        CheckBankEntity.ResultBean resultBean = entity.getResult();
                        switch (resultBean.getStatus()) {
                            case Global.STATUS_PASS:
                                break;

                            case Global.STATUS_UN_PASS://未绑卡
                                ToastUtil.showBindBankDialog(mContext);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(ResponseEntity entity) {
                        LogUtil.i("是否绑定银行卡 " + entity.errorInfo);
                    }

                }, mContext);
    }
}