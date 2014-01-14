package TobleMiner.MineFight.Util.SyncDerp;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Protection.ProtectedArea;

public class EffectSyncCalls
{
	public static void createExplosion(final Location loc, final float exploStrength)
	{
		Main.main.getServer().getScheduler().runTask(Main.main, new Runnable()
		{		
			public void run()
			{
				loc.getWorld().createExplosion(loc.getX(),loc.getY(),loc.getZ(), exploStrength);
			}
		});
	}
	
	
	public static void createExplosionRespectProtection(final Location loc, final float exploStrength)
	{
		Main.main.getServer().getScheduler().runTask(Main.main, new Runnable()
		{		
			public void run()
			{
				World world = loc.getWorld();
				List<ProtectedArea> lpa = Main.gameEngine.configuration.protectionRegions.get(loc.getWorld());
				boolean isProtected = false;
				if(lpa != null)
				{
					for(ProtectedArea pa : lpa)
					{
						isProtected = pa.isCoordInsideRegion(loc);
						if(isProtected)
						{
							break;
						}
					}
				}
				if(!isProtected)
				{
					world.createExplosion(loc, exploStrength);
				}
			}
		});
	}
	public static void showEffect(final Location loc, final Effect e, final int arg)
	{
		Main.main.getServer().getScheduler().runTask(Main.main, new Runnable()
		{		
			public void run()
			{
				loc.getWorld().playEffect(loc, e, arg);
			}
		});
	}
	public static void playSound(final Location loc, final Sound s, final float f1, final float f2)
	{
		Main.main.getServer().getScheduler().runTask(Main.main, new Runnable()
		{		
			public void run()
			{
				loc.getWorld().playSound(loc, s, f1, f2);
			}
		});
	}
}
