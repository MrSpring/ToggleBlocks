package dk.mrspring.toggle.item;

import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.util.Translator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
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
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean p_77624_4_)
    {
        super.addInformation(stack, player, lines, p_77624_4_);

        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null || !compound.hasKey("ControllerInfo", 10)) return;
        NBTTagCompound controllerInfo = compound.getCompoundTag("ControllerInfo");
        int controllerX = controllerInfo.getInteger("X");
        int controllerY = controllerInfo.getInteger("Y");
        int controllerZ = controllerInfo.getInteger("Z");
        String s = Translator.translate("tile.change_block.desc", controllerX, controllerY, controllerZ);
        lines.add(s);
//        lines.add("Linked to controller: " + controllerX + ", " + controllerY + ", " + controllerZ);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (world.isRemote)
            return false;
        NBTTagCompound stackCompound = stack.getTagCompound();
        if (stackCompound != null && stackCompound.hasKey("ControllerInfo", 10))
        {
            NBTTagCompound controllerInfo = stackCompound.getCompoundTag("ControllerInfo");
            int cx = controllerInfo.getInteger("X"), cy = controllerInfo.getInteger("Y"), cz = controllerInfo.getInteger("Z");
            TileEntity tileEntity = world.getTileEntity(cx, cy, cz);
            if (tileEntity != null && tileEntity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) tileEntity;
                if (!controller.canRegisterAnotherChangeBlock())
                {
                    player.addChatComponentMessage(new ChatComponentText("Controller is already at full capacity!")); // TODO: Localize
                    return false;
                } else return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, side);
            } else return false;
        } else return false;
    }
}
