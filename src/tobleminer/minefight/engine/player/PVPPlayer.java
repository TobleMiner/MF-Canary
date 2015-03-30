package tobleminer.minefight.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.canarymod.Canary;
import net.canarymod.LineTracer;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.PlayerInventory;
import net.canarymod.api.packet.Packet;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.effects.SoundEffect.Type;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.ChatFormat;
import net.canarymod.commandsys.commands.vanilla.Effect;
import tobleminer.minefight.Main;
import tobleminer.minefight.config.container.Killstreak;
import tobleminer.minefight.config.weapon.WeaponDescriptor;
import tobleminer.minefight.config.weapon.WeaponIndex;
import tobleminer.minefight.config.weapon.WeaponDescriptor.DamageType;
import tobleminer.minefight.config.weapon.WeaponDescriptor.WeaponUseType;
import tobleminer.minefight.engine.Score;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.match.statistics.StatType;
import tobleminer.minefight.engine.match.statistics.StatUpdateType;
import tobleminer.minefight.engine.match.team.Team;
import tobleminer.minefight.engine.player.combatclass.CombatClass;
import tobleminer.minefight.engine.player.info.MapInfoRenderer;
import tobleminer.minefight.error.Error;
import tobleminer.minefight.error.ErrorReporter;
import tobleminer.minefight.error.ErrorSeverity;
import tobleminer.minefight.util.Util;
import tobleminer.minefight.util.location.Vector;
import tobleminer.minefight.util.syncderp.EffectSync;
import tobleminer.minefight.util.syncderp.InventoryAddItemSync;
import tobleminer.minefight.util.syncderp.InventoryRemoveItemSync;
import tobleminer.minefight.util.syncderp.SoundSync;

public class PVPPlayer
{
	public final Player thePlayer;
	private CombatClass combatClass;
	private boolean spawned = false;
	private Team team;
	private final Match match;
	public int kills;
	public int deaths;
	public int killstreak;
	private double points;
	private final HashMap<PVPPlayer,Killhelper> killHelpers = new HashMap<PVPPlayer,Killhelper>();
	public boolean normalDeathBlocked = false;
	public int timer = 1;
	private final MapView mv;
	private final MapInfoRenderer mir;
	private Item[] inventoryBackup;
	private Item helmetBackup;
	private Item bodyarmorBackup;
	private Item legginsBackup;
	private Item bootBackup;
	public boolean hasMap;
	public final List<Killstreak> killstreaks = new ArrayList<Killstreak>();
	private int shotcnt = 0;
	
	public PVPPlayer(Player thePlayer,Team team,Match match, MapView mv)
	{
		this.thePlayer = thePlayer;
		this.team = team;
		this.match = match;
		this.mv = mv;
		this.mv.setScale(Scale.CLOSEST);
		this.mir = new MapInfoRenderer(match, this);
		if(mir._20pcooler)
		{
			for(MapRenderer mr : new ArrayList<MapRenderer>(mv.getRenderers()))
				mv.removeRenderer(mr);
		}
		mv.addRenderer(this.mir);
	}
	
	public void addKillhelper(PVPPlayer damager, double d)
	{
		if(damager != this && damager.getTeam() != this.getTeam())
		{
			Killhelper kh = this.killHelpers.get(damager);
			if(kh == null)
			{
				kh = new Killhelper(damager);
			}
			double maxHealth = this.thePlayer.getMaxHealth();
			kh.addDamage(d/maxHealth);
			this.killHelpers.put(damager, kh);
		}
	}
	
	public void storeInventory()
	{
		PlayerInventory pi = this.thePlayer.getInventory();
		this.inventoryBackup = pi.getContents();
		this.helmetBackup = pi.getHelmetSlot();
		this.bodyarmorBackup = pi.getChestplateSlot();
		this.legginsBackup = pi.getLeggingsSlot();
		this.bootBackup = pi.getBootsSlot();
	}
	
	public void loadInventory()
	{
		if(this.inventoryBackup != null)
		{
			PlayerInventory pi = this.thePlayer.getInventory();
			pi.setContents(this.inventoryBackup);
			pi.setHelmetSlot(this.helmetBackup);
			pi.setChestPlateSlot(this.bodyarmorBackup);
			pi.setLeggingsSlot(this.legginsBackup);
			pi.setBootsSlot(this.bootBackup);
		}
	}
	
	public void setCombatClass(CombatClass cc)
	{
		this.combatClass = cc;
	}
	
	public CombatClass getCombatClass()
	{
		return this.combatClass;
	}
		
	public boolean isSpawned()
	{
		return spawned;
	}
	
	public String getName()
	{
		String name = this.thePlayer.getName();
		if(name.length() > 12)
		{
			name = name.substring(0,9)+"..";
		}
		return this.team.color + name + ChatFormat.RESET;
	}
	
	public Player getPlayer()
	{
		return thePlayer;
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public void setTeam(Team t)
	{
		if(match.anounceTeamchange(this,team,t))
		{
			this.team = t;
			this.thePlayer.setDisplayName(this.team.color + this.thePlayer.getName() + ChatFormat.RESET);
		}
	}
	
	public void leaveMatch()
	{
		this.mv.removeRenderer(this.mir);
		this.match.anouncePlayerLeave(this);
		this.thePlayer.message(ChatFormat.DARK_GREEN + String.format(Main.gameEngine.dict.get("matchLeaveMsg"), points, ((double)kills) / ((double)(deaths == 0 ? 1 : deaths))));
	}
	
	public void leaveMatch(Location matchLeaveLoc)
	{
		this.leaveMatch();
		if(matchLeaveLoc != null)
		{
			this.thePlayer.teleportTo(matchLeaveLoc);
			this.thePlayer.message(ChatFormat.DARK_GREEN + Main.gameEngine.dict.get("matchTpMsg"));
		}
	}
	
	public boolean createFakeExplosion(Location loc, float strength, boolean playSound)
	{
		try
		{
			Packet fakeExplo = Canary.factory().getPacketFactory().explosion(loc.getX(), loc.getY(), loc.getZ(), strength, new ArrayList<Position>(), new Vector());
			this.thePlayer.sendPacket(fakeExplo);
			if(playSound)
				new SoundSync(Main.main, 0, null).prepare(Type.EXPLODE, loc, 63f, 0.5f);
			return true;
		}
		catch(Exception ex)
		{
			Error err = new Error("Failed sending fake explosion packet!", "Fake explosion could not be sent to " + thePlayer.getName() + ".", "This isn't normal at all, but it won't affect the gameplay a lot.", this.getClass().getName(), ErrorSeverity.WARNING);
			ErrorReporter.reportError(err);
		}
		return false;
	}
	
	public Match getMatch()
	{
		return match;
	}
	
	public void onKill(PVPPlayer killer)
	{
		this.deaths++;
		this.killstreak = 0;
		Collection<Killhelper> khelpers = this.killHelpers.values(); 
		for(Killhelper kh : khelpers)
		{
			if(kh.damager != killer && kh.damager != null)
			{
				kh.damager.killAsist(kh.getDamage() * Main.gameEngine.configuration.getScore(kh.damager.thePlayer.getWorld(), Score.KILL));
			}
		}
		this.killHelpers.clear();
		this.setCombatClass(null);
		this.match.sh.updatePlayer(this, StatType.DEATHS, StatUpdateType.ADD, new Long(1L));
	}
	
	public void killAsist(double d)
	{
		this.addPoints(d);
		this.thePlayer.message(ChatFormat.GOLD+String.format(Main.gameEngine.dict.get("killassist"), d));
	}
	
	public void killed()
	{
		this.kills++;
		this.killstreak++;
		double p = Main.gameEngine.configuration.getScore(this.thePlayer.getWorld(), Score.KILL);
		this.addPoints(p);
		this.match.sh.updatePlayer(this, StatType.KILLS, StatUpdateType.ADD, new Long(1L));
	}
	
	public void flagCaptured()
	{
		double points = Main.gameEngine.configuration.getScore(this.match.getWorld(), Score.FLAGCAP);
		this.thePlayer.message(ChatFormat.DARK_GREEN+String.format(Main.gameEngine.dict.get("flagcappoints"),points));
		this.addPoints(points);
		this.match.sh.updatePlayer(this, StatType.FLAGCAP, StatUpdateType.ADD, new Long(1L));
	}

	public void teleport(Location loc)
	{
		this.thePlayer.teleportTo(loc);
	}
	
	public void setSpawned(boolean b)
	{
		this.spawned = b;
	}
	
	public void doUpdate()
	{
		if(this.thePlayer.isBlocking() && this.isSpawned() && this.combatClass != null)
		{
			PlayerInventory pi = this.thePlayer.getInventory();
			Item inHand = pi.getItemInHand();
			if(inHand != null)
			{
				WeaponIndex wi = this.match.weapons.get(WeaponUseType.BLOCK);
				if(wi != null)
				{
					WeaponDescriptor wd = wi.get(inHand.getType());
					if(wd != null)
					{
						if(wd.cadence > 0 && (timer % ((int)Math.round(1200d / (double)wd.cadence))) == 0)
						{
							if(wd.ammomat == null || pi.hasItem(wd.ammomat))
							{
								if(wd.ammomat != null) new InventoryRemoveItemSync(Main.main, 0, null).prepare(pi, wd.ammomat, 1);
								if(wd.firemode == -42 || (wd.firemode > 0 && this.shotcnt < wd.firemode))
								{
									this.shotcnt++;
									if(wd.dmgType == DamageType.PROJECTILEHIT)
									{
										Block b = new LineTracer(this.thePlayer).getTargetBlock();
										if(b != null)
										{
											Location playerEyeLoc = (Location)this.thePlayer.getLocation().add(new Vector(0d, 2.0d, 0d)); 
											Vector locHelp = new Vector(b.getLocation().subtract(playerEyeLoc));
											if(locHelp.length() > 0)
											{
												double speed = wd.speed;
												Vector velocity = new Vector(locHelp.clone().multiply(speed / locHelp.length()));
												match.createWeaponProjectile(this, playerEyeLoc.clone().add(velocity.clone().multiply(1.5d / velocity.length())), velocity, wd, false);
											}
										}
									}
									else if(wd.dmgType == DamageType.FLAMETHROWER)
									{
										HashSet<Byte> trans = new HashSet<Byte>();
										trans.add((byte)31);
										trans.add((byte)0);
										trans.add((byte)20);
										trans.add((byte)102);
										Block b = new LineTracer(this.thePlayer).getTargetBlock();
										if(b != null)
										{
											Location playerEyeLoc = (Location)this.thePlayer.getLocation().add(new Vector(0d, 1.0d, 0d)); 
											Vector locHelp = new Vector(b.getLocation().subtract(playerEyeLoc));
											Location launchLoc = (Location)playerEyeLoc.add(locHelp.multiply(1.5d / locHelp.length()));
											new EffectSync(Main.main, 0, null).prepare(net.canarymod.api.world.effects.Particle.Type.FLAME, launchLoc, new Vector(), 0, 12);
											List<PVPPlayer> players = match.getSpawnedPlayersNearLocation(this.thePlayer.getLocation(), (int)Math.round(wd.maxDist));
											PVPPlayer target = null;
											for(PVPPlayer p : players)
											{
												if(match.canKill(this,p) && p != this)
												{
													target = p;
													break;
												}
											}
											if(target != null)
											{
												target.normalDeathBlocked = true;
												target.thePlayer.dealDamage(net.canarymod.api.DamageType.PLAYER ,
														(float)(wd.getDamage(this.thePlayer.getLocation().getDistance(target.thePlayer.getLocation()))
																* target.thePlayer.getMaxHealth()));
												if(target.thePlayer.getHealth() <= 0)
												{
													this.match.kill(this, target, wd.getName(), target.thePlayer.getHealth() > 0);
												}
												else
												{
													target.thePlayer.setFireTicks(100);
												}
												target.normalDeathBlocked = false;
											}
										}
										new LineTracer(this.thePlayer).get
										List<Block> potIgniBlocks = this.thePlayer.getLineOfSight(null,(int)Math.round(wd.maxDist));
										for(Block block : potIgniBlocks)
										{
											if(block.getType().isFlammable() && (!Util.protect.isBlockProtected(block)) && this.match.damageEnviron)
											{
												Util.block.ignite(block);
											}
										}
									}
									else if(wd.dmgType == DamageType.MEDIGUN)
									{
										List<PVPPlayer> players = match.getSpawnedPlayersNearLocation(this.thePlayer.getLocation(), (int)Math.round(wd.maxDist));
										PVPPlayer target = null;
										for(PVPPlayer p : players)
										{
											if(p.getTeam() == this.getTeam() && p != this && p.thePlayer.getHealth() < p.thePlayer.getMaxHealth())
											{
												target = p;
												break;
											}
										}
										if(target != null)
										{
											Vector dir = new Vector(target.thePlayer.getLocation().clone().subtract(this.thePlayer.getLocation().clone()));
											int len = (int)Math.round(dir.length());
											if(len != 0)
												for(int i = 0; i <= len; i++)
													new EffectSync(Main.main, 0, null).prepare(net.canarymod.api.world.effects.Particle.Type.SPELL,
															(Location)this.thePlayer.getLocation().clone().add(new Vector(0d, 1d, 0d)).add(dir.clone().multiply(((double)i) / ((double)len))),
															new Vector(), 0, 7);
											double health = target.thePlayer.getHealth() - wd.getDamage(this.thePlayer.getLocation().getDistance(target.thePlayer.getLocation())) * target.thePlayer.getMaxHealth();
											if(health > target.thePlayer.getMaxHealth())
											{
												health = target.thePlayer.getMaxHealth();
											}
											target.thePlayer.setHealth((float)health);
										}
									}
								}
							}
						}
					}
				}
			}
			timer++;	
		}
		else
		{
			shotcnt = 0;
		}
	}
	
	public MapView getMapView()
	{
		return this.mv;
	}
	
	private void addPoints(double points)
	{
		this.points += points;
		this.match.sh.updatePlayer(this, StatType.POINTS, StatUpdateType.ADD, new Double(points));
	}

	public void radioDestroyed()
	{
		double pDest = Main.gameEngine.configuration.getScore(this.match.getWorld(),Score.RSDEST);
		this.addPoints(pDest);
		this.thePlayer.message(ChatFormat.DARK_GREEN+String.format(Main.gameEngine.dict.get("rsdestpoints"),pDest));
		this.match.sh.updatePlayer(this, StatType.RSDESTROY, StatUpdateType.ADD, new Long(1L));
	}
	
	public double getPoints()
	{
		return this.points;
	}

	public void resupplyGiven() 
	{
		double p = Main.gameEngine.configuration.getScore(this.match.getWorld(),Score.RESUPPLY);
		this.addPoints(p);
		this.thePlayer.message(ChatFormat.GOLD+String.format(Main.gameEngine.dict.get("pointsResupply"), p));
	}

	public void radioArmed()
	{
		double pArm = Main.gameEngine.configuration.getScore(this.match.getWorld(),Score.RSARM);
		this.addPoints(pArm);
		this.thePlayer.message(ChatFormat.DARK_GREEN+String.format(Main.gameEngine.dict.get("rsarmpoints"),pArm));
		this.match.sh.updatePlayer(this, StatType.RSARM, StatUpdateType.ADD, new Long(1L));
	}

	public void radioDisarmed()
	{
		double pDisarm = Main.gameEngine.configuration.getScore(this.match.getWorld(),Score.RSDISARM);
		this.addPoints(pDisarm);
		this.thePlayer.message(ChatFormat.DARK_GREEN+String.format(Main.gameEngine.dict.get("rsdisarmpoints"),pDisarm));
		this.match.sh.updatePlayer(this, StatType.RSDISARM, StatUpdateType.ADD, new Long(1L));
	}

	public void addKillstreak(Killstreak ks) 
	{
		this.killstreaks.add(ks);
		this.thePlayer.message(ChatFormat.GOLD+Main.gameEngine.dict.get("killstreak"));
		this.thePlayer.message(ChatFormat.GOLD+Main.gameEngine.dict.get(ks.transname));
		Inventory i = this.thePlayer.getInventory();
		switch(ks)
		{
			case IMS: new InventoryAddItemSync(Main.main, 0, null).prepare(i, ItemType.RedStone, 1); break;
			case PLAYERSEEKER: new InventoryAddItemSync(Main.main, 0, null).prepare(i, ItemType.Stick, 1); break;
		}		
	}
	
	public enum HitZone
	{
		HEAD("head"),
		TORSO("torso"),
		LEG("leg");
		
		public final String name;
		
		private HitZone(String name)
		{
			this.name = name;
		}
	}
}
