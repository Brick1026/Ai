
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class lab1 {

    public static void main(String[] args) {
        BufferedImage myImage = null;
        File elevationFile = null;
        File pathFile = null;
        try {
            myImage = ImageIO.read(new File(args[0]));
            elevationFile = new File(args[1]);
            pathFile = new File(args[2]);
        } catch (FileNotFoundException e) {
            System.err.println("Invalid File");
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Image read failure");
            System.exit(-1);
        }

        if (myImage == null)  {
            System.err.println("Invalid image file format.");
            System.exit(-1);
        }

        //arraylist of coordinate pairs
        ArrayList<int[]> cords = new ArrayList<>();
        try {
            try (Scanner scanny = new Scanner(pathFile)) {
                while(scanny.hasNextLine()) {
                    String line = scanny.nextLine();
                    line = line.trim();
                    String[] words = line.split("\\s+");
                    int[] numbers =  new int[2];
                    for(int i = 0; i < words.length; i++) {
                        numbers[i] = Integer.parseInt(words[i]);
                    }
                    cords.add(numbers);
                }
            } catch (NumberFormatException e) {
                System.err.println("Path file read failure");
                System.exit(-1);
            }
        } catch (IOException e) {
            System.err.println("Path file read failure");
            System.exit(-1);
        }

        Map searchMap = new Map(myImage, elevationFile);
        search(searchMap, cords);

        BufferedImage outputImage = searchMap.convertToImage(); //get output image from map (post search)

        try {
            ImageIO.write(outputImage, "png", new File(args[3])); //write map to file
        } catch (IOException ex) {
            System.err.println("File write failure");
            System.exit(-1);
        }

    }

    /**
     * A* search implementation 
     * @param searchMap Map to traverse
     * @param pointsToVisit ArrayList of 2d coordiantes
     */
    private static void search(Map searchMap, ArrayList<int[]> pointsToVisit) {
        //TODO: Build search algorithm
    }

}


class Map  {
    private int width;
    private int height;
    private Pixel[][] grid;

    /**
     * Constructs a new Map object.
     * @param myImage BufferedImage board/map image
     */
    public Map(BufferedImage myImage,File elevationFile) {
        this.width = myImage.getWidth();
        this.height = myImage.getHeight();
        this.grid = new Pixel[width][height];
        double[][] elevationArray = new double[width][height];

        //Build array of elevation values
        try {
            try (Scanner scanny = new Scanner(elevationFile)) {
                int j = 0;
                while(scanny.hasNextLine()) {
                    String line = scanny.nextLine();
                    line = line.trim();
                    String[] words = line.split("\\s+");
                    for(int i = 0; i < width - 5; i++) {
                        elevationArray[i][j] = Double.parseDouble(words[i]);
                    }
                    j++;
                }
            } catch (NumberFormatException e) {
                System.err.println("Elevation file read error");
                System.exit(-1);
            }
        } catch(FileNotFoundException e) {
            System.err.println("Elevation file read error");
            System.exit(-1);
        }

        //Construct grid of pixels
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                grid[i][j] = new Pixel(elevationArray[i][j],myImage.getRGB(i,j));
            }
        }

    }
    /**
     * Converts the current grid back to an image 
     * @return a BufferedImage of the board
     */
    public BufferedImage convertToImage() {
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                canvas.setRGB(i,j, grid[i][j].getRGB());
            }
        }
        return canvas; 
    }

    // Functions utilizing internal Pixel functions. //

    /**
     * Sets the pixel at (X, Y) in grid to a path internally. Uses setAsPath from Pixel.
     * @param pixelX int x cord
     * @param pixelY int y cord
     */
    public void setPixelToPathAtXY(int pixelX, int pixelY) {
        grid[pixelX][pixelY].setAsPath();
    }

    /**
     * Get the pixel terrain at (X, Y). Uses getTerrain from Pixel.
     * @param pixelX int x cord
     * @param pixelY int y cord
     */
    public int getPixelTerrainAtXY(int pixelX, int pixelY) {
        return grid[pixelX][pixelY].getTerrain();
    }

    /**
     * Get the pixel elevation at (X, Y). Uses getElevation from Pixel.
     * @param pixelX int x cord
     * @param pixelY int y cord
     */
    public double getElevationAtXY(int pixelX, int pixelY) {
        return grid[pixelX][pixelY].getElevation();
    }

    /**
     * Computes h(n) for (x,y). Uses computeHueristicWeight from Pixel.
     * @param pixelX
     * @param pixelY
     * @return
     */
    public int computeHueristicWeightAtXY(int pixelX, int pixelY) {
        return grid[pixelX][pixelY].computeHueristicWeight();
    }

    // Search functions //

    /**
     * Computes hueristic distance to goal in 3D h(n)
     * @param curX int 
     * @param curY int
     * @param curZ double
     * @param destX int
     * @param destY int
     * @param destZ double
     * @return
     */
    private static double computeDistanceToGoalNode(int curX, int curY, double curZ, int destX, int destY, double destZ) {
        return 0; //TODO: implement compution for distance to frontier node
    }

    
    /**
     * Computes distance to frontier node in 3D g(n)
     * @param curX int 
     * @param curY int 
     * @param curZ double
     * @param destX int
     * @param destY int
     * @param destZ double
     * @return
     */
    private static double computeDistanceToFrontierNode(int curX, int curY, double curZ, int destX, int destY, double destZ) {
        return 0; //TODO: implement compution for distance to frontier node
    }

//
//
//
//
//
// Visual spacer
//
//
//
//
    protected class Pixel {
        /*
        Terrain key (in increasing difficulty assumptions):
        0 - Paved Road
        1 - Open Land
        2 - Footpath
        3 - Rough Meadow
        4 - Easy Movement Forest
        5 - Slow Run Forest
        6 - Walk Forest
        7 - Lake/Swamp/Marsh
        8 - OOB or Impassable Vegetation
        9 - Search Path
        */
        private int terrain; //derived from pixelRGB
        private final double elevation; //height of pixel
        private int pixelRGB; //color of pixel

        
        /**
         * Constructs a new Pixel object.
         * @param elevation double elevation
         * @param pixelRGB RGB from getRGB on image fil
         */
        protected Pixel(double elevation, int pixelRGB) {
            this.elevation = elevation;
            this.pixelRGB = pixelRGB;
            this.terrain = computeTerrain(pixelRGB);
        }  

        /**
         * Sets terrain and pixelRGB to a path.
         */
        protected void setAsPath() {
            this.pixelRGB = new Color(177, 86, 237).getRGB();
            this.terrain = computeTerrain(pixelRGB);
        }

        /**
         * Uses terrain key to compute terrain integer
         * @param imageRGB getRGB integer
         * @return int conversion corresponding to terrain key
         */
        protected static int computeTerrain(int imageRGB) {
            Color c = new Color(imageRGB);
            int blue = c.getBlue();
            int red = c.getRed();
            int green = c.getGreen();
            if (red == 71 && blue == 51 && green == 3) {
                return 0; //paved road
            } else if (red == 248 && blue == 148 && green == 18) {
                return 1; //open land
            } else if (red == 0 && blue == 0 && green == 0) {
                return 2; //footpath
            } else if (red == 255 && blue == 192 && green == 0) {
                return 3; //rough meadow
            } else if (red == 255 && blue == 255 && green == 255) {
                return 4; //Easy movement forest
            } else if (red == 2 && blue == 208 && green == 60) {
                return 5; //slow run forest
            } else if (red == 2 && blue == 136 && green == 40) {
                return 6; //walk forest
            } else if (red == 0 && blue == 0 && green == 255) {
                return 7; //Lake/swamp/marsh
            } else {
                return 8; //impassable
            }
        }
        
        //NOTE: Functions below are used by map and are uneccessary at next level of abstraction


        protected int computeHueristicWeight() {
            return 0; //TODO: Setup hueristic computation
        }

        /**
         * gets elevation
         * @return double elevation
         */
        protected double getElevation() {
            return elevation;
        }

        /**
         * gets terrain
         * @return int terrain
         */
        protected int getTerrain() {
            return terrain;
        }
        
        /**
         * gets RGB
         * @return int RGB
         */
        protected int getRGB() {
            return pixelRGB;
        }
        
    }

}
