package nl.mxndarijn.api.mxworld;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Optional;

public class MxLocation {

    private double x = Double.MIN_VALUE;
    private double y = Double.MIN_VALUE;
    private double z = Double.MIN_VALUE;
    private int yaw = 0;
    private int pitch = 0;

    public static MxLocation getFromLocation(Location loc) {
        MxLocation mxLocation = new MxLocation();
        mxLocation.setX(loc.getX());
        mxLocation.setY(loc.getY());
        mxLocation.setZ(loc.getZ());
        mxLocation.setPitch((int) loc.getPitch());
        mxLocation.setYaw((int) loc.getYaw());

        return mxLocation;
    }

    public static Optional<MxLocation> loadFromMap(Map<String, Object> section) {
        if (section == null) {
            return Optional.empty();
        }
        MxLocation mxLocation = new MxLocation();
        mxLocation.setX((double) section.get("x"));
        mxLocation.setY((double) section.get("y"));
        mxLocation.setZ((double) section.get("z"));
        if (section.containsKey("yaw") && section.containsKey("pitch")) {
            mxLocation.setYaw((int) section.get("yaw"));
            mxLocation.setPitch((int) section.get("pitch"));
        }

        return mxLocation.validate() ? Optional.of(mxLocation) : Optional.empty();
    }

    public static Optional<MxLocation> loadFromConfigurationSection(ConfigurationSection section) {
        if (section == null) {
            return Optional.empty();
        }
        MxLocation mxLocation = new MxLocation();
        mxLocation.setX(section.getDouble("x"));
        mxLocation.setY(section.getDouble("y"));
        mxLocation.setZ(section.getDouble("z"));
        if (section.contains("yaw") && section.contains("pitch")) {
            mxLocation.setYaw(section.getInt("yaw"));
            mxLocation.setPitch(section.getInt("pitch"));
        }

        return mxLocation.validate() ? Optional.of(mxLocation) : Optional.empty();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getYaw() {
        return yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public Location getLocation(World w) {
        return new Location(w, x, y, z, yaw, pitch);
    }

    public boolean validate() {
        return
                x != Double.MIN_VALUE &&
                        y != Double.MIN_VALUE &&
                        z != Double.MIN_VALUE;

    }

    @Override
    public String toString() {
        return "MxLocation{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }

    public void write(ConfigurationSection section) {
        section.set("x", x);
        section.set("y", y);
        section.set("z", z);
        section.set("yaw", yaw);
        section.set("pitch", pitch);

//        try {
//            fc.save(file);
//        } catch (IOException e) {
//            Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not write MxLocation (" + file.getAbsolutePath() + ")");
//            e.printStackTrace();
//        }
    }

    public boolean equals(MxLocation l) {
        return x == l.getX() && y == l.getY() && z == l.getZ();
    }
}
