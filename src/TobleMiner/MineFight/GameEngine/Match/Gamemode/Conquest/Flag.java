package TobleMiner.MineFight.GameEngine.Match.Gamemode.Conquest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Configuration.Container.FlagContainer;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.Score;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Team.Team;
import TobleMiner.MineFight.GameEngine.Match.Team.TeamBlue;
import TobleMiner.MineFight.GameEngine.Match.Team.TeamRed;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;

public class Flag
{
	private final List<Block> flagArea;
	private final Sign sign;
	private final String name;
	private final Match match;
	private final double flagCapDist;
	private final double flagCapSpeed;
	private final double flagCapAccelPerPerson;
	private double percRed = 0.0d;
	private double percBlue = 0.0d;
	private Team owner = null;
	private int timer = 0;
	private List<PVPPlayer> helpers= new ArrayList<PVPPlayer>();
	
	public Flag(FlagContainer fc,Match match,double flagCaptureDist,double flagCapSpeed,double flagCapAccel)
	{
		this.sign = fc.sign;
		this.flagArea = getFlagBlocks(sign, getFacing(sign));
		this.name = fc.name;
		for(Block b : flagArea)
		{
			b.setType(Material.WOOL);
			b.getState().setData(new Wool(DyeColor.WHITE));
		}
		this.match = match;
		this.flagCapDist = flagCaptureDist;
		this.flagCapSpeed = flagCapSpeed;
		this.flagCapAccelPerPerson = flagCapAccel;
	}
	
	public void doUpdate()
	{
		if(timer >= 50) //Update each 0.5s
		{
			try
			{
				//flag capture logic
				List<PVPPlayer> players = match.getPlayers();
				int playersBlue = 0;
				int playersRed  = 0;
				TeamRed teamRed = match.getTeamRed();
				TeamBlue teamBlue = match.getTeamBlue();
				helpers.clear();
				Team lastOwner = this.getOwner();
				for(PVPPlayer player : players)
				{
					double dist = player.thePlayer.getLocation().distance(this.sign.getLocation());
					if(dist <= flagCapDist)
					{
						if(player.isSpawned())
						{
							helpers.add(player);
							if(player.getTeam().equals(teamRed))
							{
								playersRed++;
							}
							else if(player.getTeam().equals(teamBlue))
							{
								playersBlue++;
							}
							else
							{
								Error error = new Error("Player without clearly assigned team near flag found!","Happy plane! Going to kill your server!","No, seriously better take a nuke and blow this damn shit up!",this.getClass().getCanonicalName(),ErrorSeverity.DOUBLERAINBOOM);
								ErrorReporter.reportError(error);
							}
						}
					}
					if(playersRed != playersBlue)
					{
						double change = ((double)this.flagCapSpeed)/2d*Math.pow(flagCapAccelPerPerson,Math.abs(playersRed-playersBlue));
						if(playersRed > playersBlue)
						{
							if(this.percBlue == 0d && percRed < 100d)
							{
								this.percRed += change;
								if(percRed > 100d)
								{
									percRed = 100d;
								}
								if(percRed == 100d)
								{
									this.owner = teamRed;
									for(Block b : flagArea)
									{
										b.setType(Material.WOOL);
										BlockState bs = b.getState();
										bs.setData(new Wool(DyeColor.RED));
										bs.update();
									}
									this.match.sendTeamMessage(teamRed,ChatColor.GREEN+String.format(Main.gameEngine.dict.get("flagcap"),this.name));
								}
							}
							else if(percBlue > 0d)
							{
								this.percBlue -= change;
								if(this.percBlue < 0d)
								{
									this.percBlue = 0d;
									this.owner = null;
									this.match.sendTeamMessage(teamRed,ChatColor.GREEN+String.format(Main.gameEngine.dict.get("flagneut"),this.name));
									this.match.sendTeamMessage(teamBlue,ChatColor.RED+String.format(Main.gameEngine.dict.get("flaglost"),this.name));
									for(Block b : flagArea)
									{
										b.setType(Material.WOOL);
										BlockState bs = b.getState();
										bs.setData(new Wool(DyeColor.WHITE));
										bs.update();
									}
								}
							}
						}
						else
						{
							if(this.percRed == 0d && this.percBlue < 100d)
							{
								this.percBlue += change;
								if(percBlue > 100d)
								{
									percBlue = 100d;
								}
								if(percBlue == 100d)
								{
									this.owner = teamBlue;
									this.match.sendTeamMessage(teamBlue,ChatColor.GREEN+String.format(Main.gameEngine.dict.get("flagcap"),this.name));
									for(Block b : flagArea)
									{
										b.setType(Material.WOOL);
										BlockState bs = b.getState();
										bs.setData(new Wool(DyeColor.BLUE));
										bs.update();
									}
								}
							}
							else if(percRed > 0d)
							{
								this.percRed -= change;
								if(this.percRed < 0d)
								{
									this.percRed = 0d;
									this.owner = null;
									this.match.sendTeamMessage(teamRed,ChatColor.RED+String.format(Main.gameEngine.dict.get("flagneut"),this.name));
									this.match.sendTeamMessage(teamBlue,ChatColor.GREEN+String.format(Main.gameEngine.dict.get("flaglost"),this.name));
									for(Block b : flagArea)
									{
										b.setType(Material.WOOL);
										BlockState bs = b.getState();
										bs.setData(new Wool(DyeColor.WHITE));
										bs.update();
									}
								}
							}
						}
					}
					Team ownerNow = this.getOwner();
					if(ownerNow != lastOwner)
					{
						if(ownerNow != null)
						{
							for(PVPPlayer helper : helpers)
							{
								if(helper.getTeam() == ownerNow)
								{
									double points = Main.gameEngine.configuration.getScore(this.sign.getWorld(), Score.FLAGCAP);
									helper.thePlayer.sendMessage(ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("flagcappoints"),points));
									helper.points += points;
								}
							}
						}
					}
					//Flag info update
					if(owner != null)
					{
						this.sign.setLine(0,owner.color+"Flag");
					}
					else
					{
						this.sign.setLine(0,"Flag");
					}
					this.sign.setLine(1,"RED | BLUE");
					this.sign.setLine(2,Integer.toString((int)Math.floor(percRed))+" | "+Integer.toString((int)Math.floor(percBlue)));
					this.sign.setLine(3, Integer.toString(match.getFlagsRed())+" | "+Integer.toString(match.getFlagsBlue()));
					this.sign.update(true);
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			timer = 0;
		}
		timer++;
	}
	
	public static List<Block> getFlagBlocks(Sign sign,Location facing)
	{
		List<Block> blocks = new ArrayList<Block>();
		for(int i=1;i<=6;i++)
		{
			Location woolLoc = sign.getLocation().add(new Location(sign.getWorld(),-facing.getZ(), 0, facing.getX()).multiply(Math.ceil(((double)i)/2d)).add(0,3,0).add(facing));
			blocks.add(sign.getBlock().getWorld().getBlockAt(woolLoc.getBlockX(),woolLoc.getBlockY()+(i % 2 == 0 ? 0 : 1),woolLoc.getBlockZ()));
		}		
		return blocks;
	}
	
	public static void buildFlag(Sign sign)
	{
		Location facing = Flag.getFacing(sign);
		for(int i=-1;i<=4;i++)
		{
			Location fenceLoc = sign.getLocation().clone().add(0,i,0).add(facing);
			Block b = sign.getBlock().getWorld().getBlockAt(fenceLoc);
			b.setType(Material.FENCE);
		}
		List<Block> blocks = Flag.getFlagBlocks(sign, facing);
		for(Block b : blocks)
		{
			b.setType(Material.WOOL);
			b.getState().setData(new Wool(DyeColor.WHITE));
		}
	}
	
	public static Location getFacing(Sign sign)
	{
		int fenceOffsetX = 0;
		int fenceOffsetZ = 0;
		MaterialData data = sign.getData();
		int dataInt = (int)data.getData();
		if(dataInt == 5)
		{
			fenceOffsetX = -1;
		}
		else if(dataInt == 4)
		{
			fenceOffsetX = 1;
		}
		else if(dataInt == 2)
		{
			fenceOffsetZ = 1;
		}
		else if(dataInt == 3)
		{
			fenceOffsetZ = -1;
		}
		return new Location(sign.getWorld(), fenceOffsetX, 0, fenceOffsetZ);
	}
	
	public Team getOwner()
	{
		return this.owner;
	}
	
	public Location getLocation()
	{
		return this.sign.getLocation().clone();
	}
}
