package dk.mrspring.toggle.common.message;

import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.api.Mode;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created on 21-12-2015 for ToggleBlocks.
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
        this.controllerPos = pos;
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
        this.controllerPos = new BlockPos(x, y, z);
        this.markForUpdate = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(mode.ordinal());
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
                    TileEntity entity = world.getTileEntity(message.controllerPos);
                    if (entity != null && entity instanceof IToggleController)
                    {
                        IToggleController controller = (IToggleController) entity;
                        controller.setCurrentMode(message.mode);
                        if (message.markForUpdate) world.markBlockForUpdate(message.controllerPos);
                    }
                }
            }
            return null;
        }
    }
}
