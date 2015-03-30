package tobleminer.minefight.util.material;

import net.canarymod.api.DyeColor;
import net.canarymod.chat.ChatFormat;

public class ColorUtil 
{
	public static DyeColor ChatColorToDyeColor(ChatFormat col)
	{
		if(col == ChatFormat.AQUA)
			return DyeColor.LIGHT_BLUE;
		if(col == ChatFormat.DARK_BLUE || col == ChatFormat.BLUE)
			return DyeColor.BLUE;
		if(col == ChatFormat.GREEN)
			return DyeColor.LIME;
		if(col == ChatFormat.DARK_GREEN)
			return DyeColor.GREEN;
		if(col == ChatFormat.RED || col == ChatFormat.DARK_RED)
			return DyeColor.RED;
		if(col == ChatFormat.BLACK)
			return DyeColor.BLACK;
		if(col == ChatFormat.GRAY || col == ChatFormat.DARK_GRAY)
			return DyeColor.GRAY;
		if(col == ChatFormat.GOLD || col == ChatFormat.YELLOW)
			return DyeColor.YELLOW;
		if(col == ChatFormat.LIGHT_PURPLE)
			return DyeColor.MAGENTA;
		if(col == ChatFormat.DARK_PURPLE)
			return DyeColor.PURPLE;
		return DyeColor.PINK;
	}
}
