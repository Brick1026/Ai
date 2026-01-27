
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
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
         /*
         For Each point in pointsToVisit {
            curx and cury = start x and y
            While curX and curY do not equal destX and destY {
                 curX and curY = getNextMove()
            }
         }
        */
        
    
        //TODO: Build search algorithm
    }

}


class Map  {
    private int width;
    private int height;
    private Pixel[][] grid;
    private PriorityQueue<Pixel> frontier = new PriorityQueue<>();

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
                grid[i][j] = new Pixel(elevationArray[i][j],myImage.getRGB(i,j),i,j);
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


    /**
     * Sets the pixel at (X, Y) in grid to a path internally. Uses setAsPath from Pixel.
     * @param pixelX int x cord
     * @param pixelY int y cord
     */
    public void setPixelToPathAtXY(int pixelX, int pixelY) {
        grid[pixelX][pixelY].setAsPath();
    }

    /**
     * Sets starting point for algo. (sets this Pixel's shortest path to 0) 
     * @param startX int x cord
     * @param startY int y cord
     */
    public void setStart(int startX,int startY) {
        grid[startX][startY].setShortestPathToMe(0);
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

    // Search functions //

    public void updateFrontierFromXY(int curX, int curY) {
        Pixel currentPixel = grid[curX][curY];
        Pixel northPixel = grid[curX][curY + 1];
        Pixel southPixel = grid[curX][curY - 1];
        Pixel eastPixel = grid[curX-1][curY];
        Pixel westPixel = grid[curX+1][curY];

        double compare =  currentPixel.getShortestPathToMe() + currentPixel.computeDistanceToNode(northPixel);
        if(northPixel.getShortestPathToMe() > compare) {
            //add shortest distance g(n) into node not accounting for hueristic
            northPixel.setShortestPathToMe(compare);
            //add Pixel deeep copy to frontier (disconnect from original object)
            frontier.add(new Pixel(northPixel));
        }

        compare = currentPixel.getShortestPathToMe() + currentPixel.computeDistanceToNode(southPixel);
        if(southPixel.getShortestPathToMe() > compare) {
            //add shortest distance g(n) into node not accounting for hueristic
            southPixel.setShortestPathToMe(compare);
            //add Pixel deeep copy to frontier (disconnect from original object)
            frontier.add(new Pixel(southPixel));
        }

        compare = currentPixel.getShortestPathToMe() + currentPixel.computeDistanceToNode(eastPixel);
        if(eastPixel.getShortestPathToMe() > compare) {
            //add shortest distance g(n) into node not accounting for hueristic
            eastPixel.setShortestPathToMe(compare);
            //add Pixel deeep copy to frontier (disconnect from original object)
            frontier.add(new Pixel(eastPixel));
        }

        compare = currentPixel.getShortestPathToMe() + currentPixel.computeDistanceToNode(westPixel);
        if(westPixel.getShortestPathToMe() > compare) {
            //add shortest distance g(n) into node not accounting for hueristic
            westPixel.setShortestPathToMe(compare);
            //add Pixel deeep copy to frontier (disconnect from original object)
            frontier.add(new Pixel(westPixel));
        }
    }




    /**
     * Given an input curX and curY uses the board and priority queue to select next move.
     * Once a move is chosen this new move's prevPixel is set to current board position.
     * @param curX current x position
     * @param curY current y position
     * @return int[2] x and y dimension
     */
    public int[] getNextMove(int curX, int curY) {
        //pop until one of the results is in agreement with shortest path for that grid space on board (must be valid non-duplicate)
        Pixel nextPotential = frontier.remove();
        while(isGarbage(nextPotential)) {
            nextPotential = frontier.remove();
        }
        int destX = nextPotential.getPixelX();
        int destY = nextPotential.getPixelY();
        grid[destX][destY].setPrev(grid[curX][curY]);
        return new int[]{destX,destY};
    }
    
    /**
     * Helper function for getNextMove. Compares copy in priorityQueue to current board state to verify if it is garbage.
     * @param nextPotential pixel to check if garbage copy
     * @return boolean 
     */
    public boolean isGarbage(Pixel nextPotential) {
        return nextPotential.getShortestPathToMe() != grid[nextPotential.getPixelX()][nextPotential.getPixelY()].getShortestPathToMe();
    }


    /**
     * This only resets shortestPath and frontier. To be triggered for each subsequent point reached.
     */
    public void resetBoard() {
        frontier.clear();
        for(Pixel[] w : grid) {
            for(Pixel p : w) {
              p.setShortestPathToMe(Double.MAX_VALUE);
            }
        }
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
        private final double elevation; //height of pixel
        private final int pixelX;
        private final int pixelY;
        private int pixelRGB; //color of pixel
        private int terrain; //derived from pixelRGB
        private double shortestPathToMe;
        private Pixel prevPixel;

        
        /**
         * Constructs a new Pixel object.
         * @param elevation double elevation
         * @param pixelRGB RGB from getRGB on image fil
         */
        protected Pixel(double elevation, int pixelRGB, int pixelX, int pixelY) {
            this.elevation = elevation;
            this.pixelRGB = pixelRGB;
            this.pixelX = pixelX;
            this.pixelY = pixelY;
            this.shortestPathToMe = Double.MAX_VALUE;
            this.prevPixel = null;
            this.terrain = computeTerrain(pixelRGB);
        }  

        protected Pixel(Pixel other) {
            this.elevation = other.getElevation();
            this.pixelX = other.getPixelX();
            this.pixelY = other.getPixelY();
            this.pixelRGB = other.getRGB();
            this.terrain = other.getTerrain();
            this.prevPixel = new Pixel(other.getPrevPixel());
            this.shortestPathToMe = other.getShortestPathToMe();
        }

        /**
         * Sets terrain and pixelRGB to a path.
         */
        protected void setAsPath() {
            this.pixelRGB = new Color(177, 86, 237).getRGB();
            this.terrain = computeTerrain(pixelRGB);
        }

        protected void setShortestPathToMe(double shortestPathToMe) {
            this.shortestPathToMe = shortestPathToMe;
        }

        protected void setPrev(Pixel previous) {
            this.prevPixel = previous;
        }


        protected int resolveTerrainWeight() {
            //TODO implement weights
            return 0;
        }

        //TODO IMPLEMENT COMPARETO WHICH OPERATES AS THE HUERISTIC. 
        //ONCE A NEW CHECKPOINT IS HIT CLEAR THE PRIORITY QUEUE RESETS
        //compareTO ranks pixels based on their shortestPathToMe (current pixel) + distanceToGoal

        /* 
        since I'm updating the actual object each time a shorter path to a node is found
        If something popped from the priority queue has a different shortestPathToMe
        then the board then we can discard and pop again. It is a duplicate.
        */

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

         /**
         * Computes distance to node in 3D (accounts for terrain, used for g(n))
         * @param cur Pixel 
         * @param dest Pixel
         * @return distance to node + terrain weight
         */
        protected double computeDistanceToNode(Pixel dest) {

            //convert to meters
            double curDblX = 10.29*this.pixelX;
            double curDblY = 7.55*this.pixelY;
            double destDblX = 10.29*dest.getPixelX();
            double destDblY = 7.55*dest.getPixelY();

            //compute distance
            double xDiff = curDblX - destDblX;
            double yDiff = curDblY - destDblY;
            double zDiff = this.elevation - dest.getElevation();
            double distanceToNewNode = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff,2) +  Math.pow(zDiff,2));
            
            return distanceToNewNode + dest.resolveTerrainWeight();
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

        /**
         * gets pixel's y cord
         * @return int pixelY
         */
        protected int getPixelY() {
            return pixelY;
        }

        /**
         * gets pixel's x cord
         * @return int pixelX
         */
        protected int getPixelX() {
            return pixelX;
        }

        protected double getShortestPathToMe() {
            return shortestPathToMe;
        }

        protected Pixel getPrevPixel() {
            return prevPixel;
        }
        
    }

}
