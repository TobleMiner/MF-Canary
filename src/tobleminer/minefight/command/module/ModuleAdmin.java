package tobleminer.minefight.command.module;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.ChatFormat;
import net.canarymod.chat.MessageReceiver;
import tobleminer.minefight.Main;
import tobleminer.minefight.command.CommandHelp;
import tobleminer.minefight.permission.Permission;

public class ModuleAdmin extends CommandModule
{
	private final Main mane;
	
	public ModuleAdmin(Main mane)
	{
		this.mane = mane;
	}
	
	public boolean handleCommand(String[] args, MessageReceiver sender)
	{
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_RELOAD))
					{
						p.message(this.noPermMsg);
						return true;
					}
				}
				Main.gameEngine.reload(this.mane);
				sender.message(ChatFormat.GREEN + Main.gameEngine.dict.get("configrl"));				
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName()
	{
		return "admin";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for(CommandHelp help : CommandAdmin.values())
			if(help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}
	
	@Override
	public CommandHelp[] getHelp()
	{
		return CommandAdmin.values();
	}
	
	private enum CommandAdmin implements CommandHelp
	{
		RELOAD("admin", "reload", 0, 0, "cmdDescrAdminReload", "/mpvp admin reload", Permission.MPVP_RELOAD.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;
		
		CommandAdmin(String module, String cmd, int argnumMin,int argnumMax, String descr, String syntax, String perm)
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