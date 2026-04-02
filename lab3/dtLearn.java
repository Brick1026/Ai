
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

//Decision tree learning algorithm instance.

public class dtLearn implements Serializable {
    private decisionTree output;

    public dtLearn(ArrayList<observation> knowledge, int iterations) {
        output = new decisionTree(knowledge);
        for(int i = 0; i <= iterations; i++) {
            output.train();
        }
    }

    /**
     * predict() - Makes a predication based on a decision tree.
     */
    public boolean predict(ArrayList<observation> examples) {
        //TODO: Implement
        return false;
    }
    
    @Override
    public String toString() {
        return output.toString();
    }

}