package cn.edu.heuet.login.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.activity.LoginActivity;
import cn.edu.heuet.login.constant.ModelConstant;
import cn.edu.heuet.login.constant.NetConstant;
import cn.edu.heuet.login.databinding.FragmentMineBinding;

import com.google.gson.Gson;
import com.vondear.rxui.view.RxTitle;
import com.vondear.rxui.view.dialog.RxDialogScaleView;
import com.vondear.rxui.view.dialog.RxDialogSureCancel;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import static android.content.Context.MODE_PRIVATE;

//import com.yalantis.ucrop.UCrop;
//import com.yalantis.ucrop.UCropActivity;

import cn.edu.heuet.login.bean.User;


/**
 * 我的个人资料页面
 * 注意点：
 * 1、裁切头像的 Activity 需要在 AndroidManifest.xml 中注册，否则无法使用
 * 2、new RxDialogChooseImage() 调用 Dialog 的构造方法要切换成 Fragment 的，否则无法调起 Dialog
 * 3、initUCrop() 最后裁切后需要切换成 Fragment 的用法，否则无法返回图片
 */


public class MineFragment extends androidx.fragment.app.Fragment implements View.OnClickListener{

    private final String TAG = "MineFragment";
    private Uri resultUri;
    private ImageView mIvAvatar;
    private RxTitle mRxTitle;
    private TextView tv_show_name;
    private TextView tv_show_classes;
    private TextView tv_show_stunum;
    private TextView tv_show_qq;
    private Button mBtnExit;
    private FragmentMineBinding mBinding;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public MineFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_mine, container,false);
        //使用databinding
        // 1、对布局需要绑定的内容进行加载
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false);
        // 2、获取到视图
        View view = mBinding.getRoot();
        // 3、绑定数据
        tv_show_name = view.findViewById(R.id.tv_show_name);
        tv_show_classes = view.findViewById(R.id.tv_show_classes);
        tv_show_stunum = view.findViewById(R.id.tv_show_stunum);
        tv_show_qq = view.findViewById(R.id.tv_show_qq);


        //String tel = user.getTelephone();
        //在fragment里面需要写成 getactivity().getSharedPreferences
        SharedPreferences sp = getActivity().getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
        String tel = sp.getString("telephone","");
        // 把对应的 tel 信息传递过来
        String url = NetConstant.getUserInfoURL() + tel;
        Log.d(TAG,"请求的地址为："+url);
        asyncGetUserInfo(url);     //根据用户的手机号来发送get请求，得到该用户的相关信息

        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    //获取用户信息
    private void asyncGetUserInfo(String url){
        XHttp.get(url)
                .syncRequest(false) //异步请求
                .execute(new SimpleCallBack<Object>(){
                    @Override
                    public void onSuccess(Object obj){
                        Log.d(TAG, "POST请求URL成功,成功获取用户的信息");
                        if(obj != null){
                            Gson gson = new Gson();
                            User user = gson.fromJson(String.valueOf(obj),User.class);
                            //绑定用户信息数据
                            tv_show_name.setText(user.getRealname());
                            tv_show_classes.setText(user.getClasses());
                            tv_show_stunum.setText(user.getStunum());
                            tv_show_qq.setText(user.getQq());
                        }
                    }
                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "请求用户信息异常： " + e.getMessage());
                        Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }



    //initUI
    private void initUI(View view){
        mIvAvatar = view.findViewById(R.id.iv_avatar);
        mBtnExit = view.findViewById(R.id.btn_exit);

        mBtnExit.setOnClickListener(this);
    }

    private void initView() {
        Resources r = getActivity().getResources();
        resultUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(R.drawable.ic_fab) + "/"
                + r.getResourceTypeName(R.drawable.ic_fab) + "/"
                + r.getResourceEntryName(R.drawable.ic_fab));


        // 单击头像响应事件

        // 长按头像响应事件
        mIvAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                RxImageTool.showBigImageView(mContext, resultUri);
                RxDialogScaleView rxDialogScaleView = new RxDialogScaleView(getActivity());
                rxDialogScaleView.setImage(resultUri);
                rxDialogScaleView.show();
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 响应退出登录按钮点击事件
            case R.id.btn_exit:
                final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(getActivity());
                rxDialogSureCancel.show();

                rxDialogSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rxDialogSureCancel.cancel();
                    }
                });
                rxDialogSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 清空 SharedPreferences 中的登录信息 (在Fragment中操作sharedPreference有点区别，需要先getContext)
                        SharedPreferences sp = getContext().getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
                        sp.edit().clear().apply();

                        // 跳转到登录页
                        startActivity(new Intent(getActivity(), LoginActivity.class));

                        //清空当页资源
                        rxDialogSureCancel.dismiss();
                        getActivity().finish();

                    }
                });
                break;
        }
    }

}
