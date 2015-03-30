package tobleminer.minefight.util.syncderp;

import java.util.UUID;

import net.canarymod.api.entity.Entity;
import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;

public class RemoveEntitySync extends ServerTask
{
	private final Callback callback;
	private Entity entity;
	
	private UUID uuid;
	
	public RemoveEntitySync(TaskOwner owner, long delay, Callback callback)
	{
		this(owner, delay, false, callback);
	}
	
	public RemoveEntitySync(TaskOwner owner, long delay, boolean repeat, Callback callback)
	{
		super(owner, delay, repeat);
		this.callback = callback;
	}
	
	public interface Callback
	{
		public void onEntityRemoved(Entity entity, UUID uuid);
	}
	
	public UUID prepare(Entity entity)
	{
		this.entity = entity;
		this.uuid = UUID.randomUUID();
		ServerTaskManager.addTask(this);
		return this.uuid;
	}

	@Override
	public void run()
	{
		this.entity.destroy();
		if(this.callback != null)
			this.callback.onEntityRemoved(this.entity, this.uuid);
	}
}
