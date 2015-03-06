/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Nikos
 */
public  class AreaGui extends JPanel
{            
    private int row, col;
    private ArrayList<ImageLabel> labels;
    
    AreaGui(LayoutManager layout, int row , int col)
    {        
        super(layout);
        this.row = row;
        this.col = col;                        
        labels = new ArrayList<ImageLabel>();
    } 
    
    void add(ImageLabel label){
        super.add(label);
        this.revalidate();
        this.repaint();        
    }

    void add(ArrayList<ImageLabel> labels)
    {                    
        this.removeAll();     
        this.labels = null;
        this.labels = new ArrayList<ImageLabel>(labels);
        if(this.labels .size() == 1){
            this.setLayout(new GridLayout(0,1));                
        }
        else if(this.labels .size() > 1){
            this.setLayout(new GridLayout(0,2));
        }
        for(int i = 0; i < this.labels .size(); i++)
        {              
            super.add(this.labels .get(i));
        }    
        this.revalidate();
        this.repaint();
    }
    
    ArrayList<ImageLabel> getLabels(){
        return this.labels;
    }
    
    int getCol()
    {
        return this.col;
    }
    
    int getRow()
    {
        return this.row;
    }
    
    boolean containsWall()
    {        
        for(int i = 0; i < labels.size(); i++)
        {          
            if(labels.get(i).getPath().equalsIgnoreCase("images//wall.jpg"))
                return true;
        }
        return false;
    }
    
    boolean containsAgent()
    {        
        for(int i = 0; i < labels.size(); i++)
        {            
            if(labels.get(i).getPath().equalsIgnoreCase("images//bilboNorth.jpg")
                    || labels.get(i).getPath().equalsIgnoreCase("images//bilboSouth.jpg")
                    || labels.get(i).getPath().equalsIgnoreCase("images//bilboEast.jpg")
                    || labels.get(i).getPath().equalsIgnoreCase("images//bilboWest.jpg"))
                return true;
        }
        return false;
    }
    boolean containsPit()
    {
        for(int i = 0; i < labels.size(); i++)
        {
            if(labels.get(i).getPath().equalsIgnoreCase("images//pit.jpg"))
                return true;
        }
        return false;
    }
    boolean containsGold()
    {
        for(int i = 0; i < labels.size(); i++)
        {
            if(labels.get(i).getPath().equalsIgnoreCase("images//ring.jpg"))
                return true;
        }
        return false;
    }
    boolean containsWumpus()
    {
        for(int i = 0; i < labels.size(); i++)
        {
            if(labels.get(i).getPath().equalsIgnoreCase("images//gollum.jpg"))
                return true;
        }
        return false;
    }    
}  
