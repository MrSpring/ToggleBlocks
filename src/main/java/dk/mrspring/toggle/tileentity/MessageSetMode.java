package dk.mrspring.toggle.tileentity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

/**
 * Created by Konrad on 01-03-2015.
 */
public class MessageSetMode implements IMessage
{
    int controllerX, controllerY, controllerZ;
    TileEntityToggleBlock.Mode mode;
    boolean markForUpdate;

    public MessageSetMode()
    {
        mode = TileEntityToggleBlock.Mode.EDITING;
    }

    public MessageSetMode(int x, int y, int z, TileEntityToggleBlock.Mode mode, boolean update)
    {
        this.controllerX = x;
        this.controllerY = y;
        this.controllerZ = z;
        this.mode = mode;
        this.markForUpdate = update;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.mode = TileEntityToggleBlock.Mode.fromInt(buffer.readInt());
        this.controllerX = buffer.readInt();
        this.controllerY = buffer.readInt();
        this.controllerZ = buffer.readInt();
        this.markForUpdate = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(mode.getId());
        buffer.writeInt(controllerX);
        buffer.writeInt(controllerY);
        buffer.writeInt(controllerZ);
        buffer.writeBoolean(markForUpdate);
    }

    public static class MessageHandler implements IMessageHandler<MessageSetMode, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSetMode message, MessageContext context)
        {
            if (context.getServerHandler().playerEntity != null)
            {
                if (!context.getServerHandler().playerEntity.worldObj.isRemote)
                {
                    World world = context.getServerHandler().playerEntity.worldObj;
                    int x = message.controllerX, y = message.controllerY, z = message.controllerZ;
                    if (world.getTileEntity(x, y, z) instanceof TileEntityToggleBlock)
                    {
                        TileEntityToggleBlock tileEntity = (TileEntityToggleBlock) world.getTileEntity(x, y, z);
                        tileEntity.setCurrentMode(message.mode);
                    }
                }
            }
            return null;
        }
    }
}
