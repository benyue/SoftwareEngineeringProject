import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class UserInterface{
	
	private JFrame frame;
	private JTextField textField;

	/**
	 * Create the application.
	 */
	public UserInterface() {
		initialize();
		EventQueue.invokeLater(new Runnable() {
            public void run() {
            	
				try {
					UserInterface window = new UserInterface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
                
            }
        });
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setAlwaysOnTop(true);
		frame.setTitle("Text Game by Group 10");
		frame.setSize(467, 324);
		frame.setResizable(false);
		frame.setBounds(100, 100, 400, 281);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblLoadEnvironment = new JLabel("Load Environment:");
		lblLoadEnvironment.setBounds(10, 10, 102, 15);
		frame.getContentPane().add(lblLoadEnvironment);
		
		textField = new JTextField();
		textField.setBounds(115, 7, 178, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Load file...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		btnNewButton.setBounds(301, 6, 83, 19);
		frame.getContentPane().add(btnNewButton);
		
	}
	
	
	void printString(String string){
		System.out.printf(string);
	}
	
	void printStrings(String[] string){
		System.out.println(string);
	}
	
	String readUserInput(){

		return null;
	}
}
