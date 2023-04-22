package nl.mxndarijn.world.mxworld;

import java.io.File;
import java.util.UUID;

public class MxWorld {
    private String name;
    private UUID worldUID;
    private String uuid;
    private File dir;
    private boolean loaded;

    public MxWorld(String name, String uuid, File dir) {
        this.name = name;
        this.uuid = uuid;
        this.dir = dir;
        this.loaded = false;
        this.worldUID = null;
    }

    public void setWorldUID(UUID uid) {
        this.worldUID = uid;
    }


    public void setLoaded(boolean b) {
        this.loaded = b;
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

    public File getDir() {
        return dir;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
