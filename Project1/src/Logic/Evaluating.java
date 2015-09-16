/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import FileParser.FileParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import org.pi4.locutil.GeoPosition;

/**
 *
 * @author Pere
 */
public class Evaluating {
    
    private File file;
    
    public Evaluating(File file) {
        this.file = file;
    }
    
    public void scoreNN(File outputFile) throws FileNotFoundException {
        FileParser parser = new FileParser();
        HashMap<GeoPosition,GeoPosition> positions = parser.readToFile(this.file);
        
        ArrayList<DistanceError> errList = new ArrayList();
        
        for (HashMap.Entry<GeoPosition,GeoPosition> position : positions.entrySet()) {
            DistanceError error = new DistanceError();
            error.computeError(position.getKey(), position.getValue());
            errList.add(error);
        }
        
        parser.writeEvaluation(errList,outputFile);
        
    }
    
}
