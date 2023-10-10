package nl.mxndarijn.api.mxworld;

import java.io.File;
import java.util.UUID;

public class MxWorld {
    private final String name;
    private final String uuid;
    private final File dir;
    private UUID worldUID;
    private boolean loaded;

    public MxWorld(String name, String uuid, File dir) {
        this.name = name;
        this.uuid = uuid;
        this.dir = dir;
        this.loaded = false;
        this.worldUID = null;
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public UUID getWorldUID() {
        return worldUID;
    }

    public void setWorldUID(UUID uid) {
        this.worldUID = uid;
    }

    public File getDir() {
        return dir;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean b) {
        this.loaded = b;
    }
}
