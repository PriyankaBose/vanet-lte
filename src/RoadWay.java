
public class RoadWay 
{
	private static long length, width;
	private static Point P[];
	public RoadWay(long length, long width) 
	{
		this.length = length;
		this.width = width;
		this.P = new Point[4];
	}
	public static long getLength()
	{
		return length;
	}
	public static long getWidth()
	{
		return width;
	}
	public void setEndpoints(double startX, double startY)
	{
		P[0] = new Point();
		P[0].initialize(startX, startY);
		
		P[1] = new Point();
		P[1].initialize(startX + length, startY);
		
		P[2] = new Point();
		P[2].initialize(startX + length, startY + width);
		
		P[3] = new Point();
		P[3].initialize(startX , startY + width);
		
	}
	
	public static Point[] getEndPoints()
	{
		return P;
	}
	public void displayEndPoints()
	{
		System.out.println("The coordinates of roadway are: \n");
		System.out.print("Point 1:");
		P[0].display();
		System.out.println();
		System.out.print("Point 2:");
		P[1].display();
		System.out.println();
		System.out.print("Point 3:");
		P[2].display();
		System.out.println();
		System.out.print("Point 4:");
		P[3].display();
		System.out.println();
		System.out.println();
	}
}
