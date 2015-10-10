package dk.mrspring.toggle.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Konrad on 01-03-2015.
 */
public class MessageSetOverride implements IMessage
{
    BlockPos changePos;
    boolean override;
    int state;

    public MessageSetOverride()
    {
    }

    public MessageSetOverride(BlockPos pos, boolean override, int state)
    {
        this.changePos = new BlockPos(pos);
        this.override = override;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        this.changePos = new BlockPos(x, y, z);
        override = buffer.readBoolean();
        state = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(changePos.getX());
        buffer.writeInt(changePos.getY());
        buffer.writeInt(changePos.getZ());
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
                    TileEntity te = world.getTileEntity(message.changePos);
                    if (te instanceof TileEntityChangeBlock)
                    {
                        TileEntityChangeBlock tileEntity = (TileEntityChangeBlock) te;
//                        System.out.println("Overrides " + message.state + " with: " + message.override);
                        tileEntity.getBlockInfo().setOverridesState(message.state, message.override);
                        world.markBlockForUpdate(message.changePos);
                    }
                }
            }
            return null;
        }
    }
}
