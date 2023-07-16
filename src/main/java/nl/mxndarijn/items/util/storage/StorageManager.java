package nl.mxndarijn.items.util.storage;

import nl.mxndarijn.data.SpecialDirectories;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StorageManager {

    private static StorageManager instance;

    private List<StorageContainer> serverContainers;

    private HashMap<String, List<StorageContainer>> playerContainers;

    public static StorageManager getInstance() {
        if(instance == null) {
            instance = new StorageManager();
        }

        return instance;
    }

    private final File storageDir;
    private StorageManager() {
        Logger.logMessage(LogLevel.INFORMATION, Prefix.STORAGE_MANAGER, "Loading StorageManager...");
        storageDir = SpecialDirectories.STORAGE_FILES.getDirectory();
        serverContainers = new ArrayList<>();
        playerContainers = new HashMap<>();

        if(storageDir == null) {
            Logger.logMessage(LogLevel.FATAL, Prefix.STORAGE_MANAGER, "Main folder is null... ");
            return;
        }
        if(!storageDir.isDirectory()) {
            Logger.logMessage(LogLevel.FATAL, Prefix.STORAGE_MANAGER, "Main folder is a file, could not load storages... " + storageDir.getAbsolutePath());
            return;
        }

        if(!storageDir.exists()) {
            storageDir.mkdirs();
        }

        for (File file : storageDir.listFiles()) {
            if(!file.isDirectory())
                continue;
            if(file.getName().equalsIgnoreCase("server")) {
                for (File serverContainer : file.listFiles()) {
                    if(serverContainer.isDirectory())
                        continue;

                    serverContainers.add(new StorageContainer(serverContainer));
                }
            } else {
                List<StorageContainer> specificPlayerContainer = new ArrayList<>();
                for (File playerContainer : file.listFiles()) {
                    if(playerContainer.isDirectory())
                        continue;
                    specificPlayerContainer.add(new StorageContainer(playerContainer));
                }
                playerContainers.put(file.getName(), specificPlayerContainer);
            }
        }
    }

    public void save() {
        Logger.logMessage(LogLevel.INFORMATION, Prefix.STORAGE_MANAGER, "Saving all storages...");
        serverContainers.forEach(StorageContainer::save);
        playerContainers.forEach((s, storageContainers) -> storageContainers.forEach(StorageContainer::save));
    }

    public List<StorageContainer> getServerContainers() {
        return serverContainers;
    }

    public List<StorageContainer> getPlayerContainers(String uuid) {
        return playerContainers.containsKey(uuid) ? playerContainers.get(uuid) : new ArrayList<>();
    }

    public List<StorageContainer> getPublicContainers() {
        List<StorageContainer> publicContainers = new ArrayList<>();
        playerContainers.forEach((s, storageContainers) -> {
            storageContainers.forEach(container -> {
                if(container.isPublic()) {
                    publicContainers.add(container);
                }
            });
        });
        return publicContainers;
    }

    public void removeContainer(StorageContainer storageContainer) {

        serverContainers.remove(storageContainer);
        playerContainers.forEach((s, storageContainers) -> {
            storageContainers.remove(storageContainer);
        });
    }

    public void addPlayerContainer(String string, StorageContainer storageContainer) {
        List<StorageContainer> containers = playerContainers.getOrDefault(string, new ArrayList<>());
        containers.add(storageContainer);

        playerContainers.put(string, containers);
    }

    public void addServerContainer(StorageContainer newContainer) {
        serverContainers.add(newContainer);
    }
}
