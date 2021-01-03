package cn.edu.heuet.login.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.edu.heuet.login.R;

public class HomeFragment extends androidx.fragment.app.Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        ImageView iv_logo;
        iv_logo = view.findViewById(R.id.iv_logo);
        iv_logo.setImageResource(R.drawable.xzcode);

        return view;
    }

}
