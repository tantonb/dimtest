package net.tantonb.dimtest.dimensions;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.blocks.ModBlocks;
import net.tantonb.dimtest.blocks.BasePortalBlock;
import net.tantonb.dimtest.tileentity.AltoverTE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

/**
 * Handles teleportation between test dimension 1 teleporter blocks in the overworld and the test dimension.
 *
 * Implements forge ITeleporter.  The placeEntity() method is triggered by TestDim1TeleporterBlock activation.
 */
public class Teleporter implements ITeleporter {

    private static final Logger LOGGER = LogManager.getLogger(DimTestMod.MODID);

    // the starting position of a player to teleport
    private BasePortalBlock fromTeleporter;
    private BlockPos fromPos;

    public Teleporter(BlockPos fromPos) {
        this.fromPos = fromPos;
    }

    private BlockPos findTeleporterInChunk(Chunk chunk) {

        // scan tile entities in chunk, return position of first teleporter found
        for (TileEntity tile : chunk.getTileEntityMap().values()) {
            if (tile instanceof AltoverTE) {
                BlockPos pos = tile.getPos();
                if (chunk.getBlockState(pos.up()).isAir()) {
                    return pos;
                }
            }
        }
        return null;
    }

    private void placeTeleporterInWorld(ServerWorld world, BlockPos pos) {
        BlockState teleporter = ModBlocks.CAVE_PORTAL.get().getDefaultState();
        world.setBlockState(pos, teleporter);
    }

    private boolean isAirOrStone(Chunk chunk, BlockPos pos) {
        BlockState state = chunk.getBlockState(pos);
        return state.getBlock().equals(Blocks.STONE) || state.isAir();
    }

    private boolean isReplaceable(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock().equals(Blocks.STONE) ||
                state.getBlock().equals(Blocks.GRANITE) ||
                state.getBlock().equals(Blocks.ANDESITE) ||
                state.getBlock().equals(Blocks.DIORITE) ||
                state.getBlock().equals(Blocks.DIRT) ||
                state.getBlock().equals(Blocks.GRAVEL) ||
                state.getBlock().equals(Blocks.LAVA) ||
                state.isAir();
    }

    private boolean isValidTeleporterPos(Chunk chunk, BlockPos chunkPos, boolean allowWater) {
        if (chunk.getBlockState(chunkPos).isAir() &&
                chunk.getBlockState(chunkPos.up(1)).isAir() &&
                chunk.getBlockState(chunkPos.up(2)).isAir()) {
            //BlockState state = world
        }
        return false;
    }

    private boolean isValidTeleporterPos(Chunk chunk, BlockPos pos) {
        return isValidTeleporterPos(chunk, pos, false);
    }

    private BlockPos placeSurfaceTeleporter(ServerWorld world, Chunk chunk) {

        BlockPos.Mutable chunkPos = new BlockPos.Mutable();
        for (int y = 253; y > 0; y--) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunkPos.setPos(x, y, z);
                    if (isValidTeleporterPos(chunk, chunkPos)) {
                        BlockPos worldPos = chunk.getPos().asBlockPos().add(chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
                        placeTeleporterInWorld(world, worldPos);
                        return worldPos;
                    }
                    if (chunk.getBlockState(chunkPos).isAir() &&
                            chunk.getBlockState(chunkPos.up(1)).isAir() &&
                            chunk.getBlockState(chunkPos.up(2)).isAir()

                    ) {
                        BlockPos worldPos = chunk.getPos().asBlockPos().add(chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
                        placeTeleporterInWorld(world, worldPos);
                        return worldPos;
                    }
                }
            }
        }

        // TODO: handle surface water situations...

        return null;
    }

    private BlockPos placeTeleporterInOverworld(ServerWorld world, Chunk chunk) {
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 63; y < 255; y++) {
                    pos.setPos(x, y, z);
                    if (chunk.getBlockState(pos).isAir() && chunk.getBlockState(pos.up(1)).isAir()) {
                        BlockPos absolutePos = chunk.getPos().asBlockPos().add(pos.getX(), pos.getY(), pos.getZ());
                        placeTeleporterInWorld(world, absolutePos);
                        return absolutePos;
                    }
                }
            }
        }
        return null;
    }

    private ServerPlayerEntity teleportPlayer(BlockPos fromPos, ServerWorld destWorld, ServerPlayerEntity player) {

        // locate destination world teleporter
        //destPos = findTeleporter(destWorld, fromPos);

        // look for existing teleporter near player's position
        Chunk chunk = (Chunk) destWorld.getChunk(this.fromPos);
        BlockPos teleporterPos = findTeleporterInChunk(chunk);
        if (teleporterPos == null) {

            // if no teleporter found place one at an appropriate location
            if (destWorld.getDimensionKey().equals(ModDimensions.ALTOVER_DIM)) {
                teleporterPos = placeSurfaceTeleporter(destWorld, chunk);
            } else {
                teleporterPos = placeTeleporterInOverworld(destWorld, chunk);
            }

            // trouble, no suitable location found for teleporter block...
            if (teleporterPos == null) {
                LOGGER.warn("No suitable teleport location found in destination dimension...");
                return null;
            }
        }

        // perhaps this could add negative exp levels to
        // implement exp cost for teleporting?
        // player.addExperienceLevel(0);

        // set player position centered above teleporter block
        player.setPositionAndUpdate(teleporterPos.getX() + 0.5D, teleporterPos.getY() + 1D, teleporterPos.getZ() + 0.5D);

        return player;
    }

    private BlockPos findDestPos(BlockPos fromPos, ServerWorld destWorld) {
        return fromPos;
    }

    @Override
    public Entity placeEntity(Entity srcEntity, ServerWorld fromWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> createInDest)
    {
        // prepare to generate player entity in destination world
        Entity destEntity = createInDest.apply(false);
        if (!(destEntity instanceof ServerPlayerEntity)) {
            return destEntity;
        }

        // locate destination world arrival position
        BlockPos destPos = findDestPos(fromPos, destWorld);
        if (destPos == null) {
            return destEntity;
        }

        // return the teleported player entity
        return teleportPlayer(fromPos, destWorld, (ServerPlayerEntity)destEntity);
    }
}
