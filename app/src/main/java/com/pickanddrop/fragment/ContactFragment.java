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
import com.pickanddrop.databinding.ContactFragmentBinding;
import com.pickanddrop.databinding.TermsFragmentBinding;

import static com.pickanddrop.html_page.TermsConditions.Proce_Guide;
import static com.pickanddrop.html_page.TermsConditions.TermsCond;
import static com.pickanddrop.utils.AppConstants.PN_VALUE;

/**
 * Created by Raghvendra Sahu on 19-Apr-20.
 */
public class ContactFragment extends BaseFragment implements View.OnClickListener {
     ContactFragmentBinding binding;
    private Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           // status = getArguments().getString(PN_VALUE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.contact_fragment, container, false);
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
            binding.toolbarTitle.setText("Contact Us");

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
