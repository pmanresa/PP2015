/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import FileParser.FileParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pi4.locutil.GeoPosition;
import org.pi4.locutil.MACAddress;
import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.Parser;
import org.pi4.locutil.trace.SignalStrengthSamples;
import org.pi4.locutil.trace.TraceEntry;


/**
 *
 * @author Pere
 */
public class Positioning {
    
    // Offline and online measurement files
    private static final String OFFLINE_PATH = "src/data/MU.1.5meters.offline.trace";
    private static final String ONLINE_PATH = "src/data/MU.1.5meters.online.trace";
    
    // Offline and online sample sizes per true position
    private static final int OFFLINE_SAMPLE_SIZE = 10;
    private static final int ONLINE_SAMPLE_SIZE = 5;
    
    private TraceGenerator tg;
    
    
    public Positioning() throws IOException {
        Parser offlineParser = new Parser(new File(OFFLINE_PATH));
        Parser onlineParser = new Parser(new File(ONLINE_PATH));
        tg = new TraceGenerator(offlineParser,onlineParser,OFFLINE_SAMPLE_SIZE,ONLINE_SAMPLE_SIZE);
    }
    
    /*
    Empirical_FP_KNN implements fingerprinting-based k-nearest neighbors. 
    Its outcome will be written to disk with a line for each estimated position along with the 
    true position for that estimate.
    k parameter indicates de k-nearest neighbors that we will use for the measurement.
    file parameter indicates the file to write the output to.
    */
    public void empirical_FP_KNN(int k, File file) throws FileNotFoundException {
        
        // Getting offline and online trace entries
        tg.generate();
        List<TraceEntry> offlineTraceEntries = tg.getOffline();
	List<TraceEntry> onlineTraceEntries = tg.getOnline();
        
        
        // Obtain the joint signal strengths for the traces
        HashMap<GeoPosition, SignalStrengthSamples> jointSSOffline = getJointSS(offlineTraceEntries);
        HashMap<GeoPosition, SignalStrengthSamples> jointSSOnline = getJointSS(onlineTraceEntries);
        
        // Compute the k-nearest neighbors
        // For each position in the online trace we have to compare such position with all the offline 
        // measurements. Therefore, the best position match will be computed to get the distance.
        HashMap<GeoPosition, GeoPosition> outputRadioMap = new HashMap();
        
        for (HashMap.Entry<GeoPosition, SignalStrengthSamples> onlinePos : jointSSOnline.entrySet()) {
            
            ArrayList<Neighbor> neighbors = new ArrayList();
            
            // We obtain every single neighbor that the online position has, storing their distance between such a position.
            for (HashMap.Entry<GeoPosition, SignalStrengthSamples> offlinePos : jointSSOffline.entrySet()) {
                double dist = getEuclideanDistSS(onlinePos.getValue(), offlinePos.getValue());
                neighbors.add(new Neighbor(offlinePos.getKey(),dist));
            }
            
            // We compute the K-nearest neighbor algorithm
            if (k > neighbors.size())
                throw new IllegalArgumentException("k must be smaller than the number of neighbors.");
            
            // Sort the list and average over the k first members
            Collections.sort(neighbors);
            double x = 0.0;
            double y = 0.0;
            for (int i = 0; i < k; i++) {
                x += neighbors.get(i).getPosition().getX();
                y += neighbors.get(i).getPosition().getY();
            }
            GeoPosition estimation = new GeoPosition(x/k, y/k);
            
            outputRadioMap.put(onlinePos.getKey(), estimation);
        }
        FileParser parser = new FileParser();
        parser.writeToFile(outputRadioMap, file);
        
    }
    
    /*
    Empirical_FP_NN implements fingerprinting-based nearest neighbors. 
    Its outcome will be written to disk with a line for each estimated position along with the 
    true position for that estimate.
    file parameter indicates the file to write the output to.
    */
    
    public void empirical_FP_NN(File file) throws FileNotFoundException {
        
        // Getting offline and online trace entries
        tg.generate();
        List<TraceEntry> offlineTraceEntries = tg.getOffline();
	List<TraceEntry> onlineTraceEntries = tg.getOnline();

        //
        // Model based method of RADAR without WAF
        // P(d)[dBm] = P(d0)[dBm] - 10 * n * log(d/d0)

        // We now make a Hashmap to replace each entry
        hashmap = new HashMap<MACAddress, Double>();

        for (TraceEntry offline : offlineTrace) {

            for (MACAddress mac : offline.getSignalStrengthSamples().keySet()) {

                // We now compute the signal strength via the propagation model
                // from RADAR
                double pD = pd0 - 10 * n * Math.log10(offline.getGeoPosition().distance(APPosition(mac)) / d0);
                hashmap.put(mac, pD);
            }

            for (MACAddress mac : hashmap.keySet()) {
                // We now replace each signal strength in the offline set by the computed
                offline.getSignalStrengthSamples().remove(mac);
                offline.getSignalStrengthSamples().put(mac, hashmap.get(mac));

            }
            //hashmap.clear();
        }


        //

        // Obtain the joint signal strengths for the traces
        HashMap<GeoPosition, SignalStrengthSamples> jointSSOffline = getJointSS(offlineTraceEntries);
        HashMap<GeoPosition, SignalStrengthSamples> jointSSOnline = getJointSS(onlineTraceEntries);
        
        // Compute the k-nearest neighbors
        // For each position in the online trace we have to compare such position with all the offline 
        // measurements. Therefore, the best position match will be computed to get the distance.
        HashMap<GeoPosition, GeoPosition> outputRadioMap = new HashMap();
        
        for (HashMap.Entry<GeoPosition, SignalStrengthSamples> onlinePos : jointSSOnline.entrySet()) {
            
            ArrayList<Neighbor> neighbors = new ArrayList();
            
            // We obtain every single neighbor that the online position has, storing their distance between such a position.
            for (HashMap.Entry<GeoPosition, SignalStrengthSamples> offlinePos : jointSSOffline.entrySet()) {
                double dist = getEuclideanDistSS(onlinePos.getValue(), offlinePos.getValue());
                neighbors.add(new Neighbor(offlinePos.getKey(),dist));
            }
            
            // We compute the FP nearest neighbor algorithm
            
            Collections.sort(neighbors);
            // As we have sorted the neighbors list by distance, the only thing we have to do is to get the first element of such a list
            GeoPosition estimation = new GeoPosition(neighbors.get(0).getPosition().getX(), neighbors.get(0).getPosition().getY());
            
            outputRadioMap.put(onlinePos.getKey(), estimation);
        }
        FileParser parser = new FileParser();
        parser.writeToFile(outputRadioMap, file);
        
    }
    
    public void model_FP_NN(File file) {
        //To be implemented
    }
    
    public void model_FP_KNN(File file) {
        //To be implemented
    }
    
    
    /*
    In order to construct the proper Radio Map, first we have to iterate over each trace entry 
    and combine it with its proper geoPosition and SignalStrength, so that we will get a tuple of 
    the form E = (x,y,z,s1,s2,...,sn), where n is the number of access points. In other words, 
    E = (geoPosition, SignalStrengthListOfSuchPosition).
    */
    public HashMap<GeoPosition, SignalStrengthSamples> getJointSS (List<TraceEntry> traceEntries) {
        
        HashMap<GeoPosition, SignalStrengthSamples> jointSS = new HashMap();
        
        // Iterate over each trace entry and assign its signal strength to its GeoPosition
        for (TraceEntry traceEntry : traceEntries) {
            boolean inHashMap = false;
            for (GeoPosition pos : jointSS.keySet()) {
                if (traceEntry.getGeoPosition().equalsWithoutOrientation(pos)) {
                    inHashMap = true;
                    jointSS.get(pos).add(traceEntry.getSignalStrengthSamples());
                    break;
                }
            }
            // If it isn't in the HashMap, we add it to it
            if (!inHashMap) {
                jointSS.put(traceEntry.getGeoPosition(),traceEntry.getSignalStrengthSamples());
            }
        }
        return jointSS;
    }
    
    public double getEuclideanDistSS(SignalStrengthSamples ss1, SignalStrengthSamples ss2) {
        
        double dist = 0.0;
        
        for (MACAddress ss1address : ss1.keySet()) {
            if (ss2.containsKey(ss1address)) {
                dist += Math.pow(ss1.getAverageSignalStrength(ss1address)
                                - ss2.getAverageSignalStrength(ss1address), 2);
            }
        }
        return Math.sqrt(dist);
    }
    
}
