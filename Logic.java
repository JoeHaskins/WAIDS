import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;


public class Logic {

	public static void Start(String host, String port) {
		if (host.length()!=0) {
			ArrayList<VulnData> list = new ArrayList<VulnData>();
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
				/*
				List<String> server = map.get("Server");
				if (server != null) {
					for (String header : server) {
						VulnData vuln = new VulnData("Server: "+header, "Detected from \"Server\" header in response!",u);
						list.add(vuln);
					}
				}
				*/
				//Get X-Powered-By Header
				List<String> powered = map.get("X-Powered-By");
				if (powered != null) {
					for (String header : powered) {
						VulnData vuln = new VulnData("Using: "+header, "Detected from \"X-Powered-By\" header in response!",u);
						list.add(vuln);
					}
				}

				//Get AspNet Version Header
				List<String> Aspnet = map.get("X-AspNet-Version");
				if (Aspnet != null) {
					for (String header : Aspnet) {
						VulnData vuln = new VulnData("AspNet Version: "+header, "Detected from \"X-AspNet-Version\" header in response!",u);
						list.add(vuln);
					}
				}

				//Check if sitemap.xml exists
				HttpURLConnection connection = requestHandler(u+"/sitemap.xml");
				if (connection.getResponseCode()==200) {
					VulnData vuln = new VulnData("Sitemap Detected", "The path /sitemap.xml exists possibly providing potential attackers with urls to hidden/sensitive directorys!",u+"/sitemap.xml");
					list.add(vuln);
					headercheck(connection, list,u+"/sitemap.xml");
				}

				//Check if robots.txt exists
				connection = requestHandler(u+"/robots.txt");
				if (connection.getResponseCode()==200) {
					VulnData vuln = new VulnData("Robots File Detected", "The path /robots.txt exists possibly providing potential attackers with urls to hidden/sensitive directorys!",u+"/robots.txt");
					list.add(vuln);
					headercheck(connection, list,u+"/robots.txt");
				}

				//Check if phpinfo.php exists
				connection = requestHandler(u+"/phpinfo.php");
				if (connection.getResponseCode()==200) {
					String regex = "PHP Version\\s\\d+.\\d+.\\d+";
					Pattern r = Pattern.compile(regex);
					Matcher m = r.matcher(connectionToString(connection));
					if (m.find()) {
						VulnData vuln = new VulnData(m.group(0), "The path /phpinfo.php exists possibly providing potential attackers with detailed/sensitive information about the machine and its software!",u+"/phpinfo.php");
						list.add(vuln);
					} else {
						VulnData vuln = new VulnData("PhpInfo File Detected", "The path /phpinfo.php exists possibly providing potential attackers with detailed/sensitive information about the machine and its software!",u+"/phpinfo.php");
						list.add(vuln);
					}
				}

				//Check if default 404 error page exists and if so extract version info
				connection = requestHandler(u+"/123456abcdef");
				if (connection.getResponseCode()==404) {
					String regex = "(?<=Version\\sInformation:</b>&nbsp;).*";
					Pattern r = Pattern.compile(regex);
					Matcher m = r.matcher(connectionToString(connection));
					System.out.println(connectionToString(connection));
					if (m.find()) {
						VulnData vuln = new VulnData("Version Info in Response", "The following version information was found in response to a 404 Error: \n\n"+m.group(0),u+"/123456abcdef");
						list.add(vuln);
					}
				}

				//Check if wp-json exists
				connection = requestHandler(u+"/wp-json/");
				if (connection.getResponseCode()==200) {
					if (connection.getHeaderField("Content-Type").contains("application/json")) {	
						VulnData vuln = new VulnData("Wordpress Json", "The path /wp-json/ exists possibly providing potential attackers with detailed/sensitive information about the software and it's structure and users!",u+"/wp-json/");
						list.add(vuln);
					}
				}

				//Check if wp-json/users exists
				connection = requestHandler(u+"/wp-json/wp/v2/users");
				if (connection.getResponseCode()==200) {
					if (connection.getHeaderField("Content-Type").contains("application/json")) {
						VulnData vuln = new VulnData("Wordpress User Json", "The path /wp-json/wp/v2/users exists possibly providing potential attackers with sensitive information about user accounts and help identify potential admin accounts!",u+"/wp-json/wp/v2/users/");
						list.add(vuln);
					}
				}

				//Check if cors allows any domain
				connection = requestHandler(u+"/");
				if (connection.getResponseCode()==200) {
					if (connection.getHeaderField("Access-Control-Allow-Origin") != null) {
						if (connection.getHeaderField("Access-Control-Allow-Origin").contains("*")) {
							VulnData vuln = new VulnData("CORS Misconfiguration", "The server allows cross origin requests from arbitary domains, this could pose a security risk!",u+"/");
							list.add(vuln);
						}
					}
				}

				for (VulnData vuln : list) {
					Frame.addVuln(vuln);
				}
				Main.refresh();

				//Update title to show completion
				Frame.updateTitle("Completed Scanning: "+url);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			//Provide Error message stating url/host field is empty
			JOptionPane.showMessageDialog(null, "Please Enter a URL or Host!", "Error - NO HOST",JOptionPane.WARNING_MESSAGE);
		}
		
	}

	//Request Handler
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

	//Method to check for various headers on each request
	public static void headercheck(HttpURLConnection connection, ArrayList<VulnData> list, String u) {
		if (connection.getHeaderField("Server") != null) {
			VulnData vuln = new VulnData("Server: "+connection.getHeaderField("Server"), "Detected from \"Server\" header in response!",u);
			if (checkList(list, vuln.name)) {
				list.add(vuln);
			}
		}
	}

	//Check vuln hasn't already been detected
	public static boolean checkList(ArrayList<VulnData> list, String n) {
		for (VulnData vuln : list) {
			if (vuln.name.equals(n)) {
				System.out.println("false");
				return false;
			} 
		}
		return true;
	}

	//Convert the input stream from a connection to a String
	public static String connectionToString(HttpURLConnection con) {
		try {
			InputStream body;
			if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
				body = con.getInputStream();
			} else {
				 /* error from server */
				body = con.getErrorStream();
			}
			String bodyString = new String(body.readAllBytes(), StandardCharsets.UTF_8);
			return bodyString;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error Sending Request to Provided Host!", "Error reaching Host",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;
		}
	}

	//Stop button
	public static void Stop() {
		System.out.println("Stop");
	}
}
