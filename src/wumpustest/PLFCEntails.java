package wumpustest;

/**
 *
 * @author AIMA ,Nikos
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class PLFCEntails {

    private HashMap<String, Integer> count;

    private HashMap<Literal, Boolean> inferred;

    private Stack<Literal> agenda;    

    public PLFCEntails() {
            count = new HashMap<String, Integer>();
            inferred = new HashMap<Literal, Boolean>();
            agenda = new Stack<Literal>();            
    }    

    /**
     * Return the answer to the specified question using the PL-FC-Entails
     * algorithm
     * 
     * @param kb
     *            the knowledge base, a set of propositional definite clauses
     * @param q
     *            the query, a proposition symbol
     * 
     * @return the answer to the specified question using the PL-FC-Entails
     *         algorithm
     */
    public boolean plfcEntails(KB kb, Literal q) {                
        List<HornClause> hornClauses = asHornClauses(kb.getSentences());    
        //long start = System.currentTimeMillis();
        while (agenda.size() != 0) {                
            Literal p = agenda.pop();
            //agenda has all symbols that are considered true in the KB
            while (!inferred(p)) {                    
                this.inferred.put(p, Boolean.TRUE);
                kb.tell(p);                
                for (HornClause hornClause : hornClauses) 
                {                                                            
                    if (hornClause.premisesContainsSymbol(p)) 
                    {                              
                        decrementCount(hornClause);
                        //decrement the number of premises that are true until we reach zero which means we proved the impliment 
                        //for example a ^ v ^ f => x. if a and v and g are inferred then x is inferred too
                        //if x == q return true. Else push x in the agenda and try again with the new proposition to see if q can be proved
                        if (countisZero(hornClause))
                        {
                            if (hornClause.head().equals(q)){ 
                                kb.tell(q);
                                //we finally have proved the asked query
                                //System.out.println(hornClause);
                                return true;                 
                            }
                            else{                                                                                        
                                this.agenda.push(hornClause.head());
                                //after we proved that head is true we push it to 
                                //the agenda and use it to prove anothe symbol containining this to its premises
                            }
                        }
                    }
                }                
            }
        }        
        //System.out.println(System.currentTimeMillis() - start);
        return false;
    }

    private List<HornClause> asHornClauses(List<Sentence> sentences) {
        List<HornClause> hornClauses = new ArrayList<HornClause>();
        for (Sentence sentence : sentences) {                    
                HornClause clause = new HornClause(sentence);
                hornClauses.add(clause);
        }
        return hornClauses;
    }

    private boolean countisZero(HornClause hornClause) {

            return (count.get(hornClause.toString())).intValue() == 0;
    }

    private void decrementCount(HornClause hornClause) {

            int value = (count.get(hornClause.toString())).intValue();
            count.put(hornClause.toString(), new Integer(value - 1));
    }

    private boolean inferred(Literal p) {
            Object value = inferred.get(p);
            return ((value == null) || value.equals(Boolean.TRUE));
    }

    public final class HornClause {
            Set<Literal> premiseSymbols;
            Literal head;
            Sentence sentence;

            /**
             * Constructs a horn clause from the specified sentence.
             * 
             * @param sentence
             *            a sentence in propositional logic
             */
            public HornClause(Sentence sentence) {                    
                    this.sentence = sentence;
                    if (sentence instanceof Literal) {  
                            head = (Literal) sentence;
                            premiseSymbols = new HashSet<Literal>();
                            agenda.push(head);                            
                            count.put(this.toString(), new Integer(0));
                            inferred.put(head, Boolean.FALSE);
                    } 
                    else {
                            HornSentence sen = (HornSentence) sentence;
                            head = (Literal) sen.getHead();
                            inferred.put(head, Boolean.FALSE);
                            //this.premiseSymbols = new HashSet<Literal>(sen.getPremises());
                            this.premiseSymbols = sen.getPremises();
                            for(Literal li : premiseSymbols){
                                inferred.put(li, Boolean.FALSE);
                            }
                            count.put(this.toString(), new Integer(premiseSymbols.size()));                                
                    }
            }

            /**
             * Returns the conclusion of this horn clause. In horn form, the premise
             * is called the body, and the conclusion is called the head.
             * 
             * @return the conclusion of this horn clause.
             */
            public Literal head() {

                    return head;
            }

            /**
             * Return <code>true</code> if the premise of this horn clause contains
             * the specified symbol.
             * 
             * @param q
             *            a symbol in propositional logic
             * 
             * @return <code>true</code> if the premise of this horn clause contains
             *         the specified symbol.
             */
            public boolean premisesContainsSymbol(Literal q) {
                    return premiseSymbols.contains(q);
            }

            /**
             * Returns a list of all the symbols in the premise of this horn clause
             * 
             * @return a list of all the symbols in the premise of this horn clause
             */
            public Set<Literal> getPremiseSymbols() {
                    return premiseSymbols;
            }

            @Override
            public boolean equals(Object o) {

                    if (this == o) {
                            return true;
                    }
                    if ((o == null) || (this.getClass() != o.getClass())) {
                            return false;
                    }
                    HornClause ohc = (HornClause) o;
                    if (premiseSymbols.size() != ohc.premiseSymbols.size()) {
                            return false;
                    }
                    for (Literal s : premiseSymbols) {
                            if (!ohc.premiseSymbols.contains(s)) {
                                    return false;
                            }
                    }

                    return true;
            }

            @Override
            public int hashCode() {
               return this.sentence.hashCode();
            }

            @Override
            public String toString() {
                String buffer = "";
                int i = 0;
                for(Literal l : this.premiseSymbols){
                    i++;
                    if(i != this.premiseSymbols.size()) buffer += l + Connective.AND;
                    else  buffer += l;                    
                }
                    return buffer + Connective.IMPLIES + head;
            }
    }
}
