
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

abstract class GameObjectG {
	private int posX;
	private int posY;
	private Image image;
	private int gameSpeed;

	GameObjectG(int posX, int posY, int gameSpeed, Image image) {
		this.posX = posX;
		this.posY = posY;
		this.image = image;
		this.gameSpeed = gameSpeed;
	}

	int getX() {
		return posX;
	}

	int getY() {
		return posY;
	}

	Image getImage() {
		return image;
	}

	int gameSpeed() {
		return gameSpeed;
	}

	abstract void move();
}

class BulletObjectG extends GameObjectG {
	int bulletLocation;

	BulletObjectG(int posX, int posY, int gameSpeed, Image image) {
		super(posX, posY, gameSpeed, image);
		this.bulletLocation = posY;
	}

	void move() {
		bulletLocation--;
	}
}

class PlayObjectG extends GameObjectG {

	PlayObjectG(int posX, int posY, int gameSpeed, Image image) {
		super(posX, posY, gameSpeed, image);
	}

	void move() {
	}
}

class EnemyObjectG extends GameObjectG {
	int enemyPlanePositionW;
	int enemyPlanePositionH;
	int enemyMovingNum;
	int enemyPlanesNum;
	boolean enemyMoving;

	EnemyObjectG(int posX, int posY, int enemyMovingNum, int enemyPlanesNum, boolean enemyMoving, int gameSpeed,
			Image image) {
		super(posX, posY, gameSpeed, image);
		this.enemyPlanePositionW = posX;
		this.enemyPlanePositionH = posY;
		this.enemyMovingNum = enemyMovingNum;
		this.enemyPlanesNum = enemyPlanesNum;
		this.enemyMoving = enemyMoving;
	}

	void move() {
		if (0 == enemyPlanesNum) {
			return;
		} else {
			if (enemyMoving == true) {
				if (enemyMovingNum == 20) {
					enemyMoving = false;
					enemyPlanePositionH++;
				} else {
					enemyPlanePositionW++;
					enemyMovingNum++;
				}
			} else if (enemyMoving == false) {
				if (enemyMovingNum == -50) {
					enemyMoving = true;
					enemyPlanePositionW++;
				} else {
					enemyPlanePositionW--;
					enemyMovingNum--;
				}
			}
		}
		back: for (int h = 0; h < 1; h++) {
			for (int i = 0; i < enemyPlanesNum; i++) {
				if (0 == enemyPlanesNum) {
					break back;
				}
			}
		}
	}
}

class GameHandlerG extends JFrame implements KeyListener, Runnable {
	private final int SCREEN_WIDTH = 900;
	private final int LEFT_PADDING = 2;
	private final int SCREEN_HEIGHT = 500;
	private final int FIELD_WIDTH = 800, FIELD_HEIGHT = 400;
	private int enemyPlanesNum = 8;
	private final int limitedPlanesInLine = 4;
	private final int enemyPlaneSpace = 20;
	private int enemyPlanePositionW = 60;
	private int enemyPlanePositionH = 0;
	private int playPlanePositionW = 400;
	private int playPlanePositionH = 400;
	private int score = 0;
	private int rocketNum = 0;
	private int limitedRocketNum = 10;
	private int enemyMovingNum = 0;
	private int gameSpeed = 5;
	private boolean enemyMoving = false;
	private boolean keyUp = false;
	private boolean keyDown = false;
	private boolean keyLeft = false;
	private boolean keyRight = false;
	private boolean playerLive = true;
	ArrayList Rocket_List = new ArrayList();
	ArrayList Enemy_List = new ArrayList();
	GameObjectG play;
	EnemyObjectG enemyG;
	BulletObjectG bull;
	Thread th;
	Image playerImage = new ImageIcon("image/iron man.png").getImage();
	Image enemyImage = new ImageIcon("image/iron man.png").getImage();
	Image rocketImage = new ImageIcon("image/Rocket.png").getImage();

	GameHandlerG() {
		start();
		setTitle("20130509 성은일");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 500);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void start() {
		inputEnemy();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
		th = new Thread(this);
		th.start();

	}

	public void run() {
		try {
			while (true) {
				inputPlayer();
				keyProcess();
				repaint();
				moveEnemy();
				collision();
				Thread.sleep(gameSpeed);
			}
		} catch (Exception e) {
		}
	}

	public void inputPlayer() {
		if (playerLive) {
			play = new PlayObjectG(playPlanePositionW, playPlanePositionH, gameSpeed, playerImage);
		} else {
			play = new PlayObjectG(playPlanePositionW, playPlanePositionH, gameSpeed, playerImage);
		}
	}

	public void inputRocket() {
		bull = new BulletObjectG(playPlanePositionW + 2, playPlanePositionH, gameSpeed, rocketImage);
		Rocket_List.add(bull);
	}

	public void inputEnemy() {
		for (int i = 0; i < enemyPlanesNum; i++) {
			if (i >= limitedPlanesInLine) {
				enemyG = new EnemyObjectG(
						enemyPlanePositionW - 700 + ((i * 10 - limitedPlanesInLine) * enemyPlaneSpace),
						enemyPlanePositionH + 100, enemyMovingNum, enemyPlanesNum, enemyMoving, gameSpeed, enemyImage);
				Enemy_List.add(enemyG);
			} else {
				enemyG = new EnemyObjectG(enemyPlanePositionW + (i * 10 * enemyPlaneSpace), enemyPlanePositionH,
						enemyMovingNum, enemyPlanesNum, enemyMoving, gameSpeed, enemyImage);
				Enemy_List.add(enemyG);
			}
		}
	}

	void collision() {
		if (Enemy_List.size() == 0) {
			gameSpeed--;
			inputEnemy();
		} else {
			for (int i = 0; i < Rocket_List.size(); i++) {
				for (int h = 0; h < Enemy_List.size(); h++) {

					bull = (BulletObjectG) Rocket_List.get(i);
					enemyG = (EnemyObjectG) Enemy_List.get(h);

					if (bull.getX() == enemyG.enemyPlanePositionW
							&& bull.bulletLocation == enemyG.enemyPlanePositionH) {
						rocketNum--;
						Rocket_List.remove(i);
						Enemy_List.remove(h);
						break;
					}
					if (play.getX() == enemyG.enemyPlanePositionW && play.getY() == enemyG.enemyPlanePositionH) {
						playerLive = false;
					}

				}
			}
		}
	}

	void moveEnemy() {
		for (int i = 0; i < Enemy_List.size(); i++) {
			enemyG = (EnemyObjectG) Enemy_List.get(i);
			enemyG.move();
		}
	}

	public void keyProcess() {
		if (keyUp == true) {
			if (playPlanePositionH == 0)
				return;
			else
				playPlanePositionH--;
		}
		if (keyDown == true) {
			if (playPlanePositionH >= FIELD_HEIGHT - 1)
				return;
			else
				playPlanePositionH++;
		}
		if (keyLeft == true) {
			if (playPlanePositionW <= LEFT_PADDING - 1)
				return;
			else
				playPlanePositionW--;
		}
		if (keyRight == true) {
			if (playPlanePositionW >= FIELD_WIDTH - 5)
				return;
			else
				playPlanePositionW++;
		}

		if (keyUp && keyRight)// 동시에 두개의 키를 누루면(대각선)
		{
			if (playPlanePositionH == 0 || playPlanePositionW >= FIELD_WIDTH)
				return;
			else
				playPlanePositionW++;
			playPlanePositionH--;
		}
		if (keyUp && keyLeft) {
			if (playPlanePositionH == 0 || playPlanePositionW <= LEFT_PADDING - 1)
				return;
			else
				playPlanePositionW--;
			playPlanePositionH--;
		}
		if (keyDown && keyRight) {
			if (playPlanePositionH >= FIELD_HEIGHT - 1 || playPlanePositionW >= FIELD_WIDTH)
				return;
			else
				playPlanePositionW++;
			playPlanePositionH++;

		}
		if (keyDown && keyLeft) {
			if (playPlanePositionH >= FIELD_HEIGHT - 1 || playPlanePositionW <= LEFT_PADDING - 1)
				return;
			else
				playPlanePositionH++;
			playPlanePositionW--;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			keyRight = true;
			break;
		case KeyEvent.VK_LEFT:
			keyLeft = true;
			break;
		case KeyEvent.VK_DOWN:
			keyDown = true;
			break;
		case KeyEvent.VK_UP:
			keyUp = true;
			break;
		case KeyEvent.VK_SPACE:
			if (rocketNum < limitedRocketNum) {
				rocketNum++;
				inputRocket();
			}
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent ke) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			keyUp = false;
			break;
		case KeyEvent.VK_DOWN:
			keyDown = false;
			break;
		case KeyEvent.VK_LEFT:
			keyLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			keyRight = false;
			break;
		}
	}

	public void paint(Graphics g) {
		update(g);
	}

	public void update(Graphics g) {

		Draw_Background(g);
		Draw_Player(g);
		Draw_Enemy(g);
		Draw_Rocket(g);
	}

	public void Draw_Background(Graphics g) {

		g.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	public void Draw_Player(Graphics g) {
		g.drawImage(play.getImage(), play.getX(), play.getY(), 100, 100, this);
	}

	public void Draw_Enemy(Graphics g) {
		for (int i = 0; i < Enemy_List.size(); i++) {
			enemyG = (EnemyObjectG) Enemy_List.get(i);
			g.drawImage(enemyG.getImage(), enemyG.enemyPlanePositionW, enemyG.enemyPlanePositionH, 100, 100, this);
		}
	}

	public void Draw_Rocket(Graphics g) {

		for (int i = 0; i < Rocket_List.size(); i++) {
			bull = (BulletObjectG) Rocket_List.get(i);
			if (bull.bulletLocation == 0) {
				Rocket_List.remove(i);
				rocketNum--;
			} else {
				bull.move();
				g.drawImage(bull.getImage(), bull.getX(), bull.bulletLocation, 100, 100, this);
			}
		}
	}
}

public class FlyingGameGUI {
	public static void main(String[] args) {
		GameHandlerG gh = new GameHandlerG();
	}
}
