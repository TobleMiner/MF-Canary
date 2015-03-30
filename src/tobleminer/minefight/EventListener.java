package tobleminer.minefight;

import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.ItemDropHook;
import net.canarymod.hook.system.ServerTickHook;
import net.canarymod.plugin.PluginListener;


public class EventListener implements PluginListener
{
	final Main mane;
	
	public EventListener(Main p)
	{
		this.mane = p;
	}
		
	@HookHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Main.gameEngine.playerInteract(event);
	}
	
	@HookHandler
	public void onPlayerDropItem(ItemDropHook event)
	{
		Main.gameEngine.playerDroppedItem(event);
	}
	
	@HookHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Main.gameEngine.playerPickUpItem(event);
	}
	
	@HookHandler
	public void onItemDespawn(ItemDespawnEvent event)
	{
		Main.gameEngine.itemDespawn(event);
	}
	
	@HookHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		Main.gameEngine.entityDamage(event);
	}
	
	@HookHandler
	public void onEntityCombust(EntityCombustEvent event)
	{
		Main.gameEngine.entityCombust(event);
	}
	
	@HookHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Main.gameEngine.playerDeath(event);
	}
	
	@HookHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		Main.gameEngine.playerChangedWorld(event.getPlayer(),event.getFrom());
	}
	
	@HookHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Main.gameEngine.playerQuit(event.getPlayer());
	}
	
	@HookHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		Main.gameEngine.projectileLaunched(event);
	}
	
	@HookHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		event.setCancelled(Main.gameEngine.blockPlace(event));
	}
	
	@HookHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		event.setCancelled(Main.gameEngine.blockBreak(event));
	}
	
	@HookHandler
	public void onBlockDamage(BlockDamageEvent event)
	{
		event.setCancelled(Main.gameEngine.blockDamaged(event));
	}
		
	@HookHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event)
	{
		event.setCancelled(Main.gameEngine.entityChangeBlock(event));
	}
	
	@HookHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		Main.gameEngine.entityDamageByEntity(event);
		if(event.isCancelled())
			event.setDamage(0d);
	}
	
	@HookHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		event.setRespawnLocation(Main.gameEngine.playerRespawn(event.getPlayer(),event.getRespawnLocation()));
	}
	
	@HookHandler
	public void onProjectileHit(ProjectileHitEvent event)
	{
		Main.gameEngine.projectileHit(event);
	}
	
	@HookHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent apce)
	{
		Main.gameEngine.playerChat(apce);
	}
	
	@HookHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		Main.gameEngine.foodLevelChange(event);
	}
	
	@HookHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		Main.gameEngine.entityExplosion(event);
	}
	
	@HookHandler
	public void onBlockBurn(BlockBurnEvent event)
	{
		Main.gameEngine.blockBurn(event);
	}
	
	@HookHandler
	public void onTick(ServerTickHook event)
	{
		Main.gameEngine.doUpdate();
	}
}
