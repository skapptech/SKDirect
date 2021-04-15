package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skdirect.api.RestClient;
import com.skdirect.model.AddReviewModel;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.ReviewMainModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReViewViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<ReviewMainModel> reviewModel;

    public LiveData<ReviewMainModel> getReviewModel() {
        reviewModel=null;
        reviewModel = new MutableLiveData<>();
        return reviewModel;
    }

    public MutableLiveData<ReviewMainModel> getReviewModelRequest(AddReviewModel paginationModel) {
        RestClient.getInstance().getService().getRating(paginationModel).enqueue(new Callback<ReviewMainModel>() {
            @Override
            public void onResponse(Call<ReviewMainModel> call, Response<ReviewMainModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    reviewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ReviewMainModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return reviewModel;
    }
}
