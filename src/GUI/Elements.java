/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

/**
 *
 * @author Nikos
 */
public class Elements {
       
   static final ImageLabel WUMPUS = new ImageLabel("images//gollum.jpg");               
   static final ImageLabel AGENT_NORTH = new ImageLabel("images//bilboNorth.jpg");               
   static final ImageLabel AGENT_EAST = new ImageLabel("images//bilboEast.jpg");               
   static final ImageLabel AGENT_SOUTH = new ImageLabel("images//bilboSouth.jpg");
   static final ImageLabel AGENT_WEST = new ImageLabel("images//bilboWest.jpg");   
   
   
   static ImageLabel getAgent(String dir){
       if(dir.equalsIgnoreCase("EAST")) return Elements.AGENT_EAST;
       if(dir.equalsIgnoreCase("WEST")) return Elements.AGENT_WEST;
       if(dir.equalsIgnoreCase("SOUTH")) return Elements.AGENT_SOUTH;
       if(dir.equalsIgnoreCase("NORTH")) return Elements.AGENT_NORTH;
       return null;
   }
   
   static ImageLabel getAgent(int dir){
       if(dir == 2) return Elements.AGENT_EAST;
       if(dir == 4) return Elements.AGENT_WEST;
       if(dir == 3) return Elements.AGENT_SOUTH;
       if(dir == 1) return Elements.AGENT_NORTH;
       return null;
   }   
   
   static ImageLabel newWall(){
       return new ImageLabel("images//wall.jpg");
   }
   
   static ImageLabel newPit(){
       return new ImageLabel("images//pit.jpg");
   }
   
   static ImageLabel newGold(){
       return new ImageLabel("images//ring.jpg");
   }   
   
   static ImageLabel newBlack(){
       return new ImageLabel("images//black.jpg");
   }      
    
}
