import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tank {
	public static  int speedX = 6, speedY =6; // 静态全局变量速度---------可以作为扩张来设置级别，速度快的话比较难
	public static int count = 0;
	public static final int width = 35, length = 35; // 坦克的全局大小，具有不可改变性
	private Direction direction = Direction.STOP; // 初始化状态为静止
	private Direction Kdirection = Direction.U; // 初始化方向为向上
	TankClient tc;

	private boolean good;
	private int x, y;
	private int oldX, oldY;
	private boolean live = true; // 初始化为活着
	private int life = 200; // 初始生命值

	private static Random r = new Random();
	private int step = r.nextInt(10)+5 ; // 产生一个随机数,随机模拟坦克的移动路径

	private boolean bL = false, bU = false, bR = false, bD = false;
	

	private static Toolkit tk = Toolkit.getDefaultToolkit();// 控制面板
	private static Image[] tankImags = null; // 存储全局静态
	static {
		tankImags = new Image[] {
				tk.getImage(BombTank.class.getResource("Images/tankD.png")),
				tk.getImage(BombTank.class.getResource("Images/tankU.png")),
				tk.getImage(BombTank.class.getResource("Images/tankL.png")),
				tk.getImage(BombTank.class.getResource("Images/tankR.png")), };

	}

	public Tank(int x, int y, boolean good) {// Tank的构造函数1
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
	}

	public Tank(int x, int y, boolean good, Direction dir, TankClient tc) {// Tank的构造函数2
		this(x, y, good);
		this.direction = dir;
		this.tc = tc;
	}

	public void draw(Graphics g) {
		if (!live) {
			if (!good) {
				tc.tanks.remove(this); // 删除无效的
			}
			return;
		}

		if (good)
			new DrawBloodbBar().draw(g); // 创造一个血包

		switch (Kdirection) {
							//根据方向选择坦克的图片
		case D:
			g.drawImage(tankImags[0], x, y, null);
			break;

		case U:
			g.drawImage(tankImags[1], x, y, null);
			break;
		case L:
			g.drawImage(tankImags[2], x, y, null);
			break;

		case R:
			g.drawImage(tankImags[3], x, y, null);
			break;

		}

		move();   //调用move函数
	}

	void move() {

		this.oldX = x;
		this.oldY = y;

		switch (direction) {  //选择移动方向
		case L:
			x -= speedX;
			break;
		case U:
			y -= speedY;
			break;
		case R:
			x += speedX;
			break;
		case D:
			y += speedY;
			break;
		case STOP:
			break;
		}

		if (this.direction != Direction.STOP) {
			this.Kdirection = this.direction;
		}

		if (x < 0)
			x = 0;
		if (y < 40)
			y = 40;
		if (x + Tank.width > TankClient.Fram_width)
			x = TankClient.Fram_width - Tank.width;
		if (y + Tank.length > TankClient.Fram_length)
			y = TankClient.Fram_length - Tank.length;

		if (!good) {
			Direction[] directons = Direction.values();
			if (step == 0) {                  
				step = r.nextInt(15) + 15;
				int rn = r.nextInt(directons.length);
				direction = directons[rn];
			}
			step--;
			if (r.nextInt(40) > 38)
				this.fire();
		}
	}

	private void changToOldDir() {  
		x = oldX;
		y = oldY;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_R:
			tc.tanks.clear();
			tc.bullets.clear();
			tc.trees.clear();
			tc.otherWall.clear();
			tc.homeWall.clear();
			tc.metalWall.clear();
			tc.homeTank.setLive(false);
			if (tc.tanks.size() == 0) {   //当在区域中没有坦克时，就出来坦克      
				for (int i = 0; i < 20; i++) {
					if (i < 9)
						tc.tanks.add(new Tank(150 + 70 * i, 40, false,
								Direction.R, tc));
					else if (i < 15)
						tc.tanks.add(new Tank(700, 140 + 50 * (i -6), false,
								Direction.D, tc));
					else
						tc.tanks.add(new Tank(10,  50 * (i - 12), false,
								Direction.L, tc));
				}
			}
			
			tc.homeTank = new Tank(300, 560, true, Direction.STOP, tc);//设置自己出现的位置
			
			if (!tc.home.isLive())
				tc.home.setLive(true);
			new TankClient();
			break;
		case KeyEvent.VK_RIGHT:
			bR = true;
			break;
		case KeyEvent.VK_LEFT:
			bL = true;
			break;
		case KeyEvent.VK_UP:
			bU = true;
			break;
		case KeyEvent.VK_DOWN:
			bD = true;
			break;
		}
		decideDirection();
	}

	void decideDirection() {
		if (!bL && !bU && bR && !bD)
			direction = Direction.R;
		else if (bL && !bU && !bR && !bD)
			direction = Direction.L;
		else if (!bL && bU && !bR && !bD)
			direction = Direction.U;
		else if (!bL && !bU && !bR && bD)
			direction = Direction.D;
		else if (!bL && !bU && !bR && !bD)
			direction = Direction.STOP;
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_F:
			fire();
			break;
		case KeyEvent.VK_RIGHT:
			bR = false;
			break;
		case KeyEvent.VK_LEFT:
			bL = false;
			break;
		case KeyEvent.VK_UP:
			bU = false;
			break;
		case KeyEvent.VK_DOWN:
			bD = false;
			break;
		}
		decideDirection();
	}

	public Bullets fire() {
		if (!live)
			return null;
		int x = this.x + Tank.width / 2 - Bullets.width / 2;
		int y = this.y + Tank.length / 2 - Bullets.length / 2;
		Bullets m = new Bullets(x, y + 2, good, Kdirection, this.tc);
		tc.bullets.add(m);                                                
		return m;
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, width, length);
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public boolean isGood() {
		return good;
	}

	public boolean collideWithWall(CommonWall w) {  //碰撞到普通墙时
		if (this.live && this.getRect().intersects(w.getRect())) {
			 this.changToOldDir();    //转换到原来的方向上去
			return true;
		}
		return false;
	}

	public boolean collideWithWall(MetalWall w) {  //撞到金属墙
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.changToOldDir();     
			return true;
		}
		return false;
	}

	public boolean collideRiver(River r) {    //撞到河流的时候
		if (this.live && this.getRect().intersects(r.getRect())) {
			this.changToOldDir();
			return true;
		}
		return false;
	}

	public boolean collideHome(Home h) {   //撞到家的时候
		if (this.live && this.getRect().intersects(h.getRect())) {
			this.changToOldDir();
			return true;
		}
		return false;
	}

	public boolean collideWithTanks(java.util.List<Tank> tanks) {//撞到坦克时
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			if (this != t) {
				if (this.live && t.isLive()
						&& this.getRect().intersects(t.getRect())) {
					this.changToOldDir();
					t.changToOldDir();
					return true;
				}
			}
		}
		return false;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	private class DrawBloodbBar {
		public void draw(Graphics g) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(375, 585, width, 10);
			int w = width * life / 200;
			g.fillRect(375, 585, w, 10);
			g.setColor(c);
		}
	}

	public boolean eat(GetBlood b) {
		if (this.live && b.isLive() && this.getRect().intersects(b.getRect())) {
			if(this.life<=100)
			this.life = this.life+100;      //每吃一个，增加100生命点
			else
				this.life = 200;
			b.setLive(false);
			return true;
		}
		return false;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}