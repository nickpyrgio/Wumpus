
package GUI;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import wumpustest.TextIo;
import wumpustest.WumpusWorld;

/**
 *
 * @author Nikos
 */
public class WumpusEventHandler extends JFrame implements ActionListener,MouseListener , MouseMotionListener , KeyListener{
    
    public static void main(String[] args) {
        WumpusEventHandler eventHandler = new WumpusEventHandler();
        eventHandler.showFrame();        
    }                   
    private JDialog editDialog;    
    private Checkbox addAgent;
    private Checkbox addRing;
    private Checkbox addPit;
    private Checkbox addGollum;
    private WumpusWorldGui board;       
    private JMenuBar menubar;   
    private JMenu fileMenu;    
    private JFileChooser fc;     
    private JMenu toolsMenu;    
    private JMenuItem createNewWorld;    
    private JMenuItem save;    
    private JMenuItem load;
    private JMenuItem exit;    
    private JMenuItem randomise;
    private JMenuItem editWorld;
    private JMenuItem step;
    private JMenuItem info;
    private JMenuItem startSimulation;
    private JMenuItem reset;    
    private boolean running;
    private AreaGui agentArea;
    private AreaGui gollumArea;
    private int initialDirection;
    private WumpusWorld wumpusWorld;
    private WumpusWorld copy;
    File curDir;
    
    public WumpusEventHandler()
    {
        super("Wumbus");    
        running = false;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void showFrame()
    {
        this.setLayout(new BorderLayout());        
        this.addKeyListener(this);        
        this.menuGui();               
        this.setPreferredSize(new Dimension(1024,720));
        this.pack();        
        this.setLocationRelativeTo(null);
        this.setVisible(true);
                
        this.board = new WumpusWorldGui(7, 860 , 720);        
        this.board.addMouseListener(this);
        this.add(this.board, BorderLayout.CENTER);
        this.board.revalidate();
        this.board.repaint();        
        this.repaint();                
    }    
    
    /**
     * adds menu components
     */
    private void menuGui()
    {
        this.menubar = new JMenuBar();
        this.fileMenu = new JMenu("File");         
        this.createNewWorld = new JMenuItem("Create new World   Ctrl + N");
        this.save = new JMenuItem("Save World   Ctrl + S");
        this.load = new JMenuItem("Load World   Ctrl + L");
        this.exit = new JMenuItem("Quit World     Ctrl + W");        
        this.save.addActionListener(this);
        this.createNewWorld.addActionListener(this);
        this.load.addActionListener(this);        
        this.exit.addActionListener(this);                
        this.fileMenu.add(createNewWorld);            
        this.fileMenu.addSeparator();
        this.fileMenu.add(save);            
        this.fileMenu.add(load);            
        this.fileMenu.addSeparator();
        this.fileMenu.add(exit);                
        
        this.toolsMenu = new JMenu("Tools");   
        this.randomise = new JMenuItem("Randomise ");        
        this.randomise.addActionListener(this);
        this.editWorld = new JMenuItem("Edit World  E Key");        
        this.editWorld.addActionListener(this);
        this.startSimulation = new JMenuItem("Start Simulation    F6 Key");
        this.startSimulation.addActionListener(this);
        this.reset = new JMenuItem("Reset    R Key");
        this.reset.addActionListener(this);
        this.reset.setEnabled(false);
        this.step = new JMenuItem("Step    Space Key");
        this.step.setEnabled(false);
        this.step.addActionListener(this);
        this.info = new JMenuItem("Show info    i Key");
        this.info.setEnabled(false);
        this.info.addActionListener(this);        
        this.toolsMenu.add(this.randomise);  
        this.toolsMenu.add(this.editWorld);  
        this.toolsMenu.addSeparator();
        this.toolsMenu.add(this.startSimulation);               
        this.toolsMenu.add(this.reset);               
        this.toolsMenu.add(this.step);
        this.toolsMenu.addSeparator();
        this.toolsMenu.add(this.info);               
        this.menubar.add(fileMenu);
        this.menubar.add(toolsMenu);
        this.setJMenuBar(menubar);
    }
    
    private void initFileChooser()
    {        
        fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);            
        fc.addChoosableFileFilter(new Filter());
        if(curDir != null)            
            fc.setCurrentDirectory(curDir);                             
    }    
    
    private void fileChooserMode(boolean loadMode) throws IOException
    {        
        File aFile;
        String pathName;
        int i;
        String ext;
        int returnVal = -1;       
        this.initFileChooser();
        if(loadMode)  returnVal = fc.showOpenDialog(this);
        else returnVal = fc.showSaveDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {            
            aFile = fc.getSelectedFile();   
            if(loadMode)
            {
                this.curDir = aFile.getParentFile();
                String str = TextIo.read(aFile.getAbsolutePath());
                loadData(str);
                //this.repaint();
                //load method
            }
            else
            {
                this.curDir = aFile.getParentFile();
                pathName = aFile.toString();
                i = pathName.lastIndexOf('.');
                ext = pathName.substring(i + 1);
                if(i == -1 || ! ext.equals("wumpus"))
                {
                    pathName += ".wumpus";
                }                               
                if(this.board == null ) return;
                if(!contructWumpusWorld()){
                    JOptionPane.showMessageDialog(this, "Save failed.Please add Agent and Wumpus.", null, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                this.wumpusWorld.save(pathName);                
                //WumpusWorld temp = new WumpusWorld(this.board);
                //TextIo.write(pathName, temp.toString(), false);
               
            }                 
        }        
    }
    
    private boolean contructWumpusWorld(){
        this.wumpusWorld = new WumpusWorld(this.board.getDimension());         
        for(int row = 0; row < this.wumpusWorld.getDimension(); row++){           
            for(int col = 0; col < this.wumpusWorld.getDimension(); col++){
                if(this.board.getArea(row, col).containsAgent()){                     
                    this.wumpusWorld.setAgentArea(row, col, this.initialDirection);
                }
                if(this.board.getArea(row, col).containsPit()){
                    this.wumpusWorld.setPitArea(row, col);
                }
                if(this.board.getArea(row, col).containsWumpus()){
                    this.wumpusWorld.setWumpusArea(row, col);
                }
                if(this.board.getArea(row, col).containsGold()){
                    this.wumpusWorld.setGoldArea(row, col);                    
                }                
            }
        }
        this.copy = new WumpusWorld(this.wumpusWorld);
        return this.wumpusWorld.worldHasAgentAndWumpus();
    }
    
    private boolean contructWumpusWorld(WumpusWorld copy){
        
        this.wumpusWorld = new WumpusWorld(copy);
        this.copy = copy;
        return this.wumpusWorld.worldHasAgentAndWumpus();
    }    

    private void loadData(String str){
        
        String lines[] = str.split("\r\n");
        int dimension = Integer.parseInt(lines[0]);
        this.createNewWorld(dimension);
        class Temp{
            ArrayList<ImageLabel> images = new ArrayList<ImageLabel>();            
        }        
        Temp[][] array = new Temp[dimension][dimension];        
        for(int row = 0 ; row < dimension; row++ ){
            for(int col = 0 ; col < dimension; col++ ){
                array[row][col] = new Temp();
            }
        }      
        this.initialDirection  = Integer.parseInt(lines[1]);
        String[] location = lines[2].split(" ");
        int agentRow = Integer.parseInt(location[0]);
        int agentCol = Integer.parseInt(location[1]);         
        
        ImageLabel label = Elements.getAgent(initialDirection);
        array[agentRow][agentCol].images.add(label);
        
        label = Elements.WUMPUS;
        location = lines[3].split(" ");
        int wumpusRow = Integer.parseInt(location[0]);
        int wumpusCol = Integer.parseInt(location[1]);
        array[wumpusRow][wumpusCol].images.add(label);
        
        for(int i = 4 ; i < lines.length; i ++){
            location = lines[i].split(" ");
            int row = Integer.parseInt(location[0]);
            int col = Integer.parseInt(location[1]);
            boolean hasGold = Integer.parseInt(location[2]) == 1;
            boolean hasPit = Integer.parseInt(location[3]) == 1;
            if(hasGold){
                array[row][col].images.add(Elements.newGold());
            }
            if(hasPit){
                array[row][col].images.add(Elements.newPit());
            }
        }      
        
        for(int row = 0 ; row < dimension; row++ ){
            for(int col = 0 ; col < dimension; col++ ){
                this.board.getArea(row, col).add(array[row][col].images);
            }
        }
        this.board.buildWalls();
    }
    
    private void load()
    {
        try {
            this.fileChooserMode(true);
        } 
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }           
    }
    
    private void save()
    {
        try {
            this.fileChooserMode(false);
        } 
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }           
    }
        
    private void createNewWorld()
    {
        Object answer = null;                
        Object selectionValues[] = new Object[8];               
        for(int i = 0 ;i < selectionValues.length; i++)
        {
            selectionValues[i] = (i + 5)+"x" + (i+5);
        }
        answer = (JOptionPane.showInputDialog(this,  "Choose world`s dimension", "Dimension ‼"
                , JOptionPane.PLAIN_MESSAGE, null, selectionValues, selectionValues[0]))/*.toString()*/;   
        if(answer == null) return;
        if(board != null)this.remove(board);                
        this.board = new WumpusWorldGui(Integer.valueOf(answer.toString().substring(0, answer.toString().indexOf("x") )) + 2, 1280 , 860);
        this.board.addMouseListener(this);
        this.add(this.board, BorderLayout.CENTER);     
        this.board.revalidate();
        this.board.repaint();        
        this.repaint();
        this.addAgent = new Checkbox("Add Agent", false); 
        this.addRing = new Checkbox("Add Gold",  false); 
        this.addPit = new Checkbox("Add Pit", false); 
        this.addGollum = new Checkbox("Add Gollum", false);        
        this.addGollum.setVisible(true);
        this.addAgent.setVisible(true);
        this.agentArea = null;
        this.gollumArea = null;
    }
    
    private void createNewWorld(int dimension)
    {        
        if(board != null)this.remove(board);                
        this.board = new WumpusWorldGui(dimension, 1280 , 860);
        this.board.addMouseListener(this);
        this.add(this.board, BorderLayout.CENTER);     
        this.board.revalidate();
        this.board.repaint();        
        this.repaint();
        this.addAgent = new Checkbox("Add Agent", false); 
        this.addRing = new Checkbox("Add Gold",  false); 
        this.addPit = new Checkbox("Add Pit", false); 
        this.addGollum = new Checkbox("Add Gollum", false);        
        this.addGollum.setVisible(true);
        this.addAgent.setVisible(true);
        this.agentArea = null;
        this.gollumArea = null;
    }    
    
    void showEditDialog() {
        if (editDialog == null) {
            editDialog = new JDialog(this);
            editDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    editDialog.removeAll();
                    editDialog.dispose();
                    editDialog = null;
                }
            });
            editDialog.setLayout(new BorderLayout());
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(0, 2));	//0 rows, 1 Column                             
            JCheckBox addAgentToSquare = new JCheckBox(new ImageIcon("icons//bilbo.jpg"), false);
            JCheckBox addGoldToSquare = new JCheckBox(new ImageIcon("icons//ring.jpg"), false);
            JCheckBox addPitToSquare = new JCheckBox(new ImageIcon("icons//pit.jpg"), false);
            JCheckBox addWumpusToSquare = new JCheckBox(new ImageIcon("icons//gollum.jpg"), false);
            this.addAgent = new Checkbox("Add Agent", false);
            this.addRing = new Checkbox("Add Gold", false);
            this.addPit = new Checkbox("Add Pit", false);
            this.addGollum = new Checkbox("Add Gollum", false);
            this.addAgent.setVisible(this.agentArea == null);
            this.addGollum.setVisible(this.gollumArea == null);

            panel.add(addGoldToSquare);
            panel.add(addRing);
            panel.add(addPitToSquare);
            panel.add(addPit);
            panel.add(addWumpusToSquare);
            panel.add(addGollum);
            panel.add(addAgentToSquare);
            panel.add(addAgent);
            editDialog.add(panel);
            editDialog.pack();
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);
        }
    }
    
    void showInfoDialog(){
        JOptionPane.showMessageDialog(this, this.wumpusWorld.info(), "Info", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("images//lucy.jpg"));
    }
        
    private void step(){

        if(!running)return;
        
        if(this.wumpusWorld.agentDied()){
            this.showInfoDialog();
            this.reset();
            this.startSimulation();//stops the simulation
            return;
        }                
        if(this.wumpusWorld.agentHearsScream()){
            this.showInfoDialog();                  
        }                        
        //this.wumpusWorld.print(System.out);
        int temp[] = this.wumpusWorld.getAgentCurrentStatus();      
        int prevRow = temp[0];
        int prevCol = temp[1];
        this.wumpusWorld.moveAgent();
        temp = this.wumpusWorld.getAgentCurrentStatus();
        int nextRow = temp[0];
        int nextCol = temp[1];
        int curDirection = temp[2];
        
        ImageLabel label = Elements.getAgent(curDirection);        
        
        
        ArrayList<ImageLabel> list1 = this.board.getArea(prevRow, prevCol).getLabels();        
        //list1.remove(Elements.getAgent(prevDirection));
        list1.clear();
        //label = new ImageLabel();
        //label.setText("V");                
        //list1.add(label);
        //this.board.getArea(prevRow, prevCol).add(list1);
        
        ArrayList<ImageLabel> list2 = this.board.getArea(nextRow, nextCol).getLabels();
        boolean hasGold = false;
        if(list2.contains(Elements.newGold())){
            hasGold = true;
        }
        list2.clear();
        
        list1.remove(Elements.getAgent(curDirection));
        list2.add(label);
        if(hasGold){
            list2.add(Elements.newGold());
                    
        }
        this.board.getArea(nextRow, nextCol).add(list2);
        this.board.revalidate();
        this.board.repaint();
        
        if(this.wumpusWorld.agentFoundGold()){
            this.showInfoDialog();
            this.reset();
            this.startSimulation();//stops the simulation
            return;
        }
        if(this.wumpusWorld.agentDied()){
            this.showInfoDialog();
            this.reset();
            this.startSimulation();//stops the simulation
            return;
        }                                
    }
    
    private void reset(){
        if(this.contructWumpusWorld(this.copy))        
            this.loadData(this.wumpusWorld.toString());        
    }
    
    private void startSimulation(){
        if(editDialog!= null){
            this.editDialog.dispose();
            this.editDialog = null;
        }
        if(!running){            
            //this.wumpusWorld = new WumpusWorld(this.board);
            //System.out.println(this.wumpusWorld);
            if(!contructWumpusWorld()){
                JOptionPane.showMessageDialog(this, "Simulation start failed.Please add Agent and Wumpus", null, JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.running = true;
            Object answer = null;
            Object selectionValues[] = new Object[13];
            selectionValues[0] = "30";
            selectionValues[1] = "50";
            selectionValues[2] = "60";
            selectionValues[3] = "70";
            selectionValues[4] = "80";
            selectionValues[5] = "100";
            selectionValues[6] = "120";
            selectionValues[7] = "130";
            selectionValues[8] = "140";
            selectionValues[9] = "150";
            selectionValues[10] = "180";
            selectionValues[11] = "200";
            selectionValues[12] = "250";
            answer = (JOptionPane.showInputDialog(this, "Choose agents`s maximum steps :", "Max Steps ‼", JOptionPane.PLAIN_MESSAGE, null
                , selectionValues, selectionValues[7]))/*.toString()*/;
            
            if(answer == null){
                this.startSimulation();
                return;
            }
            else 
                this.wumpusWorld.startSimulation(Integer.parseInt((String)answer));           
            this.startSimulation.setText("Stop Stimulation    F6 key");
            this.running = true;
            this.reset.setEnabled(true);
            this.step.setEnabled(true);
            this.info.setEnabled(true);
            this.load.setEnabled(false);
            this.save.setEnabled(false);
            this.createNewWorld.setEnabled(false);
            this.editWorld.setEnabled(false); 
            this.randomise.setEnabled(false); 
            
            this.board.paintUnkownBlack();
            this.board.revalidate();
            this.board.repaint();
        }
        else
        {            
            this.reset();
            this.startSimulation.setText("Start Stimulation   F6 key");
            this.running = false;
            this.reset.setEnabled(false);
            this.step.setEnabled(false); 
            this.load.setEnabled(true);
            this.save.setEnabled(true);
            this.createNewWorld.setEnabled(true);
            this.editWorld.setEnabled(true);
            this.info.setEnabled(false);
            this.randomise.setEnabled(true); 
        }             
    }    
    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        if(!running && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_L)
        {
            this.load();
        }
        if(!running && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S)
        {
            this.save();
        }        
        if(e.getKeyCode() == KeyEvent.VK_E && !this.running)
        {
            if(this.editDialog == null){
                this.showEditDialog();
            }
            else if(this.editDialog != null)
            {
                this.editDialog.removeAll();
                this.editDialog.dispose();
                this.editDialog = null;
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE && !this.running){
            if(this.editDialog != null)
            {
                this.editDialog.removeAll();
                this.editDialog.dispose();
                this.editDialog = null;
            }            
        }
        if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W)
        {
            System.exit(0);
        }
        if(e.getKeyCode() == KeyEvent.VK_I && this.running){
            this.showInfoDialog();
        }        
        if(e.getKeyCode() == KeyEvent.VK_SPACE && this.running)
        {
            this.step();
        }
        if(e.getKeyCode() == KeyEvent.VK_F6)
        {
            this.startSimulation();
        }
        if(running && e.getKeyCode() == KeyEvent.VK_R)
        {
            if(running)this.reset();
            this.running = false;
            this.startSimulation();                       
        }        
    }

    @Override
    public void keyReleased(KeyEvent e) {        

    }       
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(load))
        {       
            load();
        }
        else if(e.getSource().equals(save))
        {       
            save();
        }        
        else if(e.getSource().equals(exit))
        {
            System.exit(0);
        }
        else if(e.getSource().equals(this.editWorld))
        {
            if(this.editDialog == null && !this.running){
                this.showEditDialog();
            }
            else if(this.editDialog != null)
            {
                this.editDialog.removeAll();
                this.editDialog.dispose();
                this.editDialog = null;
            }
        }
        else if(e.getSource().equals(this.randomise)){
            WumpusWorld temp = new WumpusWorld(this.board.getDimension());
            this.contructWumpusWorld(temp);
            this.wumpusWorld.randomise(0.20, false);
            this.loadData(this.wumpusWorld.toString());
        }
        else if(e.getSource().equals(this.createNewWorld))
        {
            createNewWorld();
        }
        else if(e.getSource().equals(this.startSimulation))
        {
               startSimulation();
        }
        else if(e.getSource().equals(this.reset))
        {
            this.reset();
            this.running = false;
            this.startSimulation();            
        }
        else if(e.getSource().equals(this.step))
        {
            this.step();
        }
        else if(e.getSource().equals(this.info))
        {
            this.showInfoDialog();
        }        
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
        if(this.editDialog == null) return;
        Component c =  this.board.findComponentAt(e.getX(), e.getY());   
        ArrayList<ImageLabel> labels = new ArrayList<ImageLabel>();
        
        if(c instanceof AreaGui)
        {
            AreaGui temp = (AreaGui)c;            
            if (addAgent.getState()) {
                Object answer;
                Object selectionValues[] = new Object[4];
                selectionValues[0] = "North";
                selectionValues[1] = "South";
                selectionValues[2] = "West";
                selectionValues[3] = "East";
                answer = (JOptionPane.showInputDialog(this, "Choose agents`s direction :", "Direction ‼", JOptionPane.PLAIN_MESSAGE, null, selectionValues, selectionValues[0]))/*.toString()*/;
                if (answer == null) {
                    return;
                }
                if(answer.equals("North")) this.initialDirection = 1;
                if(answer.equals("South")) this.initialDirection = 3;
                if(answer.equals("West")) this.initialDirection = 4;
                if(answer.equals("East")) this.initialDirection = 2;
                labels.add(Elements.getAgent((String)answer));
                addAgent.setState(false);
                this.agentArea = temp;
                this.addAgent.setVisible(false);
            }
            if(addRing.getState()) {
                labels.add(Elements.newGold());
                this.addRing.setState(false);
            }                
            if(this.addPit.getState()) {
                //this.addPit.setState(false);
                labels.add(Elements.newPit());
            }
            if (addGollum.getState()) {
                labels.add(Elements.WUMPUS);
                addGollum.setVisible(false);
                addGollum.setState(false);
                gollumArea = temp;
            }
            if (labels.isEmpty()) {
                if (this.agentArea == temp) {
                    this.agentArea = null;
                    this.addAgent.setVisible(true);
                }
                if (this.gollumArea == temp) {
                    this.gollumArea = null;
                    this.gollumArea.setVisible(true);
                }
            }
            temp.add(labels);            
            this.repaint();            
        }
        else if(c instanceof ImageLabel && c.getParent() instanceof AreaGui)
        {                               
            AreaGui temp = (AreaGui) c.getParent();

            if (temp.getRow() == 0 || temp.getCol() == 0 || temp.getRow() == board.getDimension() - 1 || temp.getCol() == board.getDimension() - 1) {
                return;
            }
            if (addAgent.getState()) {
                Object answer;
                Object selectionValues[] = new Object[4];
                selectionValues[0] = "North";
                selectionValues[1] = "South";
                selectionValues[2] = "West";
                selectionValues[3] = "East";
                answer = (JOptionPane.showInputDialog(this, "Choose agents`s direction :", "Direction ‼", JOptionPane.PLAIN_MESSAGE, null, selectionValues, selectionValues[0]))/*.toString()*/;
                if(answer == null )return;
                if(answer.equals("North")) this.initialDirection = 1;
                if(answer.equals("South")) this.initialDirection = 3;
                if(answer.equals("West")) this.initialDirection = 4;
                if(answer.equals("East")) this.initialDirection = 2;                

                labels.add(Elements.getAgent((String)answer));
                addAgent.setState(false);
                this.agentArea = temp;
                this.addAgent.setVisible(false);
            }
            if (addRing.getState()) {
                if (this.agentArea == temp && !this.addAgent.getState()) {
                    this.agentArea = null;
                    this.addAgent.setVisible(true);
                }
                if (this.gollumArea == temp && !this.addGollum.getState()) {
                    this.gollumArea = null;
                    this.addGollum.setVisible(true);
                }
                labels.add(Elements.newGold());
            }
            if (this.addPit.getState()) {
                if (this.agentArea == temp && !this.addAgent.getState()) {
                    this.agentArea = null;
                    this.addAgent.setVisible(true);
                }
                if (this.gollumArea == temp && !this.addGollum.getState()) {
                    this.gollumArea = null;
                    this.addGollum.setVisible(true);
                }
                labels.add(Elements.newPit());
            }
            if (addGollum.getState()) {
                labels.add(Elements.WUMPUS);
                addGollum.setVisible(false);
                addGollum.setState(false);
                gollumArea = temp;
            }
            if (labels.isEmpty()) {
                if (this.agentArea == temp) {
                    this.agentArea = null;
                    this.addAgent.setVisible(true);
                }
                if (this.gollumArea == temp) {
                    this.gollumArea = null;
                    this.addGollum.setVisible(true);
                }
            }
            temp.add(labels);
            this.repaint(); 
        }        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }
 
}
