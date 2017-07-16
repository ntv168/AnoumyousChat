package com.example.sam.anoumyouschat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sam.anoumyouschat.pojo.ResponseData;
import com.example.sam.anoumyouschat.pojo.ResponseFind;
import com.example.sam.anoumyouschat.pojo.User;
import com.example.sam.anoumyouschat.retrofit.APIInterface;
import com.example.sam.anoumyouschat.retrofit.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    RetrofitSingleton retrofitSingleton;
    APIInterface apiInterface;
    private EditText txtName;
    private User user = new User();
    private Boolean flag = false;
    private Bundle extra = new Bundle();
    private boolean find = true;
    private Intent intent;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtName = (EditText) findViewById(R.id.name);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

    }

    public void startChat(View view) {
        String name = txtName.getText().toString().trim();
        intent = new Intent(this, ChatMain.class);


        if (txtName.getText().toString().trim().length() > 0) {
            progress = ProgressDialog.show(MainActivity.this, "Xin đợi trong giây lát",
                    "dialog message", true);
                apiInterface = retrofitSingleton.getInstance().getApiInterface();
                apiInterface.registerNewName(name).enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        user = response.body().getData().getUser();
                        flag = true;
                        extra.putString("id", user.getId());
                        extra.putString("name", user.getName());
                        find();
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {

                    }
                });

        } else {
            Toast.makeText(getApplicationContext(),
                    "Please enter your name", Toast.LENGTH_LONG).show();
        }


    }

    public void find() {
        apiInterface.findUser(user.getId()).enqueue(new Callback<ResponseFind>() {
            @Override
            public void onResponse(Call<ResponseFind> call, Response<ResponseFind> response) {
                if (response.body().getData().getUser().getStatus().equals("CONNECTED")) {
                    extra.putString("conversation", response.body().getData().getUser().getRoom());
                    intent.putExtra("user", extra);
                    Log.d(TAG, "------------------: findUser");
                    find = false;
                    progress.dismiss();
                    startActivity(intent);


                } if (response.body().getData().getUser().getStatus().equals("FINDING") ||
                        response.body().getData().getUser().getStatus().equals("ONLINE")) {
                    apiInterface.findRoom(user.getId()).enqueue(new Callback<ResponseFind>() {
                        @Override
                        public void onResponse(Call<ResponseFind> call, Response<ResponseFind> response) {
                            if (response.code() == 200) {
                                extra.putString("conversation", response.body().getData().getConversation());
                                intent.putExtra("user", extra);
//                                        Toast.makeText(MainActivity.this, ""+response.body().getData().getConversation(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "------------------: findRoom");
                                find = false;
                                progress.dismiss();
                                startActivity(intent);
                            } else {
                                find();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseFind> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseFind> call, Throwable t) {

            }
        });
    }
}
