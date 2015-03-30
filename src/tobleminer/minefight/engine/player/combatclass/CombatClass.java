package tobleminer.minefight.engine.player.combatclass;

import java.util.ArrayList;
import java.util.List;

import tobleminer.minefight.util.item.ItemStack;

public class CombatClass 
{
	public final String name;
	public List<ItemStack> kit = new ArrayList<ItemStack>();
	public ItemStack[] armor = new ItemStack[4];
	
	public CombatClass(String name)
	{
		this.name = name;
	}
}
