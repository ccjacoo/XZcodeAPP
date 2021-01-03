package cn.edu.heuet.login.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.bean.News;
import cn.yhq.dialog.core.DialogBuilder;

public class NewsDetailActivity extends BaseActivity {

    private static final String TAG = "NewsDetailActivity";
    private ImageView ivPicture;   //图片
    private TextView tvContentTitle;  //文章标题
    private TextView tvTime;   //文章创建时间
    private TextView tvContent;  //文章内容
    private Dialog dialog;  //显示大图的dialog
    private Bitmap mBitmap;     //图片转成的item数组
    List<String> list = new ArrayList<>();
    private String url;   //网上图片的url地址


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();
        News news = (News) intent.getSerializableExtra("news");

        initUI(news);

        list.add("保存图片至本地相册");
        //list.add("分享至");  待实现


        // 点击图片放大响应事件
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = NewsDetailActivity.this;
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.imagelarge);
                ImageView imageView = dialog.findViewById(R.id.iv_imageLarge);
                Glide.with(context).load(news.getPicture()).into(imageView);
                //为true表示点击其他地方可以使dialog消失
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                //点击大图片关闭dialog响应事件
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();   //再次点击图片关闭大图效果
                    }
                });

                //长按大图片保存到本地响应事件
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DialogBuilder.listDialog(NewsDetailActivity.this).setChoiceItems(list)
                                .setChoiceType(DialogBuilder.TYPE_CHOICE_SINGLE)
                                .setOnChoiceListener(new DialogBuilder.OnChoiceListener() {
                                    @Override
                                    public void onChoiceItem(int index, Object item) {
//                                // 对话框关闭后回调的一个方法，返回选择的条目
//                                Toast.makeText(MainActivity.this, "最终选择了：" + item, Toast.LENGTH_LONG).show();
                                    }
                                }).setOnChoiceClickListener(new DialogInterface.OnClickListener() {
                            // 选择某一个条目的时候回调的一个方法，返回选择的是哪一个条目
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(MainActivity.this, "点击了第" + (which + 1) + "个条目",
//                                Toast.LENGTH_LONG).show();
                            }
                        }).setOnPositiveButtonClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(MainActivity.this, "点击了确定按钮", Toast.LENGTH_LONG).show();
                                url = news.getPicture();
                                getImageCameraPermission(url);   //下载图片前首先查看权限，然后调用一系列方法
                                Toast.makeText(NewsDetailActivity.this,"正在保存请稍后.....",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                        return true;
                    }
                });

            }
        });

    }


    //初始化UI
    private void initUI(News news) {
        ivPicture = findViewById(R.id.iv_picture);
        tvContentTitle = findViewById(R.id.tv_content_title);
        tvTime = findViewById(R.id.tv_time);
        tvContent = findViewById(R.id.tv_content);
        if (news == null) {
            return;
        }
        // getPicture方法返回的是数据库里存储的url地址
        Glide.with(this).load(news.getPicture()).into(ivPicture);
        tvContentTitle.setText(news.getTitle());
        tvTime.setText("文章发布时间： "+news.getCreateTime().substring(0,10));
        tvContent.setText(Html.fromHtml("\n\u3000\u3000"+news.getContent()));
    }



    public void getImageCameraPermission(String imagePath) {
        //判断版本
        if (Build.VERSION.SDK_INT >= 23) {
            //检查权限是否被授予：
            int hasExternalPermission = ContextCompat.checkSelfPermission(NewsDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasExternalPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NewsDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
            } else {
                Glide.get(NewsDetailActivity.this).clearMemory();
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.centerCrop();
                Glide.with(NewsDetailActivity.this).load(imagePath).apply(requestOptions).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        saveImageToGallery(NewsDetailActivity.this, drawable2Bitmap(resource));
                    }
                });
            }
        } else {
            Glide.get(NewsDetailActivity.this).clearMemory();
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            Glide.with(NewsDetailActivity.this).load(imagePath).apply(requestOptions).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    saveImageToGallery(NewsDetailActivity.this, drawable2Bitmap(resource));
                }
            });
        }
    }


    Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }


    public static void saveImageToGallery(Context context, Bitmap bmp) {

        String imgpath = Environment.getExternalStorageDirectory().toString() + "/XZcode/";
        // 首先保存图片

        try {
            File appDir = new File(Environment.getExternalStorageDirectory().toString(), "XZcode");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + "";
            imgpath += fileName;
            File file = new File(appDir, fileName);
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            // 其次把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imgpath)));
            Toast.makeText(context, "已成功保存至本地相册", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
