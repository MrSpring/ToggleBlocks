package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.api.StoragePriority;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Konrad on 06-04-2015.
 */
public class MessageSetStoragePriority implements IMessage
{
    BlockPos controllerPos;
    StoragePriority priority;
    boolean markForUpdate;

    public MessageSetStoragePriority()
    {
        priority = StoragePriority.STORAGE_FIRST;
    }

    public MessageSetStoragePriority(BlockPos pos, StoragePriority priority, boolean update)
    {
        this.controllerPos = new BlockPos(pos);
        this.priority = priority;
        this.markForUpdate = update;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.priority = StoragePriority.fromInt(buffer.readInt());
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        this.controllerPos = new BlockPos(x, y, z);
        this.markForUpdate = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(priority.getId());
        buffer.writeInt(controllerPos.getX());
        buffer.writeInt(controllerPos.getY());
        buffer.writeInt(controllerPos.getZ());
        buffer.writeBoolean(markForUpdate);
    }

    public static class MessageHandler implements IMessageHandler<MessageSetStoragePriority, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSetStoragePriority message, MessageContext context)
        {
            if (context.getServerHandler().playerEntity != null)
            {
                if (!context.getServerHandler().playerEntity.worldObj.isRemote)
                {
                    World world = context.getServerHandler().playerEntity.worldObj;
                    TileEntity te = world.getTileEntity(message.controllerPos);
                    if (te instanceof TileEntityToggleBlock)
                    {
                        TileEntityToggleBlock tileEntity = (TileEntityToggleBlock) te;
                        tileEntity.getStorageHandler().setStoragePriority(message.priority);
                    }
                }
            }
            return null;
        }
    }
}
