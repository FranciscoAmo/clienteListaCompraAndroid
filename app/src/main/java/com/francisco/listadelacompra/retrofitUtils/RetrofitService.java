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
import retrofit2.http.Query;

public interface RetrofitService {

    SharedPreferences mispreferencias=null;





// lllamadas del login

    @FormUrlEncoded
    //@Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("login/signin")
    Call<ResponseLogin> logIn(@Field("email") String email,
                              @Field("password") String password);




    @FormUrlEncoded
    @POST("login/signup")
    Call<ResponseLogin> logUp(
            @Field("email") String email,
            @Field("password") String password,
            @Field("displayName") String displayName
            );


    // leo las listas del usuario

    @GET("lista/")
    Call<ListProduct.BaseList> getallList( @Header("Authorization") String token);


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
