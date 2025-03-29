package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");
        String regex = "[a-z]{4}";

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            /* Log required (Config) */
            logger.log(Level.CONFIG, "Wordle created & Connected.");
        } else {
            /* Log required (Severe) */
            logger.log(Level.SEVERE, "Cannot connect to database.");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            /* Log required (Config) */
            logger.log(Level.CONFIG, "Wordle structures in place.");
        } else {
            /* Log required (Severe) */
            logger.log(Level.SEVERE, "Cannot create wordle tables.");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;

            while ((line = br.readLine()) != null) {

                if(!line.matches(regex)) {
                    br.readLine();
                    /* Log required (Severe) */
                    logger.log(Level.SEVERE, "Invalid db input: " + line);
                    /* Log required (Warning) */
                    logger.log(Level.WARNING, "Skipping input!");
                    continue;
                }
                /* Log required (Config) */
                logger.log(Level.INFO, "Added word: " + line);
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            /* Log required (Severe) */
            logger.log(Level.SEVERE, "Cannot read data file", e);
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            /* Game Related sysout */
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                
                if(!guess.matches(regex) || "null".equals(guess)) {
                    /* Log required (info) */
                    logger.log(Level.INFO, "Input not valid: " + guess);
                    
                    /* Game Related sysout */
                    System.out.print("Enter a 4 letter word for a guess or q to quit: ");

                    guess = scanner.nextLine();
                } else {

                     /* Game Related sysout */
                    System.out.println("You've guessed '" + guess +"'.");

                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                         /* Game Related sysout */
                        System.out.println("Success! It is in the the list.\n");
                    } else {
                         /* Game Related sysout */
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                    }

                     /* Game Related sysout */
                    System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                    guess = scanner.nextLine();
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }

    }
}