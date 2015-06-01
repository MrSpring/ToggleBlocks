package dk.mrspring.toggle;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;

/**
 * Created by Konrad on 01-06-2015.
 */
public class EventHandler
{
    @SubscribeEvent
    public void blockBreakEvent(BlockEvent.BreakEvent event)
    {
        TileEntity entity = event.world.getTileEntity(event.x, event.y, event.z);
        if (entity != null && entity instanceof TileEntityChangeBlock)
        {
            TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) entity;
            int cx = changeBlock.getCx(), cy = changeBlock.getCy(), cz = changeBlock.getCz();
            TileEntity tileEntity = event.world.getTileEntity(cx, cy, cz);
            if (tileEntity != null && tileEntity instanceof IToggleController)
                ((IToggleController) tileEntity).unregisterChangeBlock(event.x, event.y, event.z);
        }
    }
}
