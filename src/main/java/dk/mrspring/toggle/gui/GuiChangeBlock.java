package dk.mrspring.toggle.gui;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.container.ContainerChangeBlock;
import dk.mrspring.toggle.container.ContainerToggleBlock;
import dk.mrspring.toggle.tileentity.MessageSetOverride;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

/**
 * Created by Konrad on 01-03-2015.
 */
public class GuiChangeBlock extends GuiContainer
{
    public GuiChangeBlock(InventoryPlayer inventoryPlayer, TileEntityChangeBlock tileEntity)
    {
        super(new ContainerChangeBlock(inventoryPlayer, tileEntity));
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(0, (width / 2) - 20, (height / 2) - (ySize / 2) + 16, 80, 20, "Override"));
        this.buttonList.add(new GuiButton(1, (width / 2) - 20, (height / 2) - (ySize / 2) + 48, 80, 20, "Override"));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);

        if (this.inventorySlots instanceof ContainerChangeBlock)
        {
            ContainerChangeBlock containerChangeBlock = (ContainerChangeBlock) this.inventorySlots;
            TileEntityChangeBlock tileEntity = containerChangeBlock.getTileEntity();
            boolean[] override = tileEntity.getOverridesStates();
            if (!override[0])
            {
                mc.renderEngine.bindTexture(new ResourceLocation("tb", "textures/gui/change_block.png"));
                drawTexturedModalRect(12, 13, 176, 0, 26, 26);
            }
            if (!override[1])
            {
                mc.renderEngine.bindTexture(new ResourceLocation("tb", "textures/gui/change_block.png"));
                drawTexturedModalRect(12, 45, 176, 0, 26, 26);
            }
        }

        fontRendererObj.drawString("Change Block", 8, 4, 4210752);
        fontRendererObj.drawString("Off", 8 + 35, 22, 4210752);
        fontRendererObj.drawString("On", 8 + 35, 54, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 94, 4210752);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (this.inventorySlots instanceof ContainerChangeBlock)
        {
            ContainerChangeBlock container = (ContainerChangeBlock) this.inventorySlots;
            TileEntityChangeBlock tileEntity = container.getTileEntity();
            boolean current = tileEntity.overrides(button.id);
            System.out.println("current = " + current);
            int x = tileEntity.xCoord, y = tileEntity.yCoord, z = tileEntity.zCoord;
            MessageSetOverride message = new MessageSetOverride(x, y, z, !current, button.id);
            ToggleBlocks.network.sendToServer(message);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1, 1, 1, 1);
        ResourceLocation texture = new ResourceLocation("tb", "textures/gui/change_block.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        mc.renderEngine.bindTexture(texture);
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        for (Object button : this.buttonList)
            if (button instanceof GuiButton)
            {
                GuiButton guiButton = (GuiButton) button;
                guiButton.drawButton(mc, p_146976_2_, p_146976_3_);
            }
    }
}
