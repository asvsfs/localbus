package amir.app.business.models.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by amin on 11/18/2016.
 */

public class Notification extends Model {
    @Column(name = "message")
    public String message;
    @Column(name = "date", index = true)
    public long date;
    @Column(name = "readed")
    public boolean readed;

    public static List<Notification> getAll() {
        return new Select()
                .from(Notification.class)
                .execute();
    }

}
