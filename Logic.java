import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class Logic {

	public static void Start(String host, String port) {
		if (host.length()!=0) {

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
						VulnData vuln = new VulnData("Server: "+header, "Detected from \"Server\" header!");
						Frame.addVuln(vuln);
						Main.refresh();
					}
				}

				//Get X-Powered-By Header
				List<String> powered = map.get("X-Powered-By");
				if (powered != null) {
					for (String header : powered) {
						VulnData vuln = new VulnData("Using: "+header, "Detected from \"X-Powered-By\" header!");
						Frame.addVuln(vuln);
						Main.refresh();
					}
				}

				//Get AspNet Version Header
				List<String> Aspnet = map.get("X-AspNet-Version");
				if (Aspnet != null) {
					for (String header : Aspnet) {
						VulnData vuln = new VulnData("AspNet Version: "+header, "Detected from \"X-AspNet-Version\" header!");
						Frame.addVuln(vuln);
						Main.refresh();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			//Provide Error message stating url/host field is empty
			JOptionPane.showMessageDialog(null, "Please Enter a URL or Host!", "Error - NO HOST",JOptionPane.WARNING_MESSAGE);
		}
		
	}

	public static void Stop() {
		System.out.println("Stop");
	}
}
