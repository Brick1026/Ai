
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class used to build an ensemble of adaStumps. 
 */
public class adaBoost implements Serializable, predictable {
    //this is the ensemble (output of adaboost)
    private ArrayList<adaStump> output = new ArrayList<>();

    public adaBoost(ArrayList<observation> knowledge, int numberOfStumps) {
        //TODO: Implement
    }

     /**
     * predict() - Makes a predication based on a decision tree.
     * @param observation example
     * @return the example observation with an assigned predction
     */
    public observation predict(observation example) {
        //TODO: Implement
        return null;
    }

}