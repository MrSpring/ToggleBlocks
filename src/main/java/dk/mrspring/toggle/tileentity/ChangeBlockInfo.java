package dk.mrspring.toggle.tileentity;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Created by Konrad on 27-02-2015.
 */
public class ChangeBlockInfo
{
    public int x, y, z;
    BlockToggleAction on, off;

    public ChangeBlockInfo(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.setOnAction(new BlockToggleAction());
        this.setOffAction(new BlockToggleAction());
    }

    public ChangeBlockInfo setOnAction(BlockToggleAction on)
    {
        this.on = on;
        return this;
    }

    public ChangeBlockInfo setOffAction(BlockToggleAction off)
    {
        this.off = off;
        return this;
    }

    public BlockToggleAction getOnAction()
    {
        return on;
    }

    public BlockToggleAction getOffAction()
    {
        return off;
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("X", x);
        compound.setInteger("Z", y);
        compound.setInteger("Y", z);
    }

    public class BlockToggleAction
    {
        /**
         * @param world                 World object
         * @param x                     The X coordinate of the block to change
         * @param y                     The Y coordinate of the block to change
         * @param z                     The Z coordinate of the block to change
         * @param direction             The direction the change block is configured with
         * @param player                The player
         * @param placing               The ItemStack to place, remember to reduce stack size! When null the block
         *                              should simply be left as air.
         * @param tileEntityToggleBlock The TileEntity of the toggle block
         */
        public void performAction(World world, int x, int y, int z, int direction, EntityPlayer player,
                                         ItemStack placing, TileEntityToggleBlock tileEntityToggleBlock)
        {
            int metadata = world.getBlockMetadata(x, y, z);
            List<ItemStack> drops = world.getBlock(x, y, z).getDrops(world, x, y, z, metadata, 0);
            ItemStack[] items = drops.toArray(new ItemStack[drops.size()]);
            tileEntityToggleBlock.addItemStacksToStorage(items);
            System.out.println("drops.size() = " + drops.size());
            world.setBlockToAir(x, y, z);
            if (placing != null)
            {
                player.setItemInUse(placing, 0);
                if (placing.tryPlaceItemIntoWorld(player, world, x, y, z, direction, 0, 0, 0))
                {
//                    placing.stackSize--;
                } // TODO: Use how if seeds etc.
            }
        }
    }

    public static class FakePlayer extends EntityPlayer
    {
        public FakePlayer(World world)
        {
            super(world, new GameProfile(new UUID(0, 0), "ToggleBlock"));
        }

        @Override
        public void addChatMessage(IChatComponent p_145747_1_)
        {

        }

        @Override
        public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_)
        {
            return false;
        }

        @Override
        public ChunkCoordinates getPlayerCoordinates()
        {
            return null;
        }
    }
}
