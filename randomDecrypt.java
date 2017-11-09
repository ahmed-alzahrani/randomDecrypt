import java.util.Random;
import java.util.ArrayList;

public class randomDecrypt{
  private static final long multiplier = 25214903917L; // Multiplier used in JAVA's random object's nextInt() linear congruential PRNG
  private static final long addend = 11; // the addend value used in the same way as multiplier
  private static final long mask = (1L << 48) - 1; // bit mask for the modulo operation used in the PRNG
  private static int iterations = 1; // iterations it took us to find the seed
  private static long seed;
  private static Random rand = new Random(); //the instance of random we we will
  private static ArrayList<Long> randArray = new ArrayList<>();
  private static ArrayList<Long> predictArray = new ArrayList<>();
  private static int matches = 0;


  public static void main(String args[]) {
      seed = getSeed(nextInt());
      padPredictArray(); // pad the predictArray with 0's for all of the outputs of nextInt() for which we do not know the seed
      System.out.println("Generate predictions until we have 5 matches....");
    while (matches < 5 ) {
      seed = modifySeed(seed);
      runIteration(seed);
    }
    System.out.println("Done!");
    displayResults();
  }

  // any time a new prediction is generated and added to the prediction array, we evaluate if it matcehs the randomArray and increment matches if so
  public static void adjustMatches(int predictIndex) {
    if (predictArray.get(predictIndex).equals(randArray.get(predictIndex))) {
      matches++;
    }
  }

  // balance ensures that the randArray is always at least 1 step ahead of the prediction array
  public static void balance() {
    while (predictArray.size() >= randArray.size()) {
      nextInt();
    }
  }

  public static void displayResults() {
    System.out.println();
    System.out.println();
    System.out.println("Below is the array of nextInt() calls from rand");
    System.out.println(randArray);
    System.out.println();
    System.out.println();
    System.out.println("Below is the array of predictions we were able to make about the results of nextInt()");
    System.out.println(predictArray);
  }

  // returns an integer representing the next predicted output from the PRNG. Does this by modifying the key and then doing the appropriate bit shifting
  public static int generateNext(long seed) {
    long modifiedSeed = modifySeed(seed);
    long nextIntAsLong = generateNextInt(modifiedSeed);
    return (int) nextIntAsLong;
  }

  // does the bit shifting as mentioned in generateNext
  public static long generateNextInt(long seed) {
    return (seed >>> 16);
  }

  // getSeed takes two inputs from the random object's PRNG and tries all possible seeds to find the right one
  public static long getSeed(long v1) {
    long v2 = nextInt();
    for (int i = 0; i < 65536; i++) {
      long seed = v1 * 65536 + i;
      if ((((seed * multiplier + 11) & mask) >>> 16) == v2) {
        System.out.println("Seed found: " + seed + " in " + iterations + " iterations");
        return seed;
      }
    }
    System.out.println("Couldn't find the seed, trying again with the next random int");
    iterations++;
    return getSeed(v2);
  }

  // modifies the seed so it is ready to generate the next output
  public static long modifySeed(long seed) {
    return ((seed * multiplier + 11) & mask);
  }

  // syntactic sugar for rand.nextInt() that ensures whenever that call is made, the output is also added to the randArray
  public static long nextInt() {
    long next = rand.nextInt();
    randArray.add(next);
    return next;
  }

  // typically the seed can be found in a single iteration with two outputs, rarely it requires more, in either case, the predictArray is padded with 0s
  // for the outputs in randArray for which we can not find the seed
  public static void padPredictArray() {
    for (int i = 0; i <= iterations; i++) {
      predictArray.add(0L);
    }
  }

  // runIteration handles the logic of the script, it gets the next prediction, adds it to the array, makes sure the arrays are balanced and evaluates if there is a match.
  public static void runIteration(long seed) {
    int next = generateNext(seed);
    predictArray.add((long) next);
    int predictIndex = predictArray.indexOf((long) next);
    balance();
    adjustMatches(predictIndex);
  }

}
