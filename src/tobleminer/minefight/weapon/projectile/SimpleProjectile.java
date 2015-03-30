package tobleminer.minefight.weapon.projectile;

import net.canarymod.api.entity.Arrow;
import net.canarymod.api.world.position.Location;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.engine.player.PVPPlayer.HitZone;

public class SimpleProjectile extends Projectile
{
	public final double dmgMul;
	public final double damage;
	
	public SimpleProjectile(PVPPlayer p, boolean crit, double mul, double dmg, Arrow arr, String name)
	{
		super(p, arr, crit, name);
		this.dmgMul = mul;
		this.damage = -1d;
	}
	
	public SimpleProjectile(PVPPlayer p, boolean crit, double mul, Arrow arr, String name)
	{
		this(p, crit, mul, -1d, arr, name);
	}
	
	public double getDmg(double base, HitZone hz, Location loc)
	{
		return base * this.dmgMul;
	}
}