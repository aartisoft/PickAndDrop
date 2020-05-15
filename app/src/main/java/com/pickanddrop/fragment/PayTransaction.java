package com.pickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.PayTransactionBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.LoginDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Raghvendra Sahu on 21-Apr-20.
 */
public class PayTransaction extends BaseFragment implements View.OnClickListener, AppConstants {
    PayTransactionBinding binding;
    private DeliveryDTO.Data deliveryDTO;
    private AppSession appSession;
    private Utilities utilities;
    private Context context;
    private String receiverReference = "";
    private MultipartBody.Part itemsImage = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
            deliveryDTO = getArguments().getParcelable("deliveryDTO");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pay_transaction, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
    }

    private void initView() {
        binding.btnSubmit.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);

        binding.tvPickupLocation.setText(getString(R.string.pickup_loc_txt) +" - "+ deliveryDTO.getPickupaddress());
        binding.tvDropAddress.setText(getString(R.string.delivery_loc_txt) +" - "+ deliveryDTO.getDropoffaddress());
        binding.tvDeliveryDate.setText(getString(R.string.delivery_datein_txt) +" - "+ deliveryDTO.getDeliveryDate());
        binding.tvDeliveryTime.setText(getString(R.string.delivery_time) +" - "+ deliveryDTO.getDeliveryTime());

        if (deliveryDTO.getDeliveryType().equals("shop_deliver")){
            binding.tvTransAmt.setText("Send ₱16"+" Transaction fee via Gcash to 09198777107");
        }else {
            binding.tvTransAmt.setText("Send ₱6"+" Transaction fee via Gcash to 09198777107");
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.btn_submit:
        receiverReference = binding.etRefrenceNmbr.getText().toString();
        if (isValid()){
            callDeliveryOrderApi();
        }
        break;
            case R.id.iv_back:
               // ((DrawerContentSlideActivity) context).popFragment();

                utilities.dialogOKre(context, "", "Are you sure you want to left", getString(R.string.yes), new OnDialogConfirmListener() {
                    @Override
                    public void onYes() {
                        ((DrawerContentSlideActivity) context).popFragment();
//                                        ((DrawerContentSlideActivity) context).onBackPressed();

                       //replaceFragmentWithoutBack(R.id.container_main, new CurrentList(), "CurrentList");
                        Intent intent=new Intent(getActivity(),DrawerContentSlideActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onNo() {

                    }
                });



                break;
    }
    }

    private boolean isValid() {
        if (receiverReference == null || receiverReference.equals("")) {
            utilities.dialogOK(context, "", "Please enter reference number", getString(R.string.ok), false);
            binding.etRefrenceNmbr.requestFocus();
            return false;
        }

        return true;
    }

    public void callDeliveryOrderApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            Map<String, String> partMap = new HashMap<>();

//            if (!imagePath.equals("")) {
//                try {
//                    File profileImageFile = new File(imagePath);
//                    RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
//                    itemsImage = MultipartBody.Part.createFormData("client_image", profileImageFile.getName(), propertyImage);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

//            if (!StoredPath.equals("")) {
//                try {
//                    File profileImageFile = new File(StoredPath);
//                    RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
//                    signatureImage = MultipartBody.Part.createFormData("signature_image", profileImageFile.getName(), propertyImage);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

            partMap.put("order_id",  deliveryDTO.getOrderId());
            partMap.put("code", APP_TOKEN);
            //partMap.put("signature_name", RequestBody.create(MediaType.parse("signature_name"), receiverName));
            partMap.put("txn_amt", "50");
           partMap.put("reference_no",  receiverReference);

            APIInterface apiInterface = APIClient.getClient();
            Call<LoginDTO> call = apiInterface.callTxnPayApi(partMap);

            call.enqueue(new Callback<LoginDTO>() {
                @Override
                public void onResponse(Call<LoginDTO> call, Response<LoginDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {
                                utilities.dialogOKre(context, "", "Payment successful", getString(R.string.ok), new OnDialogConfirmListener() {
                                    @Override
                                    public void onYes() {
                                      //  ((DrawerContentSlideActivity) context).popAllFragment();
                                       // replaceFragmentWithoutBack(R.id.container_main, new CurrentList(), "CurrentList");

                                        Intent intent=new Intent(getActivity(),DrawerContentSlideActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();

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
                    //Log.e(TAG, t.toString());
                }
            });
        }
    }
}
