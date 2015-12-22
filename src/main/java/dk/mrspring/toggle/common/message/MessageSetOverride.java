package dk.mrspring.toggle.common.message;

import dk.mrspring.toggle.common.tileentity.TileEntityToggleBlock;
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
    BlockPos controllerPos, changePos;
    boolean override;
    int state;
    boolean markForUpdate;

    public MessageSetOverride()
    {
    }

    public MessageSetOverride(BlockPos controller, BlockPos change, boolean override, int state, boolean update)
    {
        this.controllerPos = controller;
        this.changePos = change;
        this.override = override;
        this.state = state;
        this.markForUpdate = update;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        this.changePos = new BlockPos(x, y, z);
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        this.controllerPos = new BlockPos(x, y, z);
        this.override = buffer.readBoolean();
        this.state = buffer.readInt();
        this.markForUpdate = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(changePos.getX());
        buffer.writeInt(changePos.getY());
        buffer.writeInt(changePos.getZ());
        buffer.writeInt(controllerPos.getX());
        buffer.writeInt(controllerPos.getY());
        buffer.writeInt(controllerPos.getZ());
        buffer.writeBoolean(override);
        buffer.writeInt(state);
        buffer.writeBoolean(markForUpdate);
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
                    TileEntity entity = world.getTileEntity(message.controllerPos);
                    if (entity instanceof TileEntityToggleBlock)
                    {
                        TileEntityToggleBlock tileEntity = (TileEntityToggleBlock) entity;
                        tileEntity.setOverrideForState(message.changePos, message.state, message.override);
                        if (message.markForUpdate) world.markBlockForUpdate(message.controllerPos);
                    }
                }
            }
            return null;
        }
    }
}
