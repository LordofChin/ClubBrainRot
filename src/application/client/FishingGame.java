package application.client;

	import javax.swing.*;
	import java.awt.*;
	import java.awt.event.*;
	import java.util.ArrayList;
	import java.util.Random;

	public class FishingGame extends JPanel implements ActionListener, KeyListener {

	    private static final long serialVersionUID = 1L;
		private final int panelWidth = 400;
	    private final int panelHeight = 600;

	    private int hookX = 185;
	    private int hookY = 0;
	    private boolean dropping = false;
	    private final int hookSpeed = 5;

	    private int score = 0;
	    private final Timer timer;
	    private final Random rand = new Random();

	    private final Image sharkImg;
	    private final Image hookImg;

	    private final ArrayList<Rectangle> fishList = new ArrayList<>();
	    private final int[] fishYPositions = {220, 320, 420};

	    public FishingGame() {
	        setPreferredSize(new Dimension(panelWidth, panelHeight));
	        setFocusable(true);
	        addKeyListener(this);

	        sharkImg = new ImageIcon(getClass().getResource("/assets/shark.jpg")).getImage();
	        hookImg  = new ImageIcon(getClass().getResource("/assets/hook.jpg")).getImage();

	        for (int i = 0; i < 3; i++) {
	            int x = rand.nextInt(250);
	            int y = fishYPositions[i];
	            fishList.add(new Rectangle(x, y, 60, 60));
	        }

	        timer = new Timer(16, this);
	        timer.start();
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);

	        g.setColor(new Color(120, 230, 255));
	        g.fillRect(0, 0, panelWidth, 150);

	        g.setColor(new Color(0, 0, 255));
	        g.fillRect(0, 150, panelWidth, 450);

	        g.setColor(Color.BLACK);
	        g.drawLine(hookX + 5, 0, hookX + 5, hookY);

	        if (hookImg != null) {
	            g.drawImage(hookImg, hookX - 10, hookY, 25, 50, null);
	        } else {
	            g.fillRect(hookX, hookY, 10, 40);
	        }

	        for (Rectangle fish : fishList) {
	            if (sharkImg != null) {
	                g.drawImage(sharkImg, fish.x, fish.y, 60, 60, null);
	            } else {
	                g.setColor(Color.GREEN);
	                g.fillOval(fish.x, fish.y, fish.width, fish.height);
	            }
	        }

	        g.setColor(Color.WHITE);
	        g.setFont(new Font("Arial", Font.BOLD, 22));
	        g.drawString("Score: " + score, 10, 30);

	        g.setFont(new Font("Arial", Font.BOLD, 16));
	        g.drawString("SPACE = drop hook", 10, 55);
	        g.drawString("LEFT/RIGHT = move", 10, 78);
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	        if (dropping) {
	            hookY += hookSpeed;
	            if (hookY >= 520) {
	                dropping = false;
	            }
	        } else if (hookY > 0) {
	            hookY -= hookSpeed;
	        }

	        Rectangle hookRect = new Rectangle(hookX - 10, hookY, 25, 50);

	        for (int i = 0; i < fishList.size(); i++) {
	            Rectangle fish = fishList.get(i);
	            fish.x += 2;

	            if (fish.x > panelWidth) {
	                fish.x = -60;
	                fish.y = fishYPositions[i];
	            }

	            if (hookRect.intersects(fish)) {
	                score++;
	                fish.x = -60;
	                fish.y = fishYPositions[i];
	                dropping = false;
	            }
	        }

	        repaint();
	    }

	    @Override
	    public void keyPressed(KeyEvent e) {
	        int key = e.getKeyCode();

	        if (key == KeyEvent.VK_SPACE) {
	            if (!dropping && hookY == 0) {
	                dropping = true;
	            }
	        } else if (key == KeyEvent.VK_LEFT) {
	            hookX = Math.max(10, hookX - 15);
	        } else if (key == KeyEvent.VK_RIGHT) {
	            hookX = Math.min(panelWidth - 20, hookX + 15);
	        }

	        repaint();
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
	    }

	    @Override
	    public void keyTyped(KeyEvent e) {
	    }

	    public static void main(String[] args) {
	        JFrame frame = new JFrame("Fish the BrainRot");
	        FishingGame game = new FishingGame();

	        frame.add(game);
	        frame.pack();
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);

	        game.requestFocusInWindow();
	    }
	}
