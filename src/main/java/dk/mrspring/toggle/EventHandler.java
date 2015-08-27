package dk.mrspring.toggle;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;

import java.util.Random;

/**
 * Created by Konrad on 01-06-2015.
 */
public class EventHandler
{
    @SubscribeEvent
    public void blockBreakEvent(BlockEvent.BreakEvent event)
    {
        int x = event.x, y = event.y, z = event.z;
        TileEntity entity = event.world.getTileEntity(x, y, z);
        if (entity == null || !(entity instanceof TileEntityChangeBlock)) return;
        TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) entity;
        int cX = changeBlock.getCx(), cY = changeBlock.getCy(), cZ = changeBlock.getCz(); // Controller coords.
        TileEntity controller = event.world.getTileEntity(cX, cY, cZ);
        if (controller != null && controller instanceof IToggleController)
        {
            ((IToggleController) controller).unregisterChangeBlock(x, y, z);
            ItemStack[] drops = ((IToggleController) controller).createChangeBlockDrop(x, y, z);

            if (drops != null && drops.length > 0 && !event.getPlayer().capabilities.isCreativeMode)
            {
                Random random = new Random();
                EntityPlayer p = event.getPlayer();
                for (ItemStack itemstack : drops)
                {
                    float randX = random.nextFloat() * 0.8F + 0.1F;
                    float randY = random.nextFloat() * 0.8F + 0.1F;
                    float randZ = random.nextFloat() * 0.8F + 0.1F;

                    EntityItem entityitem = new EntityItem(event.world,
                            p == null ? (double) ((float) x + randX) : p.posX,
                            p == null ? (double) ((float) y + randY) : p.posY,
                            p == null ? (double) ((float) z + randZ) : p.posZ, itemstack);

                    float f3 = 0.05F;
                    entityitem.motionX = (double) ((float) random.nextGaussian() * f3);
                    entityitem.motionY = (double) ((float) random.nextGaussian() * f3 + 0.2F);
                    entityitem.motionZ = (double) ((float) random.nextGaussian() * f3);
                    event.world.spawnEntityInWorld(entityitem);
                }
            }
        }
    }
}
