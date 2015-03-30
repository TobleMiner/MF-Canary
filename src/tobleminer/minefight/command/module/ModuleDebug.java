package tobleminer.minefight.command.module;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.ChatFormat;
import net.canarymod.chat.MessageReceiver;
import tobleminer.minefight.Main;
import tobleminer.minefight.command.CommandHelp;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.match.team.Team;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.permission.Permission;

public class ModuleDebug extends CommandModule
{
	public boolean handleCommand(String[] args, MessageReceiver sender)
	{
		if(!Main.gameEngine.configuration.isDebuging())
		{
			return false;
		}
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("player"))
			{
				String pname = null;
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					pname = p.getName();
					if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_DEBUG))
					{
						p.message(this.noPermMsg);
						return true;
					}
				}
				if(args.length >= 2)
				{
					pname = args[1];
				}
				if(pname == null)
				{
					sender.sendMessage(ChatFormat.LIGHT_RED + Main.gameEngine.dict.get("statsNameConsole"));
				}
				Player p = Canary.getServer().getPlayer(pname);
				if(p != null)
				{
					Match m = Main.gameEngine.getMatch(p.getWorld());
					if(m != null)
					{
						PVPPlayer player = m.getPlayerExact(p);
						if(player != null)
						{
							sender.sendMessage("=====DEBUG=====");
							sender.sendMessage("Name: "+player.getName());
							Team t = player.getTeam();
							String teamname = "null";
							if(t != null) teamname = t.getName();
							String teamObjAddr = "null";
							if(t != null) teamObjAddr = t.toString();
							sender.sendMessage("Team: "+teamname);
							sender.sendMessage("Team (precise): "+teamObjAddr);
							sender.sendMessage("Spawned: "+player.isSpawned());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public String getName()
	{
		return "debug";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for(CommandHelp help : CommandDebug.values())
			if(help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}
	
	@Override
	public CommandHelp[] getHelp()
	{
		return CommandDebug.values();
	}
	
	enum CommandDebug implements CommandHelp
	{
		DEBUG("debug", "player", 0, 1, "cmdDescrDebugPlayer", "/mpvp debug player [name]", Permission.MPVP_DEBUG.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;
		
		CommandDebug(String module, String cmd, int argnumMin,int argnumMax, String descr, String syntax, String perm)
		{
			this.module = module;
			this.cmd = cmd;
			this.argnumMin = argnumMin;
			this.argnumMax = argnumMax;
			this.syntax = syntax;
			this.descr = descr;
			this.perm = perm;
		}

		@Override
		public String getCmd()
		{
			return cmd;
		}
		
		@Override
		public String getModule()
		{
			return module;
		}

		@Override
		public int argMin() 
		{
			return argnumMin;
		}

		@Override
		public int argMax()
		{
			return argnumMax;
		}

		@Override
		public String getDescr() 
		{
			return Main.gameEngine.dict.get(descr);
		}

		@Override
		public String getPermission()
		{
			return perm;
		}

		@Override
		public String getSyntax()
		{
			return syntax;
		}
	}
}
