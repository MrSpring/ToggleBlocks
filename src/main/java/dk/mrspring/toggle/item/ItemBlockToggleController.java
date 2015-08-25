package dk.mrspring.toggle.item;

import dk.mrspring.toggle.block.BlockToggleController;
import dk.mrspring.toggle.util.Translator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Konrad on 04-06-2015.
 */
public class ItemBlockToggleController extends ItemBlock
{
    public ItemBlockToggleController(Block block)
    {
        super(block);
    }

    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        BlockToggleController.ControllerSize size = BlockToggleController.sizes[stack.getItemDamage()];
        return this.getUnlocalizedName() + "_" + size.name;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_)
    {
        super.addInformation(stack, player, list, p_77624_4_);

        BlockToggleController.ControllerSize size = BlockToggleController.sizes[stack.getItemDamage()];
        String unlocalized = "tile." + size.name + "_toggle_block.desc";
        String s = Translator.translate(unlocalized, size.size);
        list.add(s);
    }
}
