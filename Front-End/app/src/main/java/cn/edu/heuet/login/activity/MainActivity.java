package cn.edu.heuet.login.activity;

import android.os.Bundle;

import com.next.easynavigation.view.EasyNavigationBar;
import cn.edu.heuet.login.R;
import cn.edu.heuet.login.fragment.FindFragment;
import cn.edu.heuet.login.fragment.HomeFragment;
import cn.edu.heuet.login.fragment.MineFragment;
import cn.edu.heuet.login.fragment.XZcodeFragment;

import java.util.ArrayList;
import java.util.List;

/** 迁移为 AndroidX  （实属坑爹，以下注释部分是原先的包，通通要升级为新的包并且在代码中要手动替换！！） **/
//import androidx.core.app.Fragment;

//import androidx.core.app.FragmentManager;


//import androidx.core.app.FragmentPagerAdapter;


//import androidx.core.view.ViewPager;

//import android.support.v7.app.AppCompatActivity;


public class MainActivity extends BaseActivity {

    private EasyNavigationBar navigationBar;

    private String[] tabText = {"首页", "发现", "协会", "我的"};
    //未选中icon
    private int[] normalIcon = {R.mipmap.index, R.mipmap.find, R.mipmap.message, R.mipmap.me};
    //选中时icon
    private int[] selectIcon = {R.mipmap.index1, R.mipmap.find1, R.mipmap.message1, R.mipmap.me1};

    private List<androidx.fragment.app.Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationBar = findViewById(R.id.navigationBar);

        fragments.add(new HomeFragment());  //首页
        fragments.add(new FindFragment());  //发现
        fragments.add(new XZcodeFragment()); //协会
        fragments.add(new MineFragment());  //我的

        navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(fragments)
                .fragmentManager(getSupportFragmentManager())
                .canScroll(true)
                .build();

    }

    public EasyNavigationBar getNavigationBar() {
        return navigationBar;
    }

}