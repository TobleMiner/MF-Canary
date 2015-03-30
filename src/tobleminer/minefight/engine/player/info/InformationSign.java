package tobleminer.minefight.engine.player.info;

import net.canarymod.Canary;
import net.canarymod.api.world.blocks.Sign;
import tobleminer.minefight.engine.match.Match;

public class InformationSign
{
	private final Match match;
	private final Sign sign;
	private int timer = 0;
	
	public InformationSign(Match m,Sign s)
	{
		this.match = m;
		this.sign = s;
	}
	
	public void doUpdate()
	{
		if(timer >= 100)
		{
			timer = 0;
			String[] lines = match.getInformationSignText();
			int i=0;
			for(String line : lines)  //TODO: Multi sign support
			{
				sign.setComponentOnLine(Canary.factory().getChatComponentFactory().compileChatComponent(line), i);
				i++;
			}
			sign.getBlock().update();
		}
		timer++;
	}
}
