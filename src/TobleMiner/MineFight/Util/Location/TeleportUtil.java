package TobleMiner.MineFight.Util.Location;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class TeleportUtil
{
	public static Location getSafeTeleportLocation(Location loc)
	{
		return getSafeTeleportLocation(loc, 257);
	}
	
	public static Location getSafeTeleportLocation(Location loc, int searchBeginY)
	{
		return getSafeTeleportLocation(loc.getWorld(), loc.getBlockX(), loc.getBlockZ(), searchBeginY);
	}
	
	public static Location getSafeTeleportLocation(World w, int x, int z, int searchBeginY)
	{
		
		for(;searchBeginY>=0;searchBeginY--)
		{
			if(!w.getBlockAt(x, searchBeginY, z).getType().equals(Material.AIR))
			{
				searchBeginY += 2;
				break;
			}
		}
		return new Location(w, x, searchBeginY, z);
	}

	public static List<Location> getSafeTeleportLocations(Location loc)
	{
		return getSafeTeleportLocations(loc, 257);
	}
	
	public static List<Location> getSafeTeleportLocations(Location loc, int searchBeginY)
	{
		return getSafeTeleportLocations(loc.getWorld(), loc.getBlockX(), loc.getBlockZ(), searchBeginY);
	}
	
	
	public static List<Location> getSafeTeleportLocations(World w, int x, int z, int searchBeginY)
	{
		int freespacesize = 0;
		List<Location> safeLocs = new ArrayList<Location>();
		for(;searchBeginY>=0;searchBeginY--)
		{
			if(!w.getBlockAt(x, searchBeginY, z).getType().equals(Material.AIR))
			{
				if(freespacesize > 2)
				{
					safeLocs.add(new Location(w, x, searchBeginY+1, z));
				}
				freespacesize = 0;
			}
			else
			{
				freespacesize++;
			}
		}
		return safeLocs;
	}
}