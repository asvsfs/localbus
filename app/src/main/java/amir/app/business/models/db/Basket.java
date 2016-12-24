package amir.app.business.models.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amin on 11/04/2016.
 */

public class Basket extends Model implements Serializable {
    @Column(name = "productid")
    public String productid;
    @Column(name = "name")
    public String name;
    @Column(name = "date", index = true)
    public long date;
    @Column(name = "count")
    public int count;
    @Column(name = "price")
    public long price;
    @Column(name = "comment")
    public String comment;

    public static List<Basket> getAll() {
        return new Select()
                .from(Basket.class)
                .execute();
    }
}
