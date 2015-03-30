package tobleminer.minefight.util.location;

import net.canarymod.api.world.position.Position;
import net.canarymod.api.world.position.Vector3D;

public class Vector extends Vector3D
{
	public Vector()
	{
		super();
	}
	
	public Vector(double x, double y, double z)
	{
		super(x, y, z);
	}
	
	public Vector(float x, float y, float z)
	{
		super(x, y, z);
	}
	
	public Vector(Position pos)
	{
		super(pos);
	}
	
	public Vector(Vector3D vec)
	{
		super(vec);
	}
	
	public double dot(Vector3D v)
	{
		return this.x * v.getX() + this.y * v.getY() + this.z * v.getZ();
	}
}
