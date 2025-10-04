package nl.mxndarijn.wieisdemol.map.mapscript.atla.startingroom;

import nl.mxndarijn.api.item.MxItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.wieisdemol.map.mapscript.MapAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AtlaStartingRoomTeleportAppa extends MapAction<AtlaStartingRoom> {

    protected AtlaStartingRoomTeleportAppa(@NotNull AtlaStartingRoom mapRoom) {
        super(mapRoom);
    }

    @Override
    public void onActivate(@NotNull Event event, @Nullable Player player) {
        this.getMapRoom().teleportPlayers();
    }

    @Override
    public @NotNull MxSkullItemStackBuilder createItemStack() {
        return MxSkullItemStackBuilder
                .create(1)
                .setSkinFromHeadsData("ender-pearl")
                .setName("<gray>Teleport Appa")
                .addBlankLore()
                .addLore("<gray>Teleporteer spelers die op appa zitten")
                .addLore("<gray>naar de volgende kamer.")
                .addBlankLore()
                .addLore("<yellow>Klik hier om te teleporteren.");
    }
}
