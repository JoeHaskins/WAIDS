import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class Frame extends JFrame{
	
	//Height and width of application
	static int jfwidth = 1200;
	static int jfheight = 600;
	static int y = 0;

	Logic logic = new Logic();
	
	//Create control panel
	JPanel cntrlpanel = new JPanel();
	//Create detail panel
	JPanel detailpanel = new JPanel();
	//Create list panel
	static JPanel listpanel = new JPanel();

	Frame(){
		//cntrlpanel.setBackground(Color.red);
		cntrlpanel.setPreferredSize(new Dimension((jfwidth/4)*1, jfheight));
		Border blackborder = BorderFactory.createLineBorder(Color.black);
		cntrlpanel.setBorder(blackborder);

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
		JButton resetbtn = new JButton("Rest");
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

		//detailpanel.setBackground(Color.green);
		detailpanel.setPreferredSize(new Dimension((jfwidth/4)*2, jfheight));
		detailpanel.setBorder(blackborder);

		//listpanel.setBackground(Color.blue);
		listpanel.setPreferredSize(new Dimension((jfwidth/4)*1, jfheight));
		listpanel.setBorder(blackborder);
		listpanel.setLayout(null);

		//Create Main Frame
		this.setTitle("WAIDS - Web App Scanner");
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

	public static void reset() {
		listpanel.removeAll();
		y=0;
		Main.refresh();
	}

	public static void addVuln(VulnData data) {
		JButton vuln = new JButton();
		vuln.setBounds(0,y,(jfwidth/4)*1,jfheight/15);
		vuln.setText(data.name);
		vuln.setBorder(BorderFactory.createLineBorder(Color.black));
		vuln.setBackground(Color.white);
		vuln.setFocusable(false);
		listpanel.add(vuln);
		y +=jfheight/15;
	}
}
