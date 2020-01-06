import java.io.*;

public class MetadataManager {

    // metadata files suffices
    private static final String SUFFIX = ".tmp";
    private static final String COPY_SUFFIX = ".copy.tmp";

    private String sourceFileName;
    private long sourceFileTotalSize;
    private File metadataFile = null;
    private boolean firstRun = false;

    /**
     * Creates an object to handle saving and loading of the metadata persistent files.
     * The constructor opens the metadata file if already exists, or creates one if necessary.
     * @param sourceFileName - the name of the source file.
     * @param sourceFileTotalSize - the size of the source file in bytes.
     */
    public MetadataManager(String sourceFileName, long sourceFileTotalSize) {
        this.sourceFileName = sourceFileName;
        this.sourceFileTotalSize = sourceFileTotalSize;
        if (metadataFileExists()) {
            this.metadataFile = new File(this.sourceFileName + SUFFIX);
        }
        else {
            createInitialMetadataFile();
        }
    }

    /**
     * Creates the initial file on the first run of the download manager program.
     */
    public void createInitialMetadataFile() {
        File file = new File(sourceFileName + SUFFIX);
        try {
            file.createNewFile();
        }
        catch (IOException e) {
            ProgramPrinter.printError("Failed to create metadata temp file." , e);
        }
        metadataFile = file;
        firstRun = true;
    }

    /**
     * @returns true if and only if the metadata file already exists, false otherwise.
     */
    public boolean metadataFileExists() {
        return new File(sourceFileName + SUFFIX).exists();
    }

    /**
     * Clears the temporary metadata files after a successful download.
     */
    public void clearFiles() {
        metadataFile.delete();
    }

    /**
     * Saves the serialized ChunkManager object to the metadata file. The serialized object is first
     * written to a second temporary file which is then atomically renamed to overwrite the
     * original metadata file.
     * @param chunkManager
     */
    public void save(ChunkManager chunkManager) {
        File metadataCopyFile = new File(sourceFileName + COPY_SUFFIX);
        serializeMetadata(chunkManager, metadataCopyFile);
        metadataCopyFile.renameTo(metadataFile);
    }

    /**
     * Loads the serialized object from the metadata file, deserializes it
     * and creates a ChunkManager object from it.
     * @returns the restored ChunkManager object.
     */
    public ChunkManager load() {
        ChunkManager loadedChunkManager = deserializeMetadata();
        return loadedChunkManager;
    }

    /**
     * Serializes the given ChunkManager object and writes it to the given <metadataCopyFile>.
     * @param chunkManager - the object to serialize.
     * @param metadataCopyFile - the file to write the serialized object to.
     */
    private void serializeMetadata(ChunkManager chunkManager, File metadataCopyFile) {
        try {
            FileOutputStream file = new FileOutputStream(metadataCopyFile);
            ObjectOutputStream out = new ObjectOutputStream(file);
            MinifiedChunkTable m = new MinifiedChunkTable(chunkManager);
            out.writeObject(m);
            out.flush();
            out.close();
            file.close();
        }
        catch (FileNotFoundException e) {
            ProgramPrinter.printError("Unable to create a temporary file: invalid path.", e);
        }
        catch (IOException e) {
            ProgramPrinter.printError("Failed to write the serialized object to the temporary file.", e);
        }
    }

    /**
     * Deserializes the serialized object from the metadata file. If the metadata file is empty,
     * the function creates a fresh instance of a ChunkManager object.
     * @returns a ChunkManager object.
     */
    private ChunkManager deserializeMetadata() {
        ChunkManager result = null;
        try {
            FileInputStream file = new FileInputStream(sourceFileName + SUFFIX);
            ObjectInputStream in = new ObjectInputStream(file);
            MinifiedChunkTable m = (MinifiedChunkTable) in.readObject();
            in.close();
            file.close();
            result = new ChunkManager(m);
        }
        catch (FileNotFoundException e) {
            ProgramPrinter.printError("Unable to find the metadata file.", e);
        }
        catch (EOFException e) {
            result = new ChunkManager(sourceFileTotalSize);
        }
        catch (IOException e) {
            ProgramPrinter.printError("Failed to read from the metadata file.", e);
        }
        catch (ClassNotFoundException e) {
            ProgramPrinter.printError("Failed to read from the metadata file.", e);
        }
        return result;
    }

    /* GETTERS & SETTERS */

    public boolean isFirstRun() {
        return firstRun;
    }
}
