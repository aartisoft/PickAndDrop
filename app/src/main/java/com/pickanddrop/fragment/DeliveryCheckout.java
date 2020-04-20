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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.DeliveryBookBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryCheckout extends BaseFragment implements AppConstants, View.OnClickListener {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    private DeliveryBookBinding deliveryBookBinding;
    private String email = "";
    private DeliveryDTO.Data deliveryDTO;
    private String TAG = DeliveryCheckout.class.getName();

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
        deliveryBookBinding = DataBindingUtil.inflate(inflater, R.layout.delivery_book, container, false);
        return deliveryBookBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolBar();
        setValues();
    }

    private void setValues() {

        deliveryBookBinding.etPickupAddress.setText(deliveryDTO.getPickupaddress());
        deliveryBookBinding.etDropoffAddress.setText(deliveryDTO.getDropoffaddress());
//        deliveryBookBinding.etPrice.setText(getString(R.string.us_dollar)+" "+deliveryDTO.getDeliveryCost());
//        deliveryBookBinding.etPrice.setText(getString(R.string.us_dollar)+" 20");
        try {
            if (!deliveryDTO.getDeliveryType().equalsIgnoreCase("shop_deliver")){
                deliveryBookBinding.etPrice.setText(getString(R.string.us_dollar)+" "+String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
            }else {
                    int basecharge=300;
                if (Integer.valueOf(deliveryDTO.getItemQuantity())<3000){
                    deliveryBookBinding.etPrice.setText(getString(R.string.us_dollar)+" "+basecharge);
                }else if (Integer.valueOf(deliveryDTO.getItemQuantity())>3000){

                    int ExtraItemCost=(Integer.valueOf(deliveryDTO.getItemQuantity())-3000)/1000;
                    int ExtraServicecharge=(ExtraItemCost*50)+basecharge;
                    Log.e("Extra_item_cost",""+ExtraItemCost+" total_charge"+ExtraServicecharge);

                    deliveryBookBinding.etPrice.setText(getString(R.string.us_dollar)+" "+ExtraServicecharge);

                    deliveryDTO.setDeliveryCost(String.format("%2f", String.valueOf(ExtraServicecharge)));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        deliveryBookBinding.etDeliveryDate.setText(deliveryDTO.getDeliveryDate());
        deliveryBookBinding.etDeliveryTime.setText(deliveryDTO.getDeliveryTime());
        if (deliveryDTO.getDeliveryDistance()!=null){
            deliveryBookBinding.etDistance.setText(deliveryDTO.getDeliveryDistance() +" "+ getString(R.string.km));
        }else {
            deliveryBookBinding.etDistance.setText("-- "+ getString(R.string.km));
        }


        if (deliveryDTO.getVehicleType()==null){
            deliveryDTO.setVehicleType("bike");
        }
        if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.bike))) {
            deliveryBookBinding.llCar.setAlpha(Float.parseFloat("0.4"));
            deliveryBookBinding.llVan.setAlpha(Float.parseFloat("0.4"));
            deliveryBookBinding.llTruck.setAlpha(Float.parseFloat("0.4"));
        }
//        else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.car))) {
//            deliveryBookBinding.llBike.setAlpha(Float.parseFloat("0.4"));
//            deliveryBookBinding.llVan.setAlpha(Float.parseFloat("0.4"));
//            deliveryBookBinding.llTruck.setAlpha(Float.parseFloat("0.4"));
//        } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.van))) {
//            deliveryBookBinding.llBike.setAlpha(Float.parseFloat("0.4"));
//            deliveryBookBinding.llCar.setAlpha(Float.parseFloat("0.4"));
//            deliveryBookBinding.llTruck.setAlpha(Float.parseFloat("0.4"));
//        } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.truck))) {
//            deliveryBookBinding.llBike.setAlpha(Float.parseFloat("0.4"));
//            deliveryBookBinding.llVan.setAlpha(Float.parseFloat("0.4"));
//            deliveryBookBinding.llCar.setAlpha(Float.parseFloat("0.4"));
//        }


        if (deliveryDTO.getDeliveryType().equalsIgnoreCase("shop_deliver")) {
            //deliveryBookBinding.btnFour.setAlpha(Float.parseFloat("0.4"));
            deliveryBookBinding.btnSame.setAlpha(Float.parseFloat("0.4"));
            deliveryBookBinding.tvServicePriText.setText("₱300 per transaction for 3k and below worth of goods, +₱50 for above 3k and for additional 1k worth of goods thereafter.");
        }
       // else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("4HOUR")) {
        //    deliveryBookBinding.btnSame.setAlpha(Float.parseFloat("0.4"));
        //    deliveryBookBinding.btnTwo.setAlpha(Float.parseFloat("0.4"));
       // }
        else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("pick_deliver")) {
            deliveryBookBinding.btnTwo.setAlpha(Float.parseFloat("0.4"));
           // deliveryBookBinding.btnFour.setAlpha(Float.parseFloat("0.4"));
            deliveryBookBinding.tvServicePriText.setText("₱250 per transaction +₱20/km in excess of 10 km Exemptions applied for items too heavy or too big to be carried by single travel.");
        }
    }

    private void initToolBar() {

    }

    private void initView() {
        deliveryBookBinding.btnSubmit.setOnClickListener(this);
        deliveryBookBinding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit:
                Utilities.hideKeyboard(deliveryBookBinding.btnSubmit);
                callOrderBookApi();
                break;
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
        }
    }

    public void callOrderBookApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            Map<String, String> map = new HashMap<>();
            map.put("user_id", appSession.getUser().getData().getUserId());
           // map.put("pickup_comapny_name", deliveryDTO.getPickupComapnyName());
            map.put("pickup_first_name", deliveryDTO.getPickupFirstName());
            map.put("pickup_last_name", deliveryDTO.getPickupLastName());
            map.put("pickup_mob_number", deliveryDTO.getPickupMobNumber());
            map.put("pickupaddress", deliveryDTO.getPickupaddress());
            map.put("item_description", deliveryDTO.getItemDescription());
            map.put("item_cost", deliveryDTO.getItemQuantity());
            map.put("delivery_date", deliveryDTO.getDeliveryDate());
            map.put("pickup_special_inst", deliveryDTO.getPickupSpecialInst());

            if (!deliveryDTO.getDeliveryType().equalsIgnoreCase("shop_deliver")){
                map.put("dropoff_first_name", deliveryDTO.getDropoffFirstName());
                map.put("dropoff_last_name", deliveryDTO.getDropoffLastName());
                map.put("dropoff_mob_number", deliveryDTO.getDropoffMobNumber());
                map.put("dropoff_special_inst", deliveryDTO.getDropoffSpecialInst());
                map.put("dropoffaddress", deliveryDTO.getDropoffaddress());
            }

            //map.put("parcel_height", deliveryDTO.getParcelHeight());
           // map.put("parcel_width", deliveryDTO.getParcelWidth());
          //  map.put("parcel_lenght", deliveryDTO.getParcelLenght());
          //  map.put("parcel_weight", deliveryDTO.getParcelWeight());
            map.put("delivery_type", deliveryDTO.getDeliveryType());
            if (deliveryDTO.getDeliveryDistance()!=null){
                map.put("delivery_distance", deliveryDTO.getDeliveryDistance());
            }else {
                map.put("delivery_distance", " ");
            }


            try {
                if (!deliveryDTO.getDeliveryType().equalsIgnoreCase("shop_deliver")){
                    map.put("delivery_cost", String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
                }else {
                    map.put("delivery_cost", "300");
                }

            } catch (Exception e) {

                if (!deliveryDTO.getDeliveryType().equalsIgnoreCase("shop_deliver")){
                    map.put("delivery_cost", deliveryDTO.getDeliveryCost());
                }else {
                    map.put("delivery_cost", "300");
                }
                e.printStackTrace();
            }

           // map.put("dropoff_comapny_name", deliveryDTO.getDropoffComapnyName());
            map.put("vehicle_type", deliveryDTO.getVehicleType());
            map.put("pickUpLat", deliveryDTO.getPickupLat());
            map.put("pickUpLong", deliveryDTO.getPickupLong());

            if (!deliveryDTO.getDeliveryType().equalsIgnoreCase("shop_deliver")){
                map.put("dropOffLong", deliveryDTO.getDropoffLong());
                map.put("dropOffLat", deliveryDTO.getDropoffLat());
                map.put("driver_delivery_cost", deliveryDTO.getDriverDeliveryCost());
            }else {
                map.put("dropOffLong", "");
                map.put("dropOffLat", "");
                map.put("driver_delivery_cost", "");
            }


            map.put("delivery_time", deliveryDTO.getDeliveryTime());
            map.put(PN_APP_TOKEN, APP_TOKEN);
            if (!deliveryDTO.getDeliveryType().equalsIgnoreCase("shop_deliver")){
                map.put("dropoff_country_code", deliveryDTO.getDropoffCountryCode());
            }else {
                map.put("dropoff_country_code"," ");
            }

            map.put("pickup_country_code", deliveryDTO.getPickupCountryCode());

            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.callCreateOrderApi(map);
            call.enqueue(new Callback<OtherDTO>() {
                @Override
                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {
                                utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
                                    @Override
                                    public void onYes() {
                                       // ((DrawerContentSlideActivity) context).popAllFragment();
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
                public void onFailure(Call<OtherDTO> call, Throwable t) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e(TAG, t.toString());
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);

                }
            });
        }
    }
}