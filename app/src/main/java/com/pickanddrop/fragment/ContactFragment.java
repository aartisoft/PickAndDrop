package com.pickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.activities.SplashActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.ContactFragmentBinding;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Raghvendra Sahu on 19-Apr-20.
 */
public class ContactFragment extends BaseFragment implements View.OnClickListener {
     ContactFragmentBinding binding;
    private Context context;
    private Utilities utilities;

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
        utilities = Utilities.getInstance(context);
        initToolbar();
        binding.ivBack.setOnClickListener(this);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name=binding.etName.getText().toString();
                String Subject=binding.etSubject.getText().toString();
                String Desc=binding.etDesc.getText().toString();

                if (!Name.isEmpty() && !Subject.isEmpty() && !Desc.isEmpty()){

                    CallContactUsApi(Name,Subject,Desc);

                }else {
                    utilities.dialogOK(context, getString(R.string.validation_title), "Please enter all fields", getString(R.string.ok), false);
                }
            }
        });


    }

    private void CallContactUsApi(String name, String subject, String desc) {
            if (!utilities.isNetworkAvailable())
                utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
            else {
                final ProgressDialog mProgressDialog;
                mProgressDialog = ProgressDialog.show(context, null, null);
                mProgressDialog.setContentView(R.layout.progress_loader);
                mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mProgressDialog.setCancelable(false);

                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("subject", subject);
                map.put("description", desc);

                APIInterface apiInterface = APIClient.getClient();
                Call<OtherDTO> call = apiInterface.callContactApi(map);
                call.enqueue(new Callback<OtherDTO>() {
                    @Override
                    public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body().getResult().equalsIgnoreCase("true")) {
                                    utilities.dialogOKre(context, "", response.body().getMsg(), getString(R.string.ok), new OnDialogConfirmListener() {
                                        @Override
                                        public void onYes() {
                                        binding.etName.getText().clear();
                                        binding.etDesc.getText().clear();
                                        binding.etSubject.getText().clear();

                                        }

                                        @Override
                                        public void onNo() {

                                        }
                                    });
                                } else {
                                    utilities.dialogOK(context, "", response.body().getMsg(), context.getString(R.string.ok), false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OtherDTO> call, Throwable t) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                       // Log.e(TAG, t.toString());
                    }

                });
            }
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
