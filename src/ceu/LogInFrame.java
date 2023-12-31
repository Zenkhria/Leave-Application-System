package ceu;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;

public class LogInFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// JAVA SWING-RELATED ATTRIBUTES
	private JPanel contentPane;
	private JTextField usernameTextField;
	private JPasswordField passwordField;
	private javax.swing.JLabel viewPassword;
	
	// DATABASE-RELATED ATTRITBUTES
    private Connection connection; 
    private QueryCommands qc;
	public static String usernameDB;
	private String passwordDB = "";
    private String userCategoryDB;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					LogInFrame frame = new LogInFrame();
					frame.setVisible(true);		
					frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public LogInFrame() {

		connection = DatabaseConnection.getConnection();
		qc = new QueryCommands();
		
		// CONTENT PANE
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 593, 468);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//  BACKGROUND PANEL
		JPanel backgroundPanel = new JPanel();
		backgroundPanel.setBounds(0, 0, 577, 431);
		contentPane.add(backgroundPanel);
		backgroundPanel.setLayout(null);
		
		// LOGIN PANEL
		JPanel logInPanel = new JPanel();
		logInPanel.setBackground(new Color(0, 0, 0, 1));
		logInPanel.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		logInPanel.setBounds(42, 53, 495, 326);	
		backgroundPanel.add(logInPanel);
		logInPanel.setLayout(null);
		
		// WELCOME LABEL
		JLabel welcomeLabel = new JLabel("LEAV.IO");
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 25));
		welcomeLabel.setBounds(0, 10, 495, 41);
		logInPanel.add(welcomeLabel);
		
		// USERNAME LABEL
		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setIcon(new ImageIcon("src\\images\\icons8-username-24.png"));
		usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		usernameLabel.setBounds(64, 112, 150, 25);
		logInPanel.add(usernameLabel);
		
		// USERNAME TEXT FIELD
		usernameTextField = new JTextField();
		usernameTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		usernameTextField.setBounds(189, 114, 208, 24);
		logInPanel.add(usernameTextField);
		usernameTextField.setColumns(10);
		
		// PASSWORD LABEL
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setIcon(new ImageIcon("src\\images\\icons8-lock-24.png"));
		passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		passwordLabel.setBounds(64, 159, 113, 25);
		logInPanel.add(passwordLabel);

		// PASSWORD FIELD
		passwordField = new JPasswordField();
		passwordField.setBounds(169, 157, 209, 25);
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		passwordField.setBounds(189, 161, 208, 23);
		logInPanel.add(passwordField);
		
		// VIEW PASSWORD ICON
		viewPassword = new JLabel("");
		viewPassword.addMouseListener(new MouseAdapter() {
		      @Override
		      public void mouseClicked(MouseEvent e) {
		          // Toggle password visibility
		          passwordField.setEchoChar((passwordField.getEchoChar() == 0) ? '\u2022' : (char) 0);
		          viewPassword.setVisible(true);
		          
		      }
		  });
		  viewPassword.setIcon(new ImageIcon(LogInFrame.class.getResource("/images/icons8-eye-24.png")));
		  viewPassword.setBounds(407, 163, 24, 21);
		  logInPanel.add(viewPassword);
		
		// RESET PASSWORD BUTTON
		JButton resetPasswordButton = new JButton("Forgot Password?");
		resetPasswordButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ResetPasswordFrame resetPasswordFrame = new ResetPasswordFrame();
				resetPasswordFrame.setVisible(true);
			}
		});
		resetPasswordButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		resetPasswordButton.setForeground(new Color(64, 64, 64));
		resetPasswordButton.setBackground(new Color(240, 240, 240));
		resetPasswordButton.setBounds(231, 198, 166, 18);
		logInPanel.add(resetPasswordButton);
		
		// LOGIN BUTTON
		JButton logInButton = new JButton("LOGIN");
		logInButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {

		        // GET ENTERED CREDENTIALS
		        String enteredUsername = usernameTextField.getText();
		        String enteredPassword = String.valueOf(passwordField.getPassword());

		        // SEARCH FOR COMPATIBLE USERNAME ENTRY IN DATABASE
	            try (ResultSet resultSet = qc.prepareSelectUsernameStatement(connection, enteredUsername).executeQuery()) {
	                if (resultSet.next()) {
	                    usernameDB = resultSet.getString("username");
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	            
		        // SEARCH FOR COMPATIBLE PASSWORD ENTRY IN DATABASE
	            try (ResultSet resultSet = qc.prepareSelectPasswordStatement(connection, enteredUsername, enteredPassword).executeQuery()) {
	                if (resultSet.next()) {
	                    passwordDB = resultSet.getString("pass");
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }

		        // VALIDATE ENTERED CREDENTIALS
		        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
		            JOptionPane.showMessageDialog(null, "Fill all required fields.");
		        } else if (!usernameDB.equals(enteredUsername)) {
		            JOptionPane.showMessageDialog(null, "Account does not exist.");
		        } else if (!passwordDB.equals(enteredPassword)) {
		            JOptionPane.showMessageDialog(null, "Incorrect Password");
		        } else {
		        	
		            // CLOSE LOG IN FRAME
		            LogInFrame.this.dispose();
		            
			        // SEARCH FOR USER'S CATEGORY IN DATABASE
		            try (ResultSet resultSet = qc.prepareSelectUserCategoryStatement(connection, enteredUsername).executeQuery()) {
		                if (resultSet.next()) {
		                    userCategoryDB = resultSet.getString("category");
		                }
		            } catch (SQLException ex) {
		                ex.printStackTrace();
		            }

		            if (userCategoryDB.equals("Client")) {
		                // OPEN USER DASHBOARD
		            	
		            	//EARLIER ITERATION
//		                UserDashboardFrame userDashboardFrame = new UserDashboardFrame();
//		                userDashboardFrame.setVisible(true);
//		                userDashboardFrame.setLocationRelativeTo(null);
		            	
		            	UserDashboardFrame userDashboardFrame = null;
						try {
							userDashboardFrame = new UserDashboardFrame();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		                userDashboardFrame.setVisible(true);
		                userDashboardFrame.setLocationRelativeTo(null);
		            } else if (userCategoryDB.equals("Admin")) {
		                // OPEN ADMIN DASHBOARD
		                AdminDashboardFrame adminDashboardFrame = null;
						try {
							adminDashboardFrame = new AdminDashboardFrame();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		                adminDashboardFrame.setVisible(true);
		                adminDashboardFrame.setLocationRelativeTo(null);
		            }
		        }
		    }
		});
		logInButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		logInButton.setBackground(new Color(255, 128, 192));
		logInButton.setBounds(170, 244, 139, 41);
		logInPanel.add(logInButton);
		
		// WELCOME PANEL
		JPanel welcomePanel = new JPanel();
		welcomePanel.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		welcomePanel.setBackground(new Color(255, 128, 192));
		welcomePanel.setBounds(0, 0, 495, 62);
		logInPanel.add(welcomePanel);
		welcomePanel.setLayout(null);
		
		// BACKGROUND
		JLabel backgroundLabel = new JLabel("");
        backgroundLabel.setIcon(new ImageIcon(LogInFrame.class.getResource("/images/bbg.png")));
        backgroundLabel.setBounds(0, 0, 577, 430);
        backgroundPanel.add(backgroundLabel);
 }
}

