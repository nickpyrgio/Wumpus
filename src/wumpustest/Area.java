package wumpustest;

import java.util.HashSet;

/**
 *
 * @author Nikos
 */
public final class Area {

    //the neighbours of this area
    Area northNeighbour;
    Area southNeighbour;
    Area westNeighbour;
    Area eastNeighbour;
         
    //the location of the area on the grid
    private int row;
    private int col;    
    private boolean hasGold; //1       
    private boolean hasPit;//2
    private boolean hasWumpus; //3
    private boolean hasWall;//4
    private boolean scream;//5
    private boolean visited;            

    public void set(Area northNeighbour, Area southNeighbour, Area westNeighbour, Area eastNeighbour) {
        this.northNeighbour = northNeighbour;
        this.southNeighbour = southNeighbour;
        this.westNeighbour = westNeighbour;
        this.eastNeighbour = eastNeighbour;
    }

    private void init() {
        this.hasGold = false;
        this.hasPit = false;
        this.hasWumpus = false;
        this.hasWall = false;
        this.scream = false;
        this.visited = false;        
    }

    //dummy constructor used to create not null neighbours
    Area() {
        this(-1, -1);
    }

    Area(int row, int col) {                
        this.row = row;
        this.col = col;
        init();        
    }
        
    //set and get methods for the fields(properties) of the area    
    void setHasScream(boolean value){
        this.scream = value;
    }
       
    void setHasPit(boolean value) {
        this.hasPit = value;
    }

    void setHasGold(boolean value) {
        this.hasGold = value;
    }

    void setHasWall(boolean value) {
        this.hasWall = value;
    }

    void setHasWumpus(boolean value) {
        this.hasWumpus = value;
    }
    
    void setVisited(boolean value){
        this.visited = value;
    }
    
    int getRow(){
        return this.row;
    }
    
    int getCol(){
        return this.col;
    }
    
    boolean getVisited(){
        return this.visited;                
    }
    
    boolean hasScream(){
        return scream;
    }

    boolean breezes() {
        return northNeighbour.hasPit() || southNeighbour.hasPit() || westNeighbour.hasPit() || eastNeighbour.hasPit() ? true : false;
    }

    boolean stenches() {
        return northNeighbour.hasWumpus() || southNeighbour.hasWumpus() || westNeighbour.hasWumpus() || eastNeighbour.hasWumpus() || this.hasWumpus() ? true : false;
    }  
    
    boolean glitters() {
        return this.hasGold;
    }       
    
    boolean hasWumpus() {
        return this.hasWumpus;
    }

    boolean hasPit() {
        return this.hasPit;
    }

    boolean hasWall() {
        return this.hasWall;
    }
    
    @Override
    public String toString()
    {
        return this.row + " " + this.col;
    }
    
    @Override
    public boolean equals(Object area)
    {
        if(area instanceof Area)
        {
            Area temp = (Area)area;
            if(temp.row == this.row && this.col == temp.col) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {        
        return this.toString().hashCode();
    }
    
    HashSet<Area> getNeighbours(){
        HashSet<Area> geitones = new HashSet<Area>();
        geitones.add(this.eastNeighbour);
        geitones.add(this.northNeighbour);
        geitones.add(this.southNeighbour);
        geitones.add(this.westNeighbour);        
        return geitones;
    }
}
