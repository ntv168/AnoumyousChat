package com.example.sam.anoumyouschat.retrofit;

import com.example.sam.anoumyouschat.pojo.Data;
import com.example.sam.anoumyouschat.pojo.ResponseData;
import com.example.sam.anoumyouschat.pojo.ResponseFind;
import com.example.sam.anoumyouschat.pojo.User;


import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by Sam on 4/18/2017.
 */

public interface APIInterface {

    @FormUrlEncoded
    @POST("api/register")
    Call<ResponseData> registerNewName(@Field("name") String name);

    @FormUrlEncoded
    @POST("api/find-user")
    Call<ResponseFind> findUser (@Field("id") String userid);

    @FormUrlEncoded
    @POST("api/find-room")
    Call<ResponseFind> findRoom (@Field("id") String userid);

    @FormUrlEncoded
    @POST("api/leave")
    Call<String> leave (@Field("id") String userid);



//    @POST("https://reqres.in/api/users")
//    Call<User> createUser(@Body User user);
//
//    @GET("https://reqres.in/api/users?")
//    Call<UserList> doGetUserList(@Query("page") String page);
//
//    @FormUrlEncoded
//    @POST("https://reqres.in/api/users?")
//    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);
}