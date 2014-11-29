import java.math.BigDecimal;
import java.math.RoundingMode;

public class Point 
{
	private BigDecimal X, Y;

	public Point() 
	{
		// TODO Auto-generated constructor stub
	}
	public void initialize(double x, double y)
	{
		X = new BigDecimal(x);
		Y = new BigDecimal(y);
		X = X.setScale(4, RoundingMode.HALF_UP);
		Y = Y.setScale(4, RoundingMode.HALF_UP);
	}
	public double getXCordinate()
	{
		return X.doubleValue();
	}
	public double getYCordinate()
	{
		return Y.doubleValue();
	}
	public void updateXCordinate(double x)
	{
		X = new BigDecimal(x);
		X = X.setScale(4, RoundingMode.HALF_UP);
	}
	public void updateYCordinate(double y)
	{
		this.Y = new BigDecimal(y);
		Y = Y.setScale(4, RoundingMode.HALF_UP);
	}
	public void display()
	{
		System.out.print("(" + getXCordinate() + "," + getYCordinate() +")\t");
	}
}
