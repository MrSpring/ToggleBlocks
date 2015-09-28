package dk.mrspring.toggle.item;

import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.block.ControllerInfo;
import dk.mrspring.toggle.util.Translator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
        String s = Translator.translate("tile.change_block.desc", info.x, info.y, info.z);
        lines.add(s);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        boolean result = super.onItemUse(stack, player, world, x, y, z, side, clickX, clickY, clickZ);
        if (stack.stackSize == 0)
        {
            ControllerInfo info = new ControllerInfo(stack);
            if (!info.initialized) return result;
            TileEntity entity = world.getTileEntity(info.x, info.y, info.z);
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                if (controller.canRegisterAnotherChangeBlock()) stack.stackSize = 1;
            }
        }
        return result;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (world.isRemote) return false;
        ControllerInfo info = new ControllerInfo(stack);
        if (info.initialized)
        {
            TileEntity entity = world.getTileEntity(info.x, info.y, info.z);
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                if (controller.canRegisterAnotherChangeBlock())
                {
                    ForgeDirection direction = ForgeDirection.getOrientation(side);
                    return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ,
                            direction.getOpposite().ordinal());
                }
                player.addChatComponentMessage(new ChatComponentText(Translator.translate("message.full_toggle_controller")));
            }
        }
        return false;
    }

}
