/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpustest;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author Nikos
 */
public class WumpusWorld {
    
    //the grid
    private Area[][] world;
    private int dimension;  //the size of the grid
    private Area agentInitialArea; //agent starting point
    private int agentInitialDirection; //agent starting orientation
    private Area wumpusInitialArea;   //wumpus starting position
    private Agent agent;  // an agent for this world
    private static  Wumpus wumpus;    // a wumpus for this world
    
    /**
     * Constructs a 2 dimensional world with the given dimension x dimension(square) size
     * @param dimension The size of the world
     */
    public WumpusWorld(int dimension) 
    {
        this.dimension = dimension;
        this.world = new Area[this.dimension][this.dimension];
        for(int col = 0; col < this.dimension; col++)
        {            
            for(int row = 0; row < this.dimension; row++)
            {
               this.world[row][col] = new Area(row, col);              
            }
        }
        teachNeighbours();//teach the neighbours to the areas of this world
        //Build walls around the grid
        for(int i = 0; i < dimension;i++)
        {
            world[0][i].setHasWall(true);
            if(i != 0)world[i][0].setHasWall(true);
            if(i != 0)world[i][dimension - 1].setHasWall(true);
            if(i != 0 && i != dimension - 1)world[dimension - 1][i].setHasWall(true);
        }               
    }
        
    public WumpusWorld(WumpusWorld aWumpusWorld){
        this.dimension = aWumpusWorld.dimension;
        this.world = new Area[this.dimension][this.dimension];

        for(int col = 0; col < this.dimension; col++)
        {            
            for(int row = 0; row < this.dimension; row++)
            {                
               this.world[row][col] = new Area(row, col);              
               if(aWumpusWorld.world[row][col].hasPit()){
                   this.world[row][col].setHasPit(true);
               }
               if(aWumpusWorld.world[row][col].glitters()){
                   this.world[row][col].setHasGold(true);
               }                      
            }
        }
        if(aWumpusWorld.worldHasAgentAndWumpus()){
            int tempRow, tempCol;
            tempRow = aWumpusWorld.agentInitialArea.getRow();
            tempCol = aWumpusWorld.agentInitialArea.getCol();
            this.agentInitialArea = this.world[tempRow][tempCol];
            this.agentInitialDirection = aWumpusWorld.agentInitialDirection;            
            tempRow = aWumpusWorld.wumpusInitialArea.getRow();
            tempCol = aWumpusWorld.wumpusInitialArea.getCol();            
            this.wumpusInitialArea = this.world[tempRow][tempCol];
        }        
    }
    
    
    /**
     * Agent can begin making actions with the make action method
     * @return 
     */
    public boolean startSimulation(){
        if(this.worldHasAgentAndWumpus()){
            this.agent = new Agent(this.agentInitialArea  , this.agentInitialDirection, this.dimension);
            WumpusWorld.wumpus = new Wumpus(this.wumpusInitialArea);                    
            return true;
        }
        return false;
    }
    
    public boolean startSimulation(int maxSteps){
        if(this.worldHasAgentAndWumpus()){
            this.agent = new Agent(this.agentInitialArea  , this.agentInitialDirection, this.dimension, maxSteps);
            WumpusWorld.wumpus = new Wumpus(this.wumpusInitialArea);                    
            return true;
        }
        return false;
    }    
    
    public boolean worldHasAgentAndWumpus(){
        if (this.agentInitialArea == null || (this.wumpusInitialArea == null))
                return false;                
        return true;
    }
    
    public boolean hasAgent(){
        return this.agentInitialArea == null;
    }
    
    public boolean hasWumpus(){
        return this.wumpusInitialArea == null;
    }
    
    /**
     * Move the agent and rerurns the action he made
     * @return The action the agent did
     */
    public String moveAgent(){        
        return this.agent.makeAction();
    }
    /**
     * Static cause wumpus is only one and does not moves for all the worlds. It only changes location on the grid
     * @return true iff wumpus is alive(not shot by an arrow), false otherwise
     */
    public static boolean wumpusIsAlive(){
        return WumpusWorld.wumpus.getIsAlive();
    }
    
    /**
     * Sets the wumpus`s life status to the given value.
     */
    public static void wumpusIsDead(){
        WumpusWorld.wumpus.killed();
    }
    /**
     * 
     * @return Returns the life status of the agent
     */
    public boolean agentDied(){
        return this.agent.isDead();
    }
    
    /**
     * 
     * @return Returns true if agent found the gold and grabbed
     */
    public boolean agentFoundGold(){
        return this.agent.hasGold();
    }
    
    public Area getArea(int row, int col)
    {
        return this.world[row][col];
    }
    
    private void teachNeighbours()
    {
        for(int i = 0; i < this.dimension; i++)
        {            
            for(int j = 0; j < this.dimension; j++)
            {
                //j = rows, i = col
                if((j == 0 && i == 0) || (j == 0 && i == this.dimension - 1) || (j == dimension - 1 && i == 0) || (j == dimension - 1  && i == dimension - 1)) {                    
                    world[j][i].set( new Area(),  new Area(), new Area(), new Area());                   
                }
                else if( j == 0)
                {                
                    world[j][i].set( new Area(),  world[j + 1][i], world[j][i - 1], world[j][i + 1]);
                }
                else if( j == this.dimension - 1)
                {  
                    world[j][i].set(world[j - 1][i],  new Area(), world[j][i - 1], world[j][i + 1]);
                }
                else if(i == 0)
                {         
                    world[j][i].set( world[j - 1][i],  world[j + 1][i], new Area(), world[j][i + 1]);                  
                }
                else if(i == this.dimension - 1)
                {   
                    world[j][i].set( world[j - 1][i],  world[j + 1][i], world[j][i - 1],  new Area());
                }                
                else
                {                    
                    world[j][i].set( world[j - 1][i],  world[j + 1][i], world[j][i - 1],  world[j][i + 1]);                    
                }                                        
            }            
        }        
    }   
    //sets the locations of the objects of the world. These positions are standarised when start Simulation is triggered . Until then changes can be made
    public void setGoldArea(int row, int col)
    {
        this.world[row][col].setHasGold(true);
    }
    
    public void setPitArea(int row, int col)
    {
        this.world[row][col].setHasPit(true);
    }    
    
    public void setWumpusArea(int row, int col)
    {
        this.wumpusInitialArea = this.world[row][col];
        this.wumpusInitialArea.setHasWumpus(true);        
    }        
    
    public void setAgentArea(int row, int col, int direction)
    {
        this.agentInitialArea = this.world[row][col];        
        this.agentInitialDirection = direction;
    }
    
    public Area getAgentInitialArea()
    {
        return this.agentInitialArea ;
    }
            
    public void reset(){
        
        this.agent = new Agent(this.agentInitialArea , this.agentInitialDirection, this.dimension);
        WumpusWorld.wumpus = new Wumpus(this.wumpusInitialArea);
        this.wumpusInitialArea.setHasWumpus(true);
    }
    /**
     * Saves a world (initial positions) to a the given file
     * @param filename The name(path) of the filename
     * @return true iff file was saved successfully
     * @throws IOException if something goes wrong
     */
    public boolean save(String filename) throws IOException{
        return TextIo.write(filename, this.toString(), false);
    }
    /**
     * Loads a world (initial positions) of saved world
     * @param filename 
     * @return
     * @throws IOException 
     */
    public boolean load(String filename) throws IOException{
        String str = TextIo.read(filename);
        
        String lines[] = str.split("\r\n");
        
        this.dimension = Integer.parseInt(lines[0]);        
        this.world = new Area[this.dimension][this.dimension];
        for(int col = 0; col < this.dimension; col++)
        {            
            for(int row = 0; row < this.dimension; row++)
            {
               this.world[row][col] = new Area(row, col);              
            }
        }
        teachNeighbours();
        for(int i = 0; i < dimension;i++)
        {
            world[0][i].setHasWall(true);
            if(i != 0)world[i][0].setHasWall(true);
            if(i != 0)world[i][dimension - 1].setHasWall(true);
            if(i != 0 && i != dimension - 1)world[dimension - 1][i].setHasWall(true);
        }
        this.agentInitialDirection = Integer.parseInt(lines[1]);
        String[] location = lines[2].split(" ");
        int agentRow = Integer.parseInt(location[0]);
        int agentCol = Integer.parseInt(location[1]);        
        this.agentInitialArea = this.world[agentRow][agentCol];
        location = lines[3].split(" ");
        int wumpusRow = Integer.parseInt(location[0]);
        int wumpusCol = Integer.parseInt(location[1]);        
        this.wumpusInitialArea = this.world[wumpusRow][wumpusCol];
        
        this.wumpusInitialArea.setHasWumpus(true);
        for(int i = 4 ; i < lines.length; i ++){
            location = lines[i].split(" ");
            int row = Integer.parseInt(location[0]);
            int col = Integer.parseInt(location[1]);
            boolean hasGold = Integer.parseInt(location[2]) == 1;
            boolean hasPit = Integer.parseInt(location[3]) == 1;
            this.world[row][col].setHasGold(hasGold);
            this.world[row][col].setHasPit(hasPit);
        }
        return TextIo.getErrNo() == 0;
    }    
    
    public String info(){
        String str = this.agent.toString();                
        return str;
    }
    
    @Override
    public String toString()
    {
        String eol = "\r\n"  ;
        String buffer = this.dimension + eol;
        buffer += "" + this.agentInitialDirection + eol;
        buffer += (agentInitialArea == null ? "" : agentInitialArea) + eol;
        buffer += (wumpusInitialArea == null ? "" : wumpusInitialArea) + eol;

        for(int col = 0; col < this.dimension; col ++)
        {
            for(int row = 0; row < this.dimension; row++)
            {
                buffer += row + " " + col + " " +  (this.world[row][col].glitters() ? 1 : 0)                        
                       + " " + (this.world[row][col].hasPit() ? 1 : 0)+ eol;                       
            }
        }
        buffer = buffer.substring(0, buffer.lastIndexOf(eol));            
        return buffer;
    }
    
    public boolean agentHearsScream(){
        return this.agent.hearsScream();
    }
    
    public int getDimension(){
        return this.dimension;
    }
    
    /**
     * 
     * @return an sized 3 array with these values{row, column, direction) of the agent
     */
    public int[] getAgentCurrentStatus(){
        return new int[] { this.agent.getArea().getRow(),this.agent.getArea().getCol(), this.agent.getDirection()}; 
    }
    
    public void randomise(double pitPossibility, boolean possiblyGoldInPit){
        class Point{
            int row;
            int col;
            Point(int x, int y){
                row = x;
                col = y;
            }
        }
        
        ArrayList<Point> list = new ArrayList<Point>();
        
        for(int col = 1; col < this.dimension - 1; col++)
        {            
            for(int row = 1; row < this.dimension - 1; row++)
            {
               this.world[row][col].setHasGold(false);        
               this.world[row][col].setHasWumpus(false);        
               this.world[row][col].setHasScream(false);        
               this.world[row][col].setHasPit(false);
               Point point = new Point(row, col);
               list.add(point);
            }
        }
        Random rand = new Random(System.nanoTime());
        int direction = 1 + rand.nextInt(4);
        
        Collections.shuffle(list, rand);
        Point loc;
        //agent
        loc =  list.remove(list.size() - 1);
        this.setAgentArea(loc.row, loc.col, direction);
        //wumpus
        loc =  list.remove(list.size() - 1);
        this.setWumpusArea(loc.row, loc.col);
        //gold
        if(possiblyGoldInPit){
            loc =  list.get(list.size() - 1);
            this.setGoldArea(loc.row, loc.col);  
            Collections.shuffle(list, rand);
        }else{
            loc =  list.remove(list.size() - 1);
            this.setGoldArea(loc.row, loc.col);              
        }                        
        int limitOfPits = (int)Math.floor((this.dimension - 2)*(this.dimension - 2)* pitPossibility);
        
        for(int i = 0; i < limitOfPits; i++){
            loc =  list.remove(list.size() - 1);
            this.setPitArea(loc.row, loc.col);
        }        
    }
    
    /**
     * Prints a current state of the world
     * @param printStream An output for the print method
     */
    public void print (PrintStream printStream)
    {
        String temp = "";
        class Formatter {           
            String format(String value)
            {                
                if(value.equals("")) value = "|_______";   
                else if(value.length() == 4) value = "|_" + value + "_"; 
                else if(value.length() == 3) value = "|__" + value + "__"; 
                else if(value.length() == 2) value = "|___" + value + "__"; 
                else value = "|___" + value + "___";                           
                return value;
            }
        }//end class
        Formatter aformatter = new Formatter();
        for(int i = 0 ; i < this.dimension; i++)
        {
            for(int j = 0; j < this.dimension;j++)
            {
                temp = "";
                if(this.world[i][j].hasWumpus())temp += "G";
                //if(this.world[i][j].stenches())temp += "s";
                if(this.world[i][j].hasWall())temp += "W";
                if(this.world[i][j].glitters())temp += "R";
                if(this.world[i][j].hasPit())temp += "P";
                //if(this.world[i][j].breezes())temp += "b";
                if(this.world[i][j].equals(agent.getArea())){
                    String direction = "";
                    if(this.agent.getDirection() == 1)direction = "↑";
                    if(this.agent.getDirection() == 2)direction = "→";
                    if(this.agent.getDirection() == 3)direction = "↓";
                    if(this.agent.getDirection() == 4)direction = "←";                        
                    temp += "A" + direction;
                }                                
                temp = aformatter.format(temp);
                printStream.print(temp);
                if(j == this.dimension - 1)printStream.print("|\r\n");
            }
        }
    }
}
