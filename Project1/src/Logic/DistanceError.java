/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import org.pi4.locutil.GeoPosition;

/**
 *
 * @author Pere
 */
public class DistanceError implements Comparable<DistanceError>{
    
    private double err;
    
    public DistanceError() {
        this.err = 0.0;
    }
    public DistanceError(double err) {
        this.err = err;
    }

    public double getErr() {
        return err;
    }

    public void setErr(double err) {
        this.err = err;
    }
    
    public void computeError(GeoPosition p1, GeoPosition p2) {
        this.err = Math.sqrt(Math.pow(p2.getX() - p1.getX(),2) + Math.pow(p2.getY() - p1.getY(),2));
    }
    
    @Override
    public int compareTo(DistanceError next) {
        return err < next.getErr()? -1 : 1;
    }
    
}
