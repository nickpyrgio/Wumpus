/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpustest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;

/**
 *
 * @author Nikos
 */
public class Agent
{
    //the 4 different directions of the agent at a specific time(step)
    final static int NORTH = 1;
    final static int SOUTH = 3;
    final static int WEST = 4;
    final static int EAST = 2;            

    //<Literal Labels
    //Actions
    final static String shoot = "Shoot"; //changes with time
    final static String TurnR = "TR";  //changes with time
    final static String TurnL = "TL"; //changes with time
    final static String MoveForward = "F"; //changes with time
    final static String Grab = "Grab"; //changes with time  
    //percepts 
    private final static String stench = "S";//changes with time
    private final static String breeze = "B";//Changes with time
    private final static String glitter = "G";        
    private final static String scream = "scream"; //changes with time
    private final static String bump = "bump";//changes with time
    //Objects if the world
    private final static String wumpus = "WUMPUS";//does not change with time, wumpus does not move
    private final static String breezy = "Breezy";//does not change with time
    private final static String pit = "PIT";//does not change with time, pits do not move    
    private final static String wall = "WALL";//does not change with time
    private final static String stenchy = "Stenchy";//does not change with time
    //Directions and position of agent
    private final static String at = "At";   //changes with time    
    private final static String north = "North"; //changes with time
    private final static String south = "Sourh"; //changes with time
    private final static String west = "West"; //changes with time
    private final static String east = "East";     //changes with time
    //Useful info
    private final static String atLeastOneWumpus = "atLeastOneWumpus";
    private final static String MostOneWumpus = "mostOneWumpus";
    private final static String exactlyOneWumpus = "exactlyOneWumpus";    
    private final static String aliveWumpus = "Alive";
    private final static String hasArrow = "HasArrow";
    //Conclusion of the agent about an area
    private final static String safeArea = "Safe";    
    //hearsScream = true iff wumpus is killed
    //Literal Labels>//
    private boolean hearsScream;//true iff wumpus dies in the previous round
    //grabsGold = true iff agent makes action grab.
    private boolean grabsGold;         
    private Area curArea;    //the area the agent is currently at
    private Area nextArea;  //the area the agent is going at
    private int  direction;        
    private String name;
    private  int arrowCount; // 1 arrow    
    private  int  time; 
    //an increasing number representing `time`. When time > maxSteps agent dies because he can`t remember anything more
    private final int maxSteps;//max value of the time agent has until he finds gold or dies. It is the lifespam of the Agent
    private final  KB kb; //the knowledge base of the agent, it`s initialised with the rules of the world    
    
    private final HashMap<String, Area> visitedAreas; // all the areas the agent has set foot on
    private final HashMap<String, Area> okAreas;      //safe areas that the agent might explore for gold
    private final HashMap<String, Area> unknown;      //all the areas the agent has no clue about
    private final HashMap<String, Area> uncertainAreas;   
    //areas that the agent canno entail whether are safe or not but has some clues about them.
    //For example he might know whether the area has a breezy neighbour
    private HashMap<String, Area> bannedAreas;  
    //unsafe areas the agent must never set a foot on or he dies
    private Stack<String> plan;
    //the consequentional actions of the agent to reach from a safe area to an `expected` safe area
    private AStar aStar = new AStar();   
    //algorithm for finding best path from a safe area to another `expected` safe area
    private String action;  
    //the final action of the agent. Initially null
    private int worldDimension;
    private Area WumpusArea = null;
    
    public Agent(Area startArea, int direction, int dimension)
    {
        this(startArea,direction, dimension, 70);        
    }    
    
    /**
     * Initialise the agent
     * @param startArea The starting location 
     * @param direction The starting direction
     * @param dimension The dimension of the grid the agent is operating on
     * @param maxSteps The maximum acceptable number of steps the agent is allowed to make
     */
    public Agent(Area startArea, int direction, int dimension, int maxSteps)
    {
        //init data structures
        worldDimension = dimension;
        this.visitedAreas = new HashMap<String, Area>();
        this.uncertainAreas = new HashMap<String, Area>();
        this.okAreas = new HashMap<String, Area>();        
        this.bannedAreas = new HashMap<String, Area>();
        this.bannedAreas.put(new Area().toString(), new Area());
        this.unknown = new HashMap<String, Area>();
        //initially all areas are unknown to the agent except his area(tile)
        for(int col = 0; col < dimension; col++)
        {            
            for(int row = 0; row < dimension; row++)
            {
               Area anArea = new Area(row, col); 
               if(anArea.equals(startArea))continue;
               this.unknown.put(anArea.toString(), anArea);
            }
        }        
        this.plan = new Stack<String>();
        //init fields
        this.hearsScream = false;
        this.grabsGold = false;
        this.curArea = startArea;        
        this.direction = direction; 
        this.arrowCount = 1;
        this.time = 1;  
        this.name = "LUCY";//fairy tail or ellfen lied Lucy :P
        //init the KB
        this.kb = new KB();                        
        if(maxSteps < 30)
            this.maxSteps = 30;         
        else
            this.maxSteps = maxSteps; 
        initKB(dimension);        
       //initialise the KB with sentences of the propositional logic(Horn)
    }    
    
    /**
     * Tell all the rules that are static(are independant of the time variable)
     * @param dimension The dimension of the world the agend is operating on
     */
    private void initKB(int dimension){
        HornSentence sen; 
        this.kb.tell(new Literal(Agent.atLeastOneWumpus, false));        
        this.kb.tell(new Literal(Agent.MostOneWumpus, false));
        this.kb.tell(new Literal(Agent.at + (this.time) + "" + this.curArea.getRow() + "" + this.curArea.getCol(), false));
        this.kb.tell(new Literal(orientation(this.direction) + (this.time), false));
        this.kb.tell(new Literal(Agent.aliveWumpus + 0, false));
        this.kb.tell(new Literal(Agent.hasArrow + 1, false));
        
        //Leme sth vash gnwsh oti uparxei akrivws ena wumpus
        //toulaxiston ena wumpus ^ to polu ena => akrivws ena wumpus
        sen = new HornSentence(new Literal (exactlyOneWumpus, false)
                , new Literal(atLeastOneWumpus, false)
                , new Literal(MostOneWumpus, false));
        this.kb.tell(sen);               
        for(int row = 0; row < dimension; row++){
            for(int col = 0; col < dimension; col++){ 
                //time independant, pit position and wumpus position                                 
                sen = new HornSentence(new Literal (Agent.safeArea + row + "" +  col, true)
                        , new Literal(Agent.pit + (row) + "" + (col), false));
                this.kb.tell(sen);                
                //**************************************************************************                
                sen = new HornSentence(new Literal (Agent.safeArea + (row + 1) + "" +  col, false)
                        , new Literal(Agent.aliveWumpus, true)  
                        , new Literal(Agent.pit + (row + 1) + "" + (col), true)
                        , new Literal(Agent.stenchy + (row) + "" + (col), false));
                this.kb.tell(sen);                                
                //**************************************************************************                
                sen = new HornSentence(new Literal (Agent.safeArea + (row - 1) + "" +  col, false)
                        , new Literal(Agent.aliveWumpus, true)  
                        , new Literal(Agent.pit + (row - 1) + "" + (col), true)
                        , new Literal(Agent.stenchy + (row) + "" + (col), false));
                this.kb.tell(sen);                                                
                //**************************************************************************                
                sen = new HornSentence(new Literal (Agent.safeArea + (row) + "" +  (col + 1), false)
                        , new Literal(Agent.aliveWumpus, true)  
                        , new Literal(Agent.pit + (row) + "" + (col + 1), true)
                        , new Literal(Agent.stenchy + (row) + "" + (col), false));
                this.kb.tell(sen);                                                                
                //**************************************************************************                
                sen = new HornSentence(new Literal (Agent.safeArea + (row) + "" +  (col - 1), false)
                        , new Literal(Agent.aliveWumpus, true)  
                        , new Literal(Agent.pit + (row) + "" + (col - 1), true)
                        , new Literal(Agent.stenchy + (row) + "" + (col), false));
                this.kb.tell(sen);                                                                                
                //**************************************************************************                
                sen = new HornSentence(new Literal (Agent.safeArea +  "" + row + "" +  col, false)
                        , new Literal(Agent.wumpus + (row) + "" + (col), true)  
                        , new Literal(Agent.pit + (row) + "" + (col), true));
                this.kb.tell(sen);                                      
                //PITS**************************************************************************                                
                sen = new HornSentence(new Literal (pit + row +"" +  col, false)
                        , new Literal(breezy + (row + 1) + "" + col, false)
                        , new Literal(breezy + (row - 1) + "" + col, false)
                        , new Literal(breezy + row + "" + (col + 1), false)
                        , new Literal(breezy + row + "" + (col - 1), false));                     
                if(row + 1 < dimension && row -1 >= 0 && col + 1 < dimension && col - 1>= 0) this.kb.tell(sen);
                ///***********************************************************
                sen = new HornSentence(new Literal (pit + row +"" +  col, false)
                        , new Literal(breezy + (row) + "" + (col - 1), false)
                        , new Literal(Agent.pit + (row) + "" + (col - 2), true)
                        , new Literal(Agent.pit + (row + 1) + "" + (col - 1), true)
                        , new Literal(Agent.pit + (row - 1) + "" + (col - 1), true));
                if(col - 2 >= 0 && row + 1 < dimension && row - 1 >= 0)
                    this.kb.tell(sen);                
                ///***********************************************************
                sen = new HornSentence(new Literal (pit + row + "" +  col, false)
                        , new Literal(breezy + (row) + "" + (col + 1), false)
                        , new Literal(Agent.pit + (row) + "" + (col + 2), true)
                        , new Literal(Agent.pit + (row + 1) + "" + (col + 1), true)
                        , new Literal(Agent.pit + (row - 1) + "" + (col + 1), true));
                if(col + 2 < dimension && row + 1 < dimension && row - 1 >= 0){
                    this.kb.tell(sen);                              
                }
                
                ///***********************************************************
                sen = new HornSentence(new Literal (pit + row +"" +  col, false)
                        , new Literal(breezy + (row - 1) + "" + (col), false)
                        , new Literal(Agent.pit + (row - 2) + "" + (col), true)
                        , new Literal(Agent.pit + (row - 1) + "" + (col + 1), true)
                        , new Literal(Agent.pit + (row - 1) + "" + (col - 1), true));
                if(row - 2 >= 0 && col + 1 < dimension && col - 1 >= 0)
                    this.kb.tell(sen);
                ///***********************************************************
                sen = new HornSentence(new Literal (pit + row +"" +  col, false)
                        , new Literal(breezy + (row + 1) + "" + (col), false)
                        , new Literal(Agent.pit + (row + 2) + "" + (col), true)
                        , new Literal(Agent.pit + (row + 1) + "" + (col + 1), true)
                        , new Literal(Agent.pit + (row + 1) + "" + (col - 1), true));
                if(row + 2 < dimension && col + 1 < dimension && col - 1 >= 0)                
                    this.kb.tell(sen);                
                ///END PITS*********************************************************** 
                //Wumpus finders**************************************************************************NEW                
                sen = new HornSentence(new Literal(wumpus + (row ) + "" + col, false)
                        , new Literal(stenchy + (row) + "" + (col - 1), false)
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(wumpus + (row) + "" + (col -2), true)                        
                        , new Literal(wumpus + (row + 1) + "" + (col - 1), true)
                        , new Literal(wumpus + (row - 1) + "" + (col - 1), true)                                
                        );                    
                if(col - 2 >= 0 && col - 1 >= 0 && row + 1 < dimension && row - 1 >= 0) kb.tell(sen);                                         
                //**************************************************************************NEW                
                sen = new HornSentence(new Literal(wumpus + (row ) + "" + col, false)
                        , new Literal(stenchy + (row) + "" + (col + 1), false)
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(wumpus + (row) + "" + (col + 2), true)                        
                        , new Literal(wumpus + (row + 1) + "" + (col + 1), true)
                        , new Literal(wumpus + (row - 1) + "" + (col + 1), true)                                
                        );                    
                if(col + 2 < dimension && row + 1 < dimension && col + 1 < dimension && row - 1 >= 0) kb.tell(sen);  
               //NEW
                sen = new HornSentence(new Literal(wumpus + (row ) + "" + col, false)
                        , new Literal(stenchy + (row - 1) + "" + col, false)
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(wumpus + (row - 2) + "" + col, true)                        
                        , new Literal(wumpus + (row - 1) + "" + (col + 1), true)
                        , new Literal(wumpus + (row - 1) + "" + (col - 1), true)                                
                        );
                if(row - 2 >= 0 && col + 1 < dimension && col - 1 >= 0 && row - 1 >= 0) kb.tell(sen); 
               //NEW
                sen = new HornSentence(new Literal(wumpus + (row ) + "" + col, false)
                        , new Literal(stenchy + (row + 1) + "" + col, false)
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(wumpus + (row + 2) + "" + col, true)                        
                        , new Literal(wumpus + (row + 1) + "" + (col + 1), true)
                        , new Literal(wumpus + (row + 1) + "" + (col - 1), true)                                
                        );
                if(row + 2 < dimension && row + 1 < dimension && col + 1 < dimension && col - 1 >= 0) kb.tell(sen);                     
                //**************************************************************************
                sen = new HornSentence(new Literal (wumpus + row + "" +  col, false)
                        , new Literal(stenchy + (row - 1) + "" + col, false)
                        , new Literal (exactlyOneWumpus, false)                       
                        , new Literal(stenchy + (row + 1) + "" + (col), false));
                if(row - 1 >= 0 && row + 1 < dimension) this.kb.tell(sen);
                //**************************************************************************NEW
                sen = new HornSentence(new Literal (wumpus + row + "" +  col, false)
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(wumpus + (row - 1) + "" + (col - 1), true)
                        , new Literal(stenchy + (row - 1) + "" + col, false)                             
                        , new Literal(stenchy + (row) + "" + (col - 1), false));
                if(row - 1 >= 0 && col -1 >= 0 ) this.kb.tell(sen);                
                //**************************************************************************NEW
                sen = new HornSentence(new Literal (wumpus + row + "" +  col, false)
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(wumpus + (row + 1) + "" + (col - 1), true)
                        , new Literal(stenchy + (row + 1) + "" + col, false)                                                
                        , new Literal(stenchy + (row) + "" + (col - 1), false));
                if(row + 1 < dimension && col -1 >= 0 )  this.kb.tell(sen);                    
                sen = new HornSentence(new Literal (wumpus + row + "" +  col, false)
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(wumpus + (row + 1) + "" + (col + 1), true)
                        , new Literal(stenchy + (row + 1) + "" + col, false)                        
                        , new Literal(stenchy + (row) + "" + (col + 1), false));
                if(row + 1 < dimension && col + 1 < dimension) this.kb.tell(sen);                
                //**************************************************************************NEW
                sen = new HornSentence(new Literal (wumpus + row + "" +  col, false)
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(wumpus + (row - 1) + "" + (col + 1), true)
                        , new Literal(stenchy + (row - 1) + "" + col, false)                        
                        , new Literal(stenchy + (row) + "" + (col + 1), false));
                if(row - 1 >= 0 && col + 1 < dimension) this.kb.tell(sen);
                //**************************************************************************
                sen = new HornSentence(new Literal (wumpus + row + "" +  col, false)
                        , new Literal(stenchy + (row) + "" + (col - 1), false)
                        , new Literal (exactlyOneWumpus, false)                        
                        , new Literal(stenchy + (row) + "" + (col + 1), false));                
                if(col - 1 >= 0 && col + 1 < dimension)  this.kb.tell(sen);
                //**************************************************************************
                sen = new HornSentence(new Literal (wumpus + row + "" +  col, false)
                        , new Literal(stenchy + (row - 1) + "" + col, false)
                        , new Literal(stenchy + (row) + "" + (col + 1), false)                        
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(stenchy + (row + 1) + "" + (col), false));
                if((row - 1) >=  0 && col + 1 < dimension && row + 1 < dimension)this.kb.tell(sen);                
                //**************************************************************************
                sen = new HornSentence(new Literal (wumpus + row + "" +  col, false)
                        , new Literal(stenchy + (row - 1) + "" + col, false)
                        , new Literal(stenchy + (row) + "" + (col - 1), false)                        
                        , new Literal (exactlyOneWumpus, false)
                        , new Literal(stenchy + (row + 1) + "" + (col), false));
                if((row - 1) >=  0 && col - 1 >= 0 && row + 1 < dimension) this.kb.tell(sen); 
                //END wumpus finders***************************************************************** NEW                  
                sen = new HornSentence(new Literal (wumpus + (row - 1) + "" + (col + 1) , true)
                        , new Literal (wumpus + (row) + "" + (col) , false)
                        , new Literal(stenchy + row + "" + (col + 1), false)); 
                 this.kb.tell(sen);
                //*******************************************************************************************************************
                sen = new HornSentence(new Literal (wumpus + (row + 1) + "" + col , true), new Literal(stenchy + row + "" + col, true)); 
                if(row + 1 < dimension)  this.kb.tell(sen);                
                sen = new HornSentence(new Literal (wumpus + (row - 1) + "" + col , true), new Literal(stenchy + row + "" + col, true));            
                if(row -1 >= 0 )  this.kb.tell(sen);                
                sen = new HornSentence(new Literal (wumpus + (row) + "" + (col - 1) , true), new Literal(stenchy + row + "" + col, true));            
                if(col -1 >= 0 )  this.kb.tell(sen);
                sen = new HornSentence(new Literal (wumpus + (row) + "" + (col + 1) , true), new Literal(stenchy + row + ""  + col, true));            
                if(col + 1 < dimension ) this.kb.tell(sen);     
                //***************************************************************** NEW                    
                sen = new HornSentence(new Literal (wumpus + (row - 1) + "" + (col + 1) , true)
                        , new Literal (wumpus + (row) + "" + (col) , false)
                        , new Literal(stenchy + row + "" + (col + 1), false)); 
                this.kb.tell(sen);
                                
                //*******************************************************************************************************************
                sen = new HornSentence(new Literal (pit + (row + 1) + "" + col , true)
                        , new Literal(breezy + row + "" + col, true)); 
                if(row + 1 < dimension)  this.kb.tell(sen);                
                sen = new HornSentence(new Literal (pit + (row - 1) + "" + col , true)
                        , new Literal(breezy + row + "" + col, true));            
                if(row -1 >= 0 )  this.kb.tell(sen);                
                sen = new HornSentence(new Literal (pit + (row) + "" + (col - 1) 
                        , true), new Literal(breezy + row + "" + col, true));            
                if(col -1 >= 0 )  this.kb.tell(sen);
                sen = new HornSentence(new Literal (pit + (row) + "" + (col + 1) , true)
                    , new Literal(breezy + row + ""  + col, true));            
                if(col + 1 < dimension ) this.kb.tell(sen);
                //*******************************************************************************************************************                
                sen = new HornSentence(new Literal (pit + (row) + "" + col , true)
                        , new Literal(wall + row + "" + col, false)); 
                this.kb.tell(sen);                                
                //*******************************************************************************************************************
                sen = new HornSentence(new Literal (wumpus + (row) + "" + col , true)
                        ,new Literal(wall + row + "" + col, false)); 
                this.kb.tell(sen);                                                  
            }//end col loop
        }//end row loop       
    }            
       
    /**
     * Update the KB with time dependant rules. 
     */
    private void updateKB(){
        HornSentence sen;
        //System.out.println("currentTime " + currentTime + " dasdf" + (steps));
        int dimension = this.worldDimension;
        for(int row = 0; row < dimension; row++){
            for(int col = 0; col < dimension; col++){
                //time dependant
                //Χρονικά βήματα. Ο πράκτορας πρέπει να συμπεραίνει μόνος του τα χρονικά εξαρτώμενα πεδία του όπως πχ η θέση του
                //και να μη βρίσκεται σε σύγχηση για το πότε είδε τι, πού βρισκόταν σε ποια χρονική στιγμή κτλ...
                for(int t = time; t <= time; t++){                    
                    //At(t,x,y) ^ breeze(t) => breezySquare(x,y)                                            
                    sen = new HornSentence(new Literal (breezy + (row) + "" + (col) , false)
                            //Δεν εχει χρονικο ορισμα γιατί οι αύρες δεν αλλάζουν κατάσταση με το χρόνο
                            , new Literal(at + t + "" + row + ""  + col, false)
                            , new Literal(breeze + t, false) );      
                    this.kb.tell(sen);
                    //At(t,x,y) ^ !breeze(t) => !breezySquare(x,y)
                    sen = new HornSentence(new Literal (breezy + (row) + "" + (col) , true)
                            , new Literal(at + t + "" + row + ""  + col, false)
                            , new Literal(breeze + t, true) );      
                    this.kb.tell(sen);    
                    //**************************************************************************
                    sen = new HornSentence(new Literal (stenchy + (row) + "" + (col) , false)
                            , new Literal(at + t + "" + row + ""  + col, false)
                            , new Literal(stench + t, false) );      
                    this.kb.tell(sen);
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (stenchy + (row) + "" + (col) , true)
                            , new Literal(at + t + ""  + row + ""  + col, false)
                            , new Literal(stench + t, true) );     
                    this.kb.tell(sen);
                    //*******************************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.wall + (row) + "" + (col - 1) , false)
                            , new Literal(Agent.at + t + "" + row + ""  + col, false)
                            , new Literal(Agent.bump + t, false)
                            , new Literal(Agent.west + t, false));
                    if(col - 1 >= 0)      this.kb.tell(sen);
                    sen = new HornSentence(new Literal (Agent.wall + (row) + "" + (col + 1) , false)
                            , new Literal(Agent.at + t + "" + row + ""  + col, false)
                            , new Literal(Agent.bump + t, false)
                            , new Literal(Agent.east + t, false));
                    if(col + 1 < dimension)   this.kb.tell(sen);                                                                              
                    sen = new HornSentence(new Literal (Agent.wall + (row - 1) + "" + (col) , false)
                            , new Literal(Agent.at + t + "" + row + ""  + col, false)
                            , new Literal(Agent.bump + t, false)
                            , new Literal(Agent.north + t, false));
                    if(row - 1 >= 0)  this.kb.tell(sen);
                    sen = new HornSentence(new Literal (Agent.wall + (row + 1) + "" + (col) , false)
                            , new Literal(Agent.at + t + "" + row + ""  + col, false)
                            , new Literal(Agent.bump + t, false)
                            , new Literal(Agent.south + t, false));                
                    if(row + 1 < dimension)   this.kb.tell(sen);   
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + "" +  (row) + "" + (col - 1) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.MoveForward + t, false)
                            , new Literal(Agent.west + t, false));
                    if(col - 1 >= 0)    this.kb.tell(sen);
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + (row) + "" + (col + 1) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.MoveForward + t, false)
                            , new Literal(Agent.east + t, false));
                    if(col + 1 < this.worldDimension)    this.kb.tell(sen);                     
                    //*****************************************************************************************************                                        
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + "" + (row - 1) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.MoveForward + t, false)
                            , new Literal(Agent.north + t, false));
                    if(row - 1 >= 0)    this.kb.tell(sen);
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.at+ (t + 1) + "" +  (row + 1) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.MoveForward + t, false)
                            , new Literal(Agent.south + t, false));                
                    if(row + 1 < this.worldDimension)    this.kb.tell(sen);
                    //*****************************************************************************************************                    
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + "" +  (row) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.TurnR + t, false));                            
                    this.kb.tell(sen);
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + "" +  (row) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.TurnL + t, false));                            
                    this.kb.tell(sen);                    
                    //*****************************************************************************************************NEW
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + (row) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.shoot + t, false));                            
                    this.kb.tell(sen);                                               
                    //*****************************************************************************************************                    
                    sen = new HornSentence(new Literal (Agent.pit + (row) + "" + (col) , true)
                            , new Literal(Agent.at + t + row + ""  + col, false));                           
                    this.kb.tell(sen);
                    //*****************************************************************************************************                    
                    sen = new HornSentence(new Literal (Agent.wumpus + (row) + "" + (col) , true)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.aliveWumpus + t, false));                           
                    this.kb.tell(sen);                    
                    //*****************************************************************************************************                                        
                    sen = new HornSentence(new Literal (Agent.safeArea + t + "" + row + "" +  col, true)
                            , new Literal(Agent.wumpus + (row) + "" + (col), false)                            
                            , new Literal(Agent.aliveWumpus + t, false));
                    this.kb.tell(sen);
                }//end time loop
            }//end col loop
        }//end row loop
        //only-time dependant sentences
        for(int t = time; t <= time; t++){           
            //*****************************************************************************************************                                
            sen = new HornSentence(new Literal (Agent.Grab + (t), false)
                    ,new Literal (Agent.glitter + t, false));                    
            kb.tell(sen);                                                          
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (hasArrow + (t + 1), false)
                    ,new Literal (hasArrow + (t), false)
                    ,new Literal (Agent.MoveForward + (t), false));
            kb.tell(sen);                  
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (hasArrow + (t + 1), false)
                    ,new Literal (hasArrow + (t), false)
                    ,new Literal (Agent.TurnL + (t), false));
            kb.tell(sen);                  
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (hasArrow + (t + 1), false)
                    ,new Literal (hasArrow + (t), false)
                    ,new Literal (Agent.TurnR + (t), false));
            kb.tell(sen);                  
            //****************************************************************************************************************            
            sen = new HornSentence(new Literal (hasArrow, true)                        
                    ,new Literal (shoot + (t), false));
            kb.tell(sen);            
            //****************************************************************************************************************            
            sen = new HornSentence(new Literal (aliveWumpus + (t), false)
                    ,new Literal (aliveWumpus + (t - 1), false)
                    ,new Literal (scream + (t), true));
            kb.tell(sen);      
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (aliveWumpus, true)                        
                    ,new Literal (scream + (t), false));
            kb.tell(sen);            
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.north + (t + 1) , false)                            
                    , new Literal(Agent.shoot + t, false)
                    , new Literal(Agent.north + t, false));
            kb.tell(sen);
            //***************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.south + (t + 1) , false)                            
                    , new Literal(Agent.shoot + t, false)
                    , new Literal(Agent.south + t, false));
            kb.tell(sen);            
            //***************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.east + (t + 1) , false)                            
                    , new Literal(Agent.shoot + t, false)
                    , new Literal(Agent.east + t, false));
            kb.tell(sen);                        
            //***************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.west + (t + 1) , false)                            
                    , new Literal(Agent.shoot + t, false)
                    , new Literal(Agent.west + t, false));
            kb.tell(sen);                                                
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.north + (t + 1) , false)                            
                    , new Literal(Agent.MoveForward + t, false)
                    , new Literal(Agent.north + t, false));
            kb.tell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.east + (t + 1) , false)                            
                    , new Literal(Agent.MoveForward + t, false)
                    , new Literal(Agent.east + t, false));
            kb.tell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.west + (t + 1) , false)                            
                    , new Literal(Agent.MoveForward + t, false)
                    , new Literal(Agent.west + t, false));
            kb.tell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.south + (t + 1) , false)                            
                    , new Literal(Agent.MoveForward + t, false)
                    , new Literal(Agent.south + t, false));
            kb.tell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.south + (t + 1) , false)                            
                    , new Literal(Agent.TurnL + t, false)
                    , new Literal(Agent.west + t, false));
            kb.tell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.north + (t + 1), false)                            
                    , new Literal(Agent.TurnL + t, false)
                    , new Literal(Agent.east + t, false));
            kb.tell(sen);                                                                              
            sen = new HornSentence(new Literal (Agent.west + (t + 1), false)                            
                    , new Literal(Agent.TurnL + t, false)
                    , new Literal(Agent.north + t, false));
            kb.tell(sen);
            sen = new HornSentence(new Literal (Agent.east + (t + 1), false)                            
                    , new Literal(Agent.TurnL + t, false)
                    , new Literal(Agent.south + t, false));                
            kb.tell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.north + (t + 1) , false)                            
                    , new Literal(Agent.TurnR + t, false)
                    , new Literal(Agent.west + t, false));
            kb.tell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.south + (t + 1), false)                            
                    , new Literal(Agent.TurnR + t, false)
                    , new Literal(Agent.east + t, false));
            kb.tell(sen);    
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.east + (t + 1), false)                            
                    , new Literal(Agent.TurnR + t, false)
                    , new Literal(Agent.north + t, false));
            kb.tell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.west + (t + 1), false)                            
                    , new Literal(Agent.TurnR + t, false)
                    , new Literal(Agent.south + t, false));                
            kb.tell(sen);            
        }     
        
    }
    
    /**
     * Forget the time -2 rules so the kb does not become huge and filled with expired rules.
     * The rules do not change, the time changes, thsus the past -time rules can be removed from the KB
     * The facts that are already entailed are already added in the KB.
     */
    private void forget(){
        HornSentence sen;  
        int t = this.time - 2;
        
        int dimension = this.worldDimension;
        
        for(int row = 0; row < this.worldDimension ; row++){
            for(int col = 0; col < this.worldDimension; col++){ 
                
                    sen = new HornSentence(new Literal (breezy + (row) + "" + (col) , false)
                            //Δεν εχει χρονικο ορισμα γιατί οι αύρες δεν αλλάζουν κατάσταση με το χρόνο
                            , new Literal(at + t + "" + row + ""  + col, false)
                            , new Literal(breeze + t, false) );      
                    this.kb.unTell(sen);
                    //At(t,x,y) ^ !breeze(t) => !breezySquare(x,y)
                    sen = new HornSentence(new Literal (breezy + (row) + "" + (col) , true)
                            , new Literal(at + t + "" + row + ""  + col, false)
                            , new Literal(breeze + t, true) );      
                    this.kb.unTell(sen);    
                    //**************************************************************************
                    sen = new HornSentence(new Literal (stenchy + (row) + "" + (col) , false)
                            , new Literal(at + t + "" + row + ""  + col, false)
                            , new Literal(stench + t, false) );      
                    this.kb.unTell(sen);
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (stenchy + (row) + "" + (col) , true)
                            , new Literal(at + t + ""  + row + ""  + col, false)
                            , new Literal(stench + t, true) );     
                    this.kb.unTell(sen);
                    //*******************************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.wall + (row) + "" + (col - 1) , false)
                            , new Literal(Agent.at + t + "" + row + ""  + col, false)
                            , new Literal(Agent.bump + t, false)
                            , new Literal(Agent.west + t, false));
                    if(col - 1 >= 0)      
                        this.kb.unTell(sen);
                    sen = new HornSentence(new Literal (Agent.wall + (row) + "" + (col + 1) , false)
                            , new Literal(Agent.at + t + "" + row + ""  + col, false)
                            , new Literal(Agent.bump + t, false)
                            , new Literal(Agent.east + t, false));
                    if(col + 1 < dimension)   
                        this.kb.unTell(sen);                                                                              
                    sen = new HornSentence(new Literal (Agent.wall + (row - 1) + "" + (col) , false)
                            , new Literal(Agent.at + t + "" + row + ""  + col, false)
                            , new Literal(Agent.bump + t, false)
                            , new Literal(Agent.north + t, false));
                    if(row - 1 >= 0)  
                        this.kb.unTell(sen);
                    sen = new HornSentence(new Literal (Agent.wall + (row + 1) + "" + (col) , false)
                            , new Literal(Agent.at + t + "" + row + ""  + col, false)
                            , new Literal(Agent.bump + t, false)
                            , new Literal(Agent.south + t, false));                
                    if(row + 1 < dimension)   
                        this.kb.unTell(sen);   
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + "" +  (row) + "" + (col - 1) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.MoveForward + t, false)
                            , new Literal(Agent.west + t, false));
                    if(col - 1 >= 0)    
                        this.kb.unTell(sen);
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + (row) + "" + (col + 1) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.MoveForward + t, false)
                            , new Literal(Agent.east + t, false));
                    if(col + 1 < this.worldDimension)    
                        this.kb.unTell(sen);                     
                    //*****************************************************************************************************                                        
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + "" + (row - 1) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.MoveForward + t, false)
                            , new Literal(Agent.north + t, false));
                    if(row - 1 >= 0)    
                        this.kb.unTell(sen);
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.at+ (t + 1) + "" +  (row + 1) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.MoveForward + t, false)
                            , new Literal(Agent.south + t, false));                
                    if(row + 1 < this.worldDimension)    
                        this.kb.unTell(sen);
                    //*****************************************************************************************************                    
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + "" +  (row) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.TurnR + t, false));                            
                    this.kb.unTell(sen);
                    //*****************************************************************************************************
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + "" +  (row) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.TurnL + t, false));                            
                    this.kb.unTell(sen);                    
                    //*****************************************************************************************************NEW
                    sen = new HornSentence(new Literal (Agent.at + (t + 1) + (row) + "" + (col) , false)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.shoot + t, false));                            
                    this.kb.unTell(sen);                                               
                    //*****************************************************************************************************                    
                    sen = new HornSentence(new Literal (Agent.pit + (row) + "" + (col) , true)
                            , new Literal(Agent.at + t + row + ""  + col, false));                           
                    this.kb.unTell(sen);
                    //*****************************************************************************************************                    
                    sen = new HornSentence(new Literal (Agent.wumpus + (row) + "" + (col) , true)
                            , new Literal(Agent.at + t + row + ""  + col, false)
                            , new Literal(Agent.aliveWumpus + t, false));                           
                    this.kb.unTell(sen);                    
                    //*****************************************************************************************************                                        
                    sen = new HornSentence(new Literal (Agent.safeArea + t + "" + row + "" +  col, true)
                            , new Literal(Agent.wumpus + (row) + "" + (col), false)                            
                            , new Literal(Agent.aliveWumpus + t, false));
                    this.kb.unTell(sen);                
            }//end col loop
        }//end row loop             
            sen = new HornSentence(new Literal (Agent.Grab + (t), false)
                    ,new Literal (Agent.glitter + t, false));                    
            kb.unTell(sen);                                                          
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (hasArrow + (t + 1), false)
                    ,new Literal (hasArrow + (t), false)
                    ,new Literal (Agent.MoveForward + (t), false));
            kb.unTell(sen);                  
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (hasArrow + (t + 1), false)
                    ,new Literal (hasArrow + (t), false)
                    ,new Literal (Agent.TurnL + (t), false));
            kb.unTell(sen);                  
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (hasArrow + (t + 1), false)
                    ,new Literal (hasArrow + (t), false)
                    ,new Literal (Agent.TurnR + (t), false));
            kb.unTell(sen);                  
            //****************************************************************************************************************            
            sen = new HornSentence(new Literal (hasArrow, true)                        
                    ,new Literal (shoot + (t), false));
            kb.unTell(sen);            
            //****************************************************************************************************************            
            sen = new HornSentence(new Literal (aliveWumpus + (t), false)
                    ,new Literal (aliveWumpus + (t - 1), false)
                    ,new Literal (scream + (t), true));
            kb.unTell(sen);      
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (aliveWumpus, true)                        
                    ,new Literal (scream + (t), false));
            kb.unTell(sen);            
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.north + (t + 1) , false)                            
                    , new Literal(Agent.shoot + t, false)
                    , new Literal(Agent.north + t, false));
            kb.unTell(sen);
            //***************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.south + (t + 1) , false)                            
                    , new Literal(Agent.shoot + t, false)
                    , new Literal(Agent.south + t, false));
            kb.unTell(sen);            
            //***************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.east + (t + 1) , false)                            
                    , new Literal(Agent.shoot + t, false)
                    , new Literal(Agent.east + t, false));
            kb.unTell(sen);                        
            //***************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.west + (t + 1) , false)                            
                    , new Literal(Agent.shoot + t, false)
                    , new Literal(Agent.west + t, false));
            kb.unTell(sen);                                                
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.north + (t + 1) , false)                            
                    , new Literal(Agent.MoveForward + t, false)
                    , new Literal(Agent.north + t, false));
            kb.unTell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.east + (t + 1) , false)                            
                    , new Literal(Agent.MoveForward + t, false)
                    , new Literal(Agent.east + t, false));
            kb.unTell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.west + (t + 1) , false)                            
                    , new Literal(Agent.MoveForward + t, false)
                    , new Literal(Agent.west + t, false));
            kb.unTell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.south + (t + 1) , false)                            
                    , new Literal(Agent.MoveForward + t, false)
                    , new Literal(Agent.south + t, false));
            kb.unTell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.south + (t + 1) , false)                            
                    , new Literal(Agent.TurnL + t, false)
                    , new Literal(Agent.west + t, false));
            kb.unTell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.north + (t + 1), false)                            
                    , new Literal(Agent.TurnL + t, false)
                    , new Literal(Agent.east + t, false));
            kb.unTell(sen);                                                                              
            sen = new HornSentence(new Literal (Agent.west + (t + 1), false)                            
                    , new Literal(Agent.TurnL + t, false)
                    , new Literal(Agent.north + t, false));
            kb.unTell(sen);
            sen = new HornSentence(new Literal (Agent.east + (t + 1), false)                            
                    , new Literal(Agent.TurnL + t, false)
                    , new Literal(Agent.south + t, false));                
            kb.unTell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.north + (t + 1) , false)                            
                    , new Literal(Agent.TurnR + t, false)
                    , new Literal(Agent.west + t, false));
            kb.unTell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.south + (t + 1), false)                            
                    , new Literal(Agent.TurnR + t, false)
                    , new Literal(Agent.east + t, false));
            kb.unTell(sen);    
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.east + (t + 1), false)                            
                    , new Literal(Agent.TurnR + t, false)
                    , new Literal(Agent.north + t, false));
            kb.unTell(sen);
            //****************************************************************************************************************
            sen = new HornSentence(new Literal (Agent.west + (t + 1), false)                            
                    , new Literal(Agent.TurnR + t, false)
                    , new Literal(Agent.south + t, false));                
            kb.unTell(sen);           
    }
    
    /**
     * Updates the data structures about the status of an area using knowledge base entailment
     * All new facts are added to the kb facts data structure when function PLFC is called
     * So to save time from entail the same facts again we store them and then we get quickly the boolean value of a sentence(fact)
     * @param area 
     */
    private void clusterArea(Area area){        

        if(this.kb.isFact(new Literal(Agent.wall + area.getRow() + "" + area.getCol(), false)))        
        {                           
            this.bannedAreas.put(area.toString(), area);
            this.okAreas.remove(area.toString());
            this.unknown.remove(area.toString());
            this.uncertainAreas.remove(area.toString());
        }                        
        else if(this.kb.isFact(new Literal(Agent.safeArea  + area.getRow() + "" + area.getCol(), false)))        
        { 
            if(!this.visitedAreas.containsValue(area)){
                this.okAreas.put(area.toString(), area);
                this.uncertainAreas.remove(area.toString());
                this.unknown.remove(area.toString());
            }
        }        
        else if(this.kb.isFact(new Literal(Agent.safeArea + area.getRow() + "" + area.getCol(), true))){            
            this.bannedAreas.put(area.toString(), area);
            this.unknown.remove(area.toString());
            //this.okAreas.remove(area.toString());
            this.uncertainAreas.remove(area.toString());
        }                        
        else if(this.kb.isFact(new Literal(Agent.safeArea + this.time + "" +  area.getRow() + "" +  area.getCol(), true))){            
            this.WumpusArea = area;
            this.bannedAreas.put(area.toString(), area);
            this.unknown.remove(area.toString());
            this.uncertainAreas.remove(area.toString());
        }                                
        else {
            this.unknown.remove(area.toString());
            this.uncertainAreas.put(area.toString(), area);
        }
        //System.out.println("SHOOT" + this.kb.isFact(new Literal(Agent.hasArrow + time, false)));
        //if the agent entails the location of wumpus he kills it        
    }    
    
    /**
     * 
     * @return Returns the Agents`s action
     */
    private String think()
    {                           
        //UPDATE KB AND FORGET TIME PASSED propositions !!!
        //IMPORTANT FOR ENTAILMENT SPEED
        //ALL THE FACTS entailed until that time ARE STORED IN THE KB in the facts data structure and cannot be entailed again
        //so we save some time cause propositions that have `expired` are not used for entailment of future literals                
        //this.updateKB();                
        this.forget(); //agent forgets rules about time passed (time -2)
        //agent forgets past percepts positions and actions but remembers the static parts of the world
        //he also can determine if the wumpus is alive cause he remembers time - 1 events!                        
        this.updateKB();       
        //tell the KB all the percepts of the agent at time t
        //!!Agent percepts a wall when he `sees` it. for example if agent is looking west and westArea has a wall agent will perceive a bump
        this.kb.tellAll(new Literal(Agent.stench + this.time, !this.curArea.stenches())
                , new Literal(Agent.breeze + this.time, !this.curArea.breezes())
                , new Literal(Agent.glitter + this.time, !this.curArea.glitters())
                , new Literal(Agent.bump + this.time, !this.AreaTheAgentHasInFrontOfHim().hasWall())
                , new Literal(Agent.scream + this.time, !this.hearsScream));
        //if grab is entailed agent wins                    
                
        //check if we should grab the gold and if not inform the KB with all the new facts entailed this time
        if(this.kb.askWithPLFCEntails(new Literal(Agent.Grab + this.time, false))){            
            this.action = Agent.Grab;
            this.kb.tell(new Literal(action + "" + time, false));
            this.time++;
            return this.action;
        } 
        clusterArea(this.curArea.northNeighbour);
        clusterArea(this.curArea.eastNeighbour);
        clusterArea(this.curArea.southNeighbour);
        clusterArea(this.curArea.westNeighbour);
        ArrayList temp = new ArrayList(this.uncertainAreas.values());
        for(Object area : temp){
            clusterArea((Area)area);
        }                
        this.visitedAreas.put(this.curArea.toString(), this.curArea);
        this.okAreas.remove(this.curArea.toString());        
        this.uncertainAreas.remove(this.curArea.toString());
        
        if(this.hearsScream) this.hearsScream = false;    
        
        if(this.plan.isEmpty())
        {            
            this.constructPLan();
        }
        this.action = this.plan.peek();
        if(action.equals(Agent.MoveForward)){     
            //check if we hit a wall
            if(this.bannedAreas.containsKey(this.AreaTheAgentHasInFrontOfHim().toString())){
                this.plan.clear();
                this.constructPLan();
                //if we hit one we make a new plan out of scratch
            }            
        }
        //if agent discovers a safe area while exploring uncertain areas but the uncertain area he last explored is not the targeted
        if(this.uncertainAreas.containsValue(this.nextArea) && !this.okAreas.isEmpty()){            
            this.plan.clear();
            this.constructPLan();            
        }
        this.action = this.plan.pop(); //next action is popped
        this.kb.tell(new Literal(action + "" + time, false));
        //finally we tell th kb the action we did.Now the agent knows where he is and where he is looking
        this.time++;                  
        //increment time by one at each action
        //if time > maxSteps agent unfortunately dies         
        return action;
    }        
    
    /**
     * Agent makes the action he decided
     * @return 
     */
    public String makeAction(){
        String finalAction = this.think();
        
        if(finalAction.equals(Agent.Grab)){
            this.grabGold();
        }
        if(finalAction.endsWith(Agent.MoveForward)){
            this.moveForward();
        }
        if(finalAction.equals(Agent.TurnL)){            
            this.turnLeft();
        }
        if(finalAction.equals(Agent.TurnR)){
            this.turnRight();
        }
        if(finalAction.equals(Agent.shoot)){            
            this.killWumpus();
            if(this.hearsScream){
                System.out.println("WUMPUS DIED ");
                Area wumpusArea = this.AreaTheAgentHasInFrontOfHim();                 
                this.clusterArea(wumpusArea);
                this.bannedAreas.remove(wumpusArea.toString());                
                this.unknown.remove(wumpusArea.toString());            
            }                                                              
        } 
        return finalAction;
    }
    
    /**
     * Construct a new plan out of scratch.
     * The plan is constructed going backwards from the chosen path
     * It is constructed in the Path class
     * The shorttest path is constructed with a simple implementation of A* algorithm
     * G is the how many actions must agent make to go from the curArea to an Area and h is the manhattan distance of the start and end tile
     * more info in the class AStar and Path
     */
    public void constructPLan(){
        PriorityQueue<Path> paths  = new PriorityQueue<Path>();
        Path path ;
               
        Random rand = new Random(System.currentTimeMillis());
        Object[] values ;     
        
        if(this.WumpusArea != null && !this.kb.isFact(new Literal(Agent.aliveWumpus, true)))
        {
            this.bannedAreas.remove(this.WumpusArea.toString());
            path = this.aStar.findAPath(this.curArea, this.WumpusArea, this.bannedAreas, this.unknown, this.uncertainAreas, this.visitedAreas,  direction);
            this.plan = path.getActions();
            Stack<String> temp = new Stack();
            while(!(this.plan.size() == 1)){
                temp.push(plan.pop());
            }
            plan.pop();
            plan.push(Agent.shoot);
            while(!(temp.size() == 0)){
                plan.push(temp.pop());
            }
            this.clusterArea(this.nextArea);
            this.nextArea = this.WumpusArea;
        }        
        else if(!okAreas.isEmpty()){            
            //we choose a random area                    
            for(Area area : this.okAreas.values()){
                if( (this.maxSteps*0.2) > (this.maxSteps - this.time)){
                    //pernaei kai apo uncertain paths, an einai polu kourasmneos
                    path = this.aStar.findAPath(this.curArea, area, this.bannedAreas, this.unknown, null, this.visitedAreas,  direction);                                      
                }                
                else
                {
                    path = this.aStar.findAPath(this.curArea, area, this.bannedAreas, this.unknown, this.uncertainAreas, this.visitedAreas,  direction);
                }
                if(path == null )continue;                
                    paths.add(path);                
             }
            path = paths.remove();
            this.nextArea = path.getArea();
            this.plan = path.getActions();                        
        }
        else{  
            int r; 
            if(!this.uncertainAreas.values().isEmpty()){
               do{
                    Area selectedArea;                   
                    values = this.uncertainAreas.values().toArray();
                    r = rand.nextInt(values.length);
                    
                    selectedArea = (Area)values[r];   
                    
                    this.clusterArea(selectedArea);
                    
                    this.uncertainAreas.remove(selectedArea.toString());                                  
                    path = this.aStar.findAPath(this.curArea, selectedArea, this.bannedAreas, this.unknown, null, this.visitedAreas,  direction);                                                          
                                       
                    //we choose to go towards a random  area from uncertain areas follwing the  shortest path to that area                       
                    if(path != null){                
                        this.plan = path.getActions();                        
                        this.nextArea = path.getArea();
                        //we choose to go towards the shortest area foolwing the the shortest path for that area                 
                    }                   
                }while(path == null);
            }else{
                //die
            }
        }
    }
    
    /**
     * Moves the agent to the next area(the area currently having direction at)
     */
    public void moveForward()
    {
        if(this.direction == Agent.NORTH){
            //if(curArea.northNeighbour.hasWall()) return false;
            this.curArea = this.curArea.northNeighbour;
        }
        if(this.direction == Agent.SOUTH){
            //if(curArea.southNeighbour.hasWall()) return false;
            this.curArea = this.curArea.southNeighbour;
        }
        if(this.direction == Agent.WEST){
            //if(curArea.westNeighbour.hasWall()) return false;
            this.curArea = this.curArea.westNeighbour;
        }
        if(this.direction == Agent.EAST){
            //if(curArea.eastNeighbour.hasWall()) return false;
            this.curArea = this.curArea.eastNeighbour;
        }
        //return true;
    }
        
    /**
     * NORTH->WEST->SOUTH->EAST->NORTH
     */
    public void turnLeft()
    {                
        this.direction--;
        if(this.direction == 0)direction = 4;
    }
    
    /**
     * NORTH->EAST->SOUTH->WEST->NORTH
     */    
    public void turnRight()
    {
        this.direction = (direction %4 )+ 1;
    }
    
    /**
     * Throw an arrow (the only arrow) in the area we are looking and hope to kill the monster
     * @return success if wumpus is killed
     */
    public boolean killWumpus()
    {
        boolean success = false;
        if(this.arrowCount > 0){
            this.arrowCount--;
            if(this.direction == Agent.NORTH){
                if(this.curArea.northNeighbour.hasWumpus()){
                    WumpusWorld.wumpusIsDead();
                    success = true;
                }                
            }
            else if(this.direction == Agent.EAST){
                if(this.curArea.eastNeighbour.hasWumpus()){
                    WumpusWorld.wumpusIsDead();       
                    success = true;
                }
            }
            else if(this.direction == Agent.SOUTH){
                if(this.curArea.southNeighbour.hasWumpus()){
                    WumpusWorld.wumpusIsDead();        
                    success = true;
                }
            }
            else if(this.direction == Agent.WEST){
                if(this.curArea.westNeighbour.hasWumpus()){
                    WumpusWorld.wumpusIsDead();                
                    success = true;
                }                    
            }
        }
        if(success) this.hearsScream = true;
        return success;
    }
            
    /**
     * Grabs gold if possible
     */
    public void grabGold()
    {
        if(this.curArea.glitters())this.grabsGold = true;        
    }    
    
    /**
     * 
     * @return true iff (agent has fallen into a pit or time > maxsteps or agent shares an area with the alive wumpus), false oterwise
     */
    public boolean isDead()
    {
        return this.curArea.hasPit() || (this.curArea.hasWumpus()  && WumpusWorld.wumpusIsAlive()) || (this.maxSteps < this.time);
    }
    
    /**
     * 
     * @return Returns true iff agent succed to grab the gold(ring(LOTR(BIG FAN)))
     */
    public boolean hasGold()
    {
        return this.grabsGold;
    }
    /**
     * 
     * @return the area the agent is currently operating on
     */
    public Area getArea()
    {
        return this.curArea;        
    }
    /**
     * 
     * @return The int value of the current direction of the agent
     */
    public int getDirection()
    {
        return this.direction;
    }
    
    /**
     * 
     * @return The number of steps the agent has done so far
     */
    public int getCurrentTime(){
        return this.time;
    }
    
    public int getMaxSteps(){
        return this.maxSteps;
    }        
        
    /**
     * 
     * @return the areas agents is staring at
     */
    private  Area AreaTheAgentHasInFrontOfHim(){
        if(this.direction == Agent.NORTH)return this.curArea.northNeighbour;
        else if(this.direction == Agent.SOUTH)return this.curArea.southNeighbour;
        else if(this.direction == Agent.EAST)return this.curArea.eastNeighbour;
        else return this.curArea.westNeighbour;        
    }
    /**
     * Converts int represantation of the orientation of the agent to String
     * @param direction 
     * @return 1 = NORTH, 2 = EAST, 3 = SOUTH, 4 = WEST
     */
    public static String orientation(int direction){
        if(direction == Agent.NORTH)return Agent.north;
        else if(direction == Agent.EAST)return Agent.east;
        else if(direction == Agent.SOUTH)return Agent.south;
        else if(direction == Agent.WEST)return Agent.west;
        else 
        return "";
    }    
    
    public boolean hearsScream(){
        return this.hearsScream;
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
    
    private static void shootAtDirection(int orientation1, int orientation2, Stack<String> actions){       
        if(orientation1 == Agent.NORTH && orientation2 == Agent.EAST){
            actions.add(Agent.TurnR);
        }
        else if(orientation1 == Agent.NORTH && orientation2 == Agent.WEST){
            actions.add(Agent.TurnL);
        }                
        else if(orientation1 == Agent.EAST && orientation2 == Agent.SOUTH){
            actions.add(Agent.TurnR);
        }
        else if(orientation1 == Agent.EAST && orientation2 == Agent.NORTH){
            actions.add(Agent.TurnL);
        }
        else if(orientation1 == Agent.SOUTH && orientation2 == Agent.WEST){
            actions.add(Agent.TurnR);
        }
        else if(orientation1 == Agent.SOUTH && orientation2 == Agent.EAST){
            actions.add(Agent.TurnL);
        }                
        else if(orientation1 == Agent.WEST && orientation2== Agent.NORTH){
            actions.add(Agent.TurnR);
        }
        else if(orientation1 == Agent.WEST && orientation2 == Agent.SOUTH){
            actions.add(Agent.TurnL);
        }         
    }                
    
    @Override
    public String toString(){
        //Info
        String eol = "\r\n";
        String str = "Hi I am  " + this.name + " ... " + eol + "These are some useful inforamtion about Bilbo ..."  + eol +  eol;
        
        if(this.hearsScream){
            str+= eol + eol + "GOLLUM DIED by the sword STING...." + eol;
            return str;
        }
                
        if(this.grabsGold){
            str += "Found Gold in " + this.time + " steps" + eol;
        }
        else if(this.isDead()){
            str += "Died in " + this.time + " steps" + eol;
        }  
        str+= "Bilbo percepts at [" + this.curArea +  "] [" + (this.curArea.breezes()? "<Breeze> ": "") 
                + (this.curArea.stenches()? "<Stench>": "") 
                + (this.curArea.glitters()? "<Glitter>": "")
                + (this.AreaTheAgentHasInFrontOfHim().hasWall()? "<Bump>": "")
                + (this.hearsScream? "<Scream>": "")+ "]" + eol;
        
        str+= "Heading towards : " +  (nextArea != null ?"[" + this.nextArea.getRow() + "," + this.nextArea.getCol() + "] ..." : "Thinikng. .." )+ eol; 
        
        str += "Safe Areas : " + this.okAreas.values() + eol;
        str += "Uncertain Areas : " + this.uncertainAreas.values() + eol;
        str += "Visited Areas : " + this.visitedAreas.values() + eol;
        Area area = new Area(-1,-1);
        this.bannedAreas.remove(area.toString());
        ArrayList<Area> pits = new ArrayList<Area>();
        ArrayList<Area> walls = new ArrayList<Area>();        
        for(Area a : this.bannedAreas.values()){
            if(a.hasPit()) pits.add(a);
            if(a.hasWall()) walls.add(a);            
        }
        this.bannedAreas.put(area.toString(), area);
        str += "Pits : " + pits + eol;        
        str += "Walls : " + walls + eol + eol;                        
        str += "Wumpus status :" + (WumpusWorld.wumpusIsAlive()? " Alive ..." : " Dead ...")+ eol ;  
        str += "Wumpus area : [" + (this.WumpusArea ==null?"" : this.WumpusArea) + "]" + eol;
        str += "Number of steps : " + (this.time - 1) + " ..." + eol;        
        str += "Steps left : " + (this.maxSteps - this.time) + " ..." + eol;
        str += "Max steps  : " + this.maxSteps + " ..." + eol ; 
        
        if((this.maxSteps*0.2) > (this.maxSteps - this.time)){
            str +=  eol + "The agent is exhausted. He is going to risk more from now on.";
        }                
        return str;
    } 
}
