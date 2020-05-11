package com.pickanddrop.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.databinding.TermsFragmentBinding;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.Utilities;

import static com.pickanddrop.html_page.TermsConditions.Faq_customer;
import static com.pickanddrop.html_page.TermsConditions.Faq_rider;
import static com.pickanddrop.html_page.TermsConditions.Proce_Guide;
import static com.pickanddrop.html_page.TermsConditions.TermsCond;
import static com.pickanddrop.utils.AppConstants.DRIVER;
import static com.pickanddrop.utils.AppConstants.PN_VALUE;

/**
 * Created by Raghvendra Sahu on 19-Apr-20.
 */
public class TermsFragment extends BaseFragment implements View.OnClickListener {
    TermsFragmentBinding binding;
    private Context context;
    private String status;
    private AppSession appSession;

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
        appSession = new AppSession(context);
        initToolbar();
        binding.ivBack.setOnClickListener(this);

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initToolbar() {
        if (status.equalsIgnoreCase("Terms")) {
            binding.toolbarTitle.setText("Terms & Conditions");

            binding.webview.getSettings().setJavaScriptEnabled(true);
            binding.webview.loadDataWithBaseURL("", TermsCond, "text/html", "UTF-8", "");

        }else if (status.equalsIgnoreCase("Guide")) {
            binding.toolbarTitle.setText("Guidelines & Procedures");

            binding.webview.getSettings().setJavaScriptEnabled(true);
          binding.webview.loadDataWithBaseURL("", Proce_Guide, "text/html", "UTF-8", "");
           // binding.webview.loadUrl("http://pabiliph.com/admin_panel/guidline.html");
           // startWebView("http://pabiliph.com/admin_panel/guidline.html");
        }
        else if (status.equalsIgnoreCase("Faq")) {
            binding.toolbarTitle.setText("FAQ");

            if (appSession.getUserType().equals(DRIVER)) {
                binding.webview.getSettings().setJavaScriptEnabled(true);
                binding.webview.loadDataWithBaseURL("", Faq_rider, "text/html", "UTF-8", "");
            }else {
                binding.webview.getSettings().setJavaScriptEnabled(true);
                binding.webview.loadDataWithBaseURL("", Faq_customer, "text/html", "UTF-8", "");
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void startWebView(String url) {
        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        binding.webview.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });

        // Javascript inabled on webview
        binding.webview.getSettings().setJavaScriptEnabled(true);

        //Load url in webview
        binding.webview.loadUrl(url);

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
