/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Random;

public class TicTacToeClient extends JFrame implements ActionListener {

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


    private ServerListener serverListener;


    public TicTacToeClient() {
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

    private void sendPlayerInfo(String email) {
        out.println("PlayerInfo:" + email);
    }

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
                openGameWindow();
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

    private void displayWinningVideo() {
        try {
            // Get the URI of the video file in the resources folder
            URI videoURI = getClass().getResource("/resources/winning_video.mp4").toURI();

            // Open the video file with the default media player
            Desktop.getDesktop().browse(videoURI);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying winning video.");
        }
    }

    private void openGameWindow() {
        JFrame gameFrame = new JFrame("TicTacToe Game Client");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(400, 400); // Decreased size for smaller buttons and label
        gameFrame.setLocationRelativeTo(null);

        // Set background color of the gameFrame
        gameFrame.getContentPane().setBackground(Color.decode("#6D6D3D"));

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
                if (!gameInitialized) {
                    initializeGame();
                    gameInitialized = true;
                }
            }
        });
        singlePlayerButton.setBounds(125, 70, 150, 30); // Set bounds for the button

        JButton multiPlayerButton = new JButton("Multiplayer");
        multiPlayerButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); // Set font and size for button text
        multiPlayerButton.setFocusable(false); // Remove focus border
        multiPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer(); // Connect to the server
                out.println("GetOnlinePlayers"); // Request online players from server

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

    private class ServerListener implements Runnable {

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // Handle messages from the server
                    if (message.startsWith("Online Players: ")) {
                        String[] players = message.substring(16).split(", ");
                        // Show a dialog with the list of online players
                        showOnlinePlayersDialog(players);
                    } else {
                        // Handle other messages from the server
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showOnlinePlayersDialog(String[] players) {
        JFrame dialogFrame = new JFrame("Online Players");
        dialogFrame.setSize(300, 200);
        dialogFrame.setLocationRelativeTo(null);

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String player : players) {
            model.addElement(player);
        }

        JList<String> playerList = new JList<>(model);
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(playerList);
        dialogFrame.add(scrollPane);

        JButton selectButton = new JButton("Select Player");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPlayer = playerList.getSelectedValue();
                if (selectedPlayer != null) {
                    // You can do something with the selected player here
                    JOptionPane.showMessageDialog(dialogFrame, "You selected: " + selectedPlayer);
                    dialogFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogFrame, "Please select a player.");
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectButton);
        dialogFrame.add(buttonPanel, BorderLayout.SOUTH);

        dialogFrame.setVisible(true);
    }
    


    private void initializeGame() {
        setTitle("Tic Tac Toe (Client)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set background color
        getContentPane().setBackground(Color.decode("#6D6D3D"));

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
                buttons[i][j].addActionListener(this);
                buttons[i][j].setBackground(Color.decode("#BFBFB6")); // Set background color
                buttons[i][j].setForeground(Color.WHITE); // Set foreground color
                boardPanel.add(buttons[i][j]);
                buttons[i][j].setFocusable(false); // Remove focus border for buttons

            }
        }
        add(boardPanel, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel();
        messageLabel = new JLabel("Player X's Turn");
        messageLabel = new JLabel("Waiting for server...");

        messageLabel = new JLabel("The Game");
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24)); // Set font size
        messageLabel.setForeground(Color.WHITE); // Set foreground color
        messageLabel.setForeground(Color.WHITE); // Set foreground color
        messagePanel.setBackground(Color.decode("#9E9E96")); // Set background color

        messagePanel.add(messageLabel);
        add(messagePanel, BorderLayout.SOUTH);

        JButton restartButton = new JButton("Restart");
        restartButton.setFocusable(false); // Remove focus border
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        restartButton.setBackground(Color.decode("#9E9E96")); // Set background color
        restartButton.setForeground(Color.WHITE); // Set foreground color
        add(restartButton, BorderLayout.NORTH);

        // Set the preferred size for the button
        restartButton.setPreferredSize(new Dimension(120, 30)); // Adjust size as needed

        // Add the button to the top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center align the button
        topPanel.add(restartButton);

        // Add the top panel to the frame
        add(topPanel, BorderLayout.NORTH);

        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);

        isPlayerXTurn = true;
        isGameOver = false;
        isGameStarted = false;
        resetBoard();
        waitForServerStart();
        resetGame(); // Reset game state when initializing
    }

    private void resetBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE]; // Initialize the board array
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j].setText("");
                board[i][j] = 0;
            }
        }
    }

    private void waitForServerStart() {
        if (!isGameStarted) {
            // Display message only if the game has not started yet
            messageLabel.setText("Waiting for server to start the game...");
        }
    }

    private void resetGame() {
        resetBoard();
        isGameOver = false;
        isGameStarted = false;
        isPlayerXTurn = true;
        messageLabel.setText("Player X's Turn");
    }

    private void restartGame() {
        resetGame();
        out.println("Restart");
    }

    @Override
public void actionPerformed(ActionEvent e) {
    if (isGameOver) {
        return; // If game is over, do nothing
    }

    JButton clickedButton = (JButton) e.getSource();
    int row = -1, col = -1;

    // Find the row and column of the clicked button
    outerLoop:
    for (int i = 0; i < BOARD_SIZE; i++) {
        for (int j = 0; j < BOARD_SIZE; j++) {
            if (buttons[i][j] == clickedButton) {
                row = i;
                col = j;
                break outerLoop;
            }
        }
    }

    if (row == -1 || col == -1) {
        System.err.println("Error: Button not found.");
        return;
    }

    if (board[row][col] != 0) {
        // If the button is already clicked, do nothing
        return;
    }
    

    // Human player's turn
    buttons[row][col].setText(PLAYER_X);
    board[row][col] = 1; // Set board value to 1 for X

    // Check if there is a winner after the human player's move
    if (checkWinner()) {
        messageLabel.setText("Player X wins!");
        isGameOver = true;
        displayWinningVideo();
        return; // Exit actionPerformed() since the game is over
    } else if (checkDraw()) {
        messageLabel.setText("It's a draw!");
        isGameOver = true;
        return; // Exit actionPerformed() since the game is over
    }

    // Computer player's turn
    computerMove(); // Let the computer make a move

    // Check if there is a winner after the computer's move
    if (checkWinner()) {
        messageLabel.setText("Player O wins!");
        isGameOver = true;
        displayWinningVideo();
    } else if (checkDraw()) {
        messageLabel.setText("It's a draw!");
        isGameOver = true;
    }
}
    
    private void computerMove() {
    // Simulate computer's move (random move for demonstration)
    Random rand = new Random();
    int row, col;
    do {
        row = rand.nextInt(BOARD_SIZE);
        col = rand.nextInt(BOARD_SIZE);
    } while (board[row][col] != 0); // Keep generating random positions until an empty spot is found

    // Set O on the button and update the board
    buttons[row][col].setText(PLAYER_O);
    board[row][col] = -1; // Set board value to -1 for O
}


    private boolean checkWinner() {
        // Check rows, columns, and diagonals for a winner
        for (int i = 0; i < BOARD_SIZE; i++) {
            // Check rows
            if (checkRowCol(board[i][0], board[i][1], board[i][2])) {
                return true;
            }
            // Check columns
            if (checkRowCol(board[0][i], board[1][i], board[2][i])) {
                return true;
            }
        }
        // Check diagonals
        if (checkRowCol(board[0][0], board[1][1], board[2][2])) {
            return true;
        }
        if (checkRowCol(board[0][2], board[1][1], board[2][0])) {
            return true;
        }
        return false;
    }

    private boolean checkRowCol(int c1, int c2, int c3) {
        return (c1 != 0) && (c1 == c2) && (c2 == c3);
    }

    private boolean checkDraw() {
        // Check if all cells are filled
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    return false; // If any cell is empty, not a draw
                }
            }
        }
        return true; // All cells are filled, it's a draw
    }

    public static void main(String[] args) {
        new TicTacToeClient();
    }
}
