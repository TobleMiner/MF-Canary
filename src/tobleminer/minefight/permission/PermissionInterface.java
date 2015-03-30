package tobleminer.minefight.permission;

import org.apache.logging.log4j.Level;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.plugin.PluginManager;
import tobleminer.minefight.Main;
import tobleminer.minefight.debug.Debugger;
import tobleminer.minefight.error.Error;
import tobleminer.minefight.error.ErrorReporter;
import tobleminer.minefight.error.ErrorSeverity;
import tobleminer.minefight.permission.interfaces.PermissionInterfaceFallbackMode;

public abstract class PermissionInterface 
{
	public abstract boolean hasPlayerPermissionTo(Player p, String perm);
	
	public static PermissionInterface getPermissionInterface()
	{
		PermissionPlugin pp = PermissionPlugin.NONE;
		PermissionInterface pi = null;
		PluginManager pm =  Canary.pluginManager();
		if(pm.getPlugin("Vault") != null)
		{
			pp = PermissionPlugin.VAULT;
			Main.logger.log(Level.INFO, Main.gameEngine.dict.get("vaultApproval"));
		}
		else if(pm.getPlugin("PermissionsEx") != null)
		{
			pp = PermissionPlugin.PEX;
		}
		else
		{
			pp = PermissionPlugin.OPONLYFALLBACK;
			pi = new PermissionInterfaceFallbackMode();
			Error err = new Error("No supported permission plugin found!" ,"You have neither PermissionsEx nor Vault installed! Going into op-only permission fallback mode!", "Please install Vault!", PermissionInterface.class.getName(),ErrorSeverity.WARNING);
			ErrorReporter.reportError(err);
		}
		Debugger.writeDebugOut("Permissioninterface: "+pp.toString()) ;
		return pi;
	}
}
