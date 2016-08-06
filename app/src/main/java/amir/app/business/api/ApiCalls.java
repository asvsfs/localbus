package amir.app.business.api;

import java.util.List;

import amir.app.business.models.Businesse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by amin on 08/06/2016.
 */

public interface ApiCalls {
    @GET("Businesses")
    Call<List<Businesse>> businesseFindAll();
}
