package amir.app.business.models;

import com.strongloop.android.remoting.VirtualObject;

public class Token  extends VirtualObject{
    public String id;
    public int ttl;
    public String created;
    public String userId;
}
