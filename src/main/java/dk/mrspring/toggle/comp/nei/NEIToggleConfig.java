package dk.mrspring.toggle.comp.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.ItemFilter;
import dk.mrspring.toggle.ModInfo;
import dk.mrspring.toggle.block.BlockBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created on 22-09-2015 for ToggleBlocks.
 */
public class NEIToggleConfig implements IConfigureNEI
{
    @Override
    public void loadConfig()
    {
        System.out.println("Load config");
        API.hideItem(new ItemStack(Item.getItemFromBlock(BlockBase.change_block)));
        /*API.addSubset("Toggle Blocks", new ItemFilter()
        {
            @Override
            public boolean matches(ItemStack itemStack)
            {
                return itemStack.getItem() == Item.getItemFromBlock(BlockBase.toggle_controller);
            }
        });*/
        /*API.addItemFilter(new ItemFilter.ItemFilterProvider()
        {
            @Override
            public ItemFilter getFilter()
            {
                return new ItemFilter()
                {
                    @Override
                    public boolean matches(ItemStack itemStack)
                    {
                        return itemStack.getItem() != Item.getItemFromBlock(BlockBase.change_block);
                    }
                };
            }
        });*/
    }

    @Override
    public String getName()
    {
        return ModInfo.NAME;
    }

    @Override
    public String getVersion()
    {
        return ModInfo.VERSION;
    }
}
