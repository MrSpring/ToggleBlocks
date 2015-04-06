package dk.mrspring.toggle.gui;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.container.ContainerToggleBlock;
import dk.mrspring.toggle.tileentity.MessageSetMode;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import static dk.mrspring.toggle.util.Translator.translate;

/**
 * Created by Konrad on 27-02-2015.
 */
public class GuiToggleBlock extends GuiContainer
{
    public GuiToggleBlock(InventoryPlayer player, TileEntityToggleBlock tileEntityToggleBlock)
    {
        this(new ContainerToggleBlock(player, tileEntityToggleBlock));
        this.xSize += 176;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(0, (width / 2) + 24, (height / 2) - (ySize / 2) + 18, 128, 20, translate("tile.toggle_block.container.toggle_mode")));
        this.buttonList.add(new GuiButton(1, (width / 2) + 14, (height / 2) - (ySize / 2) + 50, 148, 20, ""));
    }

    public GuiToggleBlock(Container container)
    {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);

        ContainerToggleBlock container = (ContainerToggleBlock) this.inventorySlots;
        TileEntityToggleBlock tileEntity = container.getTileEntity();
        TileEntityToggleBlock.Mode currentMode = tileEntity.getCurrentMode();
        String modeLabel = translate("tile.toggle_block.container.change_block_mode") + ": " +
                upperCaseFirstLetter(currentMode.name().toLowerCase());
        fontRendererObj.drawString(modeLabel, (xSize / 2) + 8, 5, 4210752, false); // Button text col: 14737632
        fontRendererObj.drawString(translate("tile.toggle_block.container.name"), 8, 5, 4210752);
        fontRendererObj.drawString(translate("tile.toggle_block.container.off"), 8 + 20, 20, 4210752);
        fontRendererObj.drawString(translate("tile.toggle_block.container.on"), 8 + 20, 42, 4210752);
        drawCenteredString(fontRendererObj, translate("tile.toggle_block.container.storage"), 133, -10, 0xFFFFFF);
        fontRendererObj.drawString(translate("tile.toggle_block.container.registered_blocks") + ": X/X", 8, 58, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 94, 4210752);
    }

    private static String upperCaseFirstLetter(String string)
    {
        char[] characters = string.toCharArray();
        characters[0] = Character.toUpperCase(characters[0]);
        return new String(characters);
    }

    @Override
    protected void actionPerformed(GuiButton id)
    {
        if (this.inventorySlots instanceof ContainerToggleBlock)
        {
            ContainerToggleBlock container = (ContainerToggleBlock) this.inventorySlots;
            TileEntityToggleBlock tileEntity = container.getTileEntity();
            TileEntityToggleBlock.Mode newMode;
            if (tileEntity.getCurrentMode() == TileEntityToggleBlock.Mode.EDITING)
                newMode = TileEntityToggleBlock.Mode.READY;
            else newMode = TileEntityToggleBlock.Mode.EDITING;
            int x = tileEntity.xCoord, y = tileEntity.yCoord, z = tileEntity.zCoord;
            MessageSetMode message = new MessageSetMode(x, y, z, newMode, true);
            ToggleBlocks.network.sendToServer(message);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1, 1, 1, 1);
        ResourceLocation texture = new ResourceLocation("tb", "textures/gui/toggle_controller.png");
        ResourceLocation panelTexture = new ResourceLocation("tb", "textures/gui/toggle_controller_panel.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        mc.renderEngine.bindTexture(texture);
        this.drawTexturedModalRect(x, y, 0, 0, 176, ySize);
        mc.renderEngine.bindTexture(panelTexture);
        this.drawTexturedModalRect(x + 176, y, 0, 0, 176, ySize);
        for (Object button : this.buttonList)
            if (button instanceof GuiButton)
            {
                GuiButton guiButton = (GuiButton) button;
                guiButton.drawButton(mc, p_146976_2_, p_146976_3_);
            }
    }
}
