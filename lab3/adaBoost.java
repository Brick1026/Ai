
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class used to build an ensemble of adaStumps. 
 */
public class adaBoost implements Serializable {
    //this is the ensemble (output of adaboost)
    private ArrayList<adaStump> output = new ArrayList<>();

    public adaBoost(ArrayList<observation> knowledge, int boosts, int numberOfStumps) {
        //TODO: Implement
    }

    public void predict(ArrayList<observation> examples) {
        //TODO implement
    }

}