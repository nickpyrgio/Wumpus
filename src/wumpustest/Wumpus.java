/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpustest;

/**
 *
 * @author Nikos
 */
public class Wumpus {
    
    private boolean isAlive;
    private final Area area;
    
    Wumpus(Area area){
        this.isAlive = true;
        this.area = area;
    }
    
    boolean getIsAlive(){
        return this.isAlive;
    }
    
    void killed(){
        this.isAlive = false;
    }
    
    Area getArea(){
        return this.area;
    }
    
}
