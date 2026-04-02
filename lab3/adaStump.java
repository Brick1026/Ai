
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A stump is a weighted decision tree. 
 */
public class adaStump extends decisionTree implements Serializable {
    
    private double weight = 1;

    public adaStump(ArrayList<observation> knowledge, String[] attributes) {
        super(knowledge);
    }
    
    public double getWeight() {
        //TODO: Implement
        return weight;
    }

    public void setWeight(double weight) {
        //TODO: Implement
        this.weight = weight;
    }

}
