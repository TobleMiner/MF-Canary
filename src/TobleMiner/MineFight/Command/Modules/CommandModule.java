package TobleMiner.MineFight.Command.Modules;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Command.CommandHelp;

public abstract class CommandModule 
{
	protected final String noPermMsg;
	protected final String playerOnly;
	
	public CommandModule()
	{
		this.noPermMsg = ChatColor.DARK_RED + Main.gameEngine.dict.get("nopermmsg");
		this.playerOnly = ChatColor.DARK_RED + Main.gameEngine.dict.get("playeronlycmd");
	}
	
	public abstract boolean handleCommand(String[] args, CommandSender sender);
	
	public abstract String getName();
	public abstract CommandHelp getHelp(String cmd);
	public abstract CommandHelp[] getHelp();
}
