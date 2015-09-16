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
public class Neighbor implements Comparable<Neighbor> {
    
    private GeoPosition position;
    private double distance;
    
    public Neighbor(GeoPosition pos,double dis) {
        this.position = pos;
        this.distance = dis;
    }

    public GeoPosition getPosition() {
        return position;
    }

    public void setPosition(GeoPosition position) {
        this.position = position;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    // Returns 1 if this neighbor has closest distance to the measured position than 
    // the compared neighbor one, return -1 otherwise.
    @Override
    public int compareTo(Neighbor next) {
        return distance < next.getDistance() ? -1 : 1;
    }
    
}
