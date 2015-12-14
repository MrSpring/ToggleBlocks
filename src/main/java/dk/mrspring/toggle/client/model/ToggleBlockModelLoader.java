package dk.mrspring.toggle.client.model;

import dk.mrspring.toggle.ModInfo;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.io.IOException;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class ToggleBlockModelLoader implements ICustomModelLoader
{
    private final String PREFIX = "models/block/builtin/toggle_controller_";

    @Override
    public boolean accepts(ResourceLocation modelLocation)
    {
        boolean does = modelLocation.getResourceDomain().equals(ModInfo.MOD_ID) && modelLocation.getResourcePath().startsWith(PREFIX);
        System.out.println("Accepts: " + modelLocation.toString() + "? " + does);
        return does;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws IOException
    {
        String s = modelLocation.getResourcePath().substring(PREFIX.length());
        System.out.println("Loading toggle block model of size: " + s);
        return new ModelToggleBlock(s);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
    }
}
