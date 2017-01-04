package amir.app.business.event;

/**
 * Created by amin on 01/01/2017.
 */

public class ProductListRefreshEvent {
    private String message;

    public ProductListRefreshEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
