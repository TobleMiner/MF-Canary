package TobleMiner.MineFight.Permissions;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.Permissions.Interfaces.PermissionInterfaceFallbackMode;
import TobleMiner.MineFight.Permissions.Interfaces.PermissionInterfacePEx;
import TobleMiner.MineFight.Permissions.Interfaces.PermissionInterfaceVault;

public abstract class PermissionInterface 
{
	public abstract boolean hasPlayerPermissionTo(Player p, String perm);
	
	public static PermissionInterface getPermissionInterface()
	{
		PermissionPlugin pp = PermissionPlugin.NONE;
		PermissionInterface pi = null;
		PluginManager pm = Bukkit.getServer().getPluginManager();
		if(pm.getPlugin("Vault") != null)
		{
			pp = PermissionPlugin.VAULT;
			pi = new PermissionInterfaceVault();
			Main.logger.log(Level.INFO,Main.gameEngine.dict.get("vaultApproval"));
		}
		else if(pm.getPlugin("PermissionsEx") != null)
		{
			pp = PermissionPlugin.PEX;
			pi = new PermissionInterfacePEx((PermissionsEx)pm.getPlugin("PermissionsEx"));
		}
		else
		{
			pp = PermissionPlugin.OPONLYFALLBACK;
			pi = new PermissionInterfaceFallbackMode();
			Error err = new Error("No supported permission plugin found!","You have neither PermissionsEx nor Vault installed! Going into op-only permission fallback mode!","Please install Vault!",PermissionInterface.class.getName(),ErrorSeverity.WARNING);
			ErrorReporter.reportError(err);
		}
		Debugger.writeDebugOut("Permissioninterface: "+pp.toString()) ;
		return pi;
	}
}
