package com.skdirect.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.skdirect.R;
import com.skdirect.adapter.OrderDetailsItemAdapter;
import com.skdirect.databinding.ActivityOrderDeatilsBinding;
import com.skdirect.model.MallMainModelBolleanResult;
import com.skdirect.model.MyOrderModel;
import com.skdirect.model.OrderDetailsModel;
import com.skdirect.model.OrderItemModel;
import com.skdirect.model.OrderStatusDC;
import com.skdirect.stepform.MainStepperAdapter;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.OrderDetailsViewMode;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityOrderDeatilsBinding mBinding;
    private MyOrderModel myOrderModel;
    private OrderDetailsViewMode orderDetailsViewMode;
    private List<OrderStatusDC> OrderStatusDCList = new ArrayList<>();
    private MainStepperAdapter mainStepperAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_deatils);
        orderDetailsViewMode = ViewModelProviders.of(this).get(OrderDetailsViewMode.class);
        getIntentData();
        initView();
        callOrderDetails();
    }

    private void callOrderDetails() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(OrderDetailActivity.this);
            callOrderDetailsAPI();
            callOrderItemsAPI();
        } else {
            Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
        }
    }


    private void getIntentData() {
        myOrderModel = (MyOrderModel) getIntent().getSerializableExtra("myOrderModel");

    }

    private void initView() {
        mBinding.toolbarTittle.tvTittle.setText("My Order");
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.tvCancleOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;

            case R.id.tv_cancle_order:
                cancleOrder();
                break;
        }
    }


    private void setFullHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.stepperList.getLayoutParams();
        params.height = (int) (displayMetrics.widthPixels / 1.7);
        mBinding.stepperList.setLayoutParams(params);
    }

    private void callOrderDetailsAPI() {
        orderDetailsViewMode.getOrderDetailsRequest(myOrderModel.getId());
        orderDetailsViewMode.getOrderDetailsViewMode().observe(this, new Observer<OrderDetailsModel>() {
            @Override
            public void onChanged(OrderDetailsModel orderDetailsModel) {
                Utils.hideProgressDialog();
                if (orderDetailsModel != null) {

                    OrderStatusDCList.add(new OrderStatusDC(orderDetailsModel.getOrderDate(), "Ordered"));
                    OrderStatusDCList.addAll(orderDetailsModel.getOrderStatusDC());
                    mBinding.tvOrderNumber.setText("Order No :" + orderDetailsModel.getId());
                    mBinding.tvCreatedOrder.setText("Order on " + Utils.getDateFormate(orderDetailsModel.getOrderDate()));
                    mBinding.tvSellerName.setText("Seller: " + orderDetailsModel.getSellerName());
                    mBinding.tvPaymentType.setText("Payment Mode: " + orderDetailsModel.getPaymentMode());
                    mBinding.tvAddreshOne.setText(orderDetailsModel.getAddressOne());
                    mBinding.tvSellerAddress.setText(orderDetailsModel.getAddressThree());
                    mBinding.tvCity.setText(orderDetailsModel.getCity());
                    mBinding.tvState.setText(orderDetailsModel.getState());
                    mBinding.tvOrderAmount.setText("₹ " + orderDetailsModel.getTotalPrice());
                    mBinding.tvOrderAmountTotal.setText("₹ " + orderDetailsModel.getTotalItemAmount());

                    if (orderDetailsModel.getTotalSavingAmount() != 0.0) {
                        mBinding.rlTotalSaving.setVisibility(View.VISIBLE);
                        mBinding.tvTotalSaving.setText("-₹ " + orderDetailsModel.getTotalSavingAmount());
                        double totalSavingDiscountAmount = orderDetailsModel.getTotalPrice() + orderDetailsModel.getTotalSavingAmount();
                        mBinding.tvOrderAmount.setText("₹ " + totalSavingDiscountAmount);
                    } else {
                        mBinding.rlTotalSaving.setVisibility(View.GONE);
                    }

                    if (orderDetailsModel.getTotalDeliveryCharge() != 0.0) {
                        mBinding.rlDeliveryCharge.setVisibility(View.VISIBLE);
                        double totalSavingDiscountAmount = orderDetailsModel.getTotalPrice() + orderDetailsModel.getTotalSavingAmount();
                        mBinding.tvOrderAmount.setText("₹ " + totalSavingDiscountAmount);
                        mBinding.tvTotalSaving.setText("-₹ " + orderDetailsModel.getTotalSavingAmount());
                        mBinding.tvDeliveryCharge.setText("" + orderDetailsModel.getTotalDeliveryCharge());
                        mBinding.tvOrderAmountTotal.setText("₹ " + orderDetailsModel.getTotalPrice());
                    } else {
                        mBinding.rlDeliveryCharge.setVisibility(View.GONE);
                    }


                }

                if (orderDetailsModel.getOrderStatusDC().size() > 0) {
                    for (int i = 0; i < orderDetailsModel.getOrderStatusDC().size(); i++) {

                        if (orderDetailsModel.getOrderStatusDC().get(i).getStatus().equals("Pending")) {
                            mBinding.tvCancleOrder.setVisibility(View.VISIBLE);
                        } else if (orderDetailsModel.getOrderStatusDC().get(i).getStatus().equals("Accepted")) {
                            mBinding.tvCancleOrder.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.tvCancleOrder.setVisibility(View.GONE);
                        }
                    }
                    mainStepperAdapter = new MainStepperAdapter(OrderDetailActivity.this, OrderStatusDCList);
                    mBinding.stepperList.setAdapter(mainStepperAdapter);
                    setFullHeight();
                } else {

                }
            }

        });
    }

    private void callOrderItemsAPI() {
        orderDetailsViewMode.getOrderDetailsItemsRequest(myOrderModel.getId());
        orderDetailsViewMode.getOrderDetailsItemsViewMode().observe(this, new Observer<ArrayList<OrderItemModel>>() {
            @Override
            public void onChanged(ArrayList<OrderItemModel> orderItemModels) {
                Utils.hideProgressDialog();
                if (orderItemModels.size() > 0) {
                    OrderDetailsItemAdapter orderDetailsItemAdapter = new OrderDetailsItemAdapter(OrderDetailActivity.this, orderItemModels);
                    mBinding.rMyOrder.setAdapter(orderDetailsItemAdapter);
                    mBinding.tvItemCount.setText("Price Details (" + orderItemModels.size() + " items )");
                }

            }

        });
    }

    private void cancleOrder() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(OrderDetailActivity.this);
            orderCancleFromUser();
        } else {
            Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
        }
    }

    private void orderCancleFromUser() {
        orderDetailsViewMode.getCancelOrderVMRequest(myOrderModel.getId());
        orderDetailsViewMode.getCancelOrderVM().observe(this, new Observer<MallMainModelBolleanResult>() {
            @Override
            public void onChanged(MallMainModelBolleanResult result) {
                Utils.hideProgressDialog();
                if (result.isSuccess()) {
                    onBackPressed();
                }
            }

        });

    }


}
