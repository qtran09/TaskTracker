/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author qtran
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //Make a timestamp here
        File properties = new File("properties.txt");
        if(!properties.exists())
            properties.createNewFile();
        File log;
        log = new File("log.txt");
        if(!log.exists())
            writeToFile(log,"","TASK LOGGER");       
        ArrayList<String> props = readProperties(properties);
        HashMap<String, Integer> CURRENTPROCESSES = new HashMap<>();
        HashMap<String,Integer> ALLPROCESSES;
        String process;
        
        
		writeToFile(log,getTimestamp(), "PC[ON]");	
        //Add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
        	public void run() {
        		try {
					writeToFile(log,getTimestamp(), "PC[OFF]");
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        });
        while(true){
            ALLPROCESSES = getProcesses();
            for(int i=0;i<props.size();i++){
                process = props.get(i);
                if(ALLPROCESSES.get(process) != null //Desired process is running
                        && CURRENTPROCESSES.get(process) == null){ //It wasn't running before
                    //Write down a timestamp
                    writeToFile(log,getTimestamp(), process + "[ON]");
                    CURRENTPROCESSES.put(process, 0); //Add to list of current processes
                
                }
                else if(ALLPROCESSES.get(process) == null //Desired process isn't running
                        && CURRENTPROCESSES.get(process) != null){ //It was running before
                    //Write down a timestamp
                    writeToFile(log,getTimestamp(), process + "[OFF]");
                    CURRENTPROCESSES.remove(process);
                }
            }
        }
    }
    
    //Timestamp returned as a string
    public static String getTimestamp() {
    	return "[" + new SimpleDateFormat("MM/dd/yyyy --> HH:mm:ss").format(new Date()) + "] ";
    }
    
    //Scan for processes to look for :  ArrayList<String>
    public static ArrayList<String> readProperties(File file) throws IOException{
        BufferedReader scan = new BufferedReader(new FileReader(file));
        ArrayList<String> LIST = new ArrayList<>();
        String line;
        while((line = scan.readLine()) != null){
            LIST.add(line);
        }
        scan.close();
        return LIST;
    }
    //Get list of processes : HashMap<String, Integer>
    public static HashMap<String,Integer> getProcesses() throws IOException{
        HashMap<String,Integer> HM;
        HM = new HashMap<>();
        Process p;
        BufferedReader br;
        p = Runtime.getRuntime().exec(System.getenv("windir")+"\\system32\\" + "tasklist.exe");
        br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while((line = br.readLine()) != null){
            if((line = line.split(" ")[0]).contains(".exe")){
                HM.put(line,1);
            }
        }
        return HM;
    }
    //Is process to scan for in the list of processes?
    
    //  If yes, was it already in there?
    //      Yes --> Do nothing
    //      No --> Add to it
    //  If no, was it already in there?
    //      Yes --> Remove from list, put as stopped process
    //      No --> Do nothing
    //Write to a file
    public static void writeToFile(File file, String time, String text) throws IOException{
        if(!file.exists())
            file.createNewFile();
        FileWriter fw;
        fw = new FileWriter(file,true);
        fw.write(time + text+System.lineSeparator());
        
        fw.close();
    }
}