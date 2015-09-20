
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
        File output3 = new File("modelbased_FP_NN_output.txt");
        File output4 = new File("modelbased_FP_KNN_output.txt");
        
        File score1 = new File("score_ENN.txt");
        File score2 = new File("score_EKNN.txt");
        File score3 = new File("score_MBNN.txt");
        File score4 = new File("score_MBKNN.txt");
        
        try {
            
            Positioning empiricalPositioning = new Positioning();
            Positioning modelbasedPositioning = new Positioning();
            
            empiricalPositioning.empirical_FP_KNN(1,output1);
            empiricalPositioning.empirical_FP_KNN(3, output2);
            
            modelbasedPositioning.model_FP_KNN(output3, 1, -33.77, 3.415, 1.0);
            modelbasedPositioning.model_FP_KNN(output4, 3, -33.77, 3.415, 1.0);
            
            Evaluating empNNEvaluation = new Evaluating(output1);
            Evaluating empKNNEvaluation = new Evaluating(output2);
            Evaluating mbNNEvaluation = new Evaluating(output3);
            Evaluating mbKNNEvaluation = new Evaluating(output4);
            
            empNNEvaluation.scoreNN(score1);
            empKNNEvaluation.scoreNN(score2);
            mbNNEvaluation.scoreNN(score3);
            mbKNNEvaluation.scoreNN(score4);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
