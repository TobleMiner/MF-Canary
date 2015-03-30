package tobleminer.minefight;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.canarymod.Canary;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.plugin.Plugin;
import tobleminer.minefight.command.CommandHandler;
import tobleminer.minefight.engine.GameEngine;
import tobleminer.minefight.engine.match.statistics.beans.PlayerStatBean;
import tobleminer.minefight.error.Error;
import tobleminer.minefight.error.ErrorReporter;
import tobleminer.minefight.error.ErrorSeverity;
import tobleminer.minefight.error.Logger;
import tobleminer.minefight.legalfu.LicenseHandler;
import tobleminer.minefight.network.ProtocolLibSafeLoader;
import tobleminer.minefight.permission.PermissionManager;
import tobleminer.minefight.util.Util;

public class Main extends Plugin implements CommandListener
{
	private final EventListener eventListener = new EventListener(this);
	public static Main main;
	public static PermissionManager pm;
	public static ProtocolLibSafeLoader plsl;
	public static Logger logger;
	public static Util util;
	public static CommandHandler cmdhandler;
		
	public Main()
	{
		Main.main = this;
	}
	
	public static GameEngine gameEngine;
	
	@Override
	public boolean enable()
	{
		init();
		logger.log(Level.INFO, gameEngine.dict.get("onEnable"));
		return true;
	}
	
	public void init()
	{
		Main.logger = new Logger(this);
		Main.util = new Util();
		Main.gameEngine = new GameEngine(this);
		Main.gameEngine.init();
		logger.log(Level.INFO,gameEngine.dict.get("preEnable"));
		Canary.hooks().registerListener(eventListener, this);
		try 
		{
			Canary.commands().registerCommands(this, this, false);
		}
		catch (CommandDependencyException e)
		{
			e.printStackTrace();
		}
		if(!(new LicenseHandler().init(this)))
		{
			Error err = new Error("License check failed!", "The plugins license could not be copied into the plugin's folder!", "The plugin won't start until the license is copied.", this.getClass().getName(), ErrorSeverity.DOUBLERAINBOOM);
			ErrorReporter.reportError(err);
			return;
		}
		Main.pm = new PermissionManager();
		Main.plsl = new ProtocolLibSafeLoader(this);
		Main.cmdhandler = new CommandHandler(this);
	}
	
	@Override
	public void disable()
	{
		gameEngine.isExiting = true;
		logger.log(Level.INFO,gameEngine.dict.get("onDisable"));
		Main.gameEngine.endAllMatches();
	}
		
	@Command(	aliases = { "mpvp" },
				description = "Control command for MineFight. Try /mpvp help to learn more",
				permissions = { "minefight.user" },
				toolTip = "/mpvp [args]")
	public boolean onCommand(MessageReceiver sender, String args[])
	{
		return cmdhandler.handleCommand(args, sender);
	}
	
	@Override
	public List<Class<?>> getDatabaseClasses()
	{
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(PlayerStatBean.class);
		return classes;
	}
	
	@Override
	public void installDDL()
	{
		super.installDDL();
	}
	
	public File getPluginDir()
	{
		return this.getDataFolder();
	}
}
