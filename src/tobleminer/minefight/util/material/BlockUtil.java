package tobleminer.minefight.util.material;

import java.util.ArrayList;
import java.util.List;

import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import tobleminer.minefight.util.location.Vector;

public class BlockUtil 
{
	private List<Vector> igniteVects = new ArrayList<Vector>(); 
	
	public BlockUtil()
	{
		this.igniteVects.add(new Vector(0d,1d,0d));
		this.igniteVects.add(new Vector(1d,0d,0d));
		this.igniteVects.add(new Vector(0d,0d,-1d));
		this.igniteVects.add(new Vector(-1d,0d,0d));
		this.igniteVects.add(new Vector(0d,0d,1d));
	}
		
	public boolean ignite(Block b)
	{
		for(Vector vect : this.igniteVects)
		{
			Location loc = (Location)b.getLocation().copy().add(vect);
			Block fire = b.getWorld().getBlockAt(loc);
			if(fire != null) 
			{
				if(fire.getType().equals(BlockType.Air))
				{
					fire.setType(BlockType.FireBlock);
					return true;
				}
			}
		}
		return false;
	}
}
