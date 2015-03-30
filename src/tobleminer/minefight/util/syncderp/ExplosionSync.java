package tobleminer.minefight.util.syncderp;

import java.util.UUID;

import net.canarymod.api.entity.Entity;
import net.canarymod.api.world.position.Location;
import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;
import tobleminer.minefight.util.Util;

public class ExplosionSync extends ServerTask
{
	private final Callback callback;
	private Entity entity;
	private Location loc;
	private float strength;
	private boolean smoke;
	
	private UUID uuid;
	
	public ExplosionSync(TaskOwner owner, long delay, Callback callback)
	{
		this(owner, delay, false, callback);
	}
	
	public ExplosionSync(TaskOwner owner, long delay, boolean repeat, Callback callback)
	{
		super(owner, delay, repeat);
		this.callback = callback;
	}
	
	public interface Callback
	{
		public void onExplode(Entity entity, UUID uuid);
	}
	
	public UUID prepare(Entity entity, Location loc, float strength, boolean smoke, boolean respectProtection)
	{
		if(respectProtection && Util.protect.isLocProtected(loc))
			return null;
		this.entity = entity;
		this.loc = loc;
		this.strength = strength;
		this.smoke = smoke;
		this.uuid = UUID.randomUUID();
		ServerTaskManager.addTask(this);
		return this.uuid;
	}

	@Override
	public void run()
	{
		this.loc.getWorld().makeExplosion(this.entity, this.loc, this.strength, this.smoke);
		if(this.callback != null)
			this.callback.onExplode(this.entity, this.uuid);
	}
}
