package net.tantonb.dimtest.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.tantonb.dimtest.DimTestMod;
import net.tantonb.dimtest.tileentity.AltoverPortalTE;
import net.tantonb.dimtest.world.ModDimensions;
import net.tantonb.dimtest.world.PortalSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Some example mods working in 1.16.5: Advanced Mining Dimension and Ultra Amplified Dimension, both using
 * teleporter blocks.
 */
public class DimxPortalBlock extends BasePortalBlock {

    private static final Logger LOGGER = LogManager.getLogger(DimTestMod.MODID);

    public RegistryKey<World> getWorldKeyB() { return ModDimensions.RK_DIMX_WORLD; }

    public PortalSender getSender(ServerWorld remoteWorld, BlockPos localPos) {
        return new PortalSender(remoteWorld, localPos, this);
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult rtResult) {
        entity.sendStatusMessage(new StringTextComponent("Activating altover teleporter..."), true);
        return super.onBlockActivated(state, world, pos, entity, hand, rtResult);
    }

    /**
     * Safe to call serverside as it sends particle packets to clients
     * (FROM UAD)
     */
    public static void createLotsOfParticles(ServerWorld world, Vector3d position, Random random) {
        double xPos = position.getX() + 0.5D;
        double yPos = position.getY() + 0.5D;
        double zPos = position.getZ() + 0.5D;
        double xOffset = (random.nextFloat() - 0.4D) * 0.8D;
        double zOffset = (random.nextFloat() - 0.4D) * 0.8D;

        world.spawnParticle(ParticleTypes.FLAME, xPos, yPos, zPos, 50, xOffset, 0, zOffset, random.nextFloat() * 0.1D + 0.05D);
    }

    /**
     * Spawns with tons of particles upon creation
     * (FROM UAD)
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        createLotsOfParticles((ServerWorld)world, new Vector3d(pos.getX(), pos.getY(), pos.getZ()), world.rand);
    }

    /**
     * more frequent particles than normal EndPortal block
     * (FROM UAD)
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
        double d0 = pos.getX() + (rand.nextFloat() * 3 - 1);
        double d1 = pos.getY() + (rand.nextFloat() * 3 - 1);
        double d2 = pos.getZ() + (rand.nextFloat() * 3 - 1);
        world.addParticle(ParticleTypes.SOUL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new AltoverPortalTE(); }

    public boolean matchesPortalTE(TileEntity te) { return te instanceof AltoverPortalTE; }
}
