package wumpustest;

import java.io.IOException;
import java.util.Scanner;


/**
 *
 * @author Nikos
 */
public class WumpusTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {                
        WumpusWorld aWorld = new WumpusWorld(8);        
        Scanner scanner = new Scanner(System.in); 
        //aWorld.save("a.txt");
        //aWorld.load("a.txt");        
        aWorld.randomise(0.2, false);
        aWorld.startSimulation();        
        while(true)
        {                    
            aWorld.print(System.out);
            if( aWorld.agentFoundGold()){
                System.out.println("Agent won...");                
                break;                                        
            }                        
            if(aWorld.agentDied()){
                System.out.println("Agent died...");
                break;                        
            }

            System.out.println("Press enter for Agent to make a move ...");
            scanner.nextLine();
            aWorld.moveAgent();            
        }
        /*
        KB kb = new KB();                        
        kb.tell(new Literal("G", false));
        kb.tell(new Literal("E", false));
        //kb.tell(new HornSentence((new Literal("W", false)), new Literal("G", false), new Literal("E", false)));        
        kb.tell(new HornSentence((new Literal("W", false)), new Literal("A", false), new Literal("J", false)));        
        System.out.println(kb.askWithPLBCEntails(new Literal("W", false)));
        System.out.println(kb.askWithPLFCEntails(new Literal("W", false))); 
         * 
         */
         
         
    }
}
