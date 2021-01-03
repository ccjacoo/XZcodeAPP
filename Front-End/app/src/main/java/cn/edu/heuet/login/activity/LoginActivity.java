package cn.edu.heuet.login.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.constant.NetConstant;
import cn.edu.heuet.login.databinding.ActivityLoginBinding;
import cn.edu.heuet.login.fragment.MineFragment;
import cn.edu.heuet.login.util.ValidUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity
        implements View.OnClickListener {

    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;

    // Log打印的通用Tag
    private final String TAG = "LoginActivity";

    private ActivityLoginBinding loginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fullScreenConfig();
        setContentView(R.layout.activity_login);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // 为点击事件设置监听器
        setOnClickListener();

        /*
            当输入框焦点失去时,检验输入数据，提示错误信息
            第一个参数：输入框对象
            第二个参数：输入数据类型
            第三个参数：输入不合法时提示信息
         */
        setOnFocusChangeErrMsg(loginBinding.etAccount, "phone", "手机号格式不正确");
        setOnFocusChangeErrMsg(loginBinding.etPassword, "password", "密码必须不少于8位");
    }

    // 为点击事件的UI对象设置监听器
    private void setOnClickListener() {
        loginBinding.btLogin.setOnClickListener(this); // 登录
        loginBinding.tvToRegister.setOnClickListener(this); // 注册
        loginBinding.tvForgetPassword.setOnClickListener(this); // 忘记密码
    }

    /*
    当账号输入框失去焦点时，校验账号是否是中国大陆手机号
    当密码输入框失去焦点时，校验密码是否不少于6位
    如有错误，提示错误信息
     */
    private void setOnFocusChangeErrMsg(EditText editText, String inputType, String errMsg) {
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
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
                                default:
                                    break;
                            }
                        }
                    }
                }
        );
    }


    // 因为 implements View.OnClickListener 加上已经 setOnClickListener()
    // 所以OnClick方法可以写到onCreate方法外
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        // 获取用户输入的账号和密码以进行验证
        String account = loginBinding.etAccount.getText().toString();
        String password = loginBinding.etPassword.getText().toString();

        switch (v.getId()) {
            // 登录按钮 响应事件
            case R.id.bt_login:
                // 让密码输入框失去焦点,触发setOnFocusChangeErrMsg方法
                loginBinding.etPassword.clearFocus();
                // 发送URL请求之前,先进行校验
                if (!(ValidUtils.isPhoneValid(account) && ValidUtils.isPasswordValid(password))) {
                    Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                /*
                   因为验证是耗时操作，所以独立成方法
                   在方法中开辟子线程，避免在当前UI线程进行耗时操作
                   否则会造成 ANR（application not responding）
                */
//                asyncLogin(account, password);
                asyncLoginWithXHttp2(account, password);
                break;
            // 注册用户 响应事件
            case R.id.tv_to_register:
                /*
                  关于这里传参说明：给用户一个良好的体验，
                  如果在登录界面填写过的，就不需要再填了
                  所以Intent把填写过的数据传递给注册界面
                 */
                Intent intentToRegister = new Intent(this, RegisterActivity.class);
                intentToRegister.putExtra("account", account);
                startActivity(intentToRegister);
                break;

            // 以下功能目前都没有实现
            case R.id.tv_forget_password:
                // 跳转到修改密码界面
                Intent intentToForget = new Intent(this, ForgetActivity.class);
                startActivity(intentToForget);
                break;

        }
    }


    private void asyncLoginWithXHttp2(String telephone, String password) {
        XHttp.post(NetConstant.getLoginURL())
                .params("telephone", telephone)
                .params("password", password)
                .params("type", "login")
                .syncRequest(false)
                .execute(new SimpleCallBack<Object>() {
                    @Override
                    public void onSuccess(Object obj) throws Throwable {
                        Log.d(TAG, "请求URL成功,登录成功");
                        String encryptedPassword = ValidUtils.encodeByMd5(password);
                        sp = getSharedPreferences("login_info", MODE_PRIVATE);
                        editor = sp.edit();
                        editor.putString("telephone", telephone);
                        editor.putString("encryptedPassword", encryptedPassword);

                        if (editor.commit()) {
                            Intent it_login_to_main = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(it_login_to_main);
                            // 登录成功后，登录界面就没必要占据资源了
                            finish();
                        } else {
                            showToastInThread(LoginActivity.this, "账号密码保存失败，请重新登录");
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        if(e.getMessage().contains("java.io.IOException: unexpected end of stream on")){
                            showToastInThread(LoginActivity.this, "登录失败，原因是后端服务器未开，请联系负责人:陈长江");
                        }
                        else{
                            showToastInThread(LoginActivity.this, e.getMessage());
                        }
                    }
                });
    }


    /*
      使用Gson解析response的JSON数据
      本来总共是有三步的，一、二步在方法调用之前执行了
    */
    private String getStatus(JsonObject responseBodyJSONObject) {
        /* 使用Gson解析response的JSON数据的第三步
           通过JSON对象获取对应的属性值 */
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status;
    }

    /*
      使用Gson解析response返回异常信息的JSON中的data对象
      这也属于第三步，一、二步在方法调用之前执行了
     */
    private void getResponseErrMsg(Context context, JsonObject responseBodyJSONObject) {
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context, errorMsg);
    }

}
