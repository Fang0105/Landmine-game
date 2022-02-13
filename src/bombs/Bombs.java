package bombs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import bombs.clock.*;

class Location{
	int x;
	int y;
	boolean isBomb = false;
	int surroundBombs = 0;
	boolean isFlag = false;
	Location(int x,int y){
		this.x = x;
		this.y = y;
	}
	
}

public class Bombs {
	static int size = 8;
	int bombsNumber;
	int flagNumber = 0;
	static int[] delta = {-1,0,1};
	JFrame mapFrame;
	Dimension screen;
	Location[][] gameMap = new Location[size][size];
	Component[][] area = new Component[size][size];
	JButton[][] trashButton = new JButton[size][size];
	Container[][] container = new Container[size][size];
	boolean isFirstClick = true;
	boolean[][] vis = new boolean[size][size];	
	ImageIcon bombIcon = new ImageIcon(getClass().getClassLoader().getResource("images/bomb.png"));
	ImageIcon[] numberIcon = new ImageIcon[10];
	ImageIcon flagIcon = new ImageIcon(getClass().getClassLoader().getResource("images/flag.jpg"));
	
	public void setNumberIcon() {
		for(int i=0;i<10;i++) {
			numberIcon[i] = new ImageIcon(getClass().getClassLoader().getResource("images/"+i+(i>=7?".jpg":".png")));
		}
	}
	
	public void init() {
		clock.start = clock.getTime();
		flagNumber = 0;
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				this.isFirstClick = true;
				this.area[i][j] = new JButton();
				this.gameMap[i][j] = new Location(i,j);
				this.trashButton[i][j] = new JButton();
				this.container[i][j] = new Container();
				this.vis[i][j] = false;
			}
		}
	}
	
	public void setButton() {
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				//area[i][j].setSize(50,50);
				final int x = i;
				final int y = j;
				container[i][j] = mapFrame.getContentPane();
				container[i][j].setLayout(null);
				area[x][y].setBounds(y*50,x*50,50,50);
				container[x][y].add(area[x][y]);
				area[x][y].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getButton()==e.BUTTON1) {
							//左鍵
							checkIsBomb(x,y);
							mapFrame.repaint();
						}else {
							//右鍵
							flagIcon.setImage(flagIcon.getImage().getScaledInstance(area[x][y].getWidth(), area[x][y].getHeight(), Image.SCALE_DEFAULT));
							trashButton[x][y] = (JButton) area[x][y];
							container[x][y].remove(area[x][y]);
							area[x][y] = new JButton(flagIcon);
							area[x][y].setBounds(y*50,x*50,50,50);
							container[x][y].add(area[x][y]);	
							gameMap[x][y].isFlag = true;
							if(gameMap[x][y].isBomb==true) {
								flagNumber++;
							}
							area[x][y].addMouseListener(new MouseAdapter() {
								@Override
								public void mouseClicked(MouseEvent e) {
									if(e.getButton()==e.BUTTON3) {
										container[x][y].remove(area[x][y]);
										area[x][y] = trashButton[x][y];
										container[x][y].add(area[x][y]);
										if(gameMap[x][y].isBomb==true) {
											flagNumber--;
										}
										mapFrame.repaint();
									}
								}
							});	
							if(flagNumber==bombsNumber) {
								clock.end = clock.getTime();
								JDialog jd = new JDialog(mapFrame);
								jd.setLayout(new FlowLayout());
								jd.setBounds((screen.width-mapFrame.getSize().width)/2,(screen.height-mapFrame.getSize().height)/2,200,130);
								JLabel againLabel = new JLabel("恭喜你贏了^_^   再來一局?");							
								JLabel timeLabel = new JLabel(String.valueOf(clock.getDistance()));
								JButton yesButton = new JButton("要");
								JButton noButton = new JButton("不要");
								yesButton.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										for(int i=0;i<size;i++) {
											for(int j=0;j<size;j++) {
												container[i][j].remove(area[i][j]);
											}
										}
										init();
										generateMap();
										setButton();
										jd.setVisible(false);	
										mapFrame.repaint();
									}
								});
								noButton.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {					
										System.exit(0);
										
									}
								});
								jd.add(againLabel);
								jd.add(timeLabel);
								jd.add(yesButton);
								jd.add(noButton);
								jd.setVisible(true);
							}
							mapFrame.repaint();
						}
					}
				});
			}
		}
	}
	
	public void checkIsBomb(int x,int y) {
		if(gameMap[x][y].isBomb==true) {
			if(isFirstClick==true) {
				isFirstClick = false;
				for(int i:delta) {
					for(int j:delta) {
						if(x+i>=0 && x+i<size && y+j>=0 && y+j<size && (i!=0||j!=0)) {
							this.gameMap[x+i][y+j].surroundBombs--;
						}
					}
				}
				int xx = (int)(Math.random()*8);
				int yy = (int)(Math.random()*8);
				while(this.gameMap[xx][yy].isBomb==true) {
					xx = (int)(Math.random()*8);
					yy = (int)(Math.random()*8);
				}
				this.gameMap[xx][yy].isBomb = true;
				gameMap[x][y].isBomb = false;
				for(int i:delta) {
					for(int j:delta) {
						if(xx+i>=0 && xx+i<size && yy+j>=0 && yy+j<size && (i!=0||j!=0)) {
							this.gameMap[xx+i][yy+j].surroundBombs++;
						}
					}
				}
				if(gameMap[x][y].surroundBombs==0) {
					//bfs
					Queue<Location> q = new LinkedList<>();
					q.add(gameMap[x][y]);
					while(q.isEmpty()==false) {
						Location head = q.poll();
						for(int i:delta) {
							for(int j:delta) {
								if(head.x+i>=0 && head.x+i<size && head.y+j>=0 && head.y+j<size && i*j==0 && vis[head.x+i][head.y+j]==false) {
									if(gameMap[head.x+i][head.y+j].surroundBombs==0) {
										vis[head.x+i][head.y+j] = true;			
										container[head.x+i][head.y+j].remove(area[head.x+i][head.y+j]);
										numberIcon[gameMap[head.x+i][head.y+j].surroundBombs].setImage(numberIcon[gameMap[head.x+i][head.y+j].surroundBombs].getImage().getScaledInstance(area[head.x+i][head.y+j].getWidth(), area[head.x+i][head.y+j].getHeight(), Image.SCALE_DEFAULT));
										area[head.x+i][head.y+j] = new JLabel(numberIcon[gameMap[head.x+i][head.y+j].surroundBombs]);
										area[head.x+i][head.y+j].setBounds((head.y+j)*50,(head.x+i)*50,50,50);
										container[head.x+i][head.y+j].add(area[head.x+i][head.y+j]);
										vis[head.x+i][head.y+j] = true;
										q.add(gameMap[head.x+i][head.y+j]);
									}									
								}
							}
						}
											
					}
					mapFrame.repaint();
				}else {
					container[x][y].remove(area[x][y]);
					numberIcon[gameMap[x][y].surroundBombs].setImage(numberIcon[gameMap[x][y].surroundBombs].getImage().getScaledInstance(area[x][y].getWidth(), area[x][y].getHeight(), Image.SCALE_DEFAULT));
					area[x][y] = new JLabel(numberIcon[gameMap[x][y].surroundBombs]);
					area[x][y].setBounds(y*50,x*50,50,50);
					container[x][y].add(area[x][y]);
				}
			}else {
				clock.end = clock.getTime();
				container[x][y].remove(area[x][y]);
				bombIcon.setImage(bombIcon.getImage().getScaledInstance(area[x][y].getWidth(), area[x][y].getHeight(), Image.SCALE_DEFAULT));
				area[x][y] = new JLabel(bombIcon);
				area[x][y].setBounds(y*50,x*50,50,50);
				container[x][y].add(area[x][y]);
				mapFrame.repaint();
				
				JDialog jd = new JDialog(mapFrame);
				jd.setLayout(new FlowLayout());
				jd.setBounds((screen.width-mapFrame.getSize().width)/2,(screen.height-mapFrame.getSize().height)/2,200,130);
				JLabel againLabel = new JLabel("輸了QQ  再來一局?");
				JLabel timeLabel = new JLabel(String.valueOf(clock.getDistance()));;
				JButton yesButton = new JButton("要");
				JButton noButton = new JButton("不要");
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						for(int i=0;i<size;i++) {
							for(int j=0;j<size;j++) {
								container[i][j].remove(area[i][j]);
							}
						}
						init();
						generateMap();
						setButton();
						jd.setVisible(false);	
						mapFrame.repaint();
					}
				});
				noButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {					
						System.exit(0);
						
					}
				});
				jd.add(againLabel);
				jd.add(timeLabel);
				jd.add(yesButton);
				jd.add(noButton);
				jd.setVisible(true);
			}		
		}else {
			isFirstClick = false;
			if(gameMap[x][y].surroundBombs==0) {
				//bfs
				Queue<Location> q = new LinkedList<>();
				q.add(gameMap[x][y]);
				while(q.isEmpty()==false) {
					Location head = q.poll();
					for(int i:delta) {
						for(int j:delta) {
							if(head.x+i>=0 && head.x+i<size && head.y+j>=0 && head.y+j<size && i*j==0 && vis[head.x+i][head.y+j]==false) {
								if(gameMap[head.x+i][head.y+j].surroundBombs==0) {
									vis[head.x+i][head.y+j] = true;			
									container[head.x+i][head.y+j].remove(area[head.x+i][head.y+j]);
									numberIcon[gameMap[head.x+i][head.y+j].surroundBombs].setImage(numberIcon[gameMap[head.x+i][head.y+j].surroundBombs].getImage().getScaledInstance(area[head.x+i][head.y+j].getWidth(), area[head.x+i][head.y+j].getHeight(), Image.SCALE_DEFAULT));
									area[head.x+i][head.y+j] = new JLabel(numberIcon[gameMap[head.x+i][head.y+j].surroundBombs]);
									area[head.x+i][head.y+j].setBounds((head.y+j)*50,(head.x+i)*50,50,50);
									container[head.x+i][head.y+j].add(area[head.x+i][head.y+j]);
									vis[head.x+i][head.y+j] = true;
									q.add(gameMap[head.x+i][head.y+j]);
								}									
							}
						}
					}
										
				}
				mapFrame.repaint();
			}else {
				container[x][y].remove(area[x][y]);
				numberIcon[gameMap[x][y].surroundBombs].setImage(numberIcon[gameMap[x][y].surroundBombs].getImage().getScaledInstance(area[x][y].getWidth(), area[x][y].getHeight(), Image.SCALE_DEFAULT));
				area[x][y] = new JLabel(numberIcon[gameMap[x][y].surroundBombs]);
				area[x][y].setBounds(y*50,x*50,50,50);
				container[x][y].add(area[x][y]);
			}		
		}
	}
	
	public Bombs() {
		init();
		generateMap();
		setNumberIcon();
		mapFrame = new JFrame("踩地雷");
		mapFrame.setSize(420,440);
		screen = Toolkit.getDefaultToolkit().getScreenSize();
		mapFrame.setLocation((screen.width-mapFrame.getSize().width)/2,(screen.height-mapFrame.getSize().height)/2);
		setButton();
		mapFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mapFrame.setVisible(true);
	}
	
	public void generateMap() {
		this.bombsNumber = 7 + (int)(Math.random()*3);
		int tem = this.bombsNumber;
		while(tem!=0) {
			int x = (int)(Math.random()*8);
			int y = (int)(Math.random()*8);
			if(this.gameMap[x][y].isBomb==false) {
				tem--;
				this.gameMap[x][y].isBomb = true;
				for(int i:delta) {
					for(int j:delta) {
						if(x+i>=0 && x+i<size && y+j>=0 && y+j<size && (i!=0||j!=0)) {
							this.gameMap[x+i][y+j].surroundBombs++;
						}
					}
				}
			}
		}
		//-------------------
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				System.out.print(gameMap[i][j].isBomb==true?1:0);
			}
			System.out.println();
		}
		System.out.println("--------------------------------");
		//-------------------
	}
	
	public static void main(String[] args) {
			Bombs game = new Bombs();
			
	}

}
