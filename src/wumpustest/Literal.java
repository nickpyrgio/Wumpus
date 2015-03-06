package wumpustest;

/*
 * Represents a literal; a variable
 */
public class Literal extends Sentence implements Comparable<Literal>
{
    //The name of the literal
    private String Name;
    //Whether or not the literal is negated; if negation is true then it is negated
    private boolean negation;
    
    public Literal(String n, boolean neg)
    {
        this.Name = n;
        this.negation = neg;
    }
    
    public void print()
    {
        if(negation)
            System.out.println(Connective.NOT + Name);
        else
            System.out.println(Name);
    }
        
    public void setName(String n)
    {
        this.Name = n;
    }
    
    public String getName()
    {
        return this.Name;
    }
    
    public void setNeg(boolean b)
    {
        this.negation = b;
    }
    
    public boolean getNeg()
    {
        return this.negation;
    }
      
    @Override
    public boolean equals(Object obj)            
    {
        if(!(obj instanceof Literal)) return false;
        
        Literal l = (Literal)obj;
        return this.Name.equals(l.Name) && l.getNeg() == this.negation;
    }
    
    @Override
    public String toString(){        
        return (this.negation ? Connective.NOT :"") + this.Name;
    }
    
    public String toString(boolean sameNeg){        
        return (this.negation  == sameNeg ? Connective.NOT :"") + this.Name;
    }    
    
    @Override
    public int hashCode()
    {
            if(this.negation)
            {
                return (Connective.NOT + this.Name).hashCode();
            }
            else
            {
                return this.Name.hashCode();                        
            }
    }
	
    //@Override
    @Override
    public int compareTo(Literal x)
    {
            int a = 0;
            int b = 0;
            
            if(x.getNeg())
                a = 1;
            
            if(this.getNeg())
                b = 1;
            
            return x.getName().compareTo(Name) + a-b;
    }    
}
