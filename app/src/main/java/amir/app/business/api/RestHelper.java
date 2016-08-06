package amir.app.business.api;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by amin on 10/02/2015.
 */
public class RestHelper {
    public static final String SERVICE_URL = "http://139.59.153.64:3000/api/";
    private static ApiCalls apiService;

    public static ApiCalls getRestService() {
        if (apiService == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient
                    .Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            HttpUrl originalHttpUrl = original.url();

                            HttpUrl url = originalHttpUrl.newBuilder()
                                    .addQueryParameter("access_token", "6OoV6ZCjRtAYS2dP0Vml3cWDso8dTnyQRVUQZrBcdrRrHhSXs8F9B2pQr9JqefFi")
                                    .build();

                            // Request customization: add request headers
                            Request.Builder requestBuilder = original.newBuilder()
                                    .url(url);

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVICE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

//            OkHttpClient client = new OkHttpClient();
//
//            RestAdapter adapter = new RestAdapter.Builder()
//                    .setClient(new OkClient(client))
//                    .setEndpoint(SERVICE_URL)
//                    .setConverter(new GsonConverter(new GsonBuilder()
//                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
//                            .serializeNulls()
//                            .create()))
//
//                    .setRequestInterceptor(new RequestInterceptor() {
//                        @Override
//                        public void intercept(RequestFacade request) {
//                            if (config.token!=null && config.session != null)
//                                request.addHeader("Cookie", config.session);
//                        }
//                    })
//                    .setLogLevel(RestAdapter.LogLevel.FULL).
//                            setLog(new RestAdapter.Log() {
//                                @Override
//                                public void log(String msg) {
//                                    Log.i("api", msg);
//                                }
//                            })
//                    .build();

            apiService = retrofit.create(ApiCalls.class);
        }

        return apiService;
    }
}
