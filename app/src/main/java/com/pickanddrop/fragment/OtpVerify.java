package com.pickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.activities.SplashActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.OtpVerifyBinding;
import com.pickanddrop.dto.LoginDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.OtpEditText;
import com.pickanddrop.utils.Utilities;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * Created by Raghvendra Sahu on 16-May-20.
 */
public class OtpVerify extends BaseFragment implements AppConstants, View.OnClickListener {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    OtpVerifyBinding binding;
    String PhoneNumber;
    private String OtpValue = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("PhoneNumber")) {
            PhoneNumber = getArguments().getString("PhoneNumber");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.otp_verify, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolBar();
        try {
                binding.tvTextPhone.setText("+63 "+PhoneNumber);

        } catch (Exception e) {

        }

      //  OtpTimeRemaining();//***otp enter time within 60 sec
      //  binding.tvResendOpt.setEnabled(false);

        binding.tvResendOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!utilities.isNetworkAvailable()){
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
                } else {
                  ResendOtp(PhoneNumber);
                }

            }
        });

        binding.btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (OtpValue.isEmpty() && OtpValue.equalsIgnoreCase("")) {
                   // Toast.makeText(VerifyOptActivity.this, "Please enter otp", Toast.LENGTH_SHORT).show();
                    utilities.dialogOK(context, getString(R.string.validation_title), "Please enter otp", getString(R.string.ok), false);

                } else if (OtpValue.length() < 4) {
                    //Toast.makeText(VerifyOptActivity.this, "Please enter valid otp", Toast.LENGTH_SHORT).show();
                    utilities.dialogOK(context, getString(R.string.validation_title), "Please enter valid otp", getString(R.string.ok), false);
                } else {
                    if (!utilities.isNetworkAvailable()){
                        utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);

                    } else {
                       PhoneVerifyApi(OtpValue, PhoneNumber);
                    }
                }


            }
        });

        //******************
       // final OtpEditText txtPinEntry = (OtpEditText) findViewById(R.id.et_otp);
        binding.etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                OtpValue = s.toString();

                Log.e("Otp_lenght", s.toString());

            }
        });
    }

    private void ResendOtp(String phoneNumber) {
        final ProgressDialog mProgressDialog;
        mProgressDialog = ProgressDialog.show(context, null, null);
        mProgressDialog.setContentView(R.layout.progress_loader);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setCancelable(false);

        Map<String, String> map = new HashMap<>();
        map.put("phone", phoneNumber);


        APIInterface apiInterface = APIClient.getClient();
        Call<LoginDTO> call = apiInterface.callVerifyResendApi(map);
        call.enqueue(new Callback<LoginDTO>() {
            @Override
            public void onResponse(Call<LoginDTO> call, Response<LoginDTO> response) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        if (response.body().getResult().equalsIgnoreCase("success")) {
                            utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);

                        } else {
                            utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginDTO> call, Throwable t) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                Log.e(TAG, t.toString());
            }
        });
    }


    private void PhoneVerifyApi(String otpValue, String phoneNumber) {
        final ProgressDialog mProgressDialog;
        mProgressDialog = ProgressDialog.show(context, null, null);
        mProgressDialog.setContentView(R.layout.progress_loader);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setCancelable(false);

        Map<String, String> map = new HashMap<>();
        map.put("phone", phoneNumber);
        map.put("otp", otpValue);


        APIInterface apiInterface = APIClient.getClient();
        Call<LoginDTO> call = apiInterface.callVerifyApi(map);
        call.enqueue(new Callback<LoginDTO>() {
            @Override
            public void onResponse(Call<LoginDTO> call, Response<LoginDTO> response) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        if (response.body().getResult().equalsIgnoreCase("success")) {
                            ((SplashActivity) context).popFragment();
                            utilities.dialogOKre(context, "", response.body().getMessage(), context.getString(R.string.ok), new OnDialogConfirmListener() {
                                @Override
                                public void onYes() {
                                    // ((SplashActivity) context).popFragment();
                                    ((SplashActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_splash, new Login()).commit();
                                }

                                @Override
                                public void onNo() {

                                }
                            });

                        } else {
                            utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginDTO> call, Throwable t) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                Log.e(TAG, t.toString());
            }
        });

    }

    private void OtpTimeRemaining() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.tvRemainTime.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                binding.tvRemainTime.setText("seconds remaining: " + "0");
                binding.tvResendOpt.setEnabled(true);
                binding.tvResendOpt.setBackgroundColor(getResources().getColor(R.color.white_color));
            }

        }.start();
    }

    private void initToolBar() {
    }

    private void initView() {
        binding.ivBack.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:

                utilities.dialogOKre(context, "", "Are you sure you want to left", getString(R.string.yes), new OnDialogConfirmListener() {
                    @Override
                    public void onYes() {
                   
                        ((SplashActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_splash, new Login()).commit();
                    }

                    @Override
                    public void onNo() {

                    }
                });
                break;
        }





    }


}
