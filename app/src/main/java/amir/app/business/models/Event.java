package amir.app.business.models;

import com.google.gson.Gson;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;

import java.util.HashMap;

/**
 * Created by amin on 08/05/2016.
 */

public class Event extends Model {

    private String title;
    private String description;
    private Location location;
    private String publishDate;
    private int seen;
    private String id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Float> getLocation() {
        HashMap<String, Float> param = new HashMap<>();

        param.put("lat", location.getLat());
        param.put("lng", location.getLng());

        return param;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public static class Repository extends ModelRepository<Event> {
        public Repository() {
            super("event", "events", Event.class);
        }
    }

}
