package com.example.sam.anoumyouschat.retrofit;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.example.sam.anoumyouschat.Constants;
import com.example.sam.anoumyouschat.pojo.Data;
import com.example.sam.anoumyouschat.pojo.ResponseData;
import com.example.sam.anoumyouschat.pojo.ResponseFind;
import com.example.sam.anoumyouschat.pojo.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sam on 7/7/2017.
 */

public class RetrofitSingleton {
    private static Retrofit retrofit;
    private static APIInterface apiInterface;
    private static volatile RetrofitSingleton retrofitSingleton = null;
    Data data = null;
    User user = null;

    Boolean flag = false;

    RetrofitSingleton() {}

    public static RetrofitSingleton getInstance(){
        if (retrofitSingleton == null) {
            synchronized (RetrofitSingleton.class){
                if (retrofitSingleton == null) {
                    retrofitSingleton = new RetrofitSingleton();
                    RetrofitSingleton.retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.CHAT_SERVER_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitSingleton.apiInterface = RetrofitSingleton.retrofit.create(APIInterface.class);
                }
            }
        }
        return retrofitSingleton;
    }

    public static APIInterface getApiInterface() {
        return apiInterface;
    }
    
    public Data register (String username, Context context) {

            apiInterface.registerNewName(username).enqueue(new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    if (response.code() == 201) {
                        data = response.body().getData();
                        finduser(data.getUser().getId());
                    }

                }
                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {
                }
            });
        return data;
    }

    public User finduser (String id) {
        apiInterface.findUser(id).enqueue(new Callback<ResponseFind>() {
            @Override
            public void onResponse(Call<ResponseFind> call, Response<ResponseFind> response) {
                user = response.body().getData().getUser();
                findRoom(user.getId());
            }
            @Override
            public void onFailure(Call<ResponseFind> call, Throwable t) {

            }
        });
        return user;
    }

    public Data findRoom (String id) {
        apiInterface.findRoom(id).enqueue(new Callback<ResponseFind>() {
            @Override
            public void onResponse(Call<ResponseFind> call, Response<ResponseFind> response) {
                data = response.body().getData();
            }
            @Override
            public void onFailure(Call<ResponseFind> call, Throwable t) {

            }
        });
        return data;
    }
}
