
package project1_pp;

import Logic.Evaluating;
import Logic.Positioning;
import java.io.File;

/**
 *
 * @author Pedro
 */
public class Main {

    public static void main(String[] args) {
        // TODO code application logic here

        File output1 = new File("empirical_FP_NN_output.txt");
        File output2 = new File("empirical_FP_KNN_output.txt");
        
        File score1 = new File("scoreNN.txt");
        File score2 = new File("scoreKNN.txt");
        
        try {
            
            Positioning empiricalFPKNN = new Positioning();
            Positioning empiricalFPNN = new Positioning();
            
            empiricalFPKNN.empirical_FP_NN(output1);
            empiricalFPNN.empirical_FP_KNN(3, output2);
            
            Evaluating empNNEvaluation = new Evaluating(output1);
            Evaluating empKNNEvaluation = new Evaluating(output2);
            
            empNNEvaluation.scoreNN(score1);
            empKNNEvaluation.scoreNN(score2);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
