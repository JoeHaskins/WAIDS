import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.BorderLayout;
import java.awt.Desktop;

public class Frame extends JFrame{
	
	//Height and width of application
	static int jfwidth = 1200;
	static int jfheight = 600;
	static int y = 0;

	Logic logic = new Logic();
	
	//Create control panel
	JPanel cntrlpanel = new JPanel();
	//Create detail panel and its title
	JPanel detailpanel = new JPanel();
	static JLabel title = new JLabel();
	static JButton link = new JButton();
	static JTextArea desc = new JTextArea();
	//Create list panel
	static JPanel listpanel = new JPanel();
	//Create Progress Bar
	static JProgressBar progress = new JProgressBar();

	Frame(){
		//cntrlpanel.setBackground(Color.red);
		cntrlpanel.setPreferredSize(new Dimension((jfwidth/4)*1, jfheight));
		Border blackborder = BorderFactory.createLineBorder(Color.black);
		cntrlpanel.setBorder(blackborder);

		//Setup Progress bar
		UIManager.put("ProgressBar.selectionBackground", Color.black);
		UIManager.put("ProgressBar.selectionForeground", Color.black);
		progress.setStringPainted(true);
		progress.setBorder(blackborder);
		progress.setBackground(Color.white);
		progress.setFont(new Font("Courier",Font.PLAIN,20));
		updateProg(100);

		//Setup IP/HOST textboxs
		JLabel hostlabel = new JLabel();
		hostlabel.setText("IP/HOST:          Port:");
		hostlabel.setFont(new Font("Helvetica Bold",Font.PLAIN,24));
		hostlabel.setVerticalAlignment(JLabel.TOP);
		JTextField host = new JTextField();
		host.setPreferredSize(new Dimension((jfwidth/8)*1, jfheight/15));
		host.setHorizontalAlignment(SwingConstants.LEFT);
		host.setCaretPosition(0);
		JTextField port = new JTextField();
		port.setPreferredSize(new Dimension((jfwidth/20)*1, jfheight/15));
		port.setHorizontalAlignment(SwingConstants.LEFT);
		port.setCaretPosition(0);
		JLabel hostspace = new JLabel();
		hostspace.setText("  :  ");
		hostspace.setFont(new Font("Helvetica Bold",Font.PLAIN,24));
		hostspace.setVerticalAlignment(JLabel.TOP);

		//Add Start Button
		JButton startbtn = new JButton("Start");
		startbtn.setFocusable(false);
		startbtn.setBackground(Color.green);
		startbtn.addActionListener(e -> Logic.Start(host.getText(),port.getText()));

		//Add Stop Button
		JButton stopbtn = new JButton("Stop");
		stopbtn.setFocusable(false);
		stopbtn.setBackground(Color.red);
		stopbtn.addActionListener(e -> Logic.Stop());

		//Add Reset Button
		JButton resetbtn = new JButton("Reset");
		resetbtn.setFocusable(false);
		resetbtn.setBackground(Color.orange);
		resetbtn.addActionListener(e -> reset());

		//Add Objects to control panel
		cntrlpanel.add(hostlabel);
		cntrlpanel.add(host);
		cntrlpanel.add(hostspace);
		cntrlpanel.add(port);
		cntrlpanel.add(startbtn);
		cntrlpanel.add(stopbtn);
		cntrlpanel.add(resetbtn);
		cntrlpanel.add(progress);

		//detailpanel.setBackground(Color.green);
		detailpanel.setPreferredSize(new Dimension((jfwidth/4)*2, jfheight));
		detailpanel.setBorder(blackborder);
		detailpanel.setLayout(new BorderLayout());
		title.setText("Enter a URL or Hostname to begin.");
		title.setFont(new Font("Courier",Font.BOLD,24));
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		desc.setMargin(new Insets(10,10,10,10));
		desc.setText("If the port field is left empty it will default to 443(Https).");
		desc.setFont(new Font("Courier",Font.PLAIN,20));
		desc.setBorder(blackborder);
		desc.setEditable(false);
		link.setText("Link:");
		link.setEnabled(false);
		link.setFocusable(false);
		//desc.setOpaque(true);
		detailpanel.add(title, BorderLayout.NORTH);
		detailpanel.add(desc,BorderLayout.CENTER);
		detailpanel.add(link,BorderLayout.SOUTH);

		//listpanel.setBackground(Color.blue);
		listpanel.setPreferredSize(new Dimension((jfwidth/4)*1, jfheight));
		listpanel.setBorder(blackborder);
		listpanel.setLayout(null);

		//Create Main Frame
		this.setTitle("WAIDS - Web App Information Disclosure Scanner");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		this.setSize(jfwidth, jfheight);
		this.add(detailpanel, BorderLayout.CENTER);
		this.add(listpanel, BorderLayout.WEST);
		this.add(cntrlpanel, BorderLayout.EAST);
		
		//Set at end to prevent objects not showing
		this.setVisible(true);
	}

	//Reset JFrame to default
	public static void reset() {
		listpanel.removeAll();
		updateTitle("Enter a URL or Hostname to begin.");
		updateDesc("If the port field is left empty it will default to 443.");
		link.setText("Path:");
		link.setEnabled(false);
		updateProg(0);
		y=0;
		Main.refresh();
	}

	//Add a detection to list and detail panel
	public static void addVuln(VulnData data) {
		//Add detection to list as button
		JButton vuln = new JButton();
		vuln.setBounds(0,y,(jfwidth/4)*1,jfheight/15);
		vuln.setText(data.name);
		vuln.setBorder(BorderFactory.createLineBorder(Color.black));
		vuln.setBackground(Color.white);
		vuln.setFocusable(false);
		vuln.addActionListener(e -> view(data));
		listpanel.add(vuln);
		y +=jfheight/15;
	
	}

	//Open link with default browser
	public static void openLink(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	//update detail panel on list button click
	public static void view(VulnData data) {
		updateTitle(data.name);
		updateDesc(data.details);
		updateLink(data.url);
		Main.refresh();
	}

	//Update Link button removing any existing action listeners
	public  static void updateLink(String url){
		ActionListener[] listeners = link.getActionListeners();
		for (ActionListener listener : listeners) {
			link.removeActionListener(listener);
		}
		link.setText("Path: "+url);
		link.addActionListener(e -> openLink(url));
		link.setEnabled(true);
	}

	//Update detail panel title
	public static void updateTitle(String newTitle) {
		title.setText(newTitle);
	}

	//Update desc of detail panel
	public static void updateDesc(String newDetail) {
		desc.setText(newDetail);
	}

	//Update Progress Bar
	public static void updateProg(int value) {
		if (value == 100) {
			progress.setString("Done!");
			progress.setBackground(Color.green);
		} else {
			progress.setValue(value);
		}
	}
}
