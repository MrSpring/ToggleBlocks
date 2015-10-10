package dk.mrspring.toggle;

import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IBlockToggleRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad on 01-03-2015.
 */
public class ToggleRegistry implements IBlockToggleRegistry
{
    private static ToggleRegistry instance;

    public static ToggleRegistry instance()
    {
        return instance;
    }

    static void initialize()
    {
        instance = new ToggleRegistry();
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
