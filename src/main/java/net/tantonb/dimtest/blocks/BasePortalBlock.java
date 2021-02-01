package net.tantonb.dimtest.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.tantonb.dimtest.dimensions.PortalSender;

import javax.annotation.Nonnull;

abstract public class BasePortalBlock extends Block {

    /*
        Portal idea notes...

        portal variations:

            single block
            frame (e.g. vanilla nether portal)
            other - multi block structure
                structure rules
                    spatial placement
                    location
                    block roles
                    state requirements
                        world/chunk/player/block

        portal api:

            setDestination(...) -

            isPaired() - requires remote paired portal to operate
            isAutoPairing() - will remote paired portal be automatically created if needed?
            hasRemotePair() - does remote paired portal exist?
            getRemotePair() - get remote paired portal if it exists
            setRemotePair(...) - assign remote paired portal
            pairPortals(...) - assigns two remote paired portals to each other

            isCloseable() - can be closed/opened
            isOpen() - is portal open?
            close()
            open()

            isLockable() - can be locked/unlocked
            isLocked() - is portal locked?
            lock()
            unlock()

            Chargeable vs. Powered
                power could b RF energy or custom power system
                charges could be used to limit life of portals, charge depletion could result in some event
                    portal destroyed or permanently deactivated
                power could be consumed continuously to maintain connection with remote pair
                power could be used like charges at time of use
                power could be buffered like charges/battery
                charges could be represented by items
                    items can be consumed
                    items can be replenished

            Charging interface:

            isChargeable() - can be charged
            hasCharge() - has enough charge for one use
            getCharge() - return charge amount
            getChargeCost() - return charge cost of use
            addCharge(...) - add charge amount
            removeCharge(...) - subtract charge amount
            getMaxCharge() - max charge storage

            These may be redundant with forge energy capability system...

            isPowered() - requires power to operate
            hasPower() - has enough power to operate
            getPowerCost() - return power cost per tick
            addPower(...) - add power amount
            removePower(...) - subtract power amount
            getMaxPower(...) - max power storage

            send(...) - send entity through portal (player, etc.)

    */

    public BasePortalBlock(Properties properties) {
        super(properties);
    }

    public BasePortalBlock() {
        this(Properties.create(Material.WOOD).hardnessAndResistance(3F).sound(SoundType.WOOD));
    }

    // defaults world key a to overworld, override as necessary
    public RegistryKey<World> getWorldKeyA() {
        return World.OVERWORLD;
    }

    public abstract RegistryKey<World> getWorldKeyB();

    private ServerWorld getRemoteWorld(ServerPlayerEntity player) {

        // determine remote world key based on player's current world
        RegistryKey<World> localKey = player.world.getDimensionKey();
        RegistryKey<World> remoteKey;
        if (localKey.equals(getWorldKeyA())) {
            remoteKey = getWorldKeyB();
        } else if (localKey.equals(getWorldKeyB())) {
            remoteKey = getWorldKeyA();
        } else {
            // portals only function between two specific dimensions
            LOGGER.info(
                    "Portal block between '{}' and '{}' does not work in player's current world '{}'",
                    getWorldKeyA().getRegistryName(),
                    getWorldKeyB().getRegistryName(),
                    localKey);
            return null;
        }

        // fetch remote world instance using the determined key
        ServerWorld remoteWorld = player.server.getWorld(remoteKey);
        if (remoteWorld == null) {
            LOGGER.error("Could not find destination world '{}'", remoteKey.getRegistryName());
            return null;
        }
        return remoteWorld;
    }

    private boolean isTeleportAllowed(ServerPlayerEntity player) {

        // look for conditions disallowing teleportation
        if (player.getRidingEntity() != null) {
            LOGGER.info("Player may not teleport while riding");
            return false;
        } else if (player.isBeingRidden()) {
            LOGGER.info("Player may not teleport while being ridden");
            return false;
        }
        return true;
    }

    // needs to return a dimensional teleporter to manage the actual
    // teleportation of the player to the remote dimension
    public abstract PortalSender getSender(ServerWorld remoteWorld, BlockPos localPos);

    protected boolean send(ServerPlayerEntity player, BlockPos localPos) {

        // make sure player is allowed to teleport
        if (!isTeleportAllowed(player)) {
            return false;
        }

        // determine remote world to send player to
        ServerWorld remoteWorld = getRemoteWorld(player);
        if (remoteWorld == null) {
            return false;
        }

        // send player to remote world
        return getSender(remoteWorld, localPos).send(player);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    // deprecated in AbstractBlockState...so what's correct way to handle this?
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult rtResult) {

        // pass if this is not on the server (?)
        if (!(entity instanceof ServerPlayerEntity)) {
            return ActionResultType.PASS;
        }

        // teleport player to remote world
        ServerPlayerEntity player = (ServerPlayerEntity)entity;
        return send(player, pos) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
    }

    public abstract boolean matchesPortalTE(TileEntity te);
}
