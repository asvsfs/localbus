package amir.app.business.models;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by amin on 08/05/2016.
 */

public class Category extends Model {

    private String name;
    private String id;
    private String icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public static class Repository extends ModelRepository<Category> {
        public Repository() {
            super("category", "categories", Category.class);
        }
    }
}
