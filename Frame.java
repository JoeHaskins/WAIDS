import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;

public class Frame extends JFrame{
	
	//Height and width of application
	static int jfwidth = 1200;
	static int jfheight = 600;

	Frame(){
		//Create control panel
		JPanel cntrlpanel = new JPanel();
		cntrlpanel.setBackground(Color.red);
		cntrlpanel.setPreferredSize(new Dimension((jfwidth/4)*1, jfheight));

		JLabel hostlabel = new JLabel();
		hostlabel.setText("IP/HOST:          Port:");
		hostlabel.setFont(new Font("Helvetica Bold",Font.PLAIN,24));
		hostlabel.setVerticalAlignment(JLabel.TOP);
		hostlabel.setHorizontalAlignment(JLabel.RIGHT);

		JTextField host = new JTextField();
		host.setPreferredSize(new Dimension((jfwidth/8)*1, jfheight/10));
		cntrlpanel.add(hostlabel);
		cntrlpanel.add(host);

		//Create detail panel
		JPanel detailpanel = new JPanel();
		//detailpanel.setBackground(Color.green);
		detailpanel.setPreferredSize(new Dimension((jfwidth/4)*2, jfheight));

		//Create list panel
		JPanel listpanel = new JPanel();
		//listpanel.setBackground(Color.blue);
		listpanel.setPreferredSize(new Dimension((jfwidth/4)*1, jfheight));

		//Create Main Frame
		this.setTitle("WAIDS - Web App Scanner");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		this.setSize(jfwidth, jfheight);
		this.setVisible(true);
		this.add(detailpanel, BorderLayout.CENTER);
		this.add(listpanel, BorderLayout.WEST);
		this.add(cntrlpanel, BorderLayout.EAST);
	}
}
