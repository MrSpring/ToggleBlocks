package dk.mrspring.toggle;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.api.IToggleStorage;
import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.block.BlockChangeBlock;
import dk.mrspring.toggle.block.BlockToggleController;
import dk.mrspring.toggle.block.ControllerInfo;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
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
        TileEntity entity = event.world.getTileEntity(x, y, z);
        if (block == BlockBase.change_block)
            this.breakChangeBlock(event.world, x, y, z, event.getPlayer());
        else if (entity instanceof IToggleController)
            this.breakToggleController(event.world, x, y, z, event.getPlayer(), (IToggleController) entity);
    }

    @SubscribeEvent
    public void blockPlaceEvent(BlockEvent.PlaceEvent event)
    {
        int x = event.x, y = event.y, z = event.z;
        TileEntity entity = event.world.getTileEntity(x, y, z);
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
        }
    }

    private void breakToggleController(World world, int x, int y, int z, EntityPlayer player,
                                       IToggleController controller)
    {
        int size = controller.getMaxChangeBlocks();
        int metadata = world.getBlockMetadata(x, y, z);
        ItemStack controllerStack = BlockToggleController.createToggleController(size, 1, metadata);
        System.out.println(player.capabilities.isCreativeMode + ", " + breakDrop(world));
        if (!player.capabilities.isCreativeMode && breakDrop(world))
        {
            System.out.println("Spawn");
            spawnItemStack(world, x + .5, y + .5, z + .5, controllerStack, new Random());
        }
        controller.resetAllChangeBlocks();
        ControllerInfo info = new ControllerInfo(x, y, z);
        BlockChangeBlock.clearChangeBlockFromInventory(player.inventory, info);
        IToggleStorage storage = controller.getStorageHandler();
        Random rand = new Random();
        for (int s = 0; s < storage.getStorageSlots(); s++)
            spawnItemStack(world, x, y, z, storage.getItemFromSlot(s), rand);
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

    private boolean breakDrop(World world)
    {
        return world.getGameRules().getGameRuleBooleanValue("doTileDrops"); // Checks doTileDrops game rule
    }
}
