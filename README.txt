----------------------
Anat Balzam 205387954
Niv Shani 311361661
----------------------

Submitted classes:

1. IdcDM - program entry point, containing the main method.
2. UserInputHandler - handles the user terminal arguments parsing.
3. ProgramInput - an object to aggregate the user input parameters.
4. ProgramPrinter - a static class to present user message and program output to the standard error.
5. DownloadManager - a singleton object to initialize and manage the download operation.
6. DownloadStatus - an object to keep track of the completed bytes and the download progress.
7. MetadataManager - an object responsible for saving and loading the download progress metadata (handles serialization and deserialization).
8. Chunk - an object that aggregates the chunk index, chunk range and the actual data (downloaded byte).
9. ChunkRange - an object to aggregate the range boundaries and parsing logic.
10. ChunkGetter - a runnable object to download a specific Chunk from the server, and enqueuing it into the blocking queue.
11. ChunkWriter - a runnable object to dequeue downloaded Chunks from the blocking queue, and write them to the disk.
12. ChunkManager - an object to keep track of references to the downloaded chunks, to be minified and serialized to the metadata file.
13. MinifiedChunkTable - a minified representation of the ChunkManager's table, resulting in a bitmap of the completed chunks.

----------------------