package tobleminer.minefight.util.syncderp;

import java.util.UUID;

import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;

public class SoundSync extends ServerTask
{
	private final Callback callback;
	private SoundEffect.Type type;
	private Location loc;
	private float pitch;
	private float volume;
	
	private UUID uuid;
	
	public SoundSync(TaskOwner owner, long delay, Callback callback)
	{
		this(owner, delay, false, callback);
	}
	
	public SoundSync(TaskOwner owner, long delay, boolean repeat, Callback callback)
	{
		super(owner, delay, repeat);
		this.callback = callback;
	}
	
	public interface Callback
	{
		public void onSound(SoundEffect effect, UUID uuid);
	}
	
	public UUID prepare(SoundEffect.Type type, Location loc, float pitch, float volume)
	{
		this.type = type;
		this.loc = loc;
		this.pitch = pitch;
		this.volume = volume;
		this.uuid = UUID.randomUUID();
		ServerTaskManager.addTask(this);
		return this.uuid;
	}
	
	@Override
	public void run()
	{
		SoundEffect effect = new SoundEffect(this.type,
				this.loc.getX(), this.loc.getY(), this.loc.getZ(),
				this.volume, this.pitch);
		this.loc.getWorld().playSound(effect);
		if(this.callback != null)
			this.callback.onSound(effect, this.uuid);
	}
}
