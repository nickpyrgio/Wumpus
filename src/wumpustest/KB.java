/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package wumpustest;

/**
*
* @author Nikos
*/

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/**
* @author Nikos
*/
public class KB {
    
    private final HashMap<String,Sentence> sentences ; //all hte facts and rules of the KB
    private final HashMap<String, Literal> facts;  //only the facts   
    //it is used as a memory for entailed facts so we do not have to entail them again
    private final HashMap<String,HornSentence> rules;  //only the rules              

    public KB() {        
        this.facts = new HashMap<String, Literal>(); 
        this.rules = new HashMap<String,HornSentence>();        
        this.sentences = new HashMap<String,Sentence>();          
    }

    /**
     * Adds the specified sentence to the knowledge base.
     * 
     * @param aSentence
     *            a fact to be added to the knowledge base.
     */
    public void tell(Sentence aSentence){
        if(aSentence instanceof Literal){
            this.facts.put(((Literal)aSentence).toString(),(Literal)aSentence);             
        }
        else {            
            this.rules.put(((HornSentence)aSentence).toString(),(HornSentence)aSentence);             
        }
        this.sentences.put(aSentence.toString(), aSentence);           
    }

    /**
     * Each time the agent program is called, it TELLS the knowledge base what
     * it perceives.
     * 
     * @param percepts
     *            what the agent perceives
     */
    public void tellAll(Sentence ... percepts) {
            for (int i = 0; i < percepts.length; i++) {
                    tell(percepts[i]);
            }
    }
    
    public void unTellAll(Sentence ... percepts) {
        
            for (int i = 0; i < percepts.length; i++) {
                    unTell(percepts[i]);
            }
    }    
    
    public Sentence unTell(Sentence sent){        
        if(sent instanceof Literal){
            this.facts.remove(((Literal)sent).toString());            
        }
        else {         
            this.rules.remove(((HornSentence)sent).toString());            
        }          
        return this.sentences.remove(sent.toString()) ;//!= null;
    }

    /**
     * Returns the number of sentences in the knowledge base.
     * 
     * @return the number of sentences in the knowledge base.
     */
    public int size() {
            return sentences.size();
    }
        
    
    /**
     * Returns the answer to the specified question using the TT-Entails
     * algorithm.
     * 
     * @param literal
     *            a question to ASK the knowledge base
     * 
     * @return the answer to the specified question using the TT-Entails
     *         algorithm.
     */
    public boolean askWithPLFCEntails(Literal literal) {       
       return new PLFCEntails().plfcEntails(this, literal);
    }
    
    /**
     * Use the backwards chaining algorithm to determine if a query is entailed by the KB
     * @param literal The query
     * @return true iff kb entails the query , false otherwise
     */    
    public boolean askWithPLBCEntails(Literal literal) {
       return this.plbcEntails(literal);
    }    

    @Override
    public String toString() {
        String buf = "";
        ArrayList temp = new ArrayList(this.sentences.values());
        for(int i = 0;i < this.sentences.size(); i++){
            buf += (Connective.LEFT_PARENTHESIS + temp.get(i) + Connective.RIGHT_PARENTHESIS) + "\r\n";
            if(i != (this.sentences.size() - 1)) buf += Connective.AND;
        }
        return buf;
    }
    /**
     * Returns the list of sentences in the knowledge base.
     * 
     * @return the list of sentences in the knowledge base.
     */
    public ArrayList<Sentence> getSentences() {
        return new ArrayList(this.sentences.values());
    }
    
    public Collection<HornSentence> getRules() {
        return this.rules.values();
    }    
    
    public  Collection<Literal> getFacts() {
        return   this.facts.values();
    }
    
    public void addFact(Literal l){
        facts.put(l.toString(),l);            
    }
    
    public boolean isFact(Literal l){        
        return this.facts.containsValue(l);
    }
    
    /**
     * Use the backwards chaining algorithm to determine if a query is entailed by the KB
     * @param q The query
     * @return true iff kb entails the query , false otherwise
     */
    private boolean plbcEntails(Literal q)
    {
        HashMap<String, Boolean> entailed = new HashMap<String, Boolean>();
        Stack<Literal> agendaLocal =   new Stack<Literal>();
        
        agendaLocal.push(q);
        while(!agendaLocal.isEmpty()){            
            Literal p = agendaLocal.pop();                        
            entailed.put(p.toString(), Boolean.TRUE);                        
            if (!facts.containsValue(p))
            {
                HashSet<Literal> premises = new HashSet<Literal>();                
                for(HornSentence rule : rules.values()){
                    if (rule.getHead().equals(p)){                        
                        premises = rule.getPremises(); 
                        break;
                    }	
                }                                
                if (premises.isEmpty()){ 
                     return false;                                                                   
                }
                else
                {                    
                    for(Literal li : premises){
                        Object value = entailed.get(li.toString());
                        if(value == null){
                            agendaLocal.push(li);
                            //System.out.println(li);
                        }
                    }
                }
            }
        }//while end
        return true;
    }        
}