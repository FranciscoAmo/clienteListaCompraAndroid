package com.francisco.listadelacompra.retrofitUtils;

import android.content.SharedPreferences;

import com.francisco.listadelacompra.models.ListProduct;
import com.francisco.listadelacompra.models.ProductosBBDD;
import com.francisco.listadelacompra.models.ResponseMessageStandar;
import com.francisco.listadelacompra.models.ResponseLogin;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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


    @FormUrlEncoded
    @PUT("lista/{email}/")
    Call<ResponseMessageStandar> addUser(@Header("Authorization") String token,
                                         @Field("_id") String idList,
                                         @Path("email") String email);

    @FormUrlEncoded
    @PUT("lista/{key}/{value}/remove/")
    Call<ResponseMessageStandar> removeProductFormList(@Header("Authorization") String token,
                                                       @Field("_id")String idList,
                                                       @Path("key") String key,
                                                       @Path("value")String value);

    @FormUrlEncoded
    @PUT("lista/{key}/{value}/update/")
    Call<ResponseMessageStandar> updateQuantity(@Header("Authorization") String token,
                                                @Field("_id") String idList,
                                                @Field("newquantity") Integer quantity,
                                                @Path("key") String key,
                                                @Path("value") String value);


    // productos obtengo todos los productos de la base de datos
    @GET("product/{key}/{value}")
    Call<ProductosBBDD.BaseResponse> getproductsByType(@Header("Authorization") String token,
                                          @Path("key") String key,
                                          @Path("value") String value
                                          );

    @FormUrlEncoded
    @PUT("lista/{key}/{value}/add/")
    Call<ResponseMessageStandar> addProduct(@Header("Authorization") String token,
                                                @Field("_id") String idList,
                                                @Field("quantity") Integer quantity,
                                                @Path("key") String key,
                                                @Path("value") String value);

    // obtengo una lista
    @GET("lista/{id}")
    Call<ListProduct.BaseList> getOneList( @Header("Authorization") String token,
                                           @Path("id")String id);

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
