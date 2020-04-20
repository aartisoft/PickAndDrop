package com.pickanddrop.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.databinding.CreateOrderOneBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.Utilities;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class CreateOrderOne extends BaseFragment implements AppConstants, View.OnClickListener {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    private CreateOrderOneBinding createOrderOneBinding;
    private String countryCode = "", deliveryType = "", pickupLat = "", pickupLong = "", companyName = "", firstName = "",
            lastName = "", mobile = "", pickUpAddress = "", itemDescription = "", itemQuantity = "", deliDate = "",
            deliTime = "", specialInstruction = "";
    private PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    private static final int REQUEST_PICK_PLACE = 2345;
    private int mYear, mMonth, mDay;
    private Calendar c;
    private DatePickerDialog datePickerDialog;
    private String TAG = CreateOrderOne.class.getName();
    private boolean rescheduleStatus = false;
    private DeliveryDTO.Data data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("delivery_type")) {
            deliveryType = getArguments().getString("delivery_type");
            Log.e(TAG, deliveryType);
        }

        if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
            data = getArguments().getParcelable("deliveryDTO");
            rescheduleStatus = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createOrderOneBinding = DataBindingUtil.inflate(inflater, R.layout.create_order_one, container, false);
        return createOrderOneBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolBar();

        if (rescheduleStatus) {
            setValues();
        } else if (!appSession.getUserType().equals(DRIVER)) {
            setValues();
        }


        createOrderOneBinding.etPickupAddress.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (createOrderOneBinding.etPickupAddress.getRight() - createOrderOneBinding.etPickupAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        try {
                            startActivityForResult(builder.build(getActivity()), REQUEST_PICK_PLACE);
                        } catch
                        (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                                        e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void setValues() {

        if (appSession.getUserType().equals(DRIVER)) {

            // createOrderOneBinding.etCompany.setText(data.getPickupComapnyName());
            createOrderOneBinding.etFirstName.setText(data.getPickupFirstName());
            createOrderOneBinding.etLastName.setText(data.getPickupLastName());
            createOrderOneBinding.etMobile.setText(data.getPickupMobNumber());
            createOrderOneBinding.etPickupAddress.setText(data.getPickupaddress());
            createOrderOneBinding.etItemDescription.setText(data.getItemDescription());
            createOrderOneBinding.etItemQuantity.setText(data.getItemQuantity());
            createOrderOneBinding.etDeliveryDate.setText(data.getDeliveryDate());
            createOrderOneBinding.etDeliveryTime.setText(data.getDeliveryTime());
            createOrderOneBinding.etPickSpecialInst.setText(data.getPickupSpecialInst());

            deliveryType = data.getDeliveryType();
            pickupLat = data.getPickupLat();
            pickupLong = data.getPickupLong();

        } else {

            //createOrderOneBinding.etCompany.setText(appSession.getUser().getData().getCompanyName());
            createOrderOneBinding.etFirstName.setText(appSession.getUser().getData().getFirstname());
            createOrderOneBinding.etLastName.setText(appSession.getUser().getData().getLastname());
            //createOrderOneBinding.etEmail.setText(appSession.getUser().getData().getEmail());
            createOrderOneBinding.etMobile.setText(appSession.getUser().getData().getPhone());
            createOrderOneBinding.etPickupAddress.setText(appSession.getUser().getData().getHouseNo() + ", " +
                    appSession.getUser().getData().getStreetName() + ", " + appSession.getUser().getData().getCity() + ", " +
                    appSession.getUser().getData().getState() + ", " + appSession.getUser().getData().getCountryName() + ", " +
                    appSession.getUser().getData().getPostcode());
        }

        //createOrderOneBinding.ccp.setCountryForPhoneCode(Integer.parseInt(data.getPickupCountryCode()));


        //createOrderOneBinding.etPickupAddress.setEnabled(false);
    }

    private void initToolBar() {

    }

    private void initView() {
//        createOrderOneBinding.ccp.registerPhoneNumberTextView(createOrderOneBinding.etMobile);
        c = Calendar.getInstance();
        createOrderOneBinding.ivBack.setOnClickListener(this);
        createOrderOneBinding.etPickupAddress.setOnClickListener(this);
        createOrderOneBinding.btnNext.setOnClickListener(this);
        createOrderOneBinding.etDeliveryDate.setOnClickListener(this);
        createOrderOneBinding.etDeliveryTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                Utilities.hideKeyboard(createOrderOneBinding.btnNext);
                //countryCode = createOrderOneBinding.ccp.getSelectedCountryCode();
                countryCode = createOrderOneBinding.ccp.getText().toString();
                Log.e(TAG, ">>>>>>>>>>>>>>" + countryCode);

                //companyName = createOrderOneBinding.etCompany.getText().toString();
                firstName = createOrderOneBinding.etFirstName.getText().toString();
                lastName = createOrderOneBinding.etLastName.getText().toString();
                mobile = createOrderOneBinding.etMobile.getText().toString();
                pickUpAddress = createOrderOneBinding.etPickupAddress.getText().toString();
                itemDescription = createOrderOneBinding.etItemDescription.getText().toString();
                itemQuantity = createOrderOneBinding.etItemQuantity.getText().toString();
                deliDate = createOrderOneBinding.etDeliveryDate.getText().toString();
                deliTime = createOrderOneBinding.etDeliveryTime.getText().toString();
                specialInstruction = createOrderOneBinding.etPickSpecialInst.getText().toString();

                if (isValid()) {

                    CreateOrderSecond createOrderSecond = new CreateOrderSecond();
                    DeliveryCheckout deliveryCheckout = new DeliveryCheckout();
                    DeliveryDTO.Data deliveryDTO = null;
                    Bundle bundle = new Bundle();
                    if (rescheduleStatus) {
                        deliveryDTO = data;
                        bundle.putString("rescheduleStatus", "rescheduleStatus");
                    } else {
                        deliveryDTO = new DeliveryDTO().new Data();
                    }

                    // deliveryDTO.setPickupComapnyName(companyName);
                    deliveryDTO.setPickupFirstName(firstName);
                    deliveryDTO.setPickupLastName(lastName);
                    deliveryDTO.setPickupMobNumber(mobile);
                    deliveryDTO.setPickupaddress(pickUpAddress);
                    deliveryDTO.setDeliveryDate(deliDate);
                    deliveryDTO.setDeliveryTime(deliTime);
                    deliveryDTO.setPickupSpecialInst(specialInstruction);

                    if (pickupLat.equalsIgnoreCase("") &&
                            pickupLong.equalsIgnoreCase("")){

                        Geocoder coder = new Geocoder(getActivity());
                        List<Address> address;
                        try {
                            address = coder.getFromLocationName(appSession.getUser().getData().getPostcode(),5);

                            Address location=address.get(0);
                            location.getLatitude();
                            location.getLongitude();

                            pickupLat=String.valueOf( location.getLatitude());
                            pickupLat=String.valueOf( location.getLongitude());

                            deliveryDTO.setPickupLat(String.valueOf( location.getLatitude()));
                            deliveryDTO.setPickupLong(String.valueOf( location.getLongitude()));

                            Log.e("lat_lng",""+ location.getLatitude()+" "+ location.getLongitude());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }else {
                        deliveryDTO.setPickupLat(pickupLat);
                        deliveryDTO.setPickupLong(pickupLong);
                    }
                    deliveryDTO.setDeliveryType(deliveryType);
                    deliveryDTO.setItemDescription(itemDescription);
                    deliveryDTO.setItemQuantity(itemQuantity);
                    deliveryDTO.setPickupCountryCode(countryCode);


                    if (deliveryType.equalsIgnoreCase("shop&deliver")) {



                        bundle.putParcelable("deliveryDTO", deliveryDTO);
                        deliveryCheckout.setArguments(bundle);
                        addFragmentWithoutRemove(R.id.container_main, deliveryCheckout, "DeliveryCheckout");


                    } else {
                        bundle.putParcelable("deliveryDTO", deliveryDTO);
                        createOrderSecond.setArguments(bundle);
                        addFragmentWithoutRemove(R.id.container_main, createOrderSecond, "CreateOrderSecond");
                    }


                }
                break;
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
//            case R.id.et_pickup_address:
//                try {
//                    startActivityForResult(builder.build(getActivity()), REQUEST_PICK_PLACE);
//                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//                break;
            case R.id.et_delivery_date:
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                createOrderOneBinding.etDeliveryDate.setText(Utilities.formatDateShow(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));

                            }
                        }, mYear, mMonth, mDay);
//                calendar.add(Calendar.YEAR, -18);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
                break;
            case R.id.et_delivery_time:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        etTime.setText(selectedHour + ":" + selectedMinute);
                        createOrderOneBinding.etDeliveryTime.setText(updateTime(selectedHour, selectedMinute));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
        }
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_PLACE && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(context, data);

            Log.i(getClass().getName(), "Class is >>>>>" + place.getName() + " " + place.getAddress() + "   " + place.getLatLng());
            pickupLat = place.getLatLng().latitude + "";
            pickupLong = place.getLatLng().longitude + "";
            createOrderOneBinding.etPickupAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
        }
    }

    public String getAddressFromLatLong(double latitude, double longitude, boolean status) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            return address + " " + city;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isValid() {
        if (firstName == null || firstName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_first_name), getString(R.string.ok), false);
            createOrderOneBinding.etFirstName.requestFocus();
            return false;
        } else if (lastName == null || lastName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_last_name), getString(R.string.ok), false);
            createOrderOneBinding.etLastName.requestFocus();
            return false;
        } else if (mobile.trim().length() == 0) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_mobile_number), getString(R.string.ok), false);
            createOrderOneBinding.etMobile.requestFocus();
            return false;
        } else if (!utilities.checkMobile(mobile)) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_valid_mobile_number), getString(R.string.ok), false);
            createOrderOneBinding.etMobile.requestFocus();
            return false;
        } else if (pickUpAddress == null || pickUpAddress.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_pick_address), getString(R.string.ok), false);
            createOrderOneBinding.etPickupAddress.requestFocus();
            return false;
        } else if (itemDescription == null || itemDescription.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_item_desc), getString(R.string.ok), false);
            createOrderOneBinding.etItemDescription.requestFocus();
            return false;
        } else if (itemQuantity == null || itemQuantity.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), "Please enter item quantity.", getString(R.string.ok), false);
            createOrderOneBinding.etItemQuantity.requestFocus();
            return false;
        } else if (deliDate == null || deliDate.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_deli_date), getString(R.string.ok), false);
            createOrderOneBinding.etDeliveryDate.requestFocus();
            return false;
        } else if (deliTime == null || deliTime.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_deli_time), getString(R.string.ok), false);
            createOrderOneBinding.etDeliveryTime.requestFocus();
            return false;
        }
        return true;
    }
}