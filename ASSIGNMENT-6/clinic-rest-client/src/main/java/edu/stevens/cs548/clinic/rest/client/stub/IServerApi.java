package edu.stevens.cs548.clinic.rest.client.stub;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/*
 * The API for the clinic REST server.
 */
public interface IServerApi {

    @POST("provider")
    public Call<Void> upload(@Body RequestBody requestBody);

}
