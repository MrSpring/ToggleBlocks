package dk.mrspring.toggle;

import cpw.mods.fml.common.registry.GameRegistry;
import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.block.BlockToggleController;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import static dk.mrspring.toggle.block.BlockToggleController.*;

/**
 * Created on 26-08-2015.
 */
public class Recipes
{
    public static void register()
    {
        GameRegistry.addRecipe(createToggleController(TINY, 1)/*new ItemStack(BlockBase.toggle_controller, 1, BlockToggleController.TINY.metadata)*/,
                "PIP", "SRS", "PIP",
                'P', new ItemStack(Blocks.planks, OreDictionary.WILDCARD_VALUE),
                'I', new ItemStack(Items.iron_ingot),
                'S', new ItemStack(Blocks.cobblestone),
                'R', new ItemStack(Blocks.redstone_block));

        GameRegistry.addRecipe(createToggleController(SMALL, 1)/*new ItemStack(BlockBase.toggle_controller, 1, BlockToggleController.SMALL.metadata)*/,
                "IRI", "BRB", " T ",
                'I', new ItemStack(Blocks.iron_block),
                'R', new ItemStack(Items.redstone),
                'B', new ItemStack(Blocks.redstone_block),
                'T', new ItemStack(BlockBase.toggle_controller, 1, BlockToggleController.TINY.metadata));

        GameRegistry.addRecipe(createToggleController(MEDIUM, 1)/*new ItemStack(BlockBase.toggle_controller, 1, BlockToggleController.MEDIUM.metadata)*/,
                "GBG", "RBR", " T ",
                'G', new ItemStack(Blocks.gold_block),
                'B', new ItemStack(Items.redstone),
                'R', new ItemStack(Blocks.redstone_block),
                'T', new ItemStack(BlockBase.toggle_controller, 1, BlockToggleController.SMALL.metadata));

        GameRegistry.addRecipe(createToggleController(LARGE, 1)/*new ItemStack(BlockBase.toggle_controller, 1, BlockToggleController.LARGE.metadata)*/,
                "DED", "DRD", " T ",
                'D', new ItemStack(Items.diamond),
                'E', new ItemStack(Items.ender_pearl),
                'R', new ItemStack(Blocks.redstone_block),
                'T', new ItemStack(BlockBase.toggle_controller, 1, BlockToggleController.MEDIUM.metadata));

        GameRegistry.addRecipe(createToggleController(HUGE, 1)/*new ItemStack(BlockBase.toggle_controller, 1, BlockToggleController.HUGE.metadata)*/,
                "ONO", "GRG", " T ",
                'O', new ItemStack(Blocks.obsidian),
                'N', new ItemStack(Items.nether_star),
                'G', new ItemStack(Blocks.glass),
                'R', new ItemStack(Blocks.redstone_block),
                'T', new ItemStack(BlockBase.toggle_controller, 1, BlockToggleController.LARGE.metadata));
    }
}
