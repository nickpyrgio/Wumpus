package GUI;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

class ImageLabel extends JLabel {

   private Image image;
   private ImageIcon imgIcon;
   private String filename;

   // constructor with filename    

   ImageLabel(String filename) {

      this.filename = filename;
      imgIcon = new ImageIcon(filename);
      image = imgIcon.getImage();
   } 

   // constructor with icon
   ImageLabel(){
       
   }

   ImageLabel(ImageIcon icon) {

      imgIcon = icon;
      image = imgIcon.getImage();     
   }
               
   public String getPath()
   {
       return this.filename;
   }
   public Image getImgIcon()
   {
       return image;
   }
   
    @Override
   public boolean equals(Object o){
       if(o instanceof ImageLabel){
           ImageLabel obj = (ImageLabel)o;
           if(this.filename.equalsIgnoreCase(obj.filename))
               return true;
       }
       return false;
   }
 
   // overload paint()

    @Override
   public void paint( Graphics g ) {
       super.paint( g);
       g.drawImage(image,  0 , 0 , this.getWidth() , getHeight() , null);       
   }
        
}
