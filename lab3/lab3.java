import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.Scanner;
import java.util.ArrayList;


//Decodes input and jumps to functions dtLearn and adaBoost

public class lab3 {
    //constants to tune algorithms
    private static final int dtLearnDepth = 5;
    private static final int numberOfBoosts = 100;
    private static final int numberOfStumps = 100;

    public static void main(String[] args) {
        boolean train = args[0].toLowerCase().equals("train");
        boolean predict = args[0].toLowerCase().equals("predict");
        boolean hw3 = args[0].toLowerCase().equals("hw3");

         // Acceptable functions are train, predict, and hw3 
         if(!train && !predict && !hw3) {
            System.err.println("Invalid command");
            System.exit(-1);
         }

         if(train) {
            if(args.length != 4) {
               System.err.println("Usage: train <examples> <features> <hypothesisOut> <learning-type>");
               System.exit(-1);
            }

            train(new File(args[1]),new File(args[2]),args[3],args[4]);

         } else if (predict) {
            if(args.length != 3) {
               System.err.println("Usage: predict <examples> <features> <hypothesis>");
               System.exit(-1);
            }

            predict(new File(args[1]),new File(args[2]),new File(args[3]));

         } else if (hw3) {
            if(args.length != 1) {
               System.err.println("Usage: hw3 <testFile>");
               System.exit(-1);
            }

            hw3(new File(args[0]));

         } else {
            System.err.println("Unexpected conditional behavior");
            System.exit(-1);
         }
    }

   /**
    * train() - Trains and saves a model of a given learning type. 
    * Most of the heavy lifting is done inside adaBoost and dtLearn, but this instantiates them
    * and starts the process of their construction.
    * This function finishes by saving the dtLearn session to hypothesisOut
    * @param file testfile
    */
   public static void train(File examples, File features, String hypothesisOut, String learningType) {
      //TODO: if statement for learning type and calls to dtLearn and adaBoost.

      //also handle writeback
   }


   /**
    * HW3() - Constructs a decision tree from a 
    * single testfile (following the given format)
    * but does not predict on it
    * This tree will instead be printed to std out using ASCII visualization.
    * @param file testfile
    */
   public static void hw3(File testFile) {
      ArrayList<observation> knowledge = new ArrayList<>();
      String[] attributes = {};

      try {
         Scanner scanny = new Scanner(testFile);
         String line = scanny.nextLine();
         attributes = line.substring(0,line.lastIndexOf('y')).split(" ");
         
         while(scanny.hasNext()) {
            line = scanny.nextLine();
            String[] entries = line.split(" ");

            //build an observation from a line of input
            observation o = new observation(entries[entries.length - 1]);
            for(int i = 0; i < attributes.length; i++) {
               if(entries[i].equals("T")) {
                  o.addAttribute(attributes[i],true);
               } else{
                  o.addAttribute(attributes[i],false);
               }
            }

            //save constructed observation into array as input for dtLearn
            knowledge.add(o); 
         }

      } catch (FileNotFoundException e) {
         System.err.println("Invalid test file address");
         System.exit(-1);
      }

      dtLearn algo = new dtLearn(knowledge, dtLearnDepth);

      //print and exit
      System.out.println(algo);
      System.exit(0);
   }


   /**
    * Predict() - Predict makes a predictaion based on a given learning algorithm object. 
    * Since the entire learning object is seralized it would be trivial to create a "running" 
    * loop that would allow loading and modification of previous sessions and continous command execution.
    * @param file examples
    * @param file features
    * @param file hypothesis
    */
   public static void predict(File examples, File features, File hypothesis)  {
      Object myObject;
      try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(hypothesis))) {
         myObject = ois.readObject();
         switch (myObject) {
              case dtLearn tree -> myObject = tree;
              case adaBoost boost -> myObject = boost;
              default -> {
                  System.err.println("Not a valid object");
                  System.exit(-1);
              }
         }
      } catch(Exception e) {
         System.err.println("There was a problem reading the tree");
         System.exit(-1);
      }

      //TODO: Implement prediction function calls

      //also handle writeback
   }



}
