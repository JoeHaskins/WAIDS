import javax.swing.SwingUtilities;
/**
 * waids
 * Test URLs:
 * www.danylkoweb.com
 * compudocshop.ie
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