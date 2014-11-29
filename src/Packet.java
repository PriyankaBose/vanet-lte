
public class Packet 
{
	private static int next_id = 0;
	int packetId;
	int TTL;
	int generationTime;
	int dTime;
	int vId;
	
	public Packet()
	{
		packetId = ++Packet.next_id; //for auto generation of packet ids
		generationTime = (int) (System.currentTimeMillis()/1000);
		TTL = 6;
	}
	
	Packet(Packet pak)
	{
		this.packetId = pak.packetId;
		this.TTL = pak.TTL;
		this.generationTime = pak.generationTime;
		this.dTime = pak.dTime;
	}

	public static int getNext_id() 
	{
		return next_id;
	}

	public static void setNext_id(int next_id) 
	{
		Packet.next_id = next_id;
	}

	public int getPid() 
	{
		return packetId;
	}

	public void setPid(int pid) 
	{
		this.packetId = pid;
	}

	public int getTTL() 
	{
		return TTL;
	}

	public void setTTL(int tTL) 
	{
		TTL = tTL;
	}

	public int getgTime() 
	{
		return generationTime;
	}

	public void setgTime(int gTime) 
	{
		this.generationTime = gTime;
	}

	public int getdTime()
	{
		return dTime;
	}

	public void setdTime(int dTime)
	{
		this.dTime = dTime;
	}
	public void delivered()
	{
		dTime =  (int) (System.currentTimeMillis()/1000);
	}
	public int getDelay()
	{
		return dTime - generationTime;
	}
	
	public void print()
	{
		System.out.println(packetId+" "+TTL+" "+generationTime);
	}
	void decTTL()
	{
		TTL = TTL - 1;
	}
}
