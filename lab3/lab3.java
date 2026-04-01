
import java.io.File;
import java.nio.file.attribute.FileAttributeView;
import java.util.Scanner;

public class lab3 {
    public static void main(String[] args) {
        boolean train = args[0].toLowerCase().equals("train");
        boolean predict = args[0].toLowerCase().equals("predict");
        boolean hw3 = args[0].toLowerCase().equals("hw3");

        //args must not be less then 3. Acceptable functionsare train, predict, and hw3 
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
               System.err.println("predict <examples> <features> <hypothesis>");
               System.exit(-1);
            }

            predict(new File(args[1]),new File(args[2]),new File(args[3]));

         } else if (hw3) {
            if(args.length != 1) {
               System.err.println("hw3 <testFile>");
               System.exit(-1);
            }

            hw3(new File(args[0]));

         } else {
            System.err.println("Unexpected conditional behavior");
            System.exit(-1);
         }
    }

    public static void train(File examples, File features, String hypothesisOut, String learningType) {

    }

    public static void predict(File examples, File features, File hypothesis) {

    }

    public static void hw3(File testFile) {

    }
    
}
