
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class used to build an ensemble of adaStumps. 
 */
public class adaBoost implements Serializable, predictable {
    //this is the ensemble (output of adaboost)
    //note A-> +1 and B -> -1
    private ArrayList<adaStump> output = new ArrayList<>();

    public adaBoost(ArrayList<observation> knowledge, int numberOfStumps) {

        for(int i = 0; i < numberOfStumps; i++) {
            //build one level decision tree
            adaStump newStump = new adaStump(knowledge);
            newStump.train();
            newStump.markLeaves();
            double weightedErr = 0.0;
            boolean[] isCorrect = new boolean[knowledge.size()];

            for(int j = 0; j < knowledge.size(); j++) {
                observation curObservation = knowledge.get(j);
                //get to a leaf
                while(newStump.hasNext()) {
                    //branch given our knowledge
                    newStump.branch(curObservation);
                }
                
                //check classification from our tree versus expected
                if(!newStump.currentLabel().equals(curObservation.getLabel())) {
                    weightedErr += curObservation.getWeight();
                    isCorrect[j] = false;
                } else {
                    isCorrect[j] = true;
                }
    
                //reset tree walk
                newStump.resetTraversal();
            }

            
            //compute stump weight
            double stumpWeight = .5*Math.log((1-weightedErr)/weightedErr);

            newStump.setWeight(stumpWeight);

            //update observation weights
            double sumOfNewWeights = 0.0;
            for(int j = 0; j < knowledge.size(); j++) {
                observation curObservation = knowledge.get(j);
                double curWeight = curObservation.getWeight();
                if(isCorrect[j]) {
                    curObservation.setWeight(curWeight*Math.pow(Math.E,stumpWeight*-1.0));
                    sumOfNewWeights += knowledge.get(j).getWeight();
                } else {
                    curObservation.setWeight(curWeight*Math.pow(Math.E,stumpWeight));
                    sumOfNewWeights += knowledge.get(j).getWeight();
                }
            }
  
            //TODO: Make sure weights sum to 1
             //System.out.println("Sum of new weights: " + sumOfNewWeights);   

            // normalize
            for(observation o : knowledge) {
                o.setWeight(o.getWeight()/sumOfNewWeights);
            }

            output.add(newStump);
        }
    }

     /**
     * predict() - Makes a predication based on a decision tree.
     * @param observation example
     * @return the example observation with an assigned predction
     */
    public observation predict(observation example) {
        int englishCount = 0;
        int dutchCount = 0;
        for(adaStump s : output) {
             //get to a leaf
            while(s.hasNext()) {
                s.branch(example);
            }

            //convert to external representation
            if(s.currentLabel().equals("A")) {
                englishCount++;
            } else {
                dutchCount++;
            }
            //reset traversal
            s.resetTraversal();
        }

        if(englishCount > dutchCount) {
            example.setLabel("en");
        } else {
            example.setLabel("nl");
        }
        return example;
    }
}