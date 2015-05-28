package dk.mrspring.toggle.tileentity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dk.mrspring.toggle.api.StoragePriority;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

/**
 * Created by Konrad on 06-04-2015.
 */
public class MessageSetStoragePriority implements IMessage
{
    int controllerX, controllerY, controllerZ;
    StoragePriority priority;
    boolean markForUpdate;

    public MessageSetStoragePriority()
    {
        priority = StoragePriority.STORAGE_FIRST;
    }

    public MessageSetStoragePriority(int x, int y, int z, StoragePriority priority, boolean update)
    {
        this.controllerX = x;
        this.controllerY = y;
        this.controllerZ = z;
        this.priority = priority;
        this.markForUpdate = update;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.priority = StoragePriority.fromInt(buffer.readInt());
        this.controllerX = buffer.readInt();
        this.controllerY = buffer.readInt();
        this.controllerZ = buffer.readInt();
        this.markForUpdate = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(priority.getId());
        buffer.writeInt(controllerX);
        buffer.writeInt(controllerY);
        buffer.writeInt(controllerZ);
        buffer.writeBoolean(markForUpdate);
    }

    public static class MessageHandler implements IMessageHandler<MessageSetStoragePriority, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSetStoragePriority message, MessageContext context)
        {
//            if (context.getServerHandler().playerEntity != null)
//            {
//                if (!context.getServerHandler().playerEntity.worldObj.isRemote)
//                {
//                    World world = context.getServerHandler().playerEntity.worldObj;
//                    int x = message.controllerX, y = message.controllerY, z = message.controllerZ;
//                    if (world.getTileEntity(x, y, z) instanceof TileEntityToggleBlock)
//                    {
//                        TileEntityToggleBlock tileEntity = (TileEntityToggleBlock) world.getTileEntity(x, y, z);
//                        tileEntity.setStoragePriority(message.priority);
//                    }
//                }
//            }
            return null;
        }
    }
}
