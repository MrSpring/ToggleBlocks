package dk.mrspring.toggle;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.block.BlockToggleController;
import dk.mrspring.toggle.tileentity.ControllerSize;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
        Block block = event.world.getBlock(x, y, z);
        if (block == BlockBase.change_block)
            this.breakChangeBlock(event.world, x, y, z, event.getPlayer());
        else if (block == BlockBase.toggle_controller && breakDrop(event.world))
            this.breakToggleBlock(event.world, x, y, z, event.getPlayer());
    }

    private void breakToggleBlock(World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntity entity = world.getTileEntity(x, y, z);
        if (entity == null || !(entity instanceof TileEntityToggleBlock) || player.capabilities.isCreativeMode) return;
        TileEntityToggleBlock controller = (TileEntityToggleBlock) entity;
        ControllerSize size = controller.getControllerSize();
        int metadata = world.getBlockMetadata(x, y, z);
        ItemStack controllerStack = BlockToggleController.createToggleController(size, 1, metadata);
        spawnItemStack(world, x + .5, y + .5, z + .5, controllerStack, new Random());
    }

    private void breakChangeBlock(World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntity entity = world.getTileEntity(x, y, z);
        if (entity == null || !(entity instanceof TileEntityChangeBlock)) return;
        TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) entity;
        int cX = changeBlock.getCx(), cY = changeBlock.getCy(), cZ = changeBlock.getCz(); // Controller coords.
        TileEntity controller = world.getTileEntity(cX, cY, cZ);
        if (controller != null && controller instanceof IToggleController)
        {
            ((IToggleController) controller).unregisterChangeBlock(x, y, z);
            ItemStack[] drops = ((IToggleController) controller).createChangeBlockDrop(x, y, z);

            if (drops != null && drops.length > 0 && !player.capabilities.isCreativeMode && breakDrop(world))
            {
                Random random = new Random();
                for (ItemStack stack : drops)
                    spawnItemStack(world, player.posX, player.posY, player.posZ, stack, random);
            }
        }
    }

    private void spawnItemStack(World world, double x, double y, double z, ItemStack stack, Random random)
    {
        EntityItem entityitem = new EntityItem(world, x, y, z, stack);
        float f3 = 0.05F;
        entityitem.motionX = (double) ((float) random.nextGaussian() * f3);
        entityitem.motionY = (double) ((float) random.nextGaussian() * f3 + 0.2F);
        entityitem.motionZ = (double) ((float) random.nextGaussian() * f3);
        world.spawnEntityInWorld(entityitem);
    }

    private boolean breakDrop(World world)
    {
        return world.getGameRules().getGameRuleBooleanValue("doTileDrops"); // Checks doTileDrops game rule
    }
}
