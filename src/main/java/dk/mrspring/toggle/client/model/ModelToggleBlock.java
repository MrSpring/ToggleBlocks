package dk.mrspring.toggle.client.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import dk.mrspring.toggle.ModInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class ModelToggleBlock implements IModel
{
    private final String NAME_FORMAT = ModInfo.MOD_ID + ":block/toggle_controller_%s_%s";

    ResourceLocation texture;
    ModelResourceLocation coreModelLocation;
    ModelResourceLocation[] modelLocations;
    String size;

    public ModelToggleBlock(String size)
    {
        this.size = size;
        texture = new ResourceLocation(ModInfo.MOD_ID, "blocks/toggle_controller_" + size);
        modelLocations = new ModelResourceLocation[EnumFacing.values().length];
        for (int i = 0; i < EnumFacing.values().length; i++)
            modelLocations[i] = new ModelResourceLocation(String.format(NAME_FORMAT, size, EnumFacing.getFront(i).getName()));
        coreModelLocation = new ModelResourceLocation(String.format(NAME_FORMAT, size, "core"));
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        List<ResourceLocation> locations = Lists.newArrayList();
        locations.add(coreModelLocation);
        Collections.addAll(locations, modelLocations);
        return locations;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return Lists.newArrayList(texture);
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        try
        {
            IBakedModel core = ModelLoaderRegistry.getModel(coreModelLocation).bake(state, format, bakedTextureGetter);
            IBakedModel[] directions = new IBakedModel[modelLocations.length];
            for (int i = 0; i < directions.length; i++)
            {
                ModelResourceLocation loc = modelLocations[i];
                directions[i] = ModelLoaderRegistry.getModel(loc).bake(state, format, bakedTextureGetter);
            }
            return new ModelBakedToggleBlock(core, directions);
        } catch (IOException e)
        {
            CrashReport report = new CrashReport("Failed while loading Toggle Block model!", e);
            CrashReportCategory info = report.makeCategory("Model info");
            info.addCrashSection("Core model", coreModelLocation == null ? "null" : coreModelLocation);
            for (ModelResourceLocation location : modelLocations)
                info.addCrashSection("Model part", location == null ? "null" : location);
            throw new ReportedException(report);
        }
    }

    @Override
    public IModelState getDefaultState()
    {
        return null;
    }
}
