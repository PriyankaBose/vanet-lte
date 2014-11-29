import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.math3.distribution.ExponentialDistribution;

import sun.awt.windows.ThemeReader;

public class Vehicle
{
	private double speed;
	private int VehicleId;
	private Point curLocation;
	private static long startTime;
	private Point startingLoc;
	private Deque<Packet> packetList;
	public ArrayList<Integer> nearByVehicles;
	private boolean ready;
	private volatile boolean thReady = true;
	Lock packetLock = new ReentrantLock(true);
	
	public Vehicle(int Id) 
	{
		speed = 0;
		curLocation = null;
		startTime = 0;
		startingLoc = null;
		VehicleId = Id;
	    this.packetList = new LinkedList<Packet>();
	    this.nearByVehicles = new ArrayList<Integer>();
		packetList.add(new Packet());
		ready = true;
	}
	
	public void setSpeed(double speed)
	{
		this.speed = speed;
	}
	
	public double getSpeed()
	{
		return speed;
	}
	
	public void setStartingLocation(double X, double Y)
	{
		startingLoc = new Point();
		startingLoc.initialize(X,Y);
		curLocation = new Point();
		curLocation.initialize(startingLoc.getXCordinate(), startingLoc.getYCordinate());
	}
	
	public Point getLocation()
	{
		double distance;
		long curTime = System.currentTimeMillis();
		//System.out.print(startTime + "\t");
		//curLocation.display();
		//System.out.print(curTime + "\t  ");
		double diffTime = curTime - startTime;
		//System.out.print(diffTime + "\t  	");
		diffTime = diffTime / 1000;
		distance = speed * diffTime;
	    calculatePosition(distance, diffTime);
	    //updateTime(curTime);
		return curLocation;
	}
	
	public static void startV()
	{
		startTime = System.currentTimeMillis();	
	}
	
	void packetGeneration()
	{
		Runnable r = new Runnable() 
		{
			public void run() 
			{
				while(thReady)
				{
					try 
					{
						packetLock.lock();
						Packet p = new Packet();
						p.vId = VehicleId;
						packetList.add(p);
						packetLock.unlock();
						
						try 
						{
							ExponentialDistribution exp = new ExponentialDistribution(3);
							int num = (int) exp.sample();
							Thread.sleep(1000*num);
							//Thread.sleep(10000);
						} 
						finally 
						{
							if(!packetList.isEmpty())
							{
								//System.out.println();
								//System.out.print("Vehicle ID:"+ VehicleId + "   PacketID:"+ packetList.getLast().getPid() +"\tTimeGenerated:"+packetList.getLast().getgTime());
								//System.out.println(packetList.size());
							}
							
						}
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					//System.out.print("    Current Location: ");
					//getLocation().display();					
				}
			}
		};
		
		Thread thr = new Thread(r);
		thr.start();	
	}
	
	/*public void updateTime(long curTime)
	{
		prevTime = curTime;
	}*/
	
	public Deque<Packet> getPacketList()
	{
		return packetList;
	}
	
	public boolean getStatus()
	{
		return ready;
	}
	
	public void startBackoffTimer() throws InterruptedException
	{
		ready = false;
		Random ran = new Random();
		int bTime = ran.nextInt(15);
		Thread.sleep(bTime);
		ready = true;
	}
	public Packet startTransmission()
	{
		Packet p = null;
		
		if(!packetList.isEmpty())
		{
			packetLock.lock();
			p = packetList.peek();
			packetList.remove();
			packetLock.unlock();
		}
		return p;
	}
	
	public void addIncomingPacketToList(Packet p)
	{
		packetLock.lock();
		packetList.add(p);
		packetLock.unlock();
	}
	
	private void calculatePosition(double distance, double diffTime)
	{
		Point borderPoints[];
		BigDecimal bd1,bd2,bd3;
		double x,y;
		long l = RoadWay.getLength();
		long w = RoadWay.getWidth();
		borderPoints = RoadWay.getEndPoints();
		
		bd1 = new BigDecimal(distance);
		bd1 = bd1.setScale(4,RoundingMode.HALF_UP);
		//System.out.print(bd1.doubleValue()+ "\t	");
		
		bd2 = new BigDecimal(2*(l+w));
		bd2 = bd2.setScale(4,RoundingMode.HALF_UP);
		//System.out.println(bd2.doubleValue());
		
		bd3 = bd1.remainder(bd2);
		bd3.setScale(4,RoundingMode.HALF_UP);
		
		distance = bd3.doubleValue();
		//System.out.print(distance+ "\t	");
		
		while(distance > 0)
		{ 
			x = curLocation.getXCordinate();
			y = curLocation.getYCordinate();
			
			//curLocation.display();
			//System.out.println(distance);
			if(y == borderPoints[0].getYCordinate() && y == borderPoints[1].getYCordinate() && x < borderPoints[1].getXCordinate())
			{
				if( x + distance > borderPoints[1].getXCordinate())
				{
					distance = distance + x - borderPoints[1].getXCordinate();
					curLocation.updateXCordinate(borderPoints[1].getXCordinate());
				}
				else
				{
					curLocation.updateXCordinate(x + distance);
					distance = 0;
				}
			}
			else if(x == borderPoints[1].getXCordinate() && x == borderPoints[2].getXCordinate() && y < borderPoints[2].getYCordinate())
			{
				if( y + distance > borderPoints[2].getYCordinate())
				{
					distance = distance + y - borderPoints[2].getYCordinate();
					curLocation.updateYCordinate(borderPoints[2].getYCordinate());	
				}
				else
				{
					curLocation.updateYCordinate(y + distance);
					distance = 0;
				}
			}
			else if((y == borderPoints[2].getYCordinate() && y == borderPoints[3].getYCordinate()) && ( x <= borderPoints[2].getXCordinate() && x > borderPoints[3].getXCordinate()))
			{
				if(x == borderPoints[2].getXCordinate())
				{
					if(distance > l)
					{
						distance = distance - l;
						//System.out.println(distance);
						curLocation.updateXCordinate(borderPoints[3].getXCordinate());	
					}
					else
					{
						curLocation.updateXCordinate(x - distance);
						distance = 0;
					}
				}
				else
				{
					if( distance - (x - borderPoints[3].getXCordinate()) > 0)
					{
						distance = distance - (x - borderPoints[3].getXCordinate());
						//System.out.println(distance);
						curLocation.updateXCordinate(borderPoints[3].getXCordinate());	
					}
					else
					{
						curLocation.updateXCordinate(x - distance);
						distance = 0;
					}
				}
			}
			else if((x == borderPoints[3].getXCordinate() && x == borderPoints[0].getXCordinate()) && (y <= borderPoints[3].getYCordinate() && y > borderPoints[0].getYCordinate()))
			{
				if(y == borderPoints[3].getYCordinate())
				{
					if(distance > w)
					{
						distance = distance - w;
						curLocation.updateYCordinate(borderPoints[0].getYCordinate());	
					}
					else
					{
						curLocation.updateYCordinate(y - distance);
						distance = 0;
					}
				}
				else
				{
					if( distance - (y - borderPoints[0].getYCordinate())> 0)
					{
						distance = distance - (y - borderPoints[0].getYCordinate());
						curLocation.updateYCordinate(borderPoints[0].getYCordinate());	
					}
					else
					{
						curLocation.updateYCordinate(y - distance);
						distance = 0;
					}
				}
			}
		}
	}
	
	public void clear()
	{
		startTime = 0;
		startingLoc = null;
		packetList.clear();
		thReady = false;
		nearByVehicles.clear();
	}
}
