package com.pickanddrop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.databinding.TermsFragmentBinding;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.Utilities;

import static com.pickanddrop.html_page.TermsConditions.Proce_Guide;
import static com.pickanddrop.html_page.TermsConditions.TermsCond;
import static com.pickanddrop.utils.AppConstants.PN_VALUE;

/**
 * Created by Raghvendra Sahu on 19-Apr-20.
 */
public class TermsFragment extends BaseFragment implements View.OnClickListener {
    TermsFragmentBinding binding;
    private Context context;
    private String status;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(PN_VALUE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.terms_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        initToolbar();
        binding.ivBack.setOnClickListener(this);


    }

    private void initToolbar() {
        if (status.equalsIgnoreCase("Terms")) {
            binding.toolbarTitle.setText("Terms & Conditions");

            binding.webview.getSettings().setJavaScriptEnabled(true);
            binding.webview.loadDataWithBaseURL("", TermsCond, "text/html", "UTF-8", "");

        }else if (status.equalsIgnoreCase("Guide")) {
            binding.toolbarTitle.setText("Guidelines & Procedures");

            binding.webview.getSettings().setJavaScriptEnabled(true);
            binding.webview.loadDataWithBaseURL("", Proce_Guide, "text/html", "UTF-8", "");

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).openMenuDrawer();
                break;
        }
    }
}
