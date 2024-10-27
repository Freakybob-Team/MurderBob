import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.util.ArrayList;
import java.util.Random;

public class Game extends JFrame implements ActionListener {
    private Timer timer;
    private ArrayList<Bullet> bullets;
    private ArrayList<Enemy> enemies;
    private ArrayList<Perk> perks;
    private int playerX, playerY;
    private BufferedImage freakyBobSprite;
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private final int MOVE_SPEED = 5;
    private int playerHealth = 100;
    private int currentWave = 0;
    private boolean preparing = true;
    private int prepareTime = 10;
    private Clip clip;
    private Random random;
    private Clip gunshotClip;

    public Game() {
        setTitle("MurderBob");
        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        perks = new ArrayList<>();
        playerX = 400;
        playerY = 300;
        random = new Random();
        setFocusable(true);
        
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    shoot();
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    upPressed = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    downPressed = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    leftPressed = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    rightPressed = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_R && playerHealth <= 0) {
                    restartGame();
                }
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    upPressed = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    downPressed = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    leftPressed = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    rightPressed = false;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                shoot(e.getX(), e.getY());
            }
        });

        loadSprite();
        loadGunshotSound();
        playMusic(); 
        timer = new Timer(20, this);
        timer.start();
        setContentPane(new GamePanel());
        startPreparation();
    }


    private void loadSprite() {
        try {
            freakyBobSprite = ImageIO.read(new File("Code\\Assets\\Freakybob.png"));
        } catch (Exception e) {
            System.err.println("Error loading sprite: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadGunshotSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Code\\Assets\\gunshot.wav"));
            gunshotClip = AudioSystem.getClip();
            gunshotClip.open(audioInputStream);
        } catch (Exception e) {
            System.err.println("Error loading gunshot sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playGunshotSound() {
        if (gunshotClip != null) {
            gunshotClip.setFramePosition(0);
            gunshotClip.start(); 
        }
    }
    private void playMusic() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Code\\Assets\\awesome_ass_music_David_Fesliyan.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void shoot(int targetX, int targetY) {
        int bulletX = playerX + 26;
        int bulletY = playerY + 26;
        bullets.add(new Bullet(bulletX, bulletY, targetX, targetY));
        playGunshotSound(); 
    }

    private void shoot() {
    }

    private void startPreparation() {
        new Timer(1000, new ActionListener() {
            int secondsRemaining = prepareTime;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (secondsRemaining > 0) {
                    secondsRemaining--;
                    prepareTime = secondsRemaining;
                } else {
                    preparing = false;
                    ((Timer) e.getSource()).stop();
                    spawnEnemies();
                    spawnPerk();
                }
                repaint();
            }
        }).start();
    }

    private void spawnEnemies() {
        int enemyCount = Math.min(3 + (currentWave / 2), 20); 
        enemies.clear();
        for (int i = 0; i < enemyCount; i++) {
            int enemyX = (int) (Math.random() * getWidth());
            int enemyY = (int) (Math.random() * getHeight());
            enemies.add(new Enemy(enemyX, enemyY));
        }
        currentWave++;
    }

    private void spawnPerk() {
        int perkX = random.nextInt(getWidth() - 32);
        int perkY = random.nextInt(getHeight() - 32);
        int perkType = random.nextInt(2); 
        perks.add(new Perk(perkX, perkY, perkType));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (playerHealth <= 0) {
            timer.stop();
            repaint();
            return;
        }

        int newPlayerX = playerX;
        int newPlayerY = playerY;

        if (upPressed) {
            newPlayerY -= MOVE_SPEED;
        }
        if (downPressed) {
            newPlayerY += MOVE_SPEED;
        }
        if (leftPressed) {
            newPlayerX -= MOVE_SPEED;
        }
        if (rightPressed) {
            newPlayerX += MOVE_SPEED;
        }

        if (newPlayerX < 0) newPlayerX = 0;
        if (newPlayerX + 52 > getWidth()) newPlayerX = getWidth() - 52;
        if (newPlayerY < 0) newPlayerY = 0;
        if (newPlayerY + 52 > getHeight()) newPlayerY = getHeight() - 52;

        playerX = newPlayerX;
        playerY = newPlayerY;

        if (!preparing) {
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).move();
                if (bullets.get(i).isOffScreen()) {
                    bullets.remove(i);
                    i--;
                } else {
                    for (int j = 0; j < enemies.size(); j++) {
                        if (bullets.get(i).intersects(enemies.get(j))) {
                            bullets.remove(i);
                            enemies.remove(j);
                            i--;
                            break;
                        }
                    }
                }
            }

            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                enemy.moveTowards(playerX, playerY);
                if (enemy.intersects(playerX, playerY)) {
                    playerHealth -= 7;
                    enemies.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < perks.size(); i++) {
                if (perks.get(i).intersects(playerX, playerY)) {
                    perks.get(i).applyPerk(this);
                    perks.remove(i);
                    i--;
                }
            }

            if (enemies.isEmpty()) {
                preparing = true;
                startPreparation();
            }
        }
        repaint();
    }

    private void restartGame() {
        playerHealth = 100;
        currentWave = 0;
        preparing = true;
        prepareTime = 10;
        bullets.clear();
        enemies.clear();
        perks.clear();
        playerX = 400;
        playerY = 300;
        startPreparation();
        timer.start();
    }

    private class GamePanel extends JPanel {
        public GamePanel() {
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (freakyBobSprite != null) {
                g.drawImage(freakyBobSprite, playerX, playerY, 52, 52, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(playerX, playerY, 32, 32);
            }
            for (Bullet bullet : bullets) {
                bullet.draw(g);
            }
            for (Enemy enemy : enemies) {
                enemy.draw(g);
            }
            for (Perk perk : perks) {
                perk.draw(g);
            }
            g.setColor(Color.WHITE);
            g.drawString("Health: " + playerHealth, 10, 20);
            g.drawString("Current Wave: " + currentWave, 10, 40);
            if (preparing) {
                g.drawString("Prepare! Time left: " + prepareTime, 10, 60);
            } else if (playerHealth <= 0) {
                g.drawString("Skill issue. Press R to Restart", 300, 300);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.setVisible(true);
        });
    }

    public void increaseHealth(int amount) {
        playerHealth += amount;
        if (playerHealth > 100) {
            playerHealth = 100;
        }
    }
}

class Bullet {
    private int x, y;
    private double dx, dy;

    public Bullet(int startX, int startY, int targetX, int targetY) {
        x = startX;
        y = startY;
        double angle = Math.atan2(targetY - startY, targetX - startX);
        dx = Math.cos(angle) * 10;
        dy = Math.sin(angle) * 10;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public boolean isOffScreen() {
        return x < 0 || x > 800 || y < 0 || y > 600;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, 5, 5);
    }

    public boolean intersects(Enemy enemy) {
        return x < enemy.getX() + 32 && x + 5 > enemy.getX() && y < enemy.getY() + 32 && y + 5 > enemy.getY();
    }
}

class Enemy {
    private int x, y;
    private static final int SPEED = 2;
    private BufferedImage enemySprite;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        loadSprite();
    }

    private void loadSprite() {
        try {
            enemySprite = ImageIO.read(new File("Code\\Assets\\ScarySquirtward.png"));
        } catch (IOException e) {
            System.err.println("Error loading enemy sprite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void moveTowards(int playerX, int playerY) {
        if (x < playerX) x += SPEED;
        if (x > playerX) x -= SPEED;
        if (y < playerY) y += SPEED;
        if (y > playerY) y -= SPEED;
    }

    public void draw(Graphics g) {
        if (enemySprite != null) {
            g.drawImage(enemySprite, x, y, 32, 32, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, 32, 32);
        }
    }

    public boolean intersects(int playerX, int playerY) {
        return x < playerX + 52 && x + 32 > playerX && y < playerY + 52 && y + 32 > playerY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class Perk {
    protected int x, y;
    protected BufferedImage image;
    protected int type;

    public Perk(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        loadImage();
    }

    protected void loadImage() {
        try {
            if (type == 0) {
                image = ImageIO.read(new File("Code/Assets/green-marijuana-leaf-png_252592.jpg"));
            } else if (type == 1) {
                image = ImageIO.read(new File("Code/Assets/speed_perk.png")); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, 32, 32, null);
        }
    }

    public boolean intersects(int playerX, int playerY) {
        return (x < playerX + 52 && x + 32 > playerX && y < playerY + 52 && y + 32 > playerY);
    }

    public void applyPerk(Game game) {
        if (type == 0) {
            game.increaseHealth(20);
        } else if (type == 1) {
            
        }
    }
}
// I love Freakybob :3
