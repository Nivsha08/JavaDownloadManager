import InputHandlers.ProgramInput;
import InputHandlers.UserInputHandler;

/**
 * Entry point for the entire program.
 */
public class Main {
    public static void main(String[] args) {
        ProgramInput programInput = UserInputHandler.parseArguments(args);
    }
}
