package dk.mrspring.toggle.tileentity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

/**
 * Created by Konrad on 01-03-2015.
 */
public class MessageSetOverride implements IMessage
{
    int changeX, changeY, changeZ;
    boolean override;
    int state;

    public MessageSetOverride()
    {

    }

    public MessageSetOverride(int x, int y, int z, boolean override, int state)
    {
        this.changeX = x;
        this.changeY = y;
        this.changeZ = z;
        this.override = override;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        changeX = buffer.readInt();
        changeY = buffer.readInt();
        changeZ = buffer.readInt();
        override = buffer.readBoolean();
        state = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(changeX);
        buffer.writeInt(changeY);
        buffer.writeInt(changeZ);
        buffer.writeBoolean(override);
        buffer.writeInt(state);
    }

    public static class MessageHandler implements IMessageHandler<MessageSetOverride, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSetOverride message, MessageContext context)
        {
            if (context.getServerHandler().playerEntity != null)
            {
                if (!context.getServerHandler().playerEntity.worldObj.isRemote)
                {
                    World world = context.getServerHandler().playerEntity.worldObj;
                    int x = message.changeX, y = message.changeY, z = message.changeZ;
                    if (world.getTileEntity(x, y, z) instanceof TileEntityChangeBlock)
                    {
                        TileEntityChangeBlock tileEntity = (TileEntityChangeBlock) world.getTileEntity(x, y, z);
                        System.out.println("Overrides " + message.state + " with: " + message.override);
                        tileEntity.getBlockInfo().setOverridesState(message.state, message.override);
                        world.markBlockForUpdate(x, y, z);
                    }
                }
            }
            return null;
        }
    }
}
