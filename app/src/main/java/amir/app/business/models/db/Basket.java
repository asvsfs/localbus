package amir.app.business.models.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

import java.io.Serializable;

/**
 * Created by amin on 11/04/2016.
 */

public class Basket extends Model implements Serializable {
    @Column(name = "productid")
    public String productid;
    @Column(name = "date", index = true)
    public long date;
    @Column(name = "count")
    public int count;
    @Column(name = "price")
    public long price;
    @Column(name = "comment")
    public String comment;
}
