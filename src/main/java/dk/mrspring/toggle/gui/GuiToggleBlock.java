package dk.mrspring.toggle.gui;

import dk.mrspring.toggle.container.ContainerToggleBlock;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.StatCollector;

/**
 * Created by Konrad on 27-02-2015.
 */
public class GuiToggleBlock extends GuiContainer
{
    public GuiToggleBlock(InventoryPlayer player, TileEntityToggleBlock tileEntityToggleBlock)
    {
        this(new ContainerToggleBlock(player, tileEntityToggleBlock));
    }

    public GuiToggleBlock(Container container)
    {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        fontRendererObj.drawString("Toggle Block", 1, 1, 0x000000);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }
}
