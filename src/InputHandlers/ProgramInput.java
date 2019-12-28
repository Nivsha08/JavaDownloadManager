package InputHandlers;

import java.util.ArrayList;

public class ProgramInput {

    private ArrayList<String> serverList;
    private int maxConnections = 1;

    /**
     * An compound object contains the parsed user arguments.
     * @param serverList
     * @param maxConnections
     */
    public ProgramInput(ArrayList<String> serverList, int maxConnections) {
        this.serverList = serverList;
        this.maxConnections = maxConnections;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();

        for (String serverURL : this.serverList) {
            output.append(serverURL + "\n");
        }

        output.append("Max connections: " + this.maxConnections + '\n');

        return output.toString();
    }

}
