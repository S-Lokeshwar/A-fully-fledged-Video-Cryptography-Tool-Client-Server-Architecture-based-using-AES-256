public class MyFile 
{
	private int id;
	private String name;
	private byte[] data;
	private String fileextension;
	
	public MyFile(int id, String name, byte[] data, String fileextension)
	{
		this.id = id;
		this.name = name;
		this.data = data;
		this.fileextension = fileextension;
	}
	
	public void setid(int id)
	{
		this.id = id;
	}
	
	public void setname(String name)
	{
		this.name = name;
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
	}
	
	public void setfileextension(String fileextension)
	{
		this.fileextension = fileextension;
	}
	
	public int getid()
	{
		return this.id;
	}
	
	public String getname()
	{
		return this.name;
	}
	
	public byte[] getdata()
	{
		return this.data;
	}
	
	public String getfileextension()
	{
		return this.fileextension;
	}
	
	

}