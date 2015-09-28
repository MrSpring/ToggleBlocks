package dk.mrspring.toggle.comp.waila;

import dk.mrspring.toggle.block.BlockChangeBlock;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Created on 28-09-2015 for ToggleBlocks.
 */
public class WailaCompatibility implements IWailaDataProvider
{
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (config.getConfig("show_change_block_direction"))
        {
            TileEntity entity = accessor.getTileEntity();
            if (entity != null && entity instanceof TileEntityChangeBlock)
            {
                TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) entity;
                ForgeDirection direction = changeBlock.getBlockInfo().getDirection();
                currenttip.add("Direction: " + direction.name());
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z)
    {
        return null;
    }

    public static void callbackRegister(IWailaRegistrar registrar)
    {
        final String mod = "The Kitchen Mod";

        registrar.addConfig(mod, "show_change_block_direction", true);

        System.out.println("Register");

        registrar.registerBodyProvider(new WailaCompatibility(), BlockChangeBlock.class);
    }
}
