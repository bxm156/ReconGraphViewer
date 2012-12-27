package binevi.View;

import java.util.LinkedHashMap;
import java.util.Iterator;

/**
 * A data class to store (double,double) point.
 *
 * @author Yuan Wang
 */
 class LayoutPoint {
        double x;
        double y;

        LayoutPoint(double nx, double ny) {
            x = nx;
            y = ny;
        }

        public static LayoutPoint getCenterPoint(LayoutPoint[] pts){
            int x=0,y=0;
            for(LayoutPoint lp:pts){
                x+=lp.x;
                y+=lp.y;
            }
            return new LayoutPoint(x/pts.length,y/pts.length);
        }

    /**
     * get the two center points for the two sets of nodes
     * p1,p2 must be initialized before passed here
     *
     * @param shared
     * @param p1
     * @param p2
     */
    public static void getCenterPoints(LinkedHashMap<LayoutInfo.NodeLayout, LayoutInfo.NodeLayout> shared, LayoutPoint p1, LayoutPoint p2) {
        int n = shared.size();
        LayoutPoint[] pts1 = new LayoutPoint[n];
        LayoutPoint[] pts2 = new LayoutPoint[n];
        Iterator<LayoutInfo.NodeLayout> e = shared.keySet().iterator();
        int i = 0;
        while (e.hasNext()) {
            LayoutInfo.NodeLayout nl1 = e.next();
            LayoutInfo.NodeLayout nl2 = shared.get(nl1);
            pts1[i] = new LayoutPoint(nl1.x, nl1.y);
            pts2[i] = new LayoutPoint(nl2.x, nl2.y);
            i++;
        }
        LayoutPoint tp1, tp2;
        tp1 = LayoutPoint.getCenterPoint(pts1);
        tp2 = LayoutPoint.getCenterPoint(pts2);
        p1.x = tp1.x;
        p1.y = tp1.y;
        p2.x = tp2.x;
        p2.y = tp2.y;
    }
    }
