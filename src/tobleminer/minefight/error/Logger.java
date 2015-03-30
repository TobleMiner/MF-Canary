package tobleminer.minefight.error;

import org.apache.logging.log4j.Level;

import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;

public class Logger 
{
	private final String prefix;
	private final Logman logger;
	
	public Logger(Plugin p)
	{
		prefix = p.getName();
		this.logger = p.getLogman();
	}
	
	public void log(Level lev, String str)
	{
		this.logger.log(lev,"["+prefix+"] "+str);
	}
}
