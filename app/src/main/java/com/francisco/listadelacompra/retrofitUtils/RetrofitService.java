package com.francisco.listadelacompra.retrofitUtils;

import android.content.SharedPreferences;

import com.francisco.listadelacompra.models.ListProduct;
import com.francisco.listadelacompra.models.ResponseLogin;
import com.francisco.listadelacompra.models.Users;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RetrofitService {

    SharedPreferences mispreferencias=null;




    // Usuarios
    // llamadas del login

    // logearse
    @FormUrlEncoded
    @POST("login/signin")
    Call<ResponseLogin> logIn(@Field("email") String email,
                              @Field("password") String password);



    //registrar nuevo usuario
    @FormUrlEncoded
    @POST("login/signup")
    Call<ResponseLogin> logUp(
            @Field("email") String email,
            @Field("password") String password,
            @Field("displayName") String displayName
            );

    // Listas


    // leo Todas las listas del usuario

    @GET("lista/")
    Call<ListProduct.BaseList> getallList( @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("lista/")
    Call<ListProduct.List> createList( @Header("Authorization") String token,
                                           @Field("nameList") String nameList);


    @FormUrlEncoded
    @PUT("lista/")
    Call<ListProduct.List> removefromList(@Header("Authorization") String token,
                                              @Field("_id")String idlist);
/*
    @FormUrlEncoded
    @POST("product")
    Call<SimpleResponse> postNewProduct(
            @Field("code") String code,
            @Field("name") String name,
            @Field("description") String description
    );

*/

}
