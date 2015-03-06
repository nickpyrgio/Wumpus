/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpustest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Nikos Pyrgiotis
 */
public class HornSentence extends Sentence{
    
    private Set<Literal> premises; //the negative literals Α ^ Β ^ ... ^ Ω => 
    private Literal head;
    //the positive literal ( => head)
    
    /**
     * 
     * @param head The result of the implication
     * @param premises The premises of the implication
     */
    HornSentence(Literal head, HashSet premises){
        this.head = head;
        this.premises = new HashSet(premises);
    }
    
    HornSentence(Literal head, Literal ... premises){
        this.head = head;
        this.premises = new HashSet(Arrays.asList(premises));        
    }
    
    public Literal getHead(){
        return this.head;
    }
    
    public HashSet<Literal> getPremises(){
        return (HashSet<Literal>) this.premises;
    }
    
    @Override
    public String toString(){
        String buffer = "";     
        int cnt = 0;
        for(Literal premise: premises){
            cnt ++;
            if(cnt == this.premises.size())buffer += premise.toString();
            else buffer += premise.toString() + Connective.AND;
        }
        buffer += Connective.IMPLIES + this.head.toString();
        return buffer;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof HornSentence)){
            return false;
        }        
        HornSentence sen= (HornSentence)o;
        if(this.premises.size() != sen.premises.size()){
            return false;
        }
        if(!head.equals(sen.head)){
            return false;
        }        
        Iterator<Literal> iter = sen.premises.iterator();
        
        while(iter.hasNext())
        {
            Literal lit = iter.next();
            if(!this.premises.contains(lit))
                return false;
        }                
        return true;        
    }    
}
