import javax.swing.SwingUtilities;
/**
 * WAIDS
 * Author: Joseph Haskins
 * Some URLs Project can be tested on:
 * www.danylkoweb.com
 * compudocshop.ie
 * ahreinc.com
 */

public class Main {

	static Frame ui;
	public static void main(String[] args) {
		//Create User Interface
		ui = new Frame();
	}

	//Refresh Jframe to load new components
	public static void refresh() {
		SwingUtilities.updateComponentTreeUI(ui);
	}

}