/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileParser;

import Logic.DistanceError;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.pi4.locutil.GeoPosition;

/**
 *
 * @author Pere
 */
public class FileParser {
    
    private BufferedWriter bw;
    private BufferedReader br;
    
    public FileParser() throws FileNotFoundException {
        bw = null;
        br = null;
    }
    
    public void writeToFile(HashMap<GeoPosition,GeoPosition> outputRadioMap, File file) {
        
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
            bw = new BufferedWriter(osw);
            
            int i = 0;
            for (Map.Entry<GeoPosition, GeoPosition> outputEntry : outputRadioMap.entrySet()) {
                bw.write("True=" + outputEntry.getKey().getX() + ","
                        + outputEntry.getKey().getY() + ","
                        + outputEntry.getKey().getZ() + ";Est="
                        + outputEntry.getValue().getX() + ","
                        + outputEntry.getValue().getY() + ","
                        + outputEntry.getValue().getZ());
                i++;
                if (i < outputRadioMap.size()) {
                    bw.newLine();
                }
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
    }
    
    public void writeEvaluation(ArrayList<DistanceError> errList, File file) {
        // This method evaluates the given error values list by creating a cumulative distribution function of such a list.
        
        // Sort the error values (lowest to highest error)
        Collections.sort(errList);
        
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
            bw = new BufferedWriter(osw);
            
            int i=1;
            for (DistanceError err : errList) {
                //Cumulative Distribution Function algorithm
                double percentage = (double) i / errList.size();
                bw.write("err=" + err.getErr() + ";percentage=" + percentage);
                i++;
                if (i <= errList.size()) 
                    bw.newLine();
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
    }
    
    public HashMap<GeoPosition, GeoPosition> readToFile(File file) {
        
        HashMap<GeoPosition, GeoPosition> output = null;
        
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            output = new HashMap();

            // Iterate over each line in the file
            while ((line = br.readLine()) != null) {
                String[] positionEntries = line.split(";");
                
                // Attempt to parse the positions
                GeoPosition truePosition = GeoPosition.parse(positionEntries[0].split("=")[1]);
                GeoPosition estPosition = GeoPosition.parse(positionEntries[1].split("=")[1]);
                
                // Add entry to output map
                output.put(truePosition, estPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
        return output;
    }
}
