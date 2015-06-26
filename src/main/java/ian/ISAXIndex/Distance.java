/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ian.ISAXIndex;

/**
 *
 * @author ian
 */
public abstract class Distance implements java.io.Serializable {
    public abstract double distance(double[] ts1, double[] ts2);

    public abstract void clearCount();
    
    public abstract long getCount();
}
