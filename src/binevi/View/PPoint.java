package binevi.View;

public class PPoint
 {
 private double x, y;
 public PPoint(double x, double y) {
     this.x = x;
     this.y = y;
 }
     public double getX(){return x;}
     public double getY(){return y;}
     public void setX(double ax){this.x = ax;}
     public void setY(double ay){this.y = ay;}
     public void setOffsetX(double ax){this.x+= ax;}
     public void setOffsetY(double ay){this.y+= ay;}
 }
