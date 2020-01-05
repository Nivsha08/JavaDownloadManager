import java.util.ArrayList;

public class ProgramInput {

    private int maxConnections;
    private ArrayList<String> serverList;
    private String fileName;

    /**
     * An compound object contains the parsed user arguments.
     * @param serverList
     * @param maxConnections
     */
    public ProgramInput(ArrayList<String> serverList, int maxConnections) {
        this.serverList = serverList;
        this.maxConnections = maxConnections;
        this.fileName = this.parseFileName();
    }

    /**
     * Parse the user input to
     * @return
     */
    private String parseFileName() {
        int lastBackslashPos = serverList.get(0).lastIndexOf('/');
        return serverList.get(0).substring(lastBackslashPos + 1);
    }


    /* GETTERS & SETTERS */
    public ArrayList<String> getServerList() {
        return serverList;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public String getFileName() {
        return fileName;
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
