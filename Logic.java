import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Logic {

	static ArrayList<VulnData> savelist = new ArrayList<VulnData>();
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
				Frame.updateTitle("Scanning: "+host);
				Main.refresh();

				//Check root for headers
				HttpURLConnection connection = requestHandler(u+"/");
				if (connection.getResponseCode()==200) {
					headercheck(connection, list,u+"/");
				}

				//Check malformed request for headers
				connection = requestHandler(u+"/ Santa /");
				if (connection.getResponseCode() >200) {
					headercheck(connection, list,u+"/%20Santa%20/");
				}

				//Check if sitemap.xml exists
				connection = requestHandler(u+"/sitemap.xml");
				if (connection.getResponseCode()==200) {
					headercheck(connection, list,u+"/sitemap.xml");
					VulnData vuln = new VulnData("Sitemap Detected", "The path /sitemap.xml exists possibly providing potential attackers with urls to hidden/sensitive directorys!",u+"/sitemap.xml");
					list.add(vuln);
				}

				//Check if robots.txt exists
				connection = requestHandler(u+"/robots.txt");
				if (connection.getResponseCode()==200) {
					headercheck(connection, list,u+"/robots.txt");
					VulnData vuln = new VulnData("Robots File Detected", "The path /robots.txt exists possibly providing potential attackers with urls to hidden/sensitive directorys!",u+"/robots.txt");
					list.add(vuln);
				}

				//Check if phpinfo.php exists
				connection = requestHandler(u+"/phpinfo.php");
				if (connection.getResponseCode()==200) {
					headercheck(connection, list,u+"/phpinfo.php");
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
					headercheck(connection, list,u+"/123456abcdef");
					String regex = "(?<=Version\\sInformation:</b>&nbsp;).*";
					Pattern r = Pattern.compile(regex);
					Matcher m = r.matcher(connectionToString(connection));
					if (m.find()) {
						VulnData vuln = new VulnData("Version Info in Response", "The following version information was found in response to a 404 Error: \n\n"+m.group(0),u+"/123456abcdef");
						list.add(vuln);
					}
				}

				//Check if wp-json exists
				connection = requestHandler(u+"/wp-json/");
				if (connection.getResponseCode()==200) {
					headercheck(connection, list,u+"/wp-json/");
					if (connection.getHeaderField("Content-Type").contains("application/json")) {	
						VulnData vuln = new VulnData("Wordpress Json", "The path /wp-json/ exists possibly providing potential attackers with detailed/sensitive information about the software and it's structure and users!",u+"/wp-json/");
						list.add(vuln);
					}
				}

				//Check if wp-json/users exists
				connection = requestHandler(u+"/wp-json/wp/v2/users");
				if (connection.getResponseCode()==200) {
					headercheck(connection, list,u+"/wp-json/wp/v2/users");
					if (connection.getHeaderField("Content-Type").contains("application/json")) {
						VulnData vuln = new VulnData("Wordpress User Json", "The path /wp-json/wp/v2/users exists possibly providing potential attackers with sensitive information about user accounts and help identify potential admin accounts!",u+"/wp-json/wp/v2/users/");
						list.add(vuln);
					}
				}

				for (VulnData vuln : list) {
					Frame.addVuln(vuln);
				}
				Main.refresh();

				//Update title to show completion
				Frame.updateTitle("Completed Scanning: "+host);
				savelist=list;
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
		//Check for server header
		if (connection.getHeaderField("Server") != null) {
			VulnData vuln = new VulnData("Server: "+connection.getHeaderField("Server"), "Detected from \"Server\" header in response!",u);
			if (checkList(list, vuln.name)) {
				list.add(vuln);
			}
		}
		//Check for X-Powered-by header
		if (connection.getHeaderField("X-Powered-By") != null) {
			VulnData vuln = new VulnData("X-Powered-By: "+connection.getHeaderField("X-Powered-By"), "Detected from \"X-Powered-By\" header in response!",u);
			if (checkList(list, vuln.name)) {
				list.add(vuln);
			}
		}
		//Check for X-AspNet-Version header
		if (connection.getHeaderField("X-AspNet-Version") != null) {
			VulnData vuln = new VulnData("X-AspNet-Version: "+connection.getHeaderField("X-AspNet-Version"), "Detected from \"X-AspNet-Version\" header in response!",u);
			if (checkList(list, vuln.name)) {
				list.add(vuln);
			}
		}
		//Check for X-AspNetMvc-Version header
		if (connection.getHeaderField("X-AspNetMvc-Version") != null) {
			VulnData vuln = new VulnData("X-AspNetMvc-Version: "+connection.getHeaderField("X-AspNetMvc-Version"), "Detected from \"X-AspNetMvc-Version\" header in response!",u);
			if (checkList(list, vuln.name)) {
				list.add(vuln);
			}
		}
		//Check for cors header
		if (connection.getHeaderField("Access-Control-Allow-Origin") != null) {
			if (connection.getHeaderField("Access-Control-Allow-Origin").contains("*")) {
				VulnData vuln = new VulnData("CORS Misconfiguration", "The server allows cross origin requests from arbitary domains, this could pose a security risk! Detected from the \"Access-Control-Allow-Origin\" header.",u+"/");
				if (checkList(list, vuln.name)) {
					list.add(vuln);
				}
			}
		}
	}

	//Check vuln hasn't already been detected
	public static boolean checkList(ArrayList<VulnData> list, String n) {
		for (VulnData vuln : list) {
			if (vuln.name.equals(n)) {
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

	//reset array
	public static void reset() {
		savelist.clear();
	}

	//Stop button
	public static void Save() {
		try {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setDialogTitle("Save your file");
			jfc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("txt",  "txt");
			jfc.addChoosableFileFilter(filter);

			int returnValue = jfc.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String path = jfc.getSelectedFile().getPath()+".txt";
				String text = "Detections\n\n";
				for (VulnData vuln : savelist) {
					text += "Name: "+vuln.name+"\nDetails: "+vuln.details+"\nURL: "+vuln.url+"\n\n";
				}
				Files.writeString(Paths.get(path), text);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Selecting Folder Please Try Again!", "Error Selecting File",JOptionPane.ERROR_MESSAGE);
		}
	}
}
