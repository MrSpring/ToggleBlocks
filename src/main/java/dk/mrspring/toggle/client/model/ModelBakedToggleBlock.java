package dk.mrspring.toggle.client.model;

import com.google.common.collect.Lists;
import dk.mrspring.toggle.common.block.BlockToggleController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class ModelBakedToggleBlock implements IFlexibleBakedModel, ISmartBlockModel
{
    IBakedModel core;
    IBakedModel[] directions;

    public ModelBakedToggleBlock(IBakedModel core, IBakedModel... directions)
    {
        this.core = core;
        this.directions = directions;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return core.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return core.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture()
    {
        return core.getTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return core.getItemCameraTransforms();
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_)
    {
        throw new UnsupportedOperationException("Tried to call getFaceQuads on a non-baked Toggle Block model!");
    }

    @Override
    public List<BakedQuad> getGeneralQuads()
    {
        throw new UnsupportedOperationException("Tried to call getGeneralQuads on a non-baked Toggle Block model!");
    }

    @Override
    public VertexFormat getFormat()
    {
        return Attributes.DEFAULT_BAKED_FORMAT;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state)
    {
        return state instanceof IExtendedBlockState ? new Assembled((IExtendedBlockState) state, BlockToggleController.CONNECTIONS) : new Assembled();
    }

    public class Assembled implements IBakedModel
    {
        boolean[] hasDirection = new boolean[directions.length];

        public Assembled()
        {
        }

        public Assembled(IExtendedBlockState state, IUnlistedProperty<Boolean>... properties)
        {
            if (properties.length != hasDirection.length)
                throw new IllegalArgumentException("Not enough/too many properties!");
            for (int i = 0; i < properties.length; i++)
            {
                IUnlistedProperty<Boolean> property = properties[i];
                Boolean has = state.getValue(property);
                if (has != null && has) hasDirection[i] = true;
            }
        }

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            List<BakedQuad> quads = Lists.newLinkedList();
            quads.addAll(core.getFaceQuads(side));
            for (int i = 0; i < hasDirection.length; i++)
                if (hasDirection[i]) quads.addAll(directions[i].getFaceQuads(side));
            return quads;
        }

        @Override
        public List<BakedQuad> getGeneralQuads()
        {
            List<BakedQuad> quads = Lists.newLinkedList();
            quads.addAll(core.getGeneralQuads());
            for (int i = 0; i < hasDirection.length; i++)
                if (hasDirection[i]) quads.addAll(directions[i].getGeneralQuads());
            return quads;
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return core.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return core.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getTexture()
        {
            return core.getTexture();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return core.getItemCameraTransforms();
        }
    }
}
