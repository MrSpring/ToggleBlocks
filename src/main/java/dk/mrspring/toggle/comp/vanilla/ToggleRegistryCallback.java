package dk.mrspring.toggle.comp.vanilla;

import dk.mrspring.toggle.api.IBlockToggleRegistry;

/**
 * Created on 26-08-2015.
 */
public class ToggleRegistryCallback
{
    public static void register(IBlockToggleRegistry registry)
    {
        registry.registerToggleAction(new BucketToggleAction());
        registry.registerToggleAction(new PlantableToggleAction());
    }
}
