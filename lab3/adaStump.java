
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A stump is a weighted decision tree. 
 */
public class adaStump extends decisionTree implements Serializable {
    
    private double weight = 1;

    public adaStump(ArrayList<observation> knowledge) {
        super(knowledge);
    }
    
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
