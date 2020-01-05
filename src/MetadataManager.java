import java.io.*;

public class MetadataManager {

    private static final String SUFFIX = ".tmp";
    private static final String COPY_SUFFIX = ".copy.tmp";

    private String sourceFileName;
    private long sourceFileTotalSize;
    private File metadataFile = null;
    private boolean firstRun = false;

    public MetadataManager(String sourceFileName, long sourceFileTotalSize) {
        this.sourceFileName = sourceFileName;
        this.sourceFileTotalSize = sourceFileTotalSize;
        if (metadataFileExists()) {
            this.metadataFile = new File(this.sourceFileName + SUFFIX);
        }
        else {
            initMetadataFile();
            firstRun = true;
        }
    }

    public void initMetadataFile() {
        File file = new File(sourceFileName + SUFFIX);
        try {
            file.createNewFile();
        } catch (IOException e) {
            ProgramPrinter.printError("Failed to create metadata temp file." , e);
        }
        metadataFile = file;
    }

    public boolean isFirstRun() {
        return this.firstRun;
    }

    public boolean metadataFileExists() {
        return new File(sourceFileName + SUFFIX).exists();
    }

    public void clearFiles() {
        File metadataFile = new File(sourceFileName + SUFFIX);
        metadataFile.delete();
    }

    public void save(ChunkManager chunkManager) {
        File metadataTemp = new File(sourceFileName + COPY_SUFFIX);
        serializeMetadata(metadataTemp, chunkManager);
        metadataTemp.renameTo(metadataFile);
    }

    public ChunkManager load() {
        ChunkManager loadedChunkManager = deserializeMetadata();
        return loadedChunkManager;
    }

    private void serializeMetadata(File metadataTemp, ChunkManager chunkManager) {
        try {
            FileOutputStream file = new FileOutputStream(metadataTemp);
            ObjectOutputStream out = new ObjectOutputStream(file);
            MinifiedChunkTable m = new MinifiedChunkTable(chunkManager);
            out.writeObject(m);
            out.flush();
            out.close();
            file.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
        catch (EOFException e) {
            result = new ChunkManager(sourceFileTotalSize);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}
