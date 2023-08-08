package nl.mxndarijn.wieisdemol.managers.world;

import java.util.Optional;
import java.util.UUID;

public interface WorldContainer {
    Optional<UUID> getWorldUID();
}
