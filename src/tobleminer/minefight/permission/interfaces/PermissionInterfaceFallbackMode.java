package tobleminer.minefight.permission.interfaces;

import net.canarymod.api.entity.living.humanoid.Player;
import tobleminer.minefight.permission.PermissionInterface;

public class PermissionInterfaceFallbackMode extends PermissionInterface
{

	@Override
	public boolean hasPlayerPermissionTo(Player p, String perm)
	{
		if(p != null)
		{
			return p.isAdmin();
		}
		return false;
	}

}
