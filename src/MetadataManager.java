import java.io.*;

public class MetadataManager {

    private static final String SUFFIX = ".tmp";
    private String sourceFileName;
    private File metadataFile = null;
    private boolean firstRun = false;

    public MetadataManager(String sourceFileName) {
        this.sourceFileName = sourceFileName;
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

    public File getFile() {
        return this.metadataFile;
    }

    public void save(ChunkManager chunkManager) {
        try {
            File metadataTmp = new File(sourceFileName + "copy" + SUFFIX);
            FileOutputStream file = new FileOutputStream(metadataTmp);
            ObjectOutputStream out = new ObjectOutputStream(file);
            MinifiedChunkTable m = new MinifiedChunkTable(chunkManager);
            out.writeObject(m);
            out.flush();
            metadataTmp.renameTo(metadataFile);
            out.close();
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChunkManager load() {
        ChunkManager chunkManager = null;
        try {
            FileInputStream file = new FileInputStream(sourceFileName + SUFFIX);
            ObjectInputStream in = new ObjectInputStream(file);
            MinifiedChunkTable m = (MinifiedChunkTable) in.readObject();
            chunkManager = new ChunkManager(m);
            in.close();
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return chunkManager;
    }
}
