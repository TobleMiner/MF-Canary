package tobleminer.minefight.util.geometry;

import tobleminer.minefight.util.location.Vector;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Vector3D;

public class Line3D 
{
	private final Location pos1; //Defined by two positions on the line
	private final Location pos2;
	
	public Line3D(Location loc, Vector3D vec)
	{
		this.pos1 = loc.copy();
		this.pos2 = (Location)loc.copy().add(vec);
	}
	
	public Line3D(Location pos1, Location pos2)
	{
		this.pos1 = pos1.copy();
		this.pos2 = pos2.copy();
	}
	
	public double getSmallestDist(Location loc)
	{
		return this.getSmallestDist(loc, false);
	}
	
	public double getSmallestDist(Location loc, boolean limit)
	{
		Vector v = new Vector(this.pos2.copy().subtract(this.pos1));
		Vector w = new Vector(loc.copy().subtract(this.pos1));
		double c1 = v.dot(w);
		double c2 = v.dot(v);
		if (c1 <= 0 && limit)
			return loc.getDistance(this.pos1);
        if (c2 <= c1 && limit)
        	return loc.getDistance(this.pos2);
        double b = c1 / c2;
        Location Pb = (Location)this.pos1.copy().add(v.copy().multiply(b));
        return loc.getDistance(Pb);	
	}
	
	public Vector3D getDirVec()
	{
		return this.pos1.copy().subtract(this.pos2);
	}
	
	public Location getPos1()
	{
		return this.pos1.copy();
	}

	public Location getPos2()
	{
		return this.pos2.copy();
	}
}
