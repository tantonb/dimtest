package net.tantonb.dimtest.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.energy.IEnergyStorage;

abstract public class BasePortalBlock extends Block {

    /*
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
            removePower(...) - subtract power amount`
            getMaxPower(...) - max power storage

            send(...) - send entity through portal (player, etc.)

    */

    private RegistryKey<World> srcWorld;
    private RegistryKey<World> destWorld;

    public BasePortalBlock(Properties properties) {
        super(properties);
    }

    public BasePortalBlock() {
        this(Properties.create(Material.WOOD).hardnessAndResistance(3F).sound(SoundType.WOOD));
    }

    public RegistryKey<World> getHomeWorldKey() {
        return World.OVERWORLD;
    };

    public abstract RegistryKey<World> getAwayWorldKey();

    public abstract ITeleporter getTeleporter(BlockPos pos);

    public boolean isHome(ServerPlayerEntity player) {
        return player.world.getDimensionKey().equals(getHomeWorldKey());
    }

    public boolean isAway(ServerPlayerEntity player) {
        return player.world.getDimensionKey().equals(getAwayWorldKey());
    }

    protected boolean teleport(ServerPlayerEntity player, BlockPos pos) {

        // look for conditions disallowing teleport
        if (player.getRidingEntity() != null || player.isBeingRidden()) {
            return false;
        }

        ServerWorld destWorld = null;
        RegistryKey<World> destKey = null;
        if (isHome(player)) {
            destKey = getAwayWorldKey();
        } else if (isAway(player)) {
            destKey = getHomeWorldKey();
        } else {
            LOGGER.info("Attempt to use teleporter from invalid world");
            return false;
        }
        destWorld = player.server.getWorld(destKey);
        if (destWorld == null) {
            LOGGER.error("Could not find destination world '{}'", destKey.getRegistryName());
            return false;
        }

        player.changeDimension(destWorld, getTeleporter(pos));
        return true;
    }

}
