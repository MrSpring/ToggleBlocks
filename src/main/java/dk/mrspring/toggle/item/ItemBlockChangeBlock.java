package dk.mrspring.toggle.item;

import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.block.ControllerInfo;
import dk.mrspring.toggle.util.Translator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Konrad on 01-03-2015.
 */
public class ItemBlockChangeBlock extends ItemBlock
{
    public ItemBlockChangeBlock(Block block)
    {
        super(block);

        this.setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean p_77624_4_)
    {
        super.addInformation(stack, player, lines, p_77624_4_);
        ControllerInfo info = new ControllerInfo(stack);
        String s = Translator.translate("tile.change_block.desc", info.pos.getX(), info.pos.getY(), info.pos.getZ());
        lines.add(s);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                             float clickX, float clickY, float clickZ)
    {
        boolean result = super.onItemUse(stack, player, world, pos, side, clickX, clickY, clickZ);
        if (stack.stackSize == 0)
        {
            ControllerInfo info = new ControllerInfo(stack);
            if (!info.initialized) return result;
            TileEntity entity = world.getTileEntity(info.pos);
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                if (controller.canRegisterAnotherChangeBlock()) stack.stackSize = 1;
            }
        }
        return result;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if (world.isRemote) return false;
        ControllerInfo info = new ControllerInfo(stack);
        if (info.initialized)
        {
            TileEntity entity = world.getTileEntity(info.pos);
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                if (controller.canRegisterAnotherChangeBlock())
                {
                    return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ,
                            BlockBase.change_block.makeStateFromDirection(side.getOpposite()));
                }
                player.addChatComponentMessage(new ChatComponentText(Translator.translate("message.full_toggle_controller")));
            }
        }
        return false;
    }

}
