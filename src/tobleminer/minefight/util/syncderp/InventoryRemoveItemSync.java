package tobleminer.minefight.util.syncderp;

import java.util.UUID;

import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;

public class InventoryRemoveItemSync extends ServerTask
{
	private final Callback callback;
	private ItemType type;
	private int amount;
	private Inventory inventory;
	
	private UUID uuid;
	
	public InventoryRemoveItemSync(TaskOwner owner, long delay, Callback callback)
	{
		this(owner, delay, false, callback);
	}
	
	public InventoryRemoveItemSync(TaskOwner owner, long delay, boolean repeat, Callback callback)
	{
		super(owner, delay, repeat);
		this.callback = callback;
	}
	
	public interface Callback
	{
		public void onItemRemoved(UUID uuid);
	}
	
	public UUID prepare(Inventory inventory, ItemType type, int amount)
	{
		this.inventory = inventory;
		this.type = type;
		this.amount = amount;
		this.uuid = UUID.randomUUID();
		ServerTaskManager.addTask(this);
		return this.uuid;
	}

	@Override
	public void run()
	{
		this.inventory.decreaseItemStackSize(this.type.getId(), this.amount, (short)this.type.getData());
		if(this.callback != null)
			this.callback.onItemRemoved(this.uuid);
	}
}
