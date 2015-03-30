package tobleminer.minefight.util.item;

import net.canarymod.api.inventory.ItemType;

public class ItemStack 
{
	private ItemType material;
	private int amount;
	private short data;
	
	public ItemStack(ItemType material, int amount)
	{
		this(material, amount, (short)0);
	}
	
	public ItemStack(ItemType material, int amount, short data)
	{
		this.material = material;
		this.amount = amount;
		this.data = data;
	}
	
	public void setMaterial(ItemType material)
	{
		this.material = material;
	}
	
	public void setAmount(int amount)
	{
		this.amount = amount;
	}
	
	public void setData(short data)
	{
		this.data = data;
	}
	
	public ItemType getMaterial()
	{
		return this.material;
	}
	
	public int getAmount()
	{
		return this.amount;
	}
	
	public short getData()
	{
		return this.data;
	}
}
