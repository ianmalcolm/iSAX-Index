/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISAXIndex;

/**
 *
 * @author ian
 */
public abstract class DataHandler {

    public abstract long size();

    public abstract double[] get(long i);
    
    public abstract int windowSize();
}
