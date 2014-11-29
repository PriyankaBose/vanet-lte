import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class RSUs 
{
	Lock listLock = new ReentrantLock(true);
	Lock listLock1 = new ReentrantLock(true);
    private Point rsuLocation;
    private boolean currentAccess;
    private Deque<Packet> incoming;
    private Vehicle currentVehicle;
    private int currentVehicleIndex = -1;
    public ArrayList<Integer> listOfVehicle; 
    
    public RSUs(double x, double y)
    {
	    currentAccess = false;
	    incoming = null;
	    currentVehicle = null;
	    incoming = new LinkedList<Packet>();
	    listOfVehicle = new ArrayList<Integer>();
	    rsuLocation = new Point();
	    rsuLocation.initialize(x, y);
	    
    }
    
	public Point getRsuLocation()
	{
		return rsuLocation;
	}	
	
	public Deque<Packet> getPacket()
	{
		return incoming;
	}
	public void display()
	{
		for(int i=0;i<8;i++)
			System.out.println(rsuLocation.getXCordinate() +"\t"+rsuLocation.getYCordinate());
	}
	
	public void accessRSU(Vehicle v, int vIndex)
	{
	  listLock.lock();
      currentVehicle = v;
      currentAccess = true;
      currentVehicleIndex = vIndex;
      listLock.unlock();
	}
	
	public Vehicle getCurrentVehicle()
	{
		return currentVehicle;
	}
	public void release()
	{
		listLock.lock();
		currentAccess = false;
		currentVehicle = null;
		currentVehicleIndex = -1;
		listLock.unlock();
	}
	
	public int getCurrentVindex()
	{
		return currentVehicleIndex;
	}
	
	public boolean isAccessedBy()
	{
		return currentAccess;
	}
	
	public Packet vehiclePacketRemoval()
	{
		Packet p = null;
		listLock1.lock();
		  if(!incoming.isEmpty())
		  {
			p = incoming.peek();
			incoming.pop();
		  }
		listLock1.unlock();
		return p;
	} 
	
	public void addToList(Packet p)
	{
		incoming.add(p);
	}
	public void clear()
	{
	    incoming.clear();
        currentVehicleIndex = -1;
	    listOfVehicle.clear(); 
	}
}
