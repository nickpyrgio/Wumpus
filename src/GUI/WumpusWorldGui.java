/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Nikos
 */
public final  class WumpusWorldGui extends JPanel
{

    private AreaGui[][] elements;  //the array of the squares(two dimensional)        
    private int dimension;
    private static final ArrayList<ImageLabel> temp = new ArrayList<ImageLabel>();
    
    WumpusWorldGui(int dimension, int width , int height)
    {
        this.dimension = dimension;
        this.setPreferredSize(new Dimension(width,height));
        //this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 50)); // the black border you see. it is 15 width
        this.setBackground(Color.BLACK);// cannot see it, it is the color of the background, just ignore :P
        this.setVisible(true);                   

        //adds all the squares to the background JPanel. we do this so we can see them on screen
        elements = new AreaGui[dimension][dimension];
        this.setLayout(new GridLayout(dimension, dimension, 1 ,1)); //grid Layout we use to put the squares on the board with a nice design         
        for(int i = 0 ; i < dimension; i ++)
        {
            for(int j = 0; j < dimension; j++)
            {                                                   
                this.elements[i][j] = new AreaGui(new GridLayout(0,1), j, i);
                this.elements[i][j].setPreferredSize(new Dimension(width/dimension,height/dimension));   
                this.elements[i][j].setBackground(Color.gray);
                this.add(elements[i][j]);
            }            
        }        
        buildWalls();
    }
    
    public void buildWalls(){
        for(int i = 0; i < dimension;i++)
        {            
            temp.add(Elements.newWall());
            elements[0][i].add(temp);
            temp.clear();
            if(i != 0){
                temp.add(Elements.newWall());
                elements[i][0].add(temp);
                temp.clear();
            }
            if(i != 0){
                temp.add(Elements.newWall());
                elements[i][dimension - 1].add(temp);
                temp.clear();
            }
            if(i != 0 && i != dimension - 1){
                temp.add(Elements.newWall());
                elements[dimension - 1][i].add(temp);
                temp.clear();
            }                        
        }         
    }
    
    public int getDimension()   
    {
        return this.dimension;
    }
    public AreaGui getArea(int row, int col)
    {
        return this.elements[row][col];
    }
    
    public void paintUnkownBlack(){
        
        for(int i = 0 ; i < dimension; i ++)
        {
            for(int j = 0; j < dimension; j++)
            {                                                   
                AreaGui area = this.elements[i][j];                
                if(area.getLabels().isEmpty() && !area.containsWall()){
                    
                    ImageLabel label = Elements.newBlack();                    
                    temp.add(label);
                    area.add(temp);
                    temp.clear();
                }
            }            
        }                            
    }
}    
