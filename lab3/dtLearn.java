import java.io.Serializable;
import java.util.ArrayList;

//Decision tree learning algorithm instance.

public class dtLearn implements Serializable, predictable {
    private decisionTree output;

    /**
     * Preform n iterations of learning on a tree. When that tree is done mark its leaves.
     * @param knowledge the knowledge to use for training
     * @param iterations the number of iterations of learning
     */
    public dtLearn(ArrayList<observation> knowledge, int iterations) {
        output = new decisionTree(knowledge);
        for(int i = 0; i < iterations; i++) {
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
        //get to a leaf
        while(output.hasNext()) {
            output.branch(example);
        }

        //convert to external representation
        if(output.currentLabel().equals("A")) {
            example.setLabel("en");
        } else {
            example.setLabel("nl"); 
        }

        //return the modified example
        output.resetTraversal();
        return example;
    }
    
    @Override
    public String toString() {
        return output.toString();
    }

}