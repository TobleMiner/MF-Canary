package tobleminer.minefight.weapon;

import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;

public interface Weapon 
{	
	public abstract ItemType getMaterial(World w);
	public abstract short getSubId(World w);
}
