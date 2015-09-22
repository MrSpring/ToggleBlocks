package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by MrSpring on 28-02-2015 for ToggleBlocks.
 */
public class BlockChangeBlock extends BlockContainer
{
    public static final String REMAINING_CHANGE_BLOCKS = "RemainingChangeBlocks";

    public static int renderId;

    public static boolean doesPlayerHaveChangeBlockInInventory(InventoryPlayer player, ControllerInfo info)
    {
        return getChangeBlockFromInventory(player, info) != -1;
    }

    public static int getChangeBlockFromInventory(InventoryPlayer player, ControllerInfo info)
    {
        for (int slot = 0; slot < player.getSizeInventory(); slot++)
        {
            ItemStack inSlot = player.getStackInSlot(slot);
            if (inSlot == null) continue;
            ControllerInfo fromInSlot = new ControllerInfo(inSlot);
            if (info.equals(fromInSlot)) return slot;
        }
        return -1;
    }

    public static boolean updateRemainingChangeBlocks(EntityPlayer player, ControllerInfo info, World world)
    {
        int slot = getChangeBlockFromInventory(player.inventory, info);
        if (slot == -1)
        {
            ItemStack stack = createChangeBlock(info, 1);
            updateRemainingChangeBlocks(stack, world);
            Entity entity = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
            world.spawnEntityInWorld(entity);
            return false;
        } else
        {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            updateRemainingChangeBlocks(stack, world);
            if (stack.stackSize == 0 && !player.capabilities.isCreativeMode)
                player.inventory.setInventorySlotContents(slot, null);
            else stack.stackSize = 1;
            return true;
        }
    }

    public static void clearChangeBlockFromInventory(InventoryPlayer player, ControllerInfo info)
    {
        int slot = getChangeBlockFromInventory(player, info);
        if (slot != -1) player.setInventorySlotContents(slot, null);
    }

    public static void updateRemainingChangeBlocks(ItemStack stack, World world)
    {
        ControllerInfo info = new ControllerInfo(stack);
        if (info.initialized)
        {
            int x = info.x, y = info.y, z = info.z;
            TileEntity entity = world.getTileEntity(x, y, z);
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                int max = controller.getMaxChangeBlocks();
                int remaining = max != -1 ? controller.getMaxChangeBlocks() - controller.getRegisteredChangeBlockCount() : -1;
                if (remaining == 0)
                    stack.stackSize = 0;
                stack.setTagInfo(REMAINING_CHANGE_BLOCKS, new NBTTagInt(remaining));
            }
        }
    }

    public static int getRemainingChangeBlocks(ItemStack stack, World world)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.getTagCompound().hasKey(REMAINING_CHANGE_BLOCKS, 3)) updateRemainingChangeBlocks(stack, world);
        return stack.getTagCompound().getInteger(REMAINING_CHANGE_BLOCKS);
    }

    public static ItemStack createChangeBlock(ControllerInfo info, int stackSize)
    {
        ItemStack stack = new ItemStack(BlockBase.change_block, stackSize);
        BlockToggleController.populateChangeBlock(stack, info);
        return stack;
    }

    IIcon upIcon, downIcon, leftIcon, rightIcon, frontIcon;
    IIcon[] down, up, north, south, east, west;

    public BlockChangeBlock()
    {
        super(Material.iron);

        this.setBlockName("change_block");
        this.setBlockTextureName("tb:change_block");

        this.setHardness(5.0F);
    }

    @Override
    public int getRenderType()
    {
        return renderId;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);

        upIcon = register.registerIcon(getTextureName() + "_up");
        downIcon = register.registerIcon(getTextureName() + "_down");
        leftIcon = register.registerIcon(getTextureName() + "_left");
        rightIcon = register.registerIcon(getTextureName() + "_right");
        frontIcon = register.registerIcon(getTextureName() + "_front");

        down = new IIcon[]{frontIcon, blockIcon, downIcon, downIcon, downIcon, downIcon};
        up = new IIcon[]{blockIcon, frontIcon, upIcon, upIcon, upIcon, upIcon};
        south = new IIcon[]{upIcon, upIcon, frontIcon, blockIcon, leftIcon, rightIcon};
        north = new IIcon[]{downIcon, downIcon, blockIcon, frontIcon, rightIcon, leftIcon};
        west = new IIcon[]{leftIcon, leftIcon, rightIcon, leftIcon, frontIcon, blockIcon};
        east = new IIcon[]{rightIcon, rightIcon, leftIcon, rightIcon, blockIcon, frontIcon};
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        final int UP = 0, DOWN = 1, NORTH = 2, EAST = 4, WEST = 5, SOUTH = 3;

        switch (metadata)
        {
            case UP:
                return up[side];
            case DOWN:
                return down[side];
            case NORTH:
                return north[side];
            case SOUTH:
                return south[side];
            case EAST:
                return east[side];
            case WEST:
                return west[side];
            default:
                return super.getIcon(side, metadata);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_,
                                    float p_149727_8_, float p_149727_9_)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || player.isSneaking())
        {
            world.setBlockMetadataWithNotify(x, y, z, side, 2);
            return true;
        }
        player.openGui(ToggleBlocks.instance, 1, world, x, y, z);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack placed)
    {
        if (world.isRemote) return;
        ControllerInfo info = new ControllerInfo(placed);
        if (info.initialized)
        {
            TileEntity entity = world.getTileEntity(info.x, info.y, info.z);
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                controller.registerChangeBlock(x, y, z);
                updateRemainingChangeBlocks((EntityPlayer) player, info, world);
            }
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof TileEntityChangeBlock))
            return super.getPickBlock(target, world, x, y, z, player);
        ControllerInfo info = new ControllerInfo((TileEntityChangeBlock) tileEntity);
        return createChangeBlock(info, 1);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityChangeBlock();
    }
}
