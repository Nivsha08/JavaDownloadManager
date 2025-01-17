import java.util.ArrayList;

public class ProgramInput {

    private int maxConnections;
    private ArrayList<String> serverList;
    private String fileName;

    /**
     * A compound object contains the parsed user arguments.
     * @param serverList
     * @param maxConnections
     */
    public ProgramInput(ArrayList<String> serverList, int maxConnections) {
        this.serverList = serverList;
        this.maxConnections = maxConnections;
        this.fileName = parseFileName();
    }

    /**
     * Parse the user input to extract the source file name from the server address path.
     * @returns the source file name.
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
    
}
