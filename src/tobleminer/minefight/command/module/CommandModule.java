package tobleminer.minefight.command.module;

import net.canarymod.chat.ChatFormat;
import net.canarymod.chat.MessageReceiver;
import tobleminer.minefight.Main;
import tobleminer.minefight.command.CommandHelp;

public abstract class CommandModule 
{
	protected final String noPermMsg;
	protected final String playerOnly;
	
	public CommandModule()
	{
		this.noPermMsg = ChatFormat.RED + Main.gameEngine.dict.get("nopermmsg");
		this.playerOnly = ChatFormat.RED + Main.gameEngine.dict.get("playeronlycmd");
	}
	
	public abstract boolean handleCommand(String[] args, MessageReceiver sender);
	
	public abstract String getName();
	public abstract CommandHelp getHelp(String cmd);
	public abstract CommandHelp[] getHelp();
}
