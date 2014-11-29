import java.util.ArrayList;
import java.util.Random;


public class CSMA 
{
	private Vehicle v[];
	private RSUs rsu[];
	private int noV;
	private int noRsu;
	private int allocatedRsu = -1;
	public int noOfpackets = 0;
	public long averageDelay = 0;
	
	public CSMA(Vehicle[] v, int noV, RSUs[] rsu, int noRsu)
	{
		this.v = v;
		this.rsu = rsu;
		this.noV = noV;
		this.noRsu = noRsu;
	}
	public boolean isAlreadyIncluded(int vId, ArrayList<Integer> tVehicles)
	{
		for(int i = 0; i < tVehicles.size() ; i++)
			if(vId == tVehicles.get(i))
				return true;
		return false;
	}
	
	public boolean isInNoInteferenceRange(int vId, ArrayList<Integer> tVehicles, ArrayList<Point> vPos)
	{
		int index;
		double tX = vPos.get(vId).getXCordinate();
		double tY = vPos.get(vId).getYCordinate();
		
		for(int i = 0; i < tVehicles.size(); i++)
		{
			index = tVehicles.get(i);
			double x = vPos.get(index).getXCordinate();
			double y = vPos.get(index).getYCordinate();
			double distance = (x - tX)*(x - tX) + (y - tY)*(y - tY);
			distance = Math.sqrt(distance);
			
			if(distance < 1.5)
				return false;
		}
		return true;
	}
	
	public void selectVehiclesV2VComm(ArrayList<Integer> tVehicles, ArrayList<Point> vPos)
	{
		for(int i = 0; i < noV; i++)
		{
			if(!isAlreadyIncluded(i, tVehicles) && isInNoInteferenceRange(i, tVehicles, vPos) && v[i].getStatus() && !v[i].getPacketList().isEmpty())
				tVehicles.add(i);
		}	
	}
	
	public void findNearbyVehicle(ArrayList<Integer> tVehicles, ArrayList<Point> vPos)
	{
		for(int i = 0; i < tVehicles.size(); i++)
		{
			int index = tVehicles.get(i);
			double tX = vPos.get(index).getXCordinate();
			double tY = vPos.get(index).getYCordinate();
			for(int j = 0; j < noV; j++)
			{
				double x = vPos.get(j).getXCordinate();
				double y = vPos.get(j).getYCordinate();
				double distance = (x - tX)*(x - tX) + (y - tY)*(y - tY);
				distance = Math.sqrt(distance);
				
				if(distance <= 0.3)
					v[index].nearByVehicles.add(j);
			}
		}
	}
	
	public void locateNearByRSUToVehicles() throws InterruptedException
	{
		int n = noV;
		ArrayList<Point> vehiclePosition = new ArrayList<Point>();
		ArrayList<Integer> transmittingVehicles = new ArrayList<Integer>();
		for(int i = 0; i < n; i++)
		{
			Point p = v[i].getLocation();
			//p.display();
			//System.out.println();
			vehiclePosition.add(p);
		}
		
		for(int i = 0; i < n; i++)
		{
			if(v[i].getPacketList().isEmpty() || v[i].getStatus() == false)
				continue;
			
			double x = vehiclePosition.get(i).getXCordinate();
			double y = vehiclePosition.get(i).getYCordinate();
			
			for(int j = 0; j < noRsu; j++)
			{
				double rX = rsu[j].getRsuLocation().getXCordinate();
				double rY = rsu[j].getRsuLocation().getYCordinate();
				
				if( (x >= rX - 0.3 && x <= rX + 0.3) && (y >= rY - 0.3 && y <= rY + 0.3))
				{
					allocatedRsu = j;
					//System.out.println("Vehicle :"+ i + "  Rsu  " + j);
					break;
				}
			}
			
			if(allocatedRsu != -1)
				rsu[allocatedRsu].listOfVehicle.add(i);
			allocatedRsu = -1;
		}
		
		Random ran = new Random();
		for(int i = 0; i < noRsu; i++)
		{
			if(rsu[i].listOfVehicle.size() == 0)
				continue;
			int s = rsu[i].listOfVehicle.size();
			//System.out.println(s);
			int j = ran.nextInt(s);
			//System.out.println( i + " " + j);
			int vIndex = rsu[i].listOfVehicle.get(j);
			
			for(int k = 0 ; k < s; k++)
			{
				if(k!=j)
					v[rsu[i].listOfVehicle.get(k)].startBackoffTimer();
			}
		
			rsu[i].accessRSU(v[vIndex], vIndex);	
			transmittingVehicles.add(vIndex);
			rsu[i].listOfVehicle.clear();  
		}
		
		selectVehiclesV2VComm(transmittingVehicles, vehiclePosition);
		findNearbyVehicle(transmittingVehicles, vehiclePosition);
	}
	
	public void V2ICommunication() throws InterruptedException
	{
		//System.out.println(" I am here");
		locateNearByRSUToVehicles();
		//System.out.println(" I am here 1");
		for(int i = 0; i < noRsu; i++)
		{
			if(!rsu[i].isAccessedBy())
				continue;
			int ind = rsu[i].getCurrentVindex();
			Packet pc = v[ind].startTransmission();
			Thread.sleep(100);
			for(int j = 0 ; j < v[ind].nearByVehicles.size(); j++)
			{
				int k = v[ind].nearByVehicles.get(j);
				v[k].addIncomingPacketToList(pc);
			}
			rsu[i].addToList(pc);
			//System.out.println(pc.getPid() + " " + pc.generationTime + "  rsu: " + i);
			//pc.delivered();
			//System.out.println(" I am here");
			//System.out.println("Packet Id: " + pc.packetId + "  "+ pc.getDelay());
		}
		
		
		for(int i = 0; i < noRsu; i++)
			rsu[i].release();
		for(int i = 0; i < noV; i++)
			v[i].nearByVehicles.clear();		
	}
	
	public void locateNearByVehiclesToRSU()
	{
		int n = noV;
		ArrayList<Point> vehiclePosition = new ArrayList<Point>();
		for(int i = 0; i < n; i++)
		{
			Point p = v[i].getLocation();
			vehiclePosition.add(p);
		}
		
		for(int i = 0; i < n; i++)
		{
			double x = vehiclePosition.get(i).getXCordinate();
			double y = vehiclePosition.get(i).getYCordinate();
			
			for(int j = 0; j < noRsu; j++)
			{
				double rX = rsu[j].getRsuLocation().getXCordinate();
				double rY = rsu[j].getRsuLocation().getYCordinate();
				
				if( (x >= rX - 0.3 && x <= rX + 0.3) && (y >= rY - 0.3 && y <= rY + 0.3))
					allocatedRsu = j;		
			}
			
			if(allocatedRsu != -1)
				rsu[allocatedRsu].listOfVehicle.add(i);
			allocatedRsu = -1;
		}
	}
	
	public void I2VCommunication() throws InterruptedException
	{
		locateNearByVehiclesToRSU();
		Packet p;
		for(int i = 0; i < noRsu; i++)
		{
			//System.out.println(rsu[i].listOfVehicle.size());
			if(rsu[i].listOfVehicle.size() == 0)
				continue;
			if(rsu[i].listOfVehicle.size() == 1 && rsu[i].getPacket().size()!=0 )
				if(rsu[i].listOfVehicle.get(0) == rsu[i].getPacket().peek().vId)
					continue;
			
			p = rsu[i].vehiclePacketRemoval();
			
			if(p == null)
				continue;
			
			for(int j = 0; j < rsu[i].listOfVehicle.size(); j++)
			{
				int vIndex = rsu[i].listOfVehicle.get(j);
				//System.out.println("Vehicle Id : " + vIndex);
				//v[vIndex].addIncomingPacketToList(p);
			}
			Thread.sleep(100);
			p.delivered();
			noOfpackets++;
			averageDelay = averageDelay + p.getDelay();
			System.out.println("Packet Id: " + p.getPid() + "  Delay:"+ p.getDelay()); //+ "  rsu" + " " + i);
			rsu[i].listOfVehicle.clear();
		}
	}
	
	public void clear()
	{
		allocatedRsu = - 1;
		averageDelay = 0;
		noOfpackets = 0;
	}
}
