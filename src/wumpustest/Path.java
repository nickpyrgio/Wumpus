/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpustest;

/**
 *
 * @author Nikos
 *  * sources : 
 * http://www.policyalmanac.org/games/aStarTutorial_greek.htm 
 * https://chaos.bcit.ca/svn/public/astar/
 */ 
import java.util.Random;
import java.util.Stack;

public final class Path implements Comparable<Path>    
{
    final Path parent;  //the parent of this node, only start area has parent = null
    private final Area area;
    private final int cost; //cost is F =  G + H
    private final int distance;//G the distance between this area and starts path area
    private final int heuristic;//H Manhattan distance
    private final int direction;
    private final static Random rand = new Random(System.currentTimeMillis());

    public Path(final Path p,
                final Area t,               
                final int direction,
                final int  h)
    {
        this.parent    = p;        
        this.heuristic = h;
        this.area = t;
        if(this.parent == null)
        {
            this.direction = direction;
            this.distance = 0;
            this.cost = this.heuristic;
        }
        else
        {            
            this.direction = direction;
            this.distance = parent.distance + G();            
            this.cost     = distance + heuristic;
        }                
    }
    
    /**
     * 
     * @return the cost to go from a node to next node
     */
    private int G(){
        int value = 0;        
        if(Path.isOppositeDirection(direction, parent.direction)){
            value = 30;            
        }
        if(Path.isNextDirection(direction, parent.direction)){
            value = 20;            
        }
        if(Path.isSameDirection(direction, parent.direction)){
            value = 10;            
        }                
        return value;
    }

    public int getCost()
    {
        return (cost);
    }

    public Area getArea()
    {
        return (area);
    }
    
    public int totalCost(){
        Path temp;
        int totalCost = this.cost;
        temp = parent;
        while(temp != null)
        {
            totalCost += parent.cost;
            temp = temp.parent;
        }      
        return totalCost;
    }

    final Stack<Area> getAreas()
    {
        final Stack<Area> tiles;
        Path temp;        
        tiles = new Stack<Area>();

        tiles.add(area);
        temp = parent;

        while(temp != null)
        {
            tiles.add(temp.area);
            temp = temp.parent;
        }
        return (tiles);
    }
    
    /**
     * 
     * @return The stack of moves that lead from the start of the path to the end of the path
     */
    final Stack<String> getActions(){
        Stack<String> actions = new Stack<String>();
        Path temp;        
        Path child = this;        
        temp = parent;
        while(temp != null){
            if(Path.isSameDirection(child.direction, temp.direction)){                
                actions.add(Agent.MoveForward);
            }
            else if(Path.isOppositeDirection(child.direction, temp.direction)){                
                actions.add(Agent.MoveForward);                
                int r = rand.nextInt(2);
                if(r == 0){
                    actions.add(Agent.TurnL);
                    actions.add(Agent.TurnL);                                    
                }else{
                    actions.add(Agent.TurnR);
                    actions.add(Agent.TurnR);                                                        
                }

            }
            else if(Path.isNextDirection(child.direction, temp.direction)){                 
                actions.add(Agent.MoveForward);
                if(child.direction == Agent.NORTH && temp.direction == Agent.EAST){
                    actions.add(Agent.TurnL);
                }
                else if(child.direction == Agent.NORTH && temp.direction == Agent.WEST){
                    actions.add(Agent.TurnR);
                }                
                else if(child.direction == Agent.EAST && temp.direction == Agent.SOUTH){
                    actions.add(Agent.TurnL);
                }
                else if(child.direction == Agent.EAST && temp.direction == Agent.NORTH){
                    actions.add(Agent.TurnR);
                }
                else if(child.direction == Agent.SOUTH && temp.direction == Agent.WEST){
                    actions.add(Agent.TurnL);
                }
                else if(child.direction == Agent.SOUTH && temp.direction == Agent.EAST){
                    actions.add(Agent.TurnR);
                }                
                else if(child.direction == Agent.WEST && temp.direction == Agent.NORTH){
                    actions.add(Agent.TurnL);
                }
                else if(child.direction == Agent.WEST && temp.direction == Agent.SOUTH){
                    actions.add(Agent.TurnR);
                }                  
            }
            temp = temp.parent;
            child = child.parent;
        }
        return actions;
    }

    @Override
    public int hashCode()
    {
        final int retVal;

        if(parent == null)
        {
            retVal = cost * area.hashCode();
        }
        else
        {
            retVal = (cost * (parent.hashCode() + area.hashCode()));
        }

        return (retVal);
    }

    @Override
    public boolean equals(final Object o)
    {
        final boolean retVal;

        if(this == o)
        {
            return true;
        }

        if(o instanceof Path)
        {
            final Path other;

            other = (Path)o;

            if(cost == other.cost)
            {
                if(area.equals(other.area))
                {
                    retVal = parent.equals(other.parent);
                }
                else
                {
                    retVal = false;
                }
            }
            else
            {
                retVal = false;
            }
        }
        else
        {
            retVal = false;
        }

        return (retVal);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder;

        builder = new StringBuilder();
        toString(builder);

        return (builder.toString());
    }

    private void toString(final StringBuilder builder)
    {
        if(parent != null)
        {
            parent.toString(builder);
            builder.append(", ");
        }        
        builder.append("distance: ").append(distance);
        builder.append(" heuristic: ").append(heuristic);
        builder.append(" cost: ").append(cost);
        builder.append(" -> ");
        builder.append(area);
        builder.append("\r\n");
    }

    @Override
    public int compareTo(final Path other)
    {
        if(this.totalCost() > other.totalCost())
        {
            return (1);
        }
        else if(this.totalCost() < other.totalCost())
        {
            return (-1);
        }

        return (0);
    }
    
    private static boolean isOppositeDirection(int orientation1, int orientation2){
        if(orientation1 == Agent.NORTH && orientation2 == Agent.SOUTH)return true;
        if(orientation1 == Agent.SOUTH && orientation2 == Agent.NORTH)return true;
        if(orientation1 == Agent.EAST && orientation2 == Agent.WEST)return true;
        if(orientation1 == Agent.WEST && orientation2 == Agent.EAST)return true;
        return false;
    }
    
    private static boolean isSameDirection(int orientation1, int orientation2){
        if(orientation1 == Agent.NORTH && orientation2 == Agent.NORTH)return true;
        if(orientation1 == Agent.SOUTH && orientation2 == Agent.SOUTH)return true;
        if(orientation1 == Agent.EAST && orientation2 == Agent.EAST)return true;
        if(orientation1 == Agent.WEST && orientation2 == Agent.WEST)return true;
        return false;        
    }
    
    private static boolean isNextDirection(int orientation1, int orientation2){
        if(orientation1 == Agent.NORTH && orientation2 == Agent.EAST)return true;
        if(orientation1 == Agent.NORTH && orientation2 == Agent.WEST)return true;
        if(orientation1 == Agent.EAST && orientation2 == Agent.SOUTH)return true;
        if(orientation1 == Agent.EAST && orientation2 == Agent.NORTH)return true;
        if(orientation1 == Agent.SOUTH && orientation2 == Agent.EAST)return true;
        if(orientation1 == Agent.SOUTH && orientation2 == Agent.WEST)return true;
        if(orientation1 == Agent.WEST && orientation2 == Agent.NORTH)return true;
        if(orientation1 == Agent.WEST && orientation2 == Agent.SOUTH)return true;
        return false;        
    }            
}
