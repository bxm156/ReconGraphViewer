package binevi.View.Layout;

import y.base.DataProvider;
import y.base.Node;

import java.util.HashMap;


public class BooleanNodeMap implements DataProvider {

    HashMap<Node, Boolean> contentmap;

    public BooleanNodeMap() {
        contentmap = new HashMap<Node, Boolean>();
    }

    public Object get(Object object) {
        return contentmap.get(object);
    }

    public int getInt(Object object) {
        Boolean value = contentmap.get(object);
        if (value == null || value == false) return 0;
        else return 1;
    }

    public double getDouble(Object object) {
        Boolean value = contentmap.get(object);
        if (value == null || value == false) return 0;
        else return 1;
    }

    public boolean getBool(Object object) {
        Boolean value = contentmap.get(object);
        return value != null && value;
    }

    public void setBool(Node v, boolean b) {
        contentmap.put(v, b);
    }
}
