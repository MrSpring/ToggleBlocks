package dk.mrspring.toggle.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

/**
 * Created by Konrad on 04-03-2015.
 */
public class BlockToggleControllerRenderer implements ISimpleBlockRenderingHandler
{
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        final float P = 0.0625F;
        final int CS = 8;
        final int CH = 2;
        final float START_Y = 0.1F;
        final float SCALE = 1.4F;

//        RenderHelper.enableGUIStandardItemLighting();
//        RenderHelper.enableStandardItemLighting();

        renderer.useInventoryTint = true;

        GL11.glTranslatef(0F, START_Y, 0F);
        GL11.glScalef(SCALE, SCALE, SCALE);
        drawInventoryBlock(block,
                0.5 - ((CS / 2) * P), 0.5 - ((CS / 2) * P), 0.5 - ((CS / 2) * P),
                CS * P, CS * P, CS * P, renderer);

        drawInventoryBlock(block,
                0.5 - P, 2 * P, 0.5 - P,
                2 * P, 2 * P, 2 * P, renderer);

        drawInventoryBlock(block,
                0.5 - ((CS / 2) * P), 0, 0.5 - ((CS / 2) * P),
                CS * P, CH * P, CS * P, renderer);
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
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
                                    RenderBlocks renderer)
    {
        final float P = 0.0625F;
        final int CS = 8;
        final int CH = 2;

        boolean bottom = world.getBlock(x, y - 1, z).isOpaqueCube() || world.getBlock(x, y - 1, z) == Blocks.chest;
        boolean top = world.getBlock(x, y + 1, z).isOpaqueCube() || world.getBlock(x, y + 1, z) == Blocks.chest;
        boolean north = world.getBlock(x, y, z - 1).isOpaqueCube() || world.getBlock(x, y, z - 1) == Blocks.chest;
        boolean south = world.getBlock(x, y, z + 1).isOpaqueCube() || world.getBlock(x, y, z + 1) == Blocks.chest;
        boolean east = world.getBlock(x + 1, y, z).isOpaqueCube() || world.getBlock(x + 1, y, z) == Blocks.chest;
        boolean west = world.getBlock(x - 1, y, z).isOpaqueCube() || world.getBlock(x - 1, y, z) == Blocks.chest;

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

        renderCube(block, x, y, z, renderer,
                0.5 - (CS / 2) * P, 0.5 - (CS / 2) * P, 0.5 - (CS / 2) * P,
                CS * P, CS * P, CS * P);

        if (bottom)
        {
            renderCube(block, x, y, z, renderer,
                    0.5 - ((CS * P) / 2), 0, 0.5 - ((CS * P) / 2),
                    CS * P, CH * P, CS * P);

            renderCube(block, x, y, z, renderer,
                    7 * P, 2 * P, 7 * P,
                    2 * P, 3 * P, 2 * P);
        }

        if (top)
        {
            renderCube(block, x, y, z, renderer,
                    0.5 - ((CS * P) / 2), 1 - (2 * P), 0.5 - ((CS * P) / 2),
                    CS * P, CH * P, CS * P);

            renderCube(block, x, y, z, renderer,
                    7 * P, 1 - (5 * P), 7 * P,
                    2 * P, 3 * P, 2 * P);
        }

        if (north)
        {
            renderer.flipTexture = true;
            renderCube(block, x, y, z, renderer,
                    0.5 - ((CS * P) / 2), 0.5 - ((CS * P) / 2), 0,
                    CS * P, CS * P, CH * P);

            renderCube(block, x, y, z, renderer,
                    7 * P, 7 * P, 2 * P,
                    2 * P, 2 * P, 3 * P);
            renderer.flipTexture = false;
        }

        if (south)
        {
            renderer.flipTexture = true;
            renderCube(block, x, y, z, renderer,
                    0.5 - ((CS * P) / 2), 0.5 - ((CS * P) / 2), 1 - (CH * P),
                    CS * P, CS * P, CH * P);

            renderCube(block, x, y, z, renderer,
                    7 * P, 7 * P, 11 * P,
                    2 * P, 2 * P, 3 * P);
            renderer.flipTexture = false;
        }

        if (west)
        {
            renderer.flipTexture = true;
            renderCube(block, x, y, z, renderer,
                    0, 0.5 - ((CS * P) / 2), 0.5 - ((CS * P) / 2),
                    CH * P, CS * P, CS * P);

            renderCube(block, x, y, z, renderer,
                    2 * P, 7 * P, 7 * P,
                    3 * P, 2 * P, 2 * P);
            renderer.flipTexture = false;
        }

        if (east)
        {
            renderer.flipTexture = true;
            renderCube(block, x, y, z, renderer,
                    1 - (CH * P), 0.5 - ((CS * P) / 2), 0.5 - ((CS * P) / 2),
                    CH * P, CS * P, CS * P);

            renderCube(block, x, y, z, renderer,
                    11 * P, 7 * P, 7 * P,
                    3 * P, 2 * P, 2 * P);
            renderer.flipTexture = false;
        }

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
