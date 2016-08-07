package amir.app.business;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.util.ArrayList;
import java.util.List;

import amir.app.business.api.RestHelper;
import amir.app.business.models.Businesse;
import amir.app.business.models.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Businesse.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Businesse.Repository.class);
//        Businesse b = new Businesse();
//        b.name = "android";
//        b.description = "android description";
//        b.location = new Location();
//        b.location.lat = 52;
//        b.location.lng = 32;
//        b.id = "121212";
//        b.images = new ArrayList<>();

//        b.setRepository(repository);
//        b.save(new VoidCallback() {
//            @Override
//            public void onSuccess() {
//                Toast.makeText(MainActivity.this, "saves", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//        });

//        RestHelper.getRestService().businesseFindAll().enqueue(new Callback<List<Businesse>>() {
//            @Override
//            public void onResponse(Call<List<Businesse>> call, Response<List<Businesse>> response) {
//                Toast.makeText(MainActivity.this, "count:" + response.body().size(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Call<List<Businesse>> call, Throwable t) {
//
//            }
//        });

        repository.getById("57a4ed29713f951ee89f6815", new ObjectCallback<Businesse>() {
            @Override
            public void onSuccess(Businesse object) {
                Toast.makeText(MainActivity.this, "object:" + object.name, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_LONG).show();
            }
        });
    }
}
