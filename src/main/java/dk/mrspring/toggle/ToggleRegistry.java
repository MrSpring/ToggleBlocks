package dk.mrspring.toggle;

import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IBlockToggleRegistry;
import dk.mrspring.toggle.api.IToggleController;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Konrad on 01-03-2015.
 */
public class ToggleRegistry implements IBlockToggleRegistry
{
    public static ToggleRegistry instance;

    static void initialize()
    {
        instance = new ToggleRegistry();
    }

    static void registerVanilla()
    {
        instance.registerToggleAction(new IBlockToggleAction()
        {
            @Override
            public boolean performAction(World world, int x, int y, int z, int direction, EntityPlayer player,
                                         ItemStack placing, IToggleController tileEntityToggleBlock)
            {
                world.setBlock(x, y - 1, z, Blocks.farmland);
                tileEntityToggleBlock.requestToolFromStorage("hoe").attemptDamageItem(1, new Random());
                Block plant = ((IPlantable) placing.getItem()).getPlant(world, x, y, z);
                int metadata = ((IPlantable) placing.getItem()).getPlantMetadata(world, x, y, z);
                if (world.getBlock(x, y - 1, z).canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (IPlantable) placing.getItem()))

            }

            @Override
            public boolean useWithItem(World world, int x, int y, int z, ItemStack placing,
                                       IToggleController tileEntity)
            {
                return (world.getBlock(x, y - 1, z) == Blocks.dirt || world.getBlock(x, y - 1, z) == Blocks.grass) &&
                        placing.getItem() instanceof IPlantable &&
                        tileEntity.requestToolFromStorage("hoe") != null;
            }
        });
    }

    List<IBlockToggleAction> registeredActions;

    private ToggleRegistry()
    {
        registeredActions = new ArrayList<IBlockToggleAction>();
    }

    @Override
    public void registerToggleAction(IBlockToggleAction action)
    {
        if (action != null && !registeredActions.contains(action))
            registeredActions.add(action);
    }

    public List<IBlockToggleAction> getRegisteredActions()
    {
        return this.registeredActions;
    }
}
