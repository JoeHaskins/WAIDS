import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class Logic {

	public static void Start(String host, String port) {
		if (host.length()!=0) {
			System.out.println(host);

			try {
				String u;
				if (Integer.parseInt(port) == 80) {
					u = "http://";
				} else {
					u = "https://";
				}
				u = u+host;
				URL url = new URL(u);
				URLConnection con = url.openConnection();
				Map<String, List<String>> map = con.getHeaderFields();

				System.out.println("\n----------------------------------");
				System.out.println("Scan for URL: " + url.toString());
				System.out.println("Info found:");
				System.out.println("----------------------------------");

				//Get Server Header
				List<String> server = map.get("Server");
				if (server == null) {
					System.out.println("'Server' isn't in response headers!");
				} else {
					for (String header : server) {
						System.out.println("\nServer: " + header);
					}
				}

				//Get X-Powered-By Header
				List<String> powered = map.get("X-Powered-By");
				if (powered == null) {
				} else {
					for (String header : powered) {
						System.out.println("Powered By: " + header);
					}
				}

				//Get AspNet Version Header
				List<String> Aspnet = map.get("X-AspNet-Version");
				if (Aspnet == null) {
				} else {
					for (String header : Aspnet) {
						System.out.println("AspNet Version: " + header);
					}
				}
	
				System.out.println("\n----------------------------------");

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Empty");
		}
		
	}

	public static void Stop() {
		System.out.println("Stop");
	}
}
