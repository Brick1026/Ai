
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class used to build an ensemble of adaStumps. 
 */
public class adaBoost implements Serializable {
    private ArrayList<adaStump> ensemble = new ArrayList<>();

    public adaBoost(File examples, File features, int boosts) {
        //TODO: Implement
    }

    public void storeEnsemble(String dest) {
        //TODO: Implement
    }

    public static adaBoost loadEnsemble(String dest) {
        //TODO: Implement
        return null;
    }

}