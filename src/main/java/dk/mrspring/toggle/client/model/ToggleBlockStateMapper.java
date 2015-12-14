package dk.mrspring.toggle.client.model;

import com.google.common.collect.Maps;
import dk.mrspring.toggle.common.block.ToggleBlockSize;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.model.ModelResourceLocation;

import java.util.Map;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class ToggleBlockStateMapper implements IStateMapper
{
    @Override
    public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn)
    {
        Map<IBlockState, ModelResourceLocation> map = Maps.newHashMap();
        for (ToggleBlockSize size : ToggleBlockSize.values())
        {
            IBlockState state = blockIn.getStateFromMeta(size.getMetaValue());
            ModelResourceLocation location = new ModelResourceLocation("tb:toggle_controller_" + size.getName());
            System.out.println("Mapped: " + state.toString() + " to: " + location.toString());
            map.put(state, location);
        }
        return map;
    }
}
