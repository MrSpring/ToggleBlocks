package dk.mrspring.toggle.item;

import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.block.BlockChangeBlock;
import dk.mrspring.toggle.block.BlockChangeBlockRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created on 22-09-2015 for ToggleBlocks.
 */
public class ItemBlockChangeBlockRenderer implements IItemRenderer
{
    private RenderBlocks renderBlocks;
    private BlockChangeBlockRenderer blockRenderer = new BlockChangeBlockRenderer();

    public ItemBlockChangeBlockRenderer()
    {
        renderBlocks = new RenderBlocks(Minecraft.getMinecraft().theWorld);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return false;//helper == ItemRendererHelper.INVENTORY_BLOCK;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        if (type == ItemRenderType.INVENTORY)
        {
            Minecraft mc = Minecraft.getMinecraft();
            renderBlocks.blockAccess = mc.theWorld;
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glTranslatef(-2, 3, -3.0F);
            GL11.glScalef(10F, 10F, 10F);
            GL11.glTranslatef(1.0F, 0.5F, 1.0F);
            GL11.glScalef(1.0F, 1.0F, -1F);
            GL11.glRotatef(210F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);
            int color = item.getItem().getColorFromItemStack(item, 0);
            float r = (float) (color >> 16 & 0xff) / 255F;
            float g = (float) (color >> 8 & 0xff) / 255F;
            float b = (float) (color & 0xff) / 255F;
            GL11.glColor4f(r, g, b, 1.0F);
            GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
            renderBlocks.useInventoryTint = true;
            blockRenderer.renderInventoryBlock(BlockBase.change_block, item.getItemDamage(), 0, renderBlocks);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
            int remaining = BlockChangeBlock.getRemainingChangeBlocks(item, mc.theWorld);
            String rendering = String.valueOf(remaining);
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, 20);
            if (remaining != -1) mc.fontRenderer.drawString(rendering, 0, 0, 0xFFFFFF, true);
            GL11.glPopMatrix();
        }
    }
}
