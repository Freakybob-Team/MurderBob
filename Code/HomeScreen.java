import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.*;
import java.io.IOException;

public class HomeScreen extends JFrame {
    private Clip audioClip;
    private Image backgroundImage;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public HomeScreen() {
        setTitle("MurderBob - Fire HomeScreen");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        backgroundImage = new ImageIcon(getClass().getResource("/Assets/Freakybobbg.jpeg")).getImage();
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createLeaderboardPanel(), "Leaderboard");
        
        add(mainPanel);
        
        playAudio("/Assets/Firemusic.wav");
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        ShadowLabel titleLabel = new ShadowLabel("MurderBob");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        JButton startButton = createButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (audioClip != null && audioClip.isRunning()) {
                    audioClip.stop();
                    audioClip.close();
                }
                Game game = new Game();
                game.setVisible(true);
                dispose(); 
            }
        });
        gbc.gridy = 1;
        panel.add(startButton, gbc);

        JButton leaderboardButton = createButton("Leaderboard");
        leaderboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Leaderboard");
            }
        });
        gbc.gridy = 2;
        panel.add(leaderboardButton, gbc);

        JButton quitButton = createButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        gbc.gridy = 3;
        panel.add(quitButton, gbc);

        JLabel footerLabel = new JLabel("This was completely made by the awesome mind of 5quirre1");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        footerLabel.setForeground(Color.WHITE);
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.SOUTH; 
        panel.add(footerLabel, gbc);

        return panel;
    }

    private JPanel createLeaderboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Leaderboard");
        titleLabel.setFont(new Font("COMIC SANS", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

  
        String[] dummyEntries = {
            "1. Squirrel - Wave 39",
            "2. Wish - Wave 20",
            "3. Greg - Yes",
            "4. greg - yes",
            "5. Greg - grr"
        };

        for (String entry : dummyEntries) {
            JLabel entryLabel = new JLabel(entry);
            entryLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            entryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(entryLabel);
        }

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Home");
            }
        });
        panel.add(backButton);

        return panel;
    }

    private void playAudio(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(filePath));
            audioClip = AudioSystem.getClip();
            audioClip.open(audioInputStream);
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 30));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(new Color(100, 100, 100, 200));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(150, 150, 150, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 100, 100, 200));
            }
        });

        return button;
    }

    private class ShadowLabel extends JLabel {
        public ShadowLabel(String text) {
            super(text);
            setFont(new Font("Serif", Font.BOLD, 64));
            setForeground(Color.RED);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.drawString(getText(), getX() + 2, getY() + 2);
            g.setColor(getForeground());
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomeScreen homeScreen = new HomeScreen();
            homeScreen.setVisible(true);
        });
    }
}

