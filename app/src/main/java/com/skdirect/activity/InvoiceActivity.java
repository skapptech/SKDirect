package com.skdirect.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.adapter.InvoiceAdapter;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.FragmentChatdBinding;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.InvoiceModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.SellerProductList;
import com.skdirect.model.response.InvoiceMainModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.observers.DisposableObserver;

public class InvoiceActivity extends AppCompatActivity {
    private FragmentChatdBinding mBinding;
    public DBHelper dbHelper;
    private CommonClassForAPI commonClassForAPI;
    private int skipCount = 0, takeCount = 15;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private LinearLayoutManager layoutManager;
    private final ArrayList<InvoiceModel> invoiceList = new ArrayList<>();
    private InvoiceAdapter invoiceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.fragment_chatd);
        dbHelper = MyApplication.getInstance().dbHelper;
        initViews();
        getInvoice();
    }


    private void initViews() {
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        mBinding.rvInvoice.setLayoutManager(layoutManager);

        invoiceAdapter = new InvoiceAdapter(this,invoiceList);
        mBinding.rvInvoice.setAdapter(invoiceAdapter);

        dbHelper = MyApplication.getInstance().dbHelper;
        mBinding.toolbarTittle.tvTittle.setText(dbHelper.getString(R.string.invoice));
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mBinding.rvInvoice.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false;
                            skipCount = skipCount + 15;
                            // mBinding.progressBar.setVisibility(View.VISIBLE);
                            getInvoice();
                        }
                    }
                }
            }
        });
        invoiceList.clear();
    }

    private void getInvoice() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(this);
            commonClassForAPI.getInvoice(observerInvoice, new PaginationModel(skipCount,takeCount));
        } else {
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_internet_connection));
        }
    }

    private final DisposableObserver<InvoiceMainModel> observerInvoice = new DisposableObserver<InvoiceMainModel>() {
        @Override
        public void onNext(@NotNull InvoiceMainModel invoiceMainModel) {
            Utils.hideProgressDialog();
            if (invoiceMainModel.isSuccess()) {
                if (invoiceMainModel.getResultItem().size()>0){
                    invoiceList.addAll(invoiceMainModel.getResultItem());
                    invoiceAdapter.notifyDataSetChanged();
                    loading = true;
                }else {
                    if (invoiceList.size() == 0) {
                        mBinding.tvNoInvoiceFound.setVisibility(View.VISIBLE);
                        mBinding.rvInvoice.setVisibility(View.GONE);
                    }
                }

            } else {
                loading = false;
                mBinding.tvNoInvoiceFound.setVisibility(View.VISIBLE);
                mBinding.rvInvoice.setVisibility(View.GONE);
            }

        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog();
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
        }
    };


}
