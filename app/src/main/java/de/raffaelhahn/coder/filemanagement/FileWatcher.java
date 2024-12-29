package de.raffaelhahn.coder.filemanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import lombok.Data;

@Data
public class FileWatcher {

    private File root;

    public FileWatcher(File root) {
        this.root = root;
    }

    public void watch() {
        try {
            WatchService watchService = root.toPath().getFileSystem().newWatchService();
            root.toPath().register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            while(true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents())
                {
                    // Handle the specific event
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                    {
                        System.out.println("File created: " + event.context());
                    }
                    else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
                    {
                        System.out.println("File deleted: " + event.context());
                    }
                    else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
                    {
                        System.out.println("File modified: " + event.context());
                    }
                }

                // To receive further events, reset the key
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
