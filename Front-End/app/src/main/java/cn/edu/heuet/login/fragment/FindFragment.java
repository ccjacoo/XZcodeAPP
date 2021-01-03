package cn.edu.heuet.login.fragment;

import android.os.Bundle;

//import android.support.annotation.Nullable;  改为
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.heuet.login.R;

public class FindFragment extends androidx.fragment.app.Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container,false);
        return view;
    }
}
