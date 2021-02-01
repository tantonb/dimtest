package net.tantonb.dimtest.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class PortalTE extends TileEntity {

    public PortalTE(TileEntityType<?> teType) {
        super(teType);
    }
    public PortalTE() {
        this(ModTileEntities.PORTAL_TE.get());
    }
}
