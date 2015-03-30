package tobleminer.minefight.weapon.tickcontrolled.missile;

import net.canarymod.api.entity.Arrow;
import tobleminer.minefight.Main;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.util.syncderp.RemoveEntitySync;
import tobleminer.minefight.weapon.tickcontrolled.TickControlledWeapon;

public abstract class Missile extends TickControlledWeapon
{
	protected final PVPPlayer shooter;
	protected final Arrow arr;
	
	public Missile(Match m, PVPPlayer shooter, Arrow arr)
	{
		super(m);
		this.arr = arr;
		this.shooter = shooter;
		match.addMissile(this);
	}
	
	protected boolean canTarget(PVPPlayer target)
	{
		return (this.shooter.getTeam() != target.getTeam() && target.isSpawned());
	}
	
	public void explode()
	{
		this.unregisterTickControlled();
		this.match.rmMissile(this);
		new RemoveEntitySync(Main.main, 0, null).prepare(this.arr);
	}
	
	public Arrow getProjectile()
	{
		return this.arr;
	}
}
