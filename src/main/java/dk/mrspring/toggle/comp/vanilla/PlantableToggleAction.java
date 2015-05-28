package dk.mrspring.toggle.comp.vanilla;

import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.BasicBlockToggleAction;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Konrad on 02-03-2015.
 */
public class PlantableToggleAction extends BasicBlockToggleAction
{
    @Override
    public void placeBlock(World world, int x, int y, int z, ForgeDirection direction, EntityPlayer player, ItemStack placing, IToggleController controller)
    {
        Block soil = world.getBlock(x, y - 1, z);
        if (placing.getItem() instanceof IPlantable)
        {
            IPlantable plantable = (IPlantable) placing.getItem();
            ForgeDirection up = ForgeDirection.UP;
            ItemStack hoeStack = controller.getStorageHandler().getToolFromStorage("hoe");
            if (hoeStack != null &&
                    (soil == Blocks.dirt || soil == Blocks.grass) &&
                    Blocks.farmland.canSustainPlant(world, x, y - 1, z, up, plantable))
            {
                world.setBlock(x, y - 1, z, Blocks.farmland);
                hoeStack.setItemDamage(hoeStack.getItemDamage() + 1);
            }
            super.placeBlock(world, x, y, z, direction, player, placing, controller);
        }
    }

    @Override
    public boolean canPlaceBlock(World world, int x, int y, int z, ItemStack placing, IToggleController controller)
    {
        System.out.println("Can place?");
        Block soil = world.getBlock(x, y - 1, z);
        if (placing.getItem() instanceof IPlantable)
        {
            System.out.println("Item is plantable!");
            IPlantable plantable = (IPlantable) placing.getItem();
            ForgeDirection up = ForgeDirection.UP;
            if (soil.canSustainPlant(world, x, y - 1, z, up, plantable))
            {
                System.out.println("Soil can sustain the plant");
                return true;
            } else if (
                    controller.getStorageHandler().getToolFromStorage("hoe") != null &&
                            (soil == Blocks.dirt || soil == Blocks.grass) &&
                            Blocks.farmland.canSustainPlant(world, x, y - 1, z, up, plantable))
            {
                System.out.println("Soil can't sustain plant, but farmland can!");
                return true;
            }
        }
        return false;
    }
}
