package application;

public class Main {

	public static void main(String[] args) {
        if (args.length > 0) 
      	{
       		switch (args[0]) 
       		{
           		case "server":
       				application.server.Main.main(new String[]{});
       				break;
       			case "client":
       				try 
       				{
       					application.client.Main.main(new String[]{args[1]});
               			break;
       				} 
       				catch (IndexOutOfBoundsException e)
       				{
       					System.out.println("User did not specify server ip, using default ip of 3.21.213.171\n");
       				}
       				default:
       					application.client.Main.main(new String[]{"3.21.213.171"});
           	}
        }
        else 
        {
        	System.out.println("Using default configuration.\nConnecting to server 3.21.213.171.");
   			application.client.Main.main(new String[]{"3.21.213.171"});
        }
	}
}
