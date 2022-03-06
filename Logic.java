import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class Logic {

	public static void Start(String host, String port) {
		if (host.length()!=0) {
			Frame.updateProg(0);
			try {
				String u;
				if (port.length()!=0 && Integer.parseInt(port) == 80) {
					u = "http://";
				} else {
					u = "https://";
				}
				u = u+host;
				URL url = new URL(u);
				URLConnection con = url.openConnection();
				Map<String, List<String>> map = con.getHeaderFields();
				Frame.updateTitle("Scanning: "+host);

				//Get Server Header
				List<String> server = map.get("Server");
				if (server != null) {
					for (String header : server) {
						VulnData vuln = new VulnData("Server: "+header, "Detected from \"Server\" header in response!",u);
						Frame.addVuln(vuln);
						Main.refresh();
					}
				}

				//Get X-Powered-By Header
				List<String> powered = map.get("X-Powered-By");
				if (powered != null) {
					for (String header : powered) {
						VulnData vuln = new VulnData("Using: "+header, "Detected from \"X-Powered-By\" header in response!",u);
						Frame.addVuln(vuln);
						Main.refresh();
					}
				}

				//Get AspNet Version Header
				List<String> Aspnet = map.get("X-AspNet-Version");
				if (Aspnet != null) {
					for (String header : Aspnet) {
						VulnData vuln = new VulnData("AspNet Version: "+header, "Detected from \"X-AspNet-Version\" header in response!",u);
						Frame.addVuln(vuln);
						Main.refresh();
					}
				}

				//Check if sitemap.xml exists
				HttpURLConnection connection = requestHandler(u+"/sitemap.xml");
				if (connection.getResponseCode()==200) {
					VulnData vuln = new VulnData("Sitemap Detected", "The path /sitemap.xml exists possibly providing potential attackers with urls to hidden/sensitive directorys!",u+"/sitemap.xml");
					Frame.addVuln(vuln);
					Main.refresh();
				}

				//Update Progress bar to show completion
				Frame.updateProg(100);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			//Provide Error message stating url/host field is empty
			JOptionPane.showMessageDialog(null, "Please Enter a URL or Host!", "Error - NO HOST",JOptionPane.WARNING_MESSAGE);
		}
		
	}

	public static HttpURLConnection requestHandler(String url) {
		try {
			URL Url = new URL(url);
			HttpURLConnection cons = (HttpURLConnection) Url.openConnection();
			return cons;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error Connecting to Provided Host!", "Error reaching Host",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}

	public static void Stop() {
		System.out.println("Stop");
	}
}
