import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.math3.distribution.ExponentialDistribution;

class RandomSpeedGenerator 
{ 
	public double max; 
	public double min; 
	
	public RandomSpeedGenerator (double min, double max) 
	{ 
		this.min = min; 
		this.max = max; 
	} 
	public double nextValue() 
	{ 
		BigDecimal bd = new BigDecimal(min + Math.random() * (max - min));
		BigDecimal FOUR = new BigDecimal("4");
		bd = bd.divide(FOUR);
		bd = bd.setScale(4,RoundingMode.HALF_UP);
		return bd.doubleValue(); 
	} 
	
}

class InitializeComponents
{
	
	public RSUs[] initializeRSUs(int noRsu)
	{
		int i = 0;
		//noRsu = 2;
		double x = 0.0, y = 0.0;
		BigDecimal X, Y;
		X = new BigDecimal(x);
		Y = new BigDecimal(y);
		X = X.setScale(4, RoundingMode.HALF_UP);
		Y = Y.setScale(4, RoundingMode.HALF_UP);
		BigDecimal distance = new BigDecimal(54);
		BigDecimal r = new BigDecimal(noRsu);
		//System.out.println(r);
		BigDecimal l = new BigDecimal(22);
		BigDecimal w = new BigDecimal(5);
		
		distance = distance.divide(r);
		RSUs rsu[] = new RSUs[noRsu];
		i = -1;
		while(X.doubleValue() < 22)
		{
			rsu[++i] = new RSUs(X.doubleValue(), Y.doubleValue());
			X = X.add(distance);
		}
		
		Y = X.subtract(l);
		X = l;
		
		while( Y.doubleValue() < 5)
		{
			rsu[++i] = new RSUs(X.doubleValue(), Y.doubleValue());
			Y = Y.add(distance) ;
		}
		
		X = X.subtract(Y.subtract(w));
		Y = w;
		
		while( X.doubleValue() > 0)
		{
			rsu[++i] = new RSUs(X.doubleValue(), Y.doubleValue());
			X = X.subtract(distance);
		}
		
		Y = Y.subtract(X.abs());
		X = BigDecimal.ZERO;
		
		while( Y.doubleValue() > 0)
		{
			rsu[++i] = new RSUs(X.doubleValue(), Y.doubleValue());
			Y = Y.subtract(distance);
		}
		
		//for(i = 0; i < noRsu; i++)
			//rsu[i].getRsuLocation().display();
		return rsu;
	}
	
	public Vehicle[] initializeVehicles(int noOfvehicles)
	{
		int i = 0;
		double speed;
		Vehicle vehicle[] = new Vehicle[noOfvehicles];
		RandomSpeedGenerator ran = new RandomSpeedGenerator(200,400);
		ExponentialDistribution exp = new ExponentialDistribution(600);
		//int num = (int) exp.sample();
		
		while(i < noOfvehicles)
		{
			speed = exp.sample();
			speed = speed / 1000;
			vehicle[i] = new Vehicle(i);
			vehicle[i].setSpeed(speed);
			vehicle[i].setStartingLocation(0,0);
			i++;
		}
		return vehicle;
	}
	
	public void displayRSUs(RSUs rsu[], int no)
	{
		for(int i = 0 ; i < no; i++)
		{
			rsu[i].getRsuLocation().display();
			System.out.println();
		}
	}
}

public class VANETImplement 
{
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException 
	{
		long length = 22, width = 5;
		double startX = 0, startY = 0;
		int i, noOfVehicles = 300, noOfRsu;
		RSUs rsu[];
		Vehicle v[];
		//noOfRsu = (int) (54/1.8);
		noOfRsu = 30;
		//boolean ExperimentWithRSU = false;
		
		for(int k = 1; k <= 10; k++)
		{
			InitializeComponents iC = new InitializeComponents();
			//BufferedReader inp = new BufferedReader (new InputStreamReader(System.in));
			RoadWay r = new RoadWay(length, width);
			r.setEndpoints(startX, startY);
		
			rsu = iC.initializeRSUs(noOfRsu);
			v = iC.initializeVehicles(noOfVehicles);
			
			Vehicle.startV();
			
			i = 0;
			while(i < noOfVehicles)
			{
				v[i].packetGeneration();
				i++;
			}
			
			long timeStart = (System.currentTimeMillis()/1000);
			long timeEnd = timeStart;
			CSMA csma = new CSMA(v,noOfVehicles,rsu, noOfRsu);
			while((timeEnd - timeStart) <= 60)
			{
				csma.V2ICommunication();
				Thread.sleep(1000);
				csma.I2VCommunication();
				timeEnd = (System.currentTimeMillis()/1000);
			}
			//System.out.println(csma.noOfpackets);
			double average = (double)(csma.averageDelay / csma.noOfpackets);
			System.out.println(noOfRsu +" " + average);
		    csma.clear();
		    for(i = 0; i < noOfVehicles; i++)
		    {
		    	v[i].clear();
		    }
		    for(i = 0; i < noOfRsu; i++)
		    	rsu[i].clear();   
		}
	}
}


