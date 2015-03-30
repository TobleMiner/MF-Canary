package tobleminer.minefight.util.syncderp;

import java.util.UUID;

import net.canarymod.Canary;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.world.position.Location;
import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;

public class SpawnEntitySync extends ServerTask
{
	private final Callback callback;
	private EntityType type;
	private Location loc;
	
	private UUID uuid;
	
	public SpawnEntitySync(TaskOwner owner, long delay, Callback callback)
	{
		this(owner, delay, false, callback);
	}
	
	public SpawnEntitySync(TaskOwner owner, long delay, boolean repeat, Callback callback)
	{
		super(owner, delay, repeat);
		this.callback = callback;
	}
	
	public interface Callback
	{
		public void onEntitySpawned(Entity entity, UUID uuid);
	}
	
	public UUID prepare(EntityType type, Location loc)
	{
		this.type = type;
		this.loc = loc;
		this.uuid = UUID.randomUUID();
		ServerTaskManager.addTask(this);
		return this.uuid;
	}

	@Override
	public void run()
	{
		Entity ent = Canary.factory().getEntityFactory().newEntity(type, loc);
		if(this.callback != null)
			this.callback.onEntitySpawned(ent, this.uuid);
	}
}
