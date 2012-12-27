package binevi.View.Layout;

import y.base.Node;
import y.base.NodeMap;

import java.util.HashMap;


public class IntNodeMap implements NodeMap {

    HashMap<Node, Object> contentmap;

    public IntNodeMap() {
        contentmap = new HashMap<Node, Object>();
    }

    public void set(Object key, Object value) {
        contentmap.put((Node) key, value);
    }

    public Object get(Object key) {
        return contentmap.get((Node) key);
    }

    public void setBool(Object key, boolean value) {
        contentmap.put((Node) key, value);
    }

    public boolean getBool(Object key) {
        return (Boolean) contentmap.get((Node) key);
    }

    public void setDouble(Object key, double value) {
        contentmap.put((Node) key, value);
    }

    public double getDouble(Object key) {
        return (Double) contentmap.get((Node) key);
    }

    public void setInt(Object key, int value) {
        contentmap.put((Node) key, value);
    }

    public int getInt(Object key) {
        return (Integer) contentmap.get((Node) key);
    }
}
