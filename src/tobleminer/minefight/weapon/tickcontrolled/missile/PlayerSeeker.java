package tobleminer.minefight.weapon.tickcontrolled.missile;

import java.util.List;

import net.canarymod.api.entity.Arrow;
import net.canarymod.api.world.position.Location;
import tobleminer.minefight.config.Config;
import tobleminer.minefight.config.container.PlayerSeekerContainer;
import tobleminer.minefight.engine.GameEngine;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.util.location.Vector;

public class PlayerSeeker extends Missile
{
	private PVPPlayer target;
	private Config cfg;
	private Location base;
	private int state = 0;
	private double speed = 0;
	
	public PlayerSeeker(Match m, Arrow arr, PVPPlayer shooter, PVPPlayer target, Config cfg)
	{
		super(m, shooter, arr);
		this.target = target;
		this.cfg = cfg;
	}

	@Override
	public void doUpdate() 
	{
		if(this.arr == null || this.arr.isDead()) this.explode();
		PlayerSeekerContainer psc = this.cfg.getPlayerSeekerConf(this.match.getWorld(), this.match.gmode);
		if(this.target == null)
		{
			List<PVPPlayer> players = match.getSpawnedPlayersNearLocation(this.arr.getLocation().clone(), psc.detectionDist);
			for(PVPPlayer p : players)
			{
				if(this.canTarget(p))
				{
					this.target = p;
					this.base = this.arr.getLocation().clone();
					break;
				}
			}
		}
		if(this.target != null)
		{
			if(!this.canTarget(this.target)) this.explode();
			this.speed += psc.maxAccel/GameEngine.tps;
			if(speed > psc.maxSpeed) speed = psc.maxSpeed;
			Vector dirTarget = null;
			if(state == 0)
			{
				dirTarget = new Vector(0d, 1d, 0d);
				if(this.arr.getLocation().getY() - this.base.getY() >= psc.peakHeight)
				{
					state = 1;
				}
			}
			if(state == 1)
			{
				dirTarget = new Vector(this.target.thePlayer.getLocation().clone().subtract(this.arr.getLocation()));
				if(dirTarget.getMagnitude() < psc.threshold)
				{
					state = 2;
					this.explode();
				}
			}
			Vector dirCurrent = new Vector(this.arr.getMotion().copy());
			dirTarget = new Vector(dirTarget.clone().multiply(this.speed / dirTarget.length()));
			Vector delta = new Vector(dirTarget.clone().subtract(dirCurrent.clone()));
			Vector dirEff = new Vector(dirCurrent.clone().add(delta.clone()
					.multiply(Math.min(1d, delta.length() / (psc.maxAccel / GameEngine.tps)))));
			Vector velocity = dirEff.clone();
			this.arr.setMotionX(velocity.getX());
			this.arr.setMotionY(velocity.getY());
			this.arr.setMotionZ(velocity.getZ());
		}
	}
	
	@Override
	public void explode()
	{
		PlayerSeekerContainer psc = this.cfg.getPlayerSeekerConf(this.match.getWorld(), this.match.gmode);
		this.match.createExplosion(this.shooter, this.arr.getLocation().clone(), psc.exploStr, "PLAYER SEEKER");
		super.explode();
	}
}
