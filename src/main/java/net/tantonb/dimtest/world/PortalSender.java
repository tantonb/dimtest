package net.tantonb.dimtest.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.blocks.BasePortalBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

public class PortalSender implements ITeleporter {

    private static final Logger LOGGER = LogManager.getLogger(DimTestMod.MODID);
    private ServerWorld remoteWorld;
    private BlockPos localPos;
    private BasePortalBlock portalBlock;

    public PortalSender(ServerWorld remoteWorld, BlockPos localPos, BasePortalBlock portalBlock) {
        this.remoteWorld = remoteWorld;
        this.localPos = localPos;
        this.portalBlock = portalBlock;
    }

    private BlockPos arrivalPos;

    private BlockPos getArrivalPos() {
        return arrivalPos;
    }

    private float distanceBetween(BlockPos pos1, BlockPos pos2) {
        return (float) Math.sqrt(
                Math.pow(pos1.getX() - pos2.getX(), 2) +
                Math.pow(pos1.getY() - pos2.getY(), 2) +
                Math.pow(pos1.getZ() - pos2.getZ(), 2));
    }

    private BlockPos getNearerPos(BlockPos fromPos, BlockPos pos1, BlockPos pos2) {
        return distanceBetween(fromPos, pos1) < distanceBetween(fromPos, pos2) ? pos1 : pos2;
    }

    private BlockPos findNearestPortal(ServerWorld world, BlockPos startPos) {

        LOGGER.info("Searching for nearest portal in dimension '{}'", world.getDimensionKey());

        // TODO: convert this to BlockPos.getAllInBox(), get rid of tile entities

        // scan portal tile entities in chunk, return position of nearest found
        Chunk chunk = (Chunk)world.getChunk(startPos);
        BlockPos nearestPos = null;

        for (TileEntity te : chunk.getTileEntityMap().values()) {
            if (!portalBlock.matchesPortalTE(te)) {
                continue;
            }
            LOGGER.info("Checking portal pos {}", te.getPos());
            if (nearestPos == null) {
                nearestPos = te.getPos();
            } else {
                nearestPos = getNearerPos(startPos, nearestPos, te.getPos());
            }
            LOGGER.info("nearestPos set to {}", nearestPos);
        }

        return nearestPos;
    }


    private void placeTeleporterInWorld(ServerWorld world, BlockPos pos) {
        BlockState teleporter = portalBlock.getDefaultState();
        world.setBlockState(pos, teleporter);
        LOGGER.info(
                "Placed portal in world '{}' at {}",
                world.getDimensionKey(),
                pos
        );
    }
    private BlockPos createRemotePortal(ServerWorld remoteWorld, BlockPos startPos) {

        // TODO: refactor to get rid of chunk restrictions

        Chunk chunk = (Chunk)remoteWorld.getChunk(startPos);
        BlockPos.Mutable chunkPos = new BlockPos.Mutable();
        for (int y = 252; y > 0; y--) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunkPos.setPos(x, y, z);
                    if (!chunk.getBlockState(chunkPos).isAir() &&
                            chunk.getBlockState(chunkPos.up(1)).isAir() &&
                            chunk.getBlockState(chunkPos.up(2)).isAir() &&
                            chunk.getBlockState(chunkPos.up(3)).isAir()
                    ) {
                        BlockPos worldPos = chunk.getPos().asBlockPos().add(chunkPos.getX(), chunkPos.getY() + 1, chunkPos.getZ());
                        placeTeleporterInWorld(remoteWorld, worldPos);
                        return worldPos;
                    }
                }
            }
        }
        return null;

    }

    private BlockPos findOrCreateRemotePortal(ServerWorld remoteWorld) {
        BlockPos remotePos = findNearestPortal(remoteWorld, localPos);
        if (remotePos == null) {
            remotePos = createRemotePortal(remoteWorld, localPos);
        }
        return remotePos;
    }

    public boolean send(ServerPlayerEntity player) {

        // locate or create a new remote portal block to send player to
        // note this is done before attempting to send player to remote dimension
        BlockPos portalPos = findOrCreateRemotePortal(remoteWorld);
        if (portalPos == null) {
            LOGGER.info("Unable to locate or create remote portal block in remote dimension '{}'", remoteWorld.getDimensionKey());
            return false;
        }

        // currently setting arrival position on top of located portal block
        // TODO: check validity of arrival position, it may have been obstructed, etc.
        arrivalPos = portalPos;

        // move the player entity to the arrival point in the remote world
        // using this as the ITeleporter (this will cause placeEntity() to
        // be called)
        player.changeDimension(remoteWorld, this);

        // TODO: re-evaluate what to return from send() here...
        return true;
    }


    @Override
    /**
     * Invoked as ITeleporter.placeEntity() when player.changeDimension() is called (see send()).
     */
    public Entity placeEntity(Entity srcEntity, ServerWorld fromWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> createInDest)
    {

        // prepare to generate player entity in destination world
        Entity destEntity = createInDest.apply(false);
        if (!(destEntity instanceof ServerPlayerEntity)) {
            return destEntity;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) destEntity;
        BlockPos arrivalPos = getArrivalPos();

        // set player position centered above teleporter block
        player.setPositionAndUpdate(arrivalPos.getX() + 0.5D, arrivalPos.getY() + 1D, arrivalPos.getZ() + 0.5D);
        return player;
    }
}
