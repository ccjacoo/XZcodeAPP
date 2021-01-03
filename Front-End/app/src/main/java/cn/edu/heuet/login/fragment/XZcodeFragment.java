package cn.edu.heuet.login.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.adapter.SearchAdapter;
import cn.edu.heuet.login.bean.News;
import cn.edu.heuet.login.constant.NetConstant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 展示新闻列表与搜素框
 */

public class XZcodeFragment extends androidx.fragment.app.Fragment{

    private static RecyclerView rvList;
    private static SearchAdapter searchAdapter;
    private static final String TAG = "XZCODE_ACTIVITY";
    private List<News> newsList = new ArrayList<>();
    protected Toast toast;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xzcode, container,false);

        // 搜索框
        EditText etSearch = view.findViewById(R.id.et_search);

        // 列表展示
        rvList = view.findViewById(R.id.rv_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(layoutManager);

        setAdapter(newsList);

        String url = NetConstant.getNewsListURL();
        asyncGetNewsList(url);


        // 动态搜索
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

                hideToast();

                if (TextUtils.isEmpty(s)) {
                    String url = NetConstant.getNewsListURL();
//                    MyAsyncTask listTask = new MyAsyncTask();
//                    listTask.execute(url);
                    asyncGetNewsList(url);
                    return;
                }
                //  String url = NetConstant.getNewsByIdURL() + s;
                String url = NetConstant.getNewsByTitleURL() + s;
//                MyAsyncTask detailTask = new MyAsyncTask();
//                detailTask.execute(url);
                asyncGetNewsList(url);
            }

            // 实现在子线程中显示Toast
            protected void showToastInThread(Context context, String msg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
            protected void hideToast() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
            }

        };
        etSearch.addTextChangedListener(watcher);

        return view;
    }


    private void asyncGetNewsList(String url) {
        XHttp.get(url)
                .syncRequest(false)
                .execute(new SimpleCallBack<List<News>>() {
                    @Override
                    public void onSuccess(List<News> data) throws Throwable {
                        if (data.size() == 0) {
                            Toast.makeText(getActivity(), "查询结果为空", Toast.LENGTH_LONG).show();
                            setAdapter(null);
                        } else {
                            setAdapter(data);
                        }
                    }
                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "请求URL异常,获取新闻列表失败" + e.toString());
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * 私有静态内部类
     * <p>
     * 网络请求，获取 NewsList
     */
    private static class MyAsyncTask extends AsyncTask<String, Void, List<News>> {

        @Override
        protected List<News> doInBackground(String... strings) {
            String url = strings[0];
            List<News> newsList = null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String jsonStr = response.body().string();
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                String status = jsonObject.get("status").getAsString();
                if (TextUtils.equals(status, "success")) {
                    JsonArray data = jsonObject.get("data").getAsJsonArray();
                    String dataStr = data.toString();
                    newsList = JSON.parseArray(dataStr, News.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newsList;
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);
            setAdapter(news);
        }
    }

    private static void setAdapter(List<News> newsList){
        if(newsList == null || newsList.size() == 0){
            rvList.removeAllViews();
            return;
        }
        searchAdapter = new SearchAdapter(newsList);
        rvList.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

}
