
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

//Decision tree learning algorithm instance.

public class dtLearn implements Serializable, predictable {
    private decisionTree output;

    public dtLearn(ArrayList<observation> knowledge, int iterations) {
        output = new decisionTree(knowledge);
        for(int i = 0; i <= iterations; i++) {
            output.train();
        }
        output.markLeaves();
    }

    /**
     * predict() - Makes a predication based on a decision tree.
     * @param observation example
     * @return the example observation with an assigned predction
     */
    public observation predict(observation example) {
        
        
        example.setLabel("unknown");
        return example;
    }
    
    @Override
    public String toString() {
        return output.toString();
    }

}