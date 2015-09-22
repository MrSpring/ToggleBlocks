package dk.mrspring.toggle;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.block.BlockChangeBlock;
import dk.mrspring.toggle.block.BlockToggleController;
import dk.mrspring.toggle.block.ControllerInfo;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
        Block block = event.block;
        if (block == BlockBase.change_block)
            this.breakChangeBlock(event.world, x, y, z, event.getPlayer());
        else if (block == BlockBase.toggle_controller)
            this.breakToggleBlock(event.world, x, y, z, event.getPlayer());
    }

    @SubscribeEvent
    public void blockPlaceEvent(BlockEvent.PlaceEvent event)
    {
        int x = event.x, y = event.y, z = event.z;
        Block block = event.block;
        TileEntity entity = event.world.getTileEntity(x, y, z);
        if (block == BlockBase.toggle_controller) // TODO: Test with tile entity
        {
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                ItemStack[] drops = controller.createChangeBlockDrop();
                for (ItemStack stack : drops)
                {
                    if (stack.getItem() == Item.getItemFromBlock(BlockBase.change_block))
                        BlockChangeBlock.updateRemainingChangeBlocks(stack, event.world);
                    spawnItemStack(event.world, event.player, stack);
                }
//                BlockChangeBlock.updateRemainingChangeBlocks(event.player, new ControllerInfo(x, y, z), event.world);
            }
        } /*else if (block == BlockBase.change_block)
        {
            if (entity instanceof TileEntityChangeBlock)
            {
                TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) entity;
                ControllerInfo info = new ControllerInfo(changeBlock.getCx(), changeBlock.getCy(), changeBlock.getCz());
                System.out.println("Updating. info: "+info.toString());
                BlockChangeBlock.updateRemainingChangeBlocks(event.player, info, event.world);
            }
        }*/
    }

    private void breakToggleBlock(World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntity entity = world.getTileEntity(x, y, z);
        if (entity == null || !(entity instanceof TileEntityToggleBlock)) return;
        IToggleController controller = (IToggleController) entity;
        int size = controller.getMaxChangeBlocks();
        int metadata = world.getBlockMetadata(x, y, z);
        ItemStack controllerStack = BlockToggleController.createToggleController(size, 1, metadata);
        System.out.println(player.capabilities.isCreativeMode+", "+breakDrop(world));
        if (!player.capabilities.isCreativeMode && breakDrop(world))
        {
            System.out.println("Spawn");
            spawnItemStack(world, x + .5, y + .5, z + .5, controllerStack, new Random());
        }
        controller.resetAllChangeBlocks();
        ControllerInfo info = new ControllerInfo(x, y, z);
        BlockChangeBlock.clearChangeBlockFromInventory(player.inventory, info);
    }

    private void breakChangeBlock(World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntity entity = world.getTileEntity(x, y, z);
        if (entity == null || !(entity instanceof TileEntityChangeBlock)) return;
        TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) entity;
        ControllerInfo info = new ControllerInfo(changeBlock);
        TileEntity tileEntity = world.getTileEntity(info.x, info.y, info.z);
        if (tileEntity instanceof IToggleController)
        {
            IToggleController controller = (IToggleController) tileEntity;
            controller.unregisterChangeBlock(x, y, z);
            BlockChangeBlock.updateRemainingChangeBlocks(player, info, world);
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

    private void spawnItemStack(World world, EntityPlayer player, ItemStack stack)
    {
        EntityItem entityItem = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
        world.spawnEntityInWorld(entityItem);
    }

    private boolean breakDrop(World world)
    {
        return world.getGameRules().getGameRuleBooleanValue("doTileDrops"); // Checks doTileDrops game rule
    }
}
