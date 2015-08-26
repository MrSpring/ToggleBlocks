package dk.mrspring.toggle;

import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IBlockToggleRegistry;
import dk.mrspring.toggle.comp.vanilla.BucketToggleAction;
import dk.mrspring.toggle.comp.vanilla.PlantableToggleAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad on 01-03-2015.
 */
public class ToggleRegistry implements IBlockToggleRegistry
{
    public static ToggleRegistry instance;

    static void initialize()
    {
        instance = new ToggleRegistry();
    }

    static void registerVanilla()
    {
        instance.registerToggleAction(new BucketToggleAction());
        instance.registerToggleAction(new PlantableToggleAction());
    }

    List<IBlockToggleAction> registeredActions;

    private ToggleRegistry()
    {
        registeredActions = new ArrayList<IBlockToggleAction>();
    }

    @Override
    public void registerToggleAction(IBlockToggleAction action)
    {
        if (action != null && !registeredActions.contains(action))
            registeredActions.add(action);
    }

    public List<IBlockToggleAction> getRegisteredActions()
    {
        return this.registeredActions;
    }
}
