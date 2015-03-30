package tobleminer.minefight.util.syncderp;

import java.util.UUID;

import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.inventory.Item;
import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.tasks.TaskOwner;

public class InventoryAddItemSync extends ServerTask
{
	private final Callback callback;
	private Item item;
	private Inventory inventory;
	
	private UUID uuid;
	
	public InventoryAddItemSync(TaskOwner owner, long delay, Callback callback)
	{
		this(owner, delay, false, callback);
	}
	
	public InventoryAddItemSync(TaskOwner owner, long delay, boolean repeat, Callback callback)
	{
		super(owner, delay, repeat);
		this.callback = callback;
	}
	
	public interface Callback
	{
		public void onItemAdded(Item item, UUID uuid);
	}
	
	public UUID prepare(Inventory inventory, Item item)
	{
		this.inventory = inventory;
		this.item = item;
		this.uuid = UUID.randomUUID();
		ServerTaskManager.addTask(this);
		return this.uuid;
	}

	@Override
	public void run()
	{
		this.inventory.addItem(this.item);
		if(this.callback != null)
			this.callback.onItemAdded(this.item, this.uuid);
	}
}
