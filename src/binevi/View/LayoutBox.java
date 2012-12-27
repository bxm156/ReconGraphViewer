package binevi.View;

/**
 * A data class to store the top-left position, length and height of the rectangle boxing the pathway graph.
 *
 * @author Yuan Wang
 */
    class LayoutBox {
        LayoutPoint topleft = new LayoutPoint(0, 0);
        double width;
        double height;

        LayoutBox(double tlx, double tly, double w, double h) {
            topleft.x = tlx;
            topleft.y = tly;
            width = w;
            height = h;
        }

        /**
         * to check if the point(x,y) is inside the box (not on the edges)
         * @param x
         * @param y
         * @return
         */
        public boolean pointInsideBox(double x,double y){
            if(x>topleft.x && y>topleft.y && x<(topleft.x+width) && y<(topleft.y+height)){
                return true;
            }else{
                return false;
            }
        }

        /**
         * @param b2
         * @return true if b2's size is larger
         */
        public boolean isLargerBox(LayoutBox b2){
            return b2.width*b2.height > width*height;
        }

        public double size(){
            return width*height;
        }

        /**
         * get a large box surrounding the current box and box2
         * @param b2
         * @return
         */
        public LayoutBox SurroundingBox(LayoutBox b2){
            double x = Math.min(topleft.x, b2.topleft.x);
            double y = Math.min(topleft.y,b2.topleft.y);
            double w = Math.max(topleft.x+width-x,b2.topleft.x+b2.width-x);
            double h = Math.max(topleft.y+height-y,b2.topleft.y+b2.height-y);
            return new LayoutBox(x,y,w,h);
        }

        //check if extended(with margin for node size) boxes are overlapping
        public boolean overlapLayoutBox(LayoutBox b2){
        	//*four vertices of the box
            double[] fourx = {topleft.x-120,topleft.x-120,topleft.x+width+120,topleft.x+width+120};
            double[] foury = {topleft.y-40,topleft.y+height+40,topleft.y-40,topleft.y+height+40};
            for(int i=0;i<4;i++){
                if(b2.pointInsideBox(fourx[i],foury[i])){
                     return true;
                }
            }

            //*four vertices of the box2
            fourx = new double[]{b2.topleft.x-120,b2.topleft.x-120,b2.topleft.x+b2.width+120,b2.topleft.x+b2.width+120};
            foury = new double[]{b2.topleft.y-40,b2.topleft.y+b2.height+40,b2.topleft.y-40,b2.topleft.y+b2.height+40};
            for(int i=0;i<4;i++){
                if(pointInsideBox(fourx[i],foury[i])){
                     return true;
                }
            }
            //*/

            //otherwise
            return false;
        }

        public boolean equals(Object b){
        	if(!(b instanceof LayoutBox)){
        		return false;
        	}
        	LayoutBox b2 = (LayoutBox)b;
        	if(topleft.x==b2.topleft.x && topleft.y==b2.topleft.y
        			&& width==b2.width && height==b2.height){
        		return true;
        	}else{
        		return false;
        	}
        }

    /**
         * @param box1     the layout box for nodes placed already
         * @param box2     the relative layout box for the pathway to be placed
         * @param nl1      the position of the shared node in pathway 1, could be outside the layoutbox 1 if moved since other sharing
         * @param nl2      the position of the shared node in pathway 2, could be outside the layoutbox 1 if moved since other sharing
         * @param position pathway2 on the (left,right,top,below) of pathway1
         * @return the total distance for the two positions for the shared node in the 2 pathways to be moved
         */
        public static double getDistanceMoved(LayoutBox box1, LayoutBox box2, LayoutPoint nl1, LayoutPoint nl2, String position) {
            //exact position for the top-left point of the exact box of pathway2
            double x = 0;
            double y = 0;

            //position for the shared node
            double sx, sy;
            double dist = 0;//to be returned

            if (position.equals("right")) {
                sx = box1.topleft.x + box1.width + 120;
                dist = (sx - nl1.x) + (nl2.x + 120);
            } else if (position.equals("left")) {
                sx = box1.topleft.x - 120;
                dist = (nl1.x - sx) + (box2.width - nl2.x + 120);
            } else if (position.equals("top")) {
                dist = (nl1.y - box1.topleft.y + 40) + (box2.height - nl2.y + 40);
            } else if (position.equals("below")) {
                sy = box1.topleft.y + box1.height + 40;
                dist = (sy - nl1.y) + (nl2.y + 40);
            } else {
                System.err.println("Wrong relative position string between the 2 pathways when calling getDistanceMoved");
            }
            return dist;
        }

        /**
         * @param boxes
         * @param b
         * @return
         */
        public static boolean overlapWithPlacedPathways(LayoutBox[] boxes, LayoutBox b) {
            for (int i = 0; i < boxes.length; i++) {
                LayoutBox box = boxes[i];
                if (box != null) {//pathways not actually placed(including p), the box would be null
                    if (box.overlapLayoutBox(b)) {//overlapping with some pathway already placed before
                        //System.out.println("         overlapping with box "+i);//+" "+boxes[i]
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * to get the absolute position for pathway2 depend on box1
         * box1 and nl1; box2 and nl2 must be consistent
         *
         * @param box1     the layout box for nodes placed already
         * @param box2     the relative layout box for the pathway to be placed
         * @param nl1      the position of the shared node in layoutBox 1
         * @param nl2      the position of the shared node in layoutBox 2
         * @param position pathway2 on the (left,right,top,below) of pathway1
         * @param sn       to return the absolute position for the shared node, intialized before passed here
         * @return
         */
        public static LayoutBox getExactBox2(LayoutBox box1, LayoutBox box2, LayoutPoint nl1, LayoutPoint nl2, String position, LayoutInfo.NodeLayout sn) {
            //exact position for the top-left point of the exact box of pathway2
            double x = 0;
            double y = 0;

            if (position.equals("right")) {
                x = box1.topleft.x + box1.width + 240;
                y = nl1.y - nl2.y;
                sn.x = x - 120;
                sn.y = nl1.y;
            } else if (position.equals("left")) {
                x = box1.topleft.x - box2.width - 240;
                y = nl1.y - nl2.y;
                sn.x = box1.topleft.x - 120;
                sn.y = nl1.y;
            } else if (position.equals("top")) {
                x = nl1.x - nl2.x;
                y = box1.topleft.y - box2.height - 80;
                sn.x = nl1.x;
                sn.y = box1.topleft.y - 40;
            } else if (position.equals("below")) {
                x = nl1.x - nl2.x;
                y = box1.topleft.y + box1.height + 80;
                sn.x = nl1.x;
                sn.y = box1.topleft.y + box1.height + 40;
            } else {
                System.err.println("Wrong relative position string between the 2 pathways when calling getDistanceMoved");
            }
            return new LayoutBox(x, y, box2.width, box2.height);
        }

     public String toStringcompartmentH() {
        return "LayoutBox: topleft(" + topleft.x + "," + topleft.y + ") width:" + width + " height:" + height + " size:" + size();
    }

        public String toString(){
            return "LayoutBox: topleft("+topleft.x+","+topleft.y+") width:"+width+" height:"+height;
        }
    }
