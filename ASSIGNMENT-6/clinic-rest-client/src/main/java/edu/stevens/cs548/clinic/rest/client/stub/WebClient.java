package edu.stevens.cs548.clinic.rest.client.stub;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Logger;

import com.google.gson.Gson;

import edu.stevens.cs548.clinic.service.dto.util.GsonFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebClient {
	
	protected final Logger logger = Logger.getLogger(WebClient.class.getCanonicalName());
	
    protected MediaType jsonType = MediaType.get("application/json");
	
    /*
     * The client stub used for Web service calls.
     */
	private IServerApi client;
	
	
	public WebClient(URI baseUri) {
        /*
         * Create the HTTP client stub.
         */
        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        
        /*
         * Gson converter
         */
        Gson gson = GsonFactory.createGson();

        /*
         * TODO Wrap the okhttp client with a retrofit stub factory.
         */
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUri.toString()).
        addConverterFactory(GsonConverterFactory.create(gson)).client(httpClient).build();
        
        /*
         * Create the stub that will be used for Web service calls
         */
        client = retrofit.create(IServerApi.class);
 	}

	public void upload(final IStreamingOutput output) throws IOException {
        /*
         * We will stream the output JSON data in this request body.
         */
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return jsonType;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (OutputStream os = sink.outputStream()) {
                    output.write(os);
                }
            }
        };

        /*
         * Execute the Web service call
         */
        logger.info("Uploading data to the server....");
        Response<Void> response = client.upload(requestBody).execute();
        logger.info("...done, HTTP status = "+response.code());
		
		if (!response.isSuccessful()) {
			throw new IOException("Upload failed with HTTP status "+response.code());
		}
	}

}
