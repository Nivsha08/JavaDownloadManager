import inputHandlers.ProgramInput;
import inputHandlers.UserInputHandler;

/**
 * Entry point for the entire program.
 */
public class Main {
    public static void main(String[] args) {
        ProgramInput programInput = UserInputHandler.parseArguments(args);
        System.out.println(programInput);
    }
}
