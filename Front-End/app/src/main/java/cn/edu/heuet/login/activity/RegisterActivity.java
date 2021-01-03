package cn.edu.heuet.login.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.bean.OtpCode;
import cn.edu.heuet.login.constant.ModelConstant;
import cn.edu.heuet.login.constant.NetConstant;
import cn.edu.heuet.login.databinding.ActivityRegisterBinding;
import cn.edu.heuet.login.util.ValidUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity
        implements View.OnClickListener {

    // Log打印的通用Tag
    private final String TAG = "RegisterActivity";


    String account = "";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ActivityRegisterBinding registerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        //当用户在登录页面已经输完密码后发现自己根本没有注册时，会来到注册页面，此时我们自动保留他在登录页面里输入的手机号，简化用户操作
        // 接收上一个页面 Intent 的信息，需要 getIntent() 即可，而非重新 new 一个 Intent
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        // 把对应的 account 设置到 telephone 输入框，如果用户没有输入内容则传过来自然是为空啦
        registerBinding.etTelephone.setText(account);


        // 为点击事件设置监听器
        setOnClickListener();

         /*
            设置当输入框焦点失去时提示错误信息
            第一个参数：输入框对象
            第二个参数：输入数据类型
            第三个参数：输入不合法时提示信息
         */
        setOnFocusChangeErrMsg(registerBinding.etTelephone, "phone", "手机号格式不正确");
        setOnFocusChangeErrMsg(registerBinding.etPassword, "password", "密码必须不少于8位");
        setOnFocusChangeErrMsg(registerBinding.etStunum, "numberSigned", "学号有误");

    }

    // 为按钮点击事件的UI对象设置监听器
    private void setOnClickListener() {
        registerBinding.btGetOtp.setOnClickListener(this);
        registerBinding.btSubmitRegister.setOnClickListener(this);
    }

    /*
    当输入账号FocusChange时，校验账号是否是中国大陆手机号
    当输入密码FocusChange时，校验密码是否不少于6位
     */
    private void setOnFocusChangeErrMsg(EditText editText, String inputType, String errMsg) {
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        // 失去焦点时判断输入框中内容是否合法
                        if (!hasFocus) {
                            switch (inputType) {
                                case "phone":
                                    if (!ValidUtils.isPhoneValid(inputStr)) {
                                        editText.setError(errMsg);
                                    }
                                    break;
                                case "password":
                                    if (!ValidUtils.isPasswordValid(inputStr)) {
                                        editText.setError(errMsg);
                                    }
                                    break;
                                case "numberSigned":
                                    if(!ValidUtils.isStunumValid(inputStr)){
                                        editText.setError(errMsg);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
        );
    }

    // 因为 implements View.OnClickListener 所以OnClick方法可以写到onCreate方法外
    @Override
    public void onClick(View v) {
        String telephone = registerBinding.etTelephone.getText().toString();
        String otpCode = registerBinding.etOtpCode.getText().toString();
        String realname = registerBinding.etRealname.getText().toString();
        String stunum = registerBinding.etStunum.getText().toString();
        String classes = registerBinding.etClasses.getText().toString();
        String qq = registerBinding.etQq.getText().toString();
        String password1 = registerBinding.etPassword.getText().toString();
        String password2 = registerBinding.etPassword2.getText().toString();

        switch (v.getId()) {
            case R.id.bt_get_otp:
                // 点击获取验证码的按钮响应事件
                if (TextUtils.isEmpty(telephone)) {
                    Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (ValidUtils.isPhoneValid(telephone)) {
//                      asyncGetOtpCode(telephone);
                        asyncGetOtpCodeWithXHttp2(telephone);
                    } else {
                        Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.bt_submit_register:
                asyncRegisterWithXHttp2(telephone, otpCode, realname, stunum, classes, qq,  password1, password2);
                // 点击提交注册按钮响应事件
                // 尽管后端进行了判空，但Android端依然需要判空
                break;
        }
    }

    //响应获取otp验证码的click事件
    private void asyncGetOtpCodeWithXHttp2(String telephone) {
        XHttp.post(NetConstant.getGetOtpCodeURL())
                .params("telephone", telephone)
                .syncRequest(false)
                .execute(new SimpleCallBack<OtpCode>() {
                    @Override
                    public void onSuccess(OtpCode data) throws Throwable {
                        Log.d(TAG, "请求URL成功： " + data);
                        if (data != null) {
                            String otpCode = data.getOtpCode();
                            // 自动填充验证码:
                            //setTextInThread(registerBinding.etOtpCode, otpCode);

                            // 在子线程中显示Toast
                            showToastInThread(RegisterActivity.this, "验证码：" + otpCode);
                            Log.d(TAG, "telephone: " + telephone + " otpCode: " + otpCode);
                        }
                        Log.d(TAG, "验证码已发送，注意查收！");
                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "请求URL异常： " + e.toString());
                        showToastInThread(RegisterActivity.this, e.getMessage());
                    }
                });
    }

    /* 在子线程中更新UI ,实现自动填充验证码
    private void setTextInThread(EditText editText, String otpCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setText(otpCode);
            }
        });
    }
    */

    private void asyncRegisterWithXHttp2(final String telephone,
                                         final String otpCode,
                                         final String realname,
                                         final String stunum,
                                         final String classes,
                                         final String qq,
                                         final String password1,
                                         final String password2) {
        // 多一重非空校验的检查
        if (TextUtils.isEmpty(telephone) || TextUtils.isEmpty(otpCode) || TextUtils.isEmpty(realname)
                || TextUtils.isEmpty(stunum) || TextUtils.isEmpty(classes) || TextUtils.isEmpty(qq)
                || TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
            Toast.makeText(RegisterActivity.this, "存在输入为空，注册失败,请仔细检查", Toast.LENGTH_SHORT).show();
            return;
        }

        // 密码一致校验
        if (!TextUtils.equals(password1, password2)) {
            Toast.makeText(RegisterActivity.this, "两次密码不一致，注册失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // 注册，传递参数，注意要和后端接收的参数名一一对应，否则无法传递过去
        XHttp.post(NetConstant.getRegisterURL())
                .params("telephone", telephone)
                .params("otpCode", otpCode)
                .params("realname", realname)
                .params("stunum", stunum)
                .params("classes",classes)
                .params("qq",qq)
                .params("password", password1)
                .syncRequest(false)
                .execute(new SimpleCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) throws Throwable {
                        // 注册成功，保存用户名与密码
                        sp = getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
                        editor = sp.edit();
                        editor.putString("telephone", telephone);
                        String encryptedPassword = ValidUtils.encodeByMd5(password1);
                        editor.putString("encryptedPassword", encryptedPassword);

                        if (editor.commit()) {
                            showToastInThread(RegisterActivity.this, "注册成功");
                            Intent intentToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intentToLogin);
                            // 注册成功后，注册界面就没必要占据资源了
                            finish();
                        } else {
                            showToastInThread(RegisterActivity.this, "保存密码到本地失败！");
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "请求URL异常： " + e.toString());
                        showToastInThread(RegisterActivity.this, e.getMessage());
                    }
                });
    }



    // 使用Gson解析response的JSON数据中的status，返回布尔值
    private String getResponseStatus(JsonObject responseBodyJSONObject) {
        // Gson解析JSON，总共3步
        // 1、获取response对象的字符串序列化
        // String responseData = response.body().string();
        // 2、通过JSON解析器JsonParser()把字符串解析为JSON对象，
        //
        // *****前两步抽写方法外面了*****
        //
        // JsonObject jsonObject = (JsonObject) new JsonParser().parse(responseBodyStr);
        // 3、通过JSON对象获取对应的属性值
        String status = responseBodyJSONObject.get("status").getAsString();
        return status;
    }

    // 获取验证码响应data
    // 使用Gson解析response返回异常信息的JSON中的data对象
    private void getResponseErrMsg(Context context, JsonObject responseBodyJSONObject) {
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context, errorMsg);
    }
}



