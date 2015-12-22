package dk.mrspring.toggle.common.api_impl;

import dk.mrspring.toggle.api.IBlockToggleAction;
import dk.mrspring.toggle.api.IToggleBlockActionRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 15-12-2015 for ToggleBlocks.
 */
public class ToggleActionRegistry implements IToggleBlockActionRegistry
{
    private static ToggleActionRegistry instance = new ToggleActionRegistry();

    public static ToggleActionRegistry instance()
    {
        return instance;
    }

    List<IBlockToggleAction> registeredActions;

    private ToggleActionRegistry()
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
