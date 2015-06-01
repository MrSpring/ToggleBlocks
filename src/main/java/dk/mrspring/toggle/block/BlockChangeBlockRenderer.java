package dk.mrspring.toggle.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

/**
 * Created by Konrad on 01-06-2015.
 */
public class BlockChangeBlockRenderer implements ISimpleBlockRenderingHandler
{
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        final float P = 0.0625F;
        drawInventoryBlock(block, 0, 0, 0, 5 * P, 5 * P, 5 * P, renderer);
        drawInventoryBlock(block, 1 - (5 * P), 0, 0, 5 * P, 5 * P, 5 * P, renderer);
    }

    private void drawInventoryBlock(Block block, double x, double y, double z, double w, double h, double d,
                                    RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        renderer.setRenderBounds(
                x, y, z,
                x + w, y + h, z + d);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0, 1, 0);
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(0, 0));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0, -1, 0);
        renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, 0));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(1, 0, 0);
        renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(0, 0));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(-1, 0, 0);
        renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(0, 0));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0, 0, 1);
        renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(0, 0));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0, 0, -1);
        renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(0, 0));
        tessellator.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        final float P = 0.0625F;

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        int i1 = block.colorMultiplier(world, x, y, z);
        float f = (float) (i1 >> 16 & 255) / 255.0F;
        float f1 = (float) (i1 >> 8 & 255) / 255.0F;
        float f2 = (float) (i1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);

        renderCube(block, x, y, z, renderer, P, P, P, 14*P, 14*P, 14*P);
        renderCube(block, x, y, z, renderer, 0, 0, 0, 5 * P, 5 * P, 5 * P);
        renderCube(block, x, y, z, renderer, 1 - (5 * P), 0, 0, 5 * P, 5 * P, 5 * P);
        renderCube(block, x, y, z, renderer, 0, 0, 1 - (5 * P), 5 * P, 5 * P, 5 * P);
        renderCube(block, x, y, z, renderer, 1 - (5 * P), 0, 1 - (5 * P), 5 * P, 5 * P, 5 * P);
        renderCube(block, x, y, z, renderer, 0, 1 - (5 * P), 0, 5 * P, 5 * P, 5 * P);
        renderCube(block, x, y, z, renderer, 1 - (5 * P), 1 - (5 * P), 0, 5 * P, 5 * P, 5 * P);
        renderCube(block, x, y, z, renderer, 0, 1 - (5 * P), 1 - (5 * P), 5 * P, 5 * P, 5 * P);
        renderCube(block, x, y, z, renderer, 1 - (5 * P), 1 - (5 * P), 1 - (5 * P), 5 * P, 5 * P, 5 * P);

        return true;
    }

    private void renderCube(Block block, int xCoord, int yCoord, int zCoord, RenderBlocks renderer,
                            double x, double y, double z,
                            double w, double h, double d)
    {
        renderer.setRenderBounds(x, y, z, x + w, y + h, z + d);
        renderer.renderStandardBlock(block, xCoord, yCoord, zCoord);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return -1;
    }
}
