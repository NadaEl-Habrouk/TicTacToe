/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package tactictoe;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
/**
 *
 * @author nodys
 */
public class TacTicToeClient {
    private static final int PORT = 12346;
    private static final String SERVER_IP = "localhost"; // Change to server IP if different
    private static final int BOARD_SIZE = 3;
    private static final String PLAYER_X = "X";
    private static final String PLAYER_O = "O";
    private JButton[][] buttons;
    private boolean isPlayerXTurn;
    private JLabel messageLabel;
    private boolean isLoggedIn = false;
    private boolean gameInitialized = false;
    private JTextField emailField; // Declare emailField as an instance variable
    private JPasswordField passwordField; //
    private PrintWriter out;
    private BufferedReader in;
    private JButton multiplayerButton;
    private DefaultListModel<String> onlinePlayersModel;
    private JList<String> onlinePlayersList;
    private boolean isGameOver;
    private boolean isGameStarted;
    private int[][] board;
    
     //private ServerListener serverListener;
    
     /*public TicTacToeClient() {
        connectToServer();
        showLoginWindow();
        serverListener = new ServerListener();
        new Thread(serverListener).start(); // Start the server listener thread
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_IP, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the server.");
            System.exit(1);
        }
    }
*/

     private void showLoginWindow() {
        JFrame loginFrame = new JFrame(" Client Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(600, 600);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.getContentPane().setBackground(Color.decode("#61599E")); // Set background color of the window content

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.decode("#BFBFB6")); // Set background color of the panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        Font fieldFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);

        JLabel loginLabel = new JLabel(" Client Login");
        loginLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
        loginLabel.setForeground(Color.WHITE); // Set foreground color
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginLabel, gbc);

        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(Color.WHITE); // Set foreground color
        loginPanel.add(emailLabel, gbc);

        gbc.gridy++;
        JTextField emailField = new JTextField(20);
        emailField.setFont(fieldFont);
        emailField.setBackground(Color.WHITE);
        gbc.gridy++;
        loginPanel.add(emailField, gbc);

        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.WHITE); // Set foreground color
        gbc.gridy++;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        passwordField.setBackground(Color.WHITE);
        gbc.gridy++;
        loginPanel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.decode("#9E9E96")); // Set the background color of the button
        loginButton.setForeground(Color.WHITE); // Set the text color of the button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                // Check if email syntax is valid
                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid email syntax. Please enter a valid email.");
                    return;
                }
                // Check if password length is at least 8 characters
                if (password.length() < 8) {
                    JOptionPane.showMessageDialog(loginFrame, "Password must be at least 8 characters long.");
                    return;
                }
                // Proceed with login (dummy authentication for demonstration)
                isLoggedIn = true;
                sendPlayerInfo(email); // Send player's information to the server
                loginFrame.dispose();
                //openGameWindow();
            }

            private boolean isValidEmail(String email) {
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                return email.matches(emailRegex);
            }
        });
        loginPanel.add(loginButton, gbc);

        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);
    }
      private void sendPlayerInfo(String email) {
        out.println("PlayerInfo:" + email);                  
    }
    
}

