package GUI;



import java.io.File;

import javax.swing.filechooser.FileFilter;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nikos
 */
public class Filter extends FileFilter 
{
     public final static String txt = "wumpus";

    @Override
    public boolean accept(File pathname) { 
    if (pathname.isDirectory()) 
    {
        return true;
    }
    
    String extension = Filter.getExtension(pathname);
    if(extension!=null)
    {
        if(extension.equals(txt))
        {
            return true;
        }
    }
    return false;
    }
    
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    @Override
    public String getDescription() {
        return "Accepted Files: .wumpus";
    }
}
