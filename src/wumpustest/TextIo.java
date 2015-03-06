package wumpustest;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * <H2>Text class overview</H2> <br>
 * Η κλάσση TextIo περιλάμβάνει μεθόδους διαχείρισης  αρχείων τυπου txt. 
 * Περιέχει μεθόδους:
 * <ul> 
 * <li> διαχείρισης πιθανών λαθών που μπορεί να εμφανισθούν
 * <li> αρμόδιες να γράφουν  και να διαβάζουν από αρχείο
 * </ul>
 *
 */
public class TextIo {

    private static int errNo;//error number
    private static String errMsg;//error message
    private static String eol = "\r\n";   //End Of Line
    /**
     * Η μέθοδος errClear έχει private πρόσβαση στη κλάσση TextIo
     * Αρχικοποιεί τα πεδία της TextIo
     * <ul>
     * <li> errNo = 0
     * <li> errMsg = ""
     */
    private static void errClear()
    {
        errNo = 0;
        errMsg = "";
    }
     /**
     * 
     * @return Επιστρέφει  true αν  έγινε κάποιο λάθος αλλιώς επιστρέφει false.
     *
     */
    public static boolean errFound()
    {
        return (errNo != 0);
    }
    /**
     * 
     * @return Επιστρέφει  έναν  int που αναπαριστά τον αριθμό του λάθους(error) που συνέβη. Αν δε συνέβη λάθος επιστρέφει 0.
     */
    
    public static int getErrNo()
    {
        return errNo;
    }
    /**
     * 
     * @return Επιστέφει μια String με το λόγο που συνέβη το λάθος(error).Αν δε συνέβη λάθος επιστρέφει ""
     */
    public static String getErrMsg()
    {
        return errMsg;
    }

    /**
     * 
     * @param fileName String που περιέχει το path του αρχείου στο οποίο θα γράψει.Άν δεν υπάρχει το αρχείο το δημιουργεί
     * @param text String που περιέχει τη String που θα γράψει στο αρχείο.
     * @param append Αν είναι false σβήνει τις παλαιότερες εγγραφές και γράφει από την αρχή.Αλλιώς τις προσθέτει στο τέλος 
     * @return true αν δε συνέβη κάποιο λάθος
     * @throws IOException 
     */
    public static boolean write(String fileName, String text, boolean append) throws IOException
    {
        PrintWriter outputStream = null;
        boolean success = true;
        String dataLines[];                 
        boolean lastIndexOfEol = false;

        errClear();
        try
        {
            outputStream = new PrintWriter((new BufferedWriter (new FileWriter(fileName, append))));           
            dataLines = text.split(eol);
            if(text.lastIndexOf(eol)== text.length() - 1)                
                lastIndexOfEol = true;         
            for(int i = 0; i < dataLines.length;i++)     
            {
                if(i == dataLines.length - 1 && !lastIndexOfEol) outputStream.print(dataLines[i]);
                else outputStream.println(dataLines[i]);                
            }
            
        }
        catch (IOException err)
        {
            errNo = 1;
            errMsg = "Text write error: " + eol + err.getMessage();
            success = false;
        }
        finally
        {
            if(outputStream!= null)outputStream.close();
        }
        return success;
    }//End save
    /**
     * 
     * @param fileName String που αναπαριστά το path του αρχείου που θα διαβάσει.Αν δεν υπάρχει throws FileNotFoundException
     * @return Μία String που περιέχει όλο το περιεχόμενο του αρχείου 
     * @throws IOException 
     */
    public static String read(String fileName) throws IOException
    {
        BufferedReader inputStream = null;
        String dataLine;
        String buffer = "";        

        errClear();
        try
        {
            inputStream = new BufferedReader(new FileReader(fileName));
            dataLine = inputStream.readLine();
            while(true)
            {
                if(dataLine == null) break;
                buffer += dataLine + eol;
                dataLine = inputStream.readLine();                
            }
            buffer = buffer.substring(0, buffer.lastIndexOf(eol));            
        }        
        catch (FileNotFoundException err)
        {
            errNo = 2;
            errMsg = "Could not find File: " + fileName + eol + err.getMessage();            
        }
        catch (IOException err)
        {
            errNo = 3;
            errMsg = "Text read error: " + eol + err.getMessage();            
        }
        finally
        {
            if (inputStream != null) inputStream.close();             
        }
        return buffer;
    }
    
   

}//END CLASS TextIo
