package dk.mrspring.toggle.common.item;

import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.common.block.ControllerInfo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class ItemBlockChangeBlock extends ItemBlock
{
    public ItemBlockChangeBlock(Block block)
    {
        super(block);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
        /*if (!super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ)) return false;
        ControllerInfo info = new ControllerInfo(stack);
        if (!info.initialized || worldIn.isRemote) return false;
        TileEntity entity = worldIn.getTileEntity(info.pos);
        if (entity instanceof IToggleController)
        {
            IToggleController controller = (IToggleController) entity;
            if (controller.canRegisterAnotherChangeBlock())
                return true;
            else
            {
                playerIn.addChatComponentMessage(new ChatComponentText(I18n.format("message.full_toggle_controller")));
                stack.stackSize = 0;
                return false;
            }
        } else
        {
            playerIn.addChatComponentMessage(new ChatComponentText(I18n.format("message.toggle_controller_not_found")));
            stack.stackSize = 0;
            return false;
        }*/
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        ControllerInfo info = new ControllerInfo(stack);
        if (!info.initialized)
        {
            player.addChatComponentMessage(new ChatComponentText("Controller info not initialized!"));
            return false;
        }
        TileEntity entity = world.getTileEntity(info.pos);
        if (entity instanceof IToggleController)
        {
            IToggleController controller = (IToggleController) entity;
            if (controller.canRegisterAnotherChangeBlock())
                return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
            player.addChatComponentMessage(new ChatComponentText(I18n.format("message.full_toggle_controller")));
            stack.stackSize = 0;
            return false;
        } else
        {
            player.addChatComponentMessage(new ChatComponentText(I18n.format("message.toggle_controller_not_found")));
            stack.stackSize = 0;
            return false;
        }
    }
}
