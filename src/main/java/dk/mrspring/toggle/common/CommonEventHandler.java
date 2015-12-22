package dk.mrspring.toggle.common;

import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.common.tileentity.TileEntityChangeBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class CommonEventHandler
{
    @SubscribeEvent
    public void blockPlaceEvent(BlockEvent.PlaceEvent event)
    {
        TileEntity entity = event.world.getTileEntity(event.pos);
        if (entity instanceof IToggleController)
        {
            IToggleController controller = (IToggleController) entity;
            ItemStack[] drops = controller.createChangeBlockDrop();
            for (ItemStack stack : drops) spawnItemStack(event.world, event.player, stack);
        }
    }

    @SubscribeEvent
    public void blockDestroyEvent(BlockEvent.BreakEvent event)
    {
        TileEntity entity = event.world.getTileEntity(event.pos);
        if (entity instanceof TileEntityChangeBlock)
            ((TileEntityChangeBlock) entity).onDestroyed();
    }

    private void spawnItemStack(World world, double x, double y, double z, ItemStack stack, Random random)
    {
        if (stack != null && stack.stackSize > 0)
        {
            float xRand = random.nextFloat() * 0.8F + 0.1F;
            float yRand = random.nextFloat() * 0.8F + 0.1F;
            float zRand = random.nextFloat() * 0.8F + 0.1F;
            EntityItem entityitem = new EntityItem(world,
                    ((float) x + xRand), ((float) y + yRand), ((float) z + zRand), stack.copy());
            float f = 0.05F;
            entityitem.motionX = (double) ((float) random.nextGaussian() * f);
            entityitem.motionY = (double) ((float) random.nextGaussian() * f + 0.2F);
            entityitem.motionZ = (double) ((float) random.nextGaussian() * f);
            world.spawnEntityInWorld(entityitem);
        }
    }

    private void spawnItemStack(World world, EntityPlayer player, ItemStack stack)
    {
        EntityItem entityItem = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
        world.spawnEntityInWorld(entityItem);
    }
}
