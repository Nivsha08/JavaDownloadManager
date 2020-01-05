import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class UserInputHandler {

    /**
     * Assuming program arguments are correct, parsing the servers list and
     * the max connections defined by the user.
     * @param args - the user input arguments.
     * @return ProgramInput object.
     */
    public static ProgramInput parseArguments(String[] args) {
        int maxConnection = 1; // set default number of connections to 1
        ArrayList<String> serverList = getServerAddresses(args[0]);

        if (args.length > 1) {
            maxConnection = Integer.parseInt(args[1]);
        }

        return new ProgramInput(serverList, maxConnection);
    }

    /**
     * Parsing the given address. If the address is a single URL, returns an
     * ArrayList containing only it. If the address is of a .list file, returns an
     * ArrayList of the entire list.
     * @param address - single URL or a .list file path.
     * @return List of servers' URLs.
     */
    private static ArrayList<String> getServerAddresses(String address) {
        ArrayList<String> result = new ArrayList<>();
        boolean isURL = address.matches("^(?:http(s)?).*");

        if (isURL) {
            result.add(address);
        }
        else {
            result = readServerList(address);
        }

        return result;
    }

    /**
     * In case the user entered a file containing a list of servers' URLs,
     * creates an object containing these addresses.
     * @param filePath - file path of the user input file.
     * @return List of servers' URLs.
     */
    private static ArrayList<String> readServerList(String filePath) {
        String serverURL = "";
        ArrayList<String> result = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            while ((serverURL = fileReader.readLine()) != null) {
                result.add(serverURL);
            }
        }
        catch (IOException e) {
            ProgramPrinter.printError("Unable to read server list file.", e);
        }

        return result;
    }
}
