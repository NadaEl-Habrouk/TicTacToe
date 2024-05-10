/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tactictoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nodys
 */
public class TacTicToeServer {
    private static final int PORT = 12346;
     private static List<ClientHandler> clients = new ArrayList<>();
    private static List<String> onlinePlayers = new ArrayList<>();
    private static final int BOARD_SIZE = 3;
    private static final String PLAYER_X = "X";
    private static final String PLAYER_O = "O";
    private static boolean playerXTurn;
    private static JFrame loginFrame;
    private static JFrame gameFrame;
    private static JPanel gamePanel;
    private static JButton[][] buttons;
    private static Socket multiplayerSocket;
    private static PrintWriter multiplayerOut;
    private static BufferedReader multiplayerIn;
    
    public static void main(String[] args) {
        // Call showLoginWindow() directly when the server starts
        showLoginWindow();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     static class ClientHandler extends Thread {

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Prompt the client for their name and add to online players list
                out.println("Enter your name:");
                playerName = in.readLine();
                if (playerName == null) {
                    System.out.println("Client disconnected before providing a name.");
                    return; // Client disconnected, return from the method
                }
                onlinePlayers.add(playerName);
                updateOnlinePlayers();

                // Send welcome message to client
                out.println("Welcome to Tic Tac Toe! Please login to play.");

                // Listen for client messages
                String message;
                while ((message = in.readLine()) != null) {
                    // Handle client messages here
                    System.out.println("Received from client " + playerName + ": " + message);
                    if (message.startsWith("START_GAME_WITH:")) {
                        String opponent = message.substring(16);
                       // startMultiplayerGame(opponent);
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clients.remove(this);
                    onlinePlayers.remove(playerName);
                    updateOnlinePlayers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void updateOnlinePlayers() {
            for (ClientHandler client : clients) {
                client.sendOnlinePlayers();
            }
        }

        private void sendOnlinePlayers() {
            StringBuilder playerList = new StringBuilder();
            playerList.append("Online Players: ");
            for (String player : onlinePlayers) {
                playerList.append(player).append(", ");
            }
            // Remove the last comma and space
            playerList.setLength(playerList.length() - 2);
            out.println(playerList.toString());
        }
    }
    
 private static void showLoginWindow() {
        loginFrame = new JFrame("Server Login"); // Assign to the static variable
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

        JLabel loginLabel = new JLabel("Server Login");
        loginLabel.setForeground(Color.WHITE); // Set foreground color
        loginLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
        loginPanel.add(loginLabel, gbc);

        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE); // Set foreground color
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20)); // Set font size
        loginPanel.add(emailLabel, gbc);

        gbc.gridy++;
        JTextField emailField = new JTextField(20);
        emailField.setBackground(Color.WHITE);
        emailField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20)); // Set font size
        loginPanel.add(emailField, gbc);

        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); // Set foreground color
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20)); // Set font size
        loginPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBackground(Color.WHITE);
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20)); // Set font size
        loginPanel.add(passwordField, gbc);

        gbc.gridy++;
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.decode("#9E9E96")); // Set the background color of the button
        loginButton.setForeground(Color.WHITE); // Set the text color of the button

        loginButton.addActionListener(new ActionListener() {
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
                boolean isLoggedIn = true;
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
 private static class loginFrame {
        // Implement the dispose method to properly dispose of resources

    } 
        private static void openGameWindow() {
        JFrame gameFrame = new JFrame("TicTacToe Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(400, 400);
        gameFrame.setLocationRelativeTo(null);

        JPanel gamePanel = new JPanel(null); // Use null layout to set button bounds manually
        gamePanel.setBackground(Color.decode("#BFBFB6")); // Add vertical gap of 10 between components

        // Add label with text "Hello To TicTacToe Game" and set its foreground color
        JLabel titleLabel = new JLabel("TicTacToe Game");
        titleLabel.setForeground(Color.WHITE); // Set foreground color to white
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align the text
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20)); // Set font and size
        titleLabel.setBounds(50, 20, 300, 30); // Set bounds for the label
        gamePanel.add(titleLabel);

        JButton singlePlayerButton = new JButton("Single Player");
        singlePlayerButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); // Set font and size for button text
        singlePlayerButton.setFocusable(false); // Remove focus border
        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSinglePlayerGameWindow(); // Open single-player game window
            }
        });
        singlePlayerButton.setBounds(125, 70, 150, 30); // Set bounds for the button

        JButton multiPlayerButton = new JButton("Multiplayer");
        multiPlayerButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); // Set font and size for button text
        multiPlayerButton.setFocusable(false); // Remove focus border
        multiPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String opponent = selectOpponent();
                if (opponent != null) {
                    // Establish connection with opponent
                    connectToOpponent(opponent);
                }
            }
        });
        multiPlayerButton.setBounds(125, 120, 150, 30); // Set bounds for the button

        gamePanel.add(singlePlayerButton);
        gamePanel.add(multiPlayerButton);

        gameFrame.add(gamePanel);

        // Set background color of buttons
        singlePlayerButton.setBackground(Color.decode("#9E9E96"));
        multiPlayerButton.setBackground(Color.decode("#9E9E96"));

        // Set foreground color of the buttons to match the label color in the login
        Color labelColor = UIManager.getColor("Label.foreground");
        singlePlayerButton.setForeground(Color.WHITE);
        multiPlayerButton.setForeground(Color.WHITE);

        gameFrame.setVisible(true);
    }
    private static boolean checkForDraw(JButton[][] buttons) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void resetGame(JButton[][] buttons) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true); // Enable the button

            }
        }
        playerXTurn = true;
    }

    private static void computerMove(JButton[][] buttons) {
        // Add logic for computer's move
        Random rand = new Random();
        int row, col;
        do {
            row = rand.nextInt(BOARD_SIZE);
            col = rand.nextInt(BOARD_SIZE);
        } while (!buttons[row][col].isEnabled());
        buttons[row][col].setText(PLAYER_O);
        buttons[row][col].setEnabled(false);
    }
    private static String selectOpponent() {
        if (onlinePlayers.isEmpty()) {
            JOptionPane.showMessageDialog(gameFrame, "No players found online.");
            return null; // Return null if no players are found
        }
        Object[] options = onlinePlayers.toArray();
        String opponent = (String) JOptionPane.showInputDialog(
                gameFrame,
                "Select an opponent to play with:",
                "Select Opponent",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                null);

        return opponent;
    }

}

