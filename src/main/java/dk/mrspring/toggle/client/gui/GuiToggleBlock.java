package dk.mrspring.toggle.client.gui;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.api.Mode;
import dk.mrspring.toggle.api.StoragePriority;
import dk.mrspring.toggle.common.container.ContainerToggleBlock;
import dk.mrspring.toggle.common.message.MessageSetMode;
import dk.mrspring.toggle.common.message.MessageSetStoragePriority;
import dk.mrspring.toggle.common.tileentity.TileEntityToggleBlock;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static dk.mrspring.toggle.client.util.Translator.translate;

/**
 * Created on 21-12-2015 for ToggleBlocks.
 */
public class GuiToggleBlock extends GuiContainer
{
    public static ResourceLocation texture = new ResourceLocation("tb", "textures/gui/toggle_controller.png");
    public static ResourceLocation panelTexture = new ResourceLocation("tb", "textures/gui/toggle_controller_panel.png");

    public GuiToggleBlock(InventoryPlayer player, TileEntityToggleBlock toggleBlock)
    {
        this(new ContainerToggleBlock(player, toggleBlock));
    }

    public GuiToggleBlock(Container container)
    {
        super(container);
    }

    private static void drawWrappedString(FontRenderer renderer, String drawing, int x, int y, int color, int wrap)
    {
        List<String> lines = renderer.listFormattedStringToWidth(drawing, wrap);
        for (int i = 0; i < lines.size(); i++)
            renderer.drawString(lines.get(i), x, y + (i * 9), color, false);
    }

    private static String upperCaseFirstLetter(String string)
    {
        char[] characters = string.toCharArray();
        characters[0] = Character.toUpperCase(characters[0]);
        return new String(characters);
    }

    @Override
    public void initGui()
    {
        this.xSize = 176 * 2;
        super.initGui();
        this.buttonList.add(new GuiButton(0, (width / 2) + 24, (height / 2) - (ySize / 2) + 18, 128, 20,
                translate("tile.toggle_block.container.toggle_mode")));
        this.buttonList.add(new GuiButton(1, (width / 2) + 14, (height / 2) - (ySize / 2) + 18 + 42 + 10, 148, 20,
                translate("tile.toggle_block.container.cycle_storage_priority")));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);

        ContainerToggleBlock container = (ContainerToggleBlock) this.inventorySlots;
        TileEntityToggleBlock tileEntity = container.getTileEntity();
        Mode currentMode = tileEntity.getCurrentMode();
        StoragePriority storage = tileEntity.getStorageHandler().getStoragePriority();
        String modeLabel = translate("tile.toggle_block.container.change_block_mode." + (currentMode.name().toLowerCase()));
        String priorityPrefix = "tile.toggle_block.container.storage_mode." + storage.name().toLowerCase();
        String priorityLabel = translate("tile.toggle_block.container.storage_mode") + ":"/* +
                translate(priorityPrefix + ".name")*/;
        String priorityDesc = translate(priorityPrefix + ".desc");
        fontRendererObj.drawString(modeLabel, 176 + 8, 7, 4210752, false); // Button text col: 14737632
        fontRendererObj.drawString(priorityLabel, 176 + 8, 49, 4210752, false);
        fontRendererObj.drawString(translate(priorityPrefix + ".name"), 176 + 8, 49 + 10, 4210752, false);
        drawWrappedString(fontRendererObj, priorityDesc, 176 + 8, 100, 4210752, 176 - 16);
        fontRendererObj.drawString(translate("tile.toggle_block.container.name"), 8, 5, 4210752);
        fontRendererObj.drawString(translate("tile.toggle_block.container.off"), 8 + 20, 22, 4210752);
        fontRendererObj.drawString(translate("tile.toggle_block.container.on"), 8 + 20, 44, 4210752);
        drawCenteredString(fontRendererObj, translate("tile.toggle_block.container.storage"), 133, -10, 0xFFFFFF);
        int current = tileEntity.getRegisteredChangeBlockCount(), max = tileEntity.getMaxChangeBlocks();
        String translating = "tile.toggle_block.container." + (max == -1 ? "infinite_blocks" : "registered_blocks");
        fontRendererObj.drawString(translate(translating, current, max), 8, 58, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 94, 4210752);
    }

    @Override
    protected void actionPerformed(GuiButton id)
    {
        if (this.inventorySlots instanceof ContainerToggleBlock)
        {
            ContainerToggleBlock container = (ContainerToggleBlock) this.inventorySlots;
            TileEntityToggleBlock tileEntity = container.getTileEntity();
            BlockPos pos = tileEntity.getPos();
            switch (id.id)
            {
                case 0:
                    Mode newMode;
                    if (tileEntity.getCurrentMode() == Mode.EDITING)
                        newMode = Mode.READY;
                    else newMode = Mode.EDITING;
                    MessageSetMode message = new MessageSetMode(pos, newMode, true);
                    ToggleBlocks.network.sendToServer(message);
                    break;
                case 1:
                    StoragePriority currentPriority = tileEntity.getStoragePriority();
                    StoragePriority newPriority = StoragePriority.getNext(currentPriority);
                    MessageSetStoragePriority priorityMessage = new MessageSetStoragePriority(pos, newPriority, true);
                    ToggleBlocks.network.sendToServer(priorityMessage);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1, 1, 1, 1);
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
