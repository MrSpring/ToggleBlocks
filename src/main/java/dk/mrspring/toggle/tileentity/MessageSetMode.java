package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.api.Mode;
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
public class MessageSetMode implements IMessage
{
    BlockPos controllerPos;
    Mode mode;
    boolean markForUpdate;

    public MessageSetMode()
    {
        mode = Mode.EDITING;
    }

    public MessageSetMode(BlockPos pos, Mode mode, boolean update)
    {
        this.controllerPos = new BlockPos(pos);
        this.mode = mode;
        this.markForUpdate = update;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.mode = Mode.fromInt(buffer.readInt());
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        this.controllerPos = new BlockPos(x,y,z);
        this.markForUpdate = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(mode.getId());
        buffer.writeInt(controllerPos.getX());
        buffer.writeInt(controllerPos.getY());
        buffer.writeInt(controllerPos.getZ());
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
                    TileEntity te = world.getTileEntity(message.controllerPos);
                    if (te instanceof TileEntityToggleBlock)
                    {
                        TileEntityToggleBlock tileEntity = (TileEntityToggleBlock) te;
                        tileEntity.setCurrentMode(message.mode);
                    }
                }
            }
            return null;
        }
    }
}
