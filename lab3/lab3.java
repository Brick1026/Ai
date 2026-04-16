import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


//Decodes input and jumps to functions dtLearn and adaBoost

public class lab3 {
    //constants to tune algorithms
    private static final int dtLearnDepth = 9;
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
            if(args.length != 5) {
               System.err.println("Usage: train <examples> <features> <hypothesisOut> <learning-type>");
               System.exit(-1);
            }

            train(new File(args[1]),new File(args[2]),args[3],args[4]);

         } else if (predict) {
            if(args.length != 4) {
               System.err.println("Usage: predict <examples> <features> <hypothesis>");
               System.exit(-1);
            }

            predict(new File(args[1]),new File(args[2]),new File(args[3]));

         } else if (hw3) {
            if(args.length != 2) {
               System.err.println("Usage: hw3 <testFile>");
               System.exit(-1);
            }

            hw3(new File(args[1]));

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
      ArrayList<String[]> strExamples = new ArrayList<>();
      ArrayList<String> strFeatures = new ArrayList<>();
      ArrayList<observation> knowledge = new ArrayList<>();
      boolean isDt = learningType.toLowerCase().equals("dt");
      boolean isAda = learningType.toLowerCase().equals("ada");

      Scanner scanny;
      //read in all of the examples as String[] = {label, line}
      try {
         scanny = new Scanner(examples);
         while(scanny.hasNext()) {
            String line = scanny.nextLine();
            strExamples.add(line.split("\\s*\\|\\s*",2));
         }
      } catch (FileNotFoundException e) {
         System.err.println("Invalid example file address");
         System.err.println(e.getStackTrace());
         System.exit(-1);
      }

      //read in all featuers
      try {
         scanny = new Scanner(features);
         while(scanny.hasNext()) {
            strFeatures.add(scanny.nextLine());
         }
      } catch (FileNotFoundException e) {
         System.err.println("Invalid features file address");
         System.err.println(e.getStackTrace());
         System.exit(-1);
      }

      //determine how weights should be initialized
      double INIT_WEIGHT;
      if(isDt) {
         INIT_WEIGHT = 1.0;
      } else if(isAda) {
         INIT_WEIGHT = 1.0/strExamples.size();
      } else {
         INIT_WEIGHT = -1;
         System.err.print("Not a valid learning type");
         System.exit(-1);
      }

      //Loop through each example line for each features to flag it and save it as an observation
      for(String[] s : strExamples) { //for each example
         observation o = null;

         if(s[0].equals("en")) {
            o = new observation("A", INIT_WEIGHT);
         } else {
            o = new observation("B", INIT_WEIGHT);
         }

         for(String feat : strFeatures) { //check if it contains each feature
            String clean = s[1].toLowerCase().replaceAll("[^a-z0-9\\s]", " ").trim();
            o.addAttribute(feat, Arrays.asList(clean.split("\\s+")).contains(feat));
         }
         knowledge.add(o); // add finished observation
      }

      Object algo;
      if(isDt) {  //initialize a session of learning
         algo = new dtLearn(knowledge, dtLearnDepth); 
      } else {
         algo = new adaBoost(knowledge, numberOfStumps); 
      }

      try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(hypothesisOut))) {
         oos.writeObject(algo); //save the dtlearning session
      } catch(IOException e) {
         System.err.println("not a valid output file or obj not serializable");
         System.err.println(e.getStackTrace());
         System.exit(-1);
      }
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
         attributes = line.substring(0,line.lastIndexOf('y')).split("\\s+");
         
         while(scanny.hasNext()) {
            line = scanny.nextLine();
            String[] entries = line.split("\\s+");

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
        //System.out.println(knowledge.size());

      } catch (FileNotFoundException e) {
         System.err.println("Invalid test file address");
         System.err.println(e.getStackTrace());
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
      //read all examples as unlabeled observations
      ArrayList<String> strExamples = new ArrayList<>();
      ArrayList<String> strFeatures = new ArrayList<>();
      ArrayList<observation> newlyLabeled = new ArrayList<>();
      
      //get the tree
      Object myObject = null;
      try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(hypothesis))) {
         myObject = ois.readObject();
      } catch(Exception e) {
         System.err.println("There was a problem reading the tree");
         System.err.println(e.getStackTrace());
         System.exit(-1);
      }

      predictable pred = (predictable) myObject; //the tree for predicting

      Scanner scanny;
      //read in all of the examples as String[] = {line}
      try {
         scanny = new Scanner(examples);
         while(scanny.hasNext()) {
            strExamples.add(scanny.nextLine());
         }
      } catch (FileNotFoundException e) {
         System.err.println("Invalid example file address");
         System.err.println(e.getStackTrace());
         System.exit(-1);
      }

      //read in all featuers
      try {
         scanny = new Scanner(features);
         while(scanny.hasNext()) {
            strFeatures.add(scanny.nextLine());
         }
      } catch (FileNotFoundException e) {
         System.err.println("Invalid features file address");
         System.err.println(e.getStackTrace());
         System.exit(-1);
      }

      //Loop through each example line for each features to flag it and label observation
      for(String s : strExamples) { //for each example
         //build an observation 
         observation o = new observation();
         for(String feat : strFeatures) { //check if it contains each feature
            String clean = s.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").trim();
            o.addAttribute(feat, Arrays.asList(clean.split("\\s+")).contains(feat));
         }
         newlyLabeled.add(pred.predict(o)); //will assign a label to the observation inside of predict
      }
      
      //ENABLE IF YOU WANT TO SEE THE TREE
      //System.out.println("MY TREE:\n" + pred.toString() + "\n");

      final double TEST_SET_SIZE = 100;
      double incorrect = 0;
      int number = 0;
      //loop through and print the determined labels 
      for(observation o : newlyLabeled) {
         if(number < TEST_SET_SIZE/2.0 && o.getLabel().equals("nl")) {
            incorrect++;
         } 
         if(number >= TEST_SET_SIZE/2.0 && o.getLabel().equals("en")) {
            incorrect++;
         } 
         System.out.println(o.getLabel());
         number++;
      }

      //Enable if you want to see accuracy
      // System.out.println("Number Incorrect: " + incorrect);
      // System.out.println("Accuracy: " + (TEST_SET_SIZE-incorrect)/TEST_SET_SIZE*100 + "%");
   }

}
