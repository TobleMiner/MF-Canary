package tobleminer.minefight.util.syncderp;

import java.util.UUID;

import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.position.Location;
import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;
import tobleminer.minefight.util.location.Vector;

public class EffectSync extends ServerTask
{
	private final Callback callback;
	private Particle.Type type;
	private Location loc;
	private Vector dir;
	private float speed;
	private int quantity;
	
	private UUID uuid;
	
	public EffectSync(TaskOwner owner, long delay, Callback callback)
	{
		this(owner, delay, false, callback);
	}
	
	public EffectSync(TaskOwner owner, long delay, boolean repeat, Callback callback)
	{
		super(owner, delay, repeat);
		this.callback = callback;
	}
	
	public interface Callback
	{
		public void onEffect(Particle part, UUID uuid);
	}
	
	public UUID prepare(Particle.Type type, Location loc, Vector dir, float speed, int quantity)
	{
		this.type = type;
		this.loc = loc;
		this.dir = dir;
		this.speed = speed;
		this.quantity = quantity;
		this.uuid = UUID.randomUUID();
		ServerTaskManager.addTask(this);
		return this.uuid;
	}

	@Override
	public void run()
	{
		Particle p = new Particle(this.loc.getX(), this.loc.getY(), this.loc.getZ(),
				this.dir.getX(), this.dir.getY(), this.dir.getZ(),
				this.speed, this.quantity, this.type);
		this.loc.getWorld().spawnParticle(p);
		if(this.callback != null)
			this.callback.onEffect(p, this.uuid);
	}
}
