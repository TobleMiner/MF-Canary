package tobleminer.minefight.util.syncderp;

import java.util.UUID;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;

public class InventoryClearSync extends ServerTask
{
	private final Callback callback;
	private Inventory inventory;
	private Player player;
	
	private UUID uuid;
	
	public InventoryClearSync(TaskOwner owner, long delay, Callback callback)
	{
		this(owner, delay, false, callback);
	}
	
	public InventoryClearSync(TaskOwner owner, long delay, boolean repeat, Callback callback)
	{
		super(owner, delay, repeat);
		this.callback = callback;
	}
	
	public interface Callback
	{
		public void onInventoryCleared(Player p, UUID uuid);
	}
	
	public UUID prepare(Inventory inventory, Player p)
	{
		this.inventory = inventory;
		this.player = p;
		this.uuid = UUID.randomUUID();
		ServerTaskManager.addTask(this);
		return this.uuid;
	}

	@Override
	public void run()
	{
		this.inventory.clearInventory();
		if(this.callback != null)
			this.callback.onInventoryCleared(this.player, this.uuid);
	}
}
