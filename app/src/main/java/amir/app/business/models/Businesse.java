package amir.app.business.models;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;

import java.util.List;

/**
 * Created by amin on 08/05/2016.
 */

public class Businesse  {
    public String name;
    public Location location;
    public String description;
    public List<String> images;
    public String id;

//    public static class Repository extends ModelRepository<Businesse> {
//        public Repository() {
//            super("Businesse", "Businesses", Businesse.class);
//        }
//    }

}
