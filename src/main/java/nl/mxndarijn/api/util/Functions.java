package nl.mxndarijn.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class Functions {
    public static Location getSpawnLocation() {
        return new Location(Bukkit.getWorld("world"),-1317, 56, 101, -180, 0);
    }

    public static Location getLocationFromConfiguration(World w, ConfigurationSection section) {
        if(section.contains("yaw") && section.contains("pitch")) {
            return new Location(w, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
        } else {
            return new Location(w, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"));
        }
    }

    public static void copyFileFromResources(String fileName, String path) {
        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        File destFile = new File(plugin.getDataFolder() + File.separator + path);
        destFile.getParentFile().mkdirs();

        copyFileFromResources(fileName, destFile);
    }

    public static void copyFileFromResources(String fileName, File destFile) {
        destFile.getParentFile().mkdirs();
        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        InputStream inputStream = plugin.getResource(fileName);
        if(inputStream == null) {
            Logger.logMessage(LogLevel.FATAL, Prefix.CONFIG_FILES, "Could load resource: " + fileName);
            return;
        }

        try (OutputStream outputStream = Files.newOutputStream(destFile.toPath())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            Logger.logMessage(LogLevel.FATAL, Prefix.CONFIG_FILES, "Could not create config file: " + fileName);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not close stream for config file: " + fileName);
            }
        }
    }

    public static String convertComponentToString(Component c) {
        PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
        return  plainSerializer.serialize(c);
    }
}
