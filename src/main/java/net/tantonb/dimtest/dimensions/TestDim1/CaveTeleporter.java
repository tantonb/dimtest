package net.tantonb.dimtest.dimensions.TestDim1;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.blocks.ModBlocks;
import net.tantonb.dimtest.dimensions.ModDimensions;
import net.tantonb.dimtest.tileentity.TestDim1TE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

/**
 * Handles teleportation between test dimension 1 teleporter blocks in the overworld and the test dimension.
 *
 * Implements forge ITeleporter.  The placeEntity() method is triggered by TestDim1TeleporterBlock activation.
 */
public class CaveTeleporter implements ITeleporter {

    private static final Logger LOGGER = LogManager.getLogger(DimTestMod.MODID);

    private BlockPos pos;

    public CaveTeleporter(BlockPos pos) {
        this.pos = pos;
    }

    private BlockPos findTeleporterInChunk(Chunk chunk) {

        // scan tile entities in chunk, return position of first teleporter found
        for (TileEntity tile : chunk.getTileEntityMap().values()) {
            if (tile instanceof TestDim1TE) {
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

    private BlockPos placeTeleporterInTestDim1(ServerWorld world, Chunk chunk) {

        BlockPos.Mutable chunkPos = new BlockPos.Mutable();
        for (int y = 0; y < 255; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunkPos.setPos(x, y, z);
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

        for (int y = 11; y < 255; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunkPos.setPos(x, y, z);
                    if (isAirOrStone(chunk, chunkPos) &&
                            isAirOrStone(chunk, chunkPos.up(1)) &&
                            isAirOrStone(chunk, chunkPos.up(2))) {
                        BlockPos worldPos = chunk.getPos().asBlockPos().add(chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
                        if (isReplaceable(world, worldPos.up(3)) &&
                                isReplaceable(world, worldPos.up(1).offset(Direction.NORTH)) &&
                                isReplaceable(world, worldPos.up(1).offset(Direction.SOUTH)) &&
                                isReplaceable(world, worldPos.up(1).offset(Direction.EAST)) &&
                                isReplaceable(world, worldPos.up(1).offset(Direction.WEST)) &&
                                isReplaceable(world, worldPos.up(2).offset(Direction.NORTH)) &&
                                isReplaceable(world, worldPos.up(2).offset(Direction.SOUTH)) &&
                                isReplaceable(world, worldPos.up(2).offset(Direction.EAST)) &&
                                isReplaceable(world, worldPos.up(2).offset(Direction.WEST))) {

                            placeTeleporterInWorld(world, worldPos);
                            world.setBlockState(worldPos.up(1), Blocks.AIR.getDefaultState());
                            world.setBlockState(worldPos.up(2), Blocks.AIR.getDefaultState());
                            world.setBlockState(worldPos.up(3), Blocks.STONE.getDefaultState());
                            world.setBlockState(worldPos.up(1).offset(Direction.NORTH), Blocks.STONE.getDefaultState());
                            world.setBlockState(worldPos.up(1).offset(Direction.SOUTH), Blocks.STONE.getDefaultState());
                            world.setBlockState(worldPos.up(1).offset(Direction.EAST), Blocks.STONE.getDefaultState());
                            world.setBlockState(worldPos.up(1).offset(Direction.WEST), Blocks.STONE.getDefaultState());
                            world.setBlockState(worldPos.up(2).offset(Direction.NORTH), Blocks.STONE.getDefaultState());
                            world.setBlockState(worldPos.up(2).offset(Direction.SOUTH), Blocks.STONE.getDefaultState());
                            world.setBlockState(worldPos.up(2).offset(Direction.EAST), Blocks.STONE.getDefaultState());
                            world.setBlockState(worldPos.up(2).offset(Direction.WEST), Blocks.STONE.getDefaultState());
                            return worldPos;
                        }
                    }
                }
            }
        }

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

    private ServerPlayerEntity sendPlayerToDest(ServerWorld world, ServerPlayerEntity player) {

        // look for existing teleporter near player's position
        Chunk chunk = (Chunk) world.getChunk(pos);
        BlockPos teleporterPos = findTeleporterInChunk(chunk);
        if (teleporterPos == null) {

            // if no teleporter found place one at an appropriate location
            if (world.getDimensionKey().equals(ModDimensions.CAVE_DIM)) {
                teleporterPos = placeTeleporterInTestDim1(world, chunk);
            } else {
                teleporterPos = placeTeleporterInOverworld(world, chunk);
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

    @Override
    public Entity placeEntity(Entity srcEntity, ServerWorld srcWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> createAtDest)
    {
        Entity destEntity = createAtDest.apply(false);
        if (destEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = sendPlayerToDest(destWorld, (ServerPlayerEntity)destEntity);
            if (player != null) {
                destEntity = player;
            }
        }
        return destEntity;
    }
}
