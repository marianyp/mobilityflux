package dev.mariany.mobilityflux.entity.gatecrash;

import dev.mariany.mobilityflux.MobilityFlux;
import dev.mariany.mobilityflux.server.world.MFChunkTickets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class GatecrashState {
    private final String GATECRASH_TETHER_KEY = "GatecrashTether";
    private final String GATECRASH_DIMENSION_KEY = "GatecrashDimension";

    @Nullable
    private GatecrashEntity gatecrashEntity = null;
    private final TrackedData<Boolean> tetheredTrackedData;
    private final Supplier<World> worldSupplier;
    private final Supplier<DataTracker> dataTrackerSupplier;

    public GatecrashState(
            TrackedData<Boolean> tetheredTrackedData,
            Supplier<World> worldSupplier,
            Supplier<DataTracker> dataTrackerSupplier
    ) {
        this.tetheredTrackedData = tetheredTrackedData;
        this.worldSupplier = worldSupplier;
        this.dataTrackerSupplier = dataTrackerSupplier;
    }

    public Optional<GatecrashEntity> getGatecrashEntity() {
        return Optional.ofNullable(this.gatecrashEntity);
    }

    public boolean isTethered() {
        return this.dataTrackerSupplier.get().get(this.tetheredTrackedData);
    }

    public void setGatecrashEntity(@Nullable GatecrashEntity gatecrashEntity) {
        this.gatecrashEntity = gatecrashEntity;
        this.dataTrackerSupplier.get().set(this.tetheredTrackedData, gatecrashEntity != null);
    }

    public void removeGatecrash() {
        this.setGatecrashEntity(null);
    }

    public long handleGatecrash(GatecrashEntity gatecrash) {
        if (gatecrash.getWorld() instanceof ServerWorld serverWorld) {
            this.setGatecrashEntity(gatecrash);
            serverWorld.resetIdleTimeout();
            return addGatecrashTicket(serverWorld, gatecrash.getChunkPos()) - 1;
        }

        return 0;
    }

    public static long addGatecrashTicket(ServerWorld world, ChunkPos chunkPos) {
        world.getChunkManager().addTicket(MFChunkTickets.GATECRASH, chunkPos, 2);
        return MFChunkTickets.GATECRASH.expiryTicks();
    }

    public void writeGatecrash(WriteView view) {
        if (this.gatecrashEntity != null) {
            if (this.gatecrashEntity.isRemoved()) {
                MobilityFlux.LOGGER.warn("Trying to save removed gatecrash, skipping.");
            } else {
                view.put(GATECRASH_DIMENSION_KEY, World.CODEC, this.gatecrashEntity.getWorld().getRegistryKey());

                if (view instanceof NbtWriteView nbtWriteView) {
                    try (
                            ErrorReporter.Logging logging = new ErrorReporter.Logging(
                                    this.gatecrashEntity.getErrorReporterContext(),
                                    MobilityFlux.LOGGER
                            )
                    ) {
                        NbtWriteView gatecrashView =
                                NbtWriteView.create(logging, this.gatecrashEntity.getRegistryManager());
                        this.gatecrashEntity.saveData(gatecrashView);
                        nbtWriteView.getNbt().put(GATECRASH_TETHER_KEY, gatecrashView.getNbt());
                    }
                }
            }
        }
    }

    public void readGatecrash(ReadView view) {
        Optional<RegistryKey<World>> optionalDimension = view.read(GATECRASH_DIMENSION_KEY, World.CODEC);

        if (optionalDimension.isPresent()) {
            MinecraftServer server = this.worldSupplier.get().getServer();

            if (server != null) {
                ServerWorld serverWorld = server.getWorld(optionalDimension.get());
                Optional<ReadView> optionalTether = view.getOptionalReadView(GATECRASH_TETHER_KEY);

                if (optionalTether.isPresent()) {
                    if (serverWorld != null) {
                        Entity entity = EntityType.loadEntityWithPassengers(
                                optionalTether.get(),
                                serverWorld,
                                SpawnReason.LOAD,
                                gatecrash -> serverWorld.tryLoadEntity(gatecrash) ? gatecrash : null
                        );

                        if (entity instanceof GatecrashEntity gatecrashEntity) {
                            this.setGatecrashEntity(gatecrashEntity);
                            addGatecrashTicket(serverWorld, entity.getChunkPos());
                        } else {
                            MobilityFlux.LOGGER.warn(
                                    "Failed to spawn player gatecrash in level ({}), skipping.",
                                    optionalDimension.get()
                            );
                        }
                    } else {
                        MobilityFlux.LOGGER.warn(
                                "Trying to load gatecrash without level ({}) being loaded, skipping.",
                                optionalDimension.get()
                        );
                    }
                }
            }
        }
    }
}
