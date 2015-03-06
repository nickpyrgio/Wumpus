/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpustest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author Nikos Pyrgiotis
 * A simple implementation of the AStar algorithm implemented for this particular problem(wumpus assignment)
 * sources : 
 * http://www.policyalmanac.org/games/aStarTutorial_greek.htm 
 * https://chaos.bcit.ca/svn/public/astar/
 */
public class AStar {
    
    /**
     * 
     * @param start the current area of the agent
     * @param end the area the agent is seeking the best path to go to
     * @param bannedAreas Obstacles, areas the path cannot pass
     * @param unknown Unknown, areas, the path cannot pass from these areas
     * @param uncertain Uncertain, areas the agent has some clues about but canno entail their status
     * @param safe The areas the path will pass by
     * @param direction // the direction of the agent at start
     * @return 
     */
    public Path findAPath(Area start
                        , Area end
                        , HashMap<String, Area> bannedAreas                        
                        , HashMap<String, Area> unknown
                        , HashMap<String, Area> uncertain
                        , HashMap<String, Area> safe
                        , int direction)
    {  
        //if(safe.isEmpty()) return null;                        
        final PriorityQueue<Path> paths;
        final Set<Area>           openList; 
        // Used to add all the areas evaluated in both paths and openList
        final Set<Area>           closedList;
        //Extract the `best` from the PriorityQueue<Path> paths to the closedList

        paths        = new PriorityQueue<Path>();
        openList     = new HashSet<Area>();
        closedList   = new HashSet<Area>();
        
        openList.add(start);  //add the start area to openlist nad construct a new path which has only one node(start area)
        //null is for the parent, heuristic is the manhattan distance of the 2 areas(used as an H  function)
        //, direction is the direction the agent must be looking go to go to that area(Used to the G function )
        //
        paths.add(new Path(null,start,direction,heuristic(start,start)));
        while(true){            
            if(paths.isEmpty()) break;
            //if a path does not exist break and return null
            final Path  currentPath;
            final Set<Area> adjacentTiles;
            final Area      currentArea;
            currentPath = paths.remove();
            //take the path with the best F = G + H and put it in the closed list
            //G is calculated in Path class
            //G is the distance from start area to the area the path is leading that moment
            //the heuristic is simple and it is explained in the Path class
            currentArea = currentPath.getArea();
            
            if(currentArea.equals(end)){
                //if we found a path, probably the best return it
                return currentPath;
            }
            openList.remove(currentArea);
            //remove the area(which path has smallest F) from the openList and add it to the closed list
            closedList.add(currentArea);
            adjacentTiles = currentArea.getNeighbours();
            //examinde the neighbour nodes of the area
            for(final Area area : adjacentTiles){
                //if path cannot pass from these areas continue
                if(bannedAreas.containsKey(area.toString()) || unknown.containsKey(area.toString()) 
                    || closedList.contains(area)){
                    continue;
                } 
                if(uncertain != null && uncertain.containsKey(area.toString())){
                    continue;
                }
                if(!openList.contains(area)){
                    int heuristic = heuristic(start,  end);
                    int orientation = 0;
                    //determine the direction the agent must have to go to that area from the previous area
                    if(area.equals(currentArea.eastNeighbour)){
                       orientation = Agent.EAST;
                    }
                    if(area.equals(currentArea.westNeighbour)){
                       orientation = Agent.WEST;
                    }
                    if(area.equals(currentArea.southNeighbour)){
                       orientation = Agent.SOUTH;
                    }
                    if(area.equals(currentArea.northNeighbour)){
                       orientation = Agent.NORTH;
                    }               
                    //and add a new path with parent the previous path
                    Path path  = new Path(currentPath, area, orientation, heuristic);
                    paths.add(path);
                    openList.add(currentArea);                     
                }                
            }                                                
        }                                
        return null;
    }
    
    private int  heuristic(Area start, Area end){
        return 10*(int)(Math.abs(start.getRow() - end.getRow()) + Math.abs(start.getCol()- end.getCol()));        
    }
            
}
