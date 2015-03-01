package dk.mrspring.toggle.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Created by Konrad on 01-03-2015.
 */
public class ItemBlockChangeBlock extends ItemBlock
{
    public ItemBlockChangeBlock(Block block)
    {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean p_77624_4_)
    {
        super.addInformation(stack, player, lines, p_77624_4_);

        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null)
            return;
        NBTTagCompound controllerInfo = compound.getCompoundTag("ControllerInfo");
        int controllerX = controllerInfo.getInteger("X");
        int controllerY = controllerInfo.getInteger("Y");
        int controllerZ = controllerInfo.getInteger("Z");
        lines.add("Linked to controller: " + controllerX + ", " + controllerY + ", " + controllerZ);
    }
}
