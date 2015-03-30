package tobleminer.minefight.util.protection;

import java.util.List;

import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.position.Location;
import tobleminer.minefight.Main;
import tobleminer.minefight.util.geometry.Area3D;

public class ProtectionUtil 
{
	public boolean isLocProtected(Location loc)
	{
		List<Area3D> lpa = Main.gameEngine.configuration.getProtectedAreasByWorld(loc.getWorld());
		if(lpa != null)
		{
			for(Area3D pa : lpa)
			{
				if(pa.isCoordInsideRegion(loc))
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean isBlockProtected(Block b)
	{
		return this.isLocProtected(b.getLocation());
	}
	
}
