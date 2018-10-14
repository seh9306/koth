package client.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;

import client.main.KingdomOfTheHansung;
import client.thread.MyCharacterAnimationThread;
import hash.key.SignalKey;
import hash.key.TakeOffKey;
import hash.key.UseItemKey;
import hash.key.UserMsgKey;
import model.GameMap;
import model.Item;
import model.Monster;
import model.UserPosInfo;
import value.FrameValue;
import value.MapValue;
import value.ProtocolValue;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	private MainPanel mainPanel;

	private Monster monsterMap[][] = new Monster[50][50];

	private String id;
	private int level;
	private int hp, mp;
	private int exp, gold;
	private String job;

	private MyCharacterAnimationThread myCharacterAnimationThread;

	private Vector<SignalKey> sendData;
	private Vector<UserPosInfo> userPosInfoVec;
	private Vector<Monster> monsterVec;
	private Vector<Item> itemVec;
	private Vector<ItemJLabel> itemJLabelVec;
	private UserPosInfo userPosInfo;

	private Image characterInfo, inputBox, inventory, background2, characterStatus, characterMoving, shop;
	private Image characterAction;

	private Image mapImage, soil, grass, buffer, floor, wall1, wall2, wall3, wall4, candle;
	private Image bed1, bed2, potal, cArmor, sword, skill1;

	private Image squ;

	private Item weapon = null;
	private Item armor = null;
	
	private Graphics mainGraphics, mapGraphics;

	private JTextArea chatArea = new JTextArea(10,10);
	private JTextField chatting = new JTextField(10);
	private JLabel levelLabel = new JLabel("00");
	private JLabel idLabel = new JLabel("id");
	private JLabel hpLabel = new JLabel("0");
	private JLabel mpLabel = new JLabel("0");
	private JLabel jobLabel = new JLabel("??");
	private JLabel expLabel = new JLabel("0");
	private JLabel goldLabel = new JLabel("0");

	private GameMap gm;
	private int mapWidth;
	private int mapHeight;
	private int mapIndex;

	private Thread th;

	long startTime, endTime, sendTime, endTime2;

	public MainPanel(KingdomOfTheHansung koth) {
		mainPanel = this;

		characterInfo = new ImageIcon("images//mainPanel//characterInfo.png").getImage();
		characterAction = new ImageIcon("images//mainPanel//characterAction.png").getImage();
		characterStatus = new ImageIcon("images//mainPanel//characterStatus.png").getImage();
		characterMoving = new ImageIcon("images//mainPanel//characterMoving.png").getImage();
		inputBox = new ImageIcon("images//mainPanel//inputBox2.png").getImage();
		inventory = new ImageIcon("images//mainPanel//inventory.png").getImage();
		shop = new ImageIcon("images//mainPanel//shop.png").getImage();
		squ = new ImageIcon("images//mainPanel//monster//squ.png").getImage();
		skill1 = new ImageIcon("images//mainPanel//skill1.png").getImage();
		sword = new ImageIcon("images//mainPanel//sword.png").getImage();

		floor = new ImageIcon("images//mainPanel//floor.jpg").getImage();
		wall1 = new ImageIcon("images//mainPanel//wall1.png").getImage();
		wall2 = new ImageIcon("images//mainPanel//wall2.png").getImage();
		wall3 = new ImageIcon("images//mainPanel//wall3.png").getImage();
		wall4 = new ImageIcon("images//mainPanel//wall4.png").getImage();
		cArmor = new ImageIcon("images//mainPanel//armor.png").getImage();

		candle = new ImageIcon("images//mainPanel//candle.png").getImage();
		bed1 = new ImageIcon("images//mainPanel//bed1.png").getImage();
		bed2 = new ImageIcon("images//mainPanel//bed2.png").getImage();

		background2 = new ImageIcon("images//mainPanel//background2.png").getImage();

		soil = new ImageIcon("images//mainPanel//soil.png").getImage();
		grass = new ImageIcon("images//mainPanel//grass.png").getImage();
		potal = new ImageIcon("images//mainPanel//potal.png").getImage();
		
		itemJLabelVec = new Vector<ItemJLabel>();
		
		for(int i = 0; i < 26; i++){
			ItemJLabel jl = new ItemJLabel();
			jl.setBounds(900, 122+19*i, 100, 12);
			jl.setForeground(Color.WHITE);
			
			jl.addMouseListener(new MouseAdapter(){
				long time = 0;
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1){
						if(time != 0 && e.getWhen()-time < 500 ){
							ItemJLabel itemJLabel = (ItemJLabel)e.getComponent();
							Item item = itemJLabel.getItem();
							System.out.println(itemJLabel.getText());
							if(item == null){
								System.out.println("비엇습니다.");
								return;
							}
							switch(item.getKind()){
							case 0: // 무기
								if(weapon == null){
									userPosInfo.setWeapon(true);
									weapon = item;
								}else{
									return;
								}
								repaint();
								break;
							case 1: // 갑옷
								if(armor == null){
									userPosInfo.setArmor(true);
									armor = item;
								}else{
									return;
								}
								repaint();
								break;
							case 2:
								mainPanel.setHp(hp+item.getEffect());	
								break;
							default:
								return;
							}
							UseItemKey useItemKey = new UseItemKey();
							useItemKey.setProtocol(ProtocolValue.USE_ITEM);
							useItemKey.setItem(item);
							sendData.add(useItemKey);
							itemJLabel.setText(null);
							itemJLabel.setItem(null);
						}else{
							time = e.getWhen();
						}
					}
				}         
			}); 
			add(jl);
			itemJLabelVec.add(jl);
		}

		
		
		sendData = koth.getSignalKeyVectorForSendThread();

		setLayout(null);

		chatting.setBounds(15, 733, 795, 20);

		levelLabel.setBounds(939, 1, 100, 100);
		levelLabel.setForeground(Color.WHITE);

		jobLabel.setBounds(833, 26, 100, 50);
		jobLabel.setForeground(Color.WHITE);
		jobLabel.setVerticalAlignment(SwingConstants.CENTER);
		jobLabel.setHorizontalAlignment(SwingConstants.CENTER);

		idLabel.setBounds(850, 3, 100, 50);
		idLabel.setForeground(Color.WHITE);
		idLabel.setVerticalAlignment(SwingConstants.CENTER);
		idLabel.setHorizontalAlignment(SwingConstants.CENTER);

		hpLabel.setBounds(915, 645, 100, 50);
		hpLabel.setForeground(Color.WHITE);
		hpLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		mpLabel.setBounds(915, 665, 100, 50);
		mpLabel.setForeground(Color.WHITE);
		mpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		goldLabel.setBounds(915,682,100,50);
		goldLabel.setForeground(Color.WHITE);
		goldLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		expLabel.setBounds(915,701,100,50);
		expLabel.setForeground(Color.WHITE);
		expLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		chatting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = chatting.getText().toString();
				if (msg != null && !msg.equals("")) {
					if (myCharacterAnimationThread.getMsgThread().getState().toString().equals("TIMED_WAITING")) {
						// System.out.println(myCharacterAnimationThread.getMsgThread().getState().toString());
						myCharacterAnimationThread.getMsgThread().interrupt();
					}
					try {
						UserMsgKey userMsgKey = new UserMsgKey();
						userMsgKey.setProtocol(ProtocolValue.USER_MSG);
						userMsgKey.setId(id);
						userMsgKey.setMapIndex(mapIndex);
						userMsgKey.setMsg(msg);
						userMsgKey.setPosX(userPosInfo.getX());
						userMsgKey.setPosY(userPosInfo.getY());
						sendData.add(userMsgKey);
						myCharacterAnimationThread.getMsgQueue().put(msg);
						chatArea.append("    "+id+" :"+msg+"\n");
						chatArea.setCaretPosition(chatArea.getDocument().getLength());
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					// System.out.println(myCharacterAnimationThread.getMsgThread().getState().toString());
					
					chatting.setText("");
				}
				mainPanel.requestFocus();

			}
		});
		chatArea.setBounds(11,600,817,120);
		chatArea.setBackground(new Color(10, 10, 10, 200));
		chatArea.setFocusable(false);
		chatArea.setForeground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(chatArea);
		scrollPane.setBorder(null);
		scrollPane.setBounds(11,600,817,120);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		add(scrollPane);

		chatting.setBackground(new Color(196,124,34,255));
		chatting.setForeground(Color.WHITE);
		chatting.setCaretColor(new Color(196,124,34,255));
		chatting.setHighlighter(null);
		chatting.setBorder(null);
		add(expLabel);
		add(goldLabel);
		add(idLabel);
		add(levelLabel);
		add(jobLabel);
		add(hpLabel);
		add(mpLabel);
		add(chatting);
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_ENTER) {
					chatting.requestFocus();
					return;
				}
				if(keyCode == KeyEvent.VK_1){
					
					System.out.println("벗냐?");
					if(weapon == null)
						return;
					TakeOffKey takeOffKey = new TakeOffKey();
					takeOffKey.setProtocol(ProtocolValue.TAKE_OFF);
					takeOffKey.setItem(weapon);
					sendData.add(takeOffKey);
					userPosInfo.setWeapon(false);
					weapon = null;
					return;
				}
				if(keyCode == KeyEvent.VK_2){
					
					if(armor == null)
						return;
					TakeOffKey takeOffKey = new TakeOffKey();
					takeOffKey.setProtocol(ProtocolValue.TAKE_OFF);
					takeOffKey.setItem(armor);
					sendData.add(takeOffKey);
					userPosInfo.setArmor(false);
					armor = null;
					return;
				}
				
				String state = myCharacterAnimationThread.getState().toString();
				if (state.equals("TIMED_WAITING") || state.equals("RUNNABLE")) {
					return; // 이 캐릭터가 움직이고 있을 때는 입력을 받지 않겟다.
				}
				if (keyCode != KeyEvent.VK_SPACE)
					myCharacterAnimationThread.setAction(keyCode);
				else
					myCharacterAnimationThread.notifySpace();
				myCharacterAnimationThread.notifyThread();
			}

		});

	}

	public void setUserPosInfo(UserPosInfo userPosInfo) {
		this.userPosInfo = userPosInfo;
		
		if(userPosInfo.isArmor()){
			Item item = new Item();
			item.setName("갑옷");
			item.setKind(1);
			item.setEffect(50);
			armor = item;
		}
		if(userPosInfo.isWeapon()){
			Item item = new Item();
			item.setName("검성기검");
			item.setKind(0);
			item.setEffect(200);
			weapon = item;
		}
			
	}

	public void setMap() {
		buffer = createImage(FrameValue.WINDOW_WIDTH, FrameValue.WINDOW_HEIGHT);
		mainGraphics = buffer.getGraphics();
		try {
			ObjectInputStream ois = null;

			File file = new File("map//map_" + mapIndex);
			ois = new ObjectInputStream(new FileInputStream(file));
			gm = (GameMap) ois.readObject();
			ois.close();

			mapWidth = gm.getWidth();
			mapHeight = gm.getHeight();
			System.out.println(mapWidth + "//" + mapHeight);
			mapImage = createImage(mapWidth * 60, mapHeight * 60);

			mapGraphics = mapImage.getGraphics();

			for (int i = 0; i < mapHeight; i++) {
				for (int j = 0; j < mapWidth; j++) {
					int val = gm.getMapValue(j, i);
					switch (val) {
					case 0:
						mapGraphics.drawImage(soil, j * 60, i * 60, this);
						break;
					case 51:
					case 1:
						mapGraphics.drawImage(grass, j * 60, i * 60, this);
						break;
					case 2:
					case 60:
						mapGraphics.drawImage(floor, j * 60, i * 60, this);
						break;
					case 49:
						mapGraphics.drawImage(potal, j * 60, i * 60, 60, 60, this);
						break;
					}

				}
			}

			if (mapIndex == 0) {
				mapGraphics.drawImage(shop, 60 * 9, 60 * 9, 860, 300, this);
			} else if (mapIndex == 1) {
				mapGraphics.drawImage(wall1, 360, 540, 480, 180, this);
				mapGraphics.drawImage(wall2, 0, 540, this);
				mapGraphics.drawImage(wall3, 60, 0, 720, 217, this);
				mapGraphics.drawImage(wall4, 0, 0, this);
				mapGraphics.drawImage(wall4, 780, 1, this);
				mapGraphics.drawImage(candle, 240, 240, this);
				mapGraphics.drawImage(bed1, 360, 300, this);
				mapGraphics.drawImage(bed2, 480, 300, this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// doubleBuffering();
		// repaint();
	}

	// double buffering
	private void doubleBuffering() {
		String msg;
		Color c = mainGraphics.getColor();
		int relPosX = 7 * 60;
		int relPosY = 6 * 60;
		int absPosX = userPosInfo.getX();
		int absPosY = userPosInfo.getY();

		int realWidth = mapWidth * MapValue.BLOCK_SIZE;
		int realHeight = mapHeight * MapValue.BLOCK_SIZE;

		// Graphics mapGraphics = map.getGraphics();
		if (absPosX < 420) {
			relPosX = absPosX;
		}
		if (absPosY < 360) {
			relPosY = absPosY;
		}
		if (absPosX > mapWidth * 60 - 420) {
			relPosX = -(mapWidth * 60 - 840 - absPosX);
		}

		if (absPosY > mapHeight * 60 - 360) {
			relPosY = -(mapHeight * 60 - 720 - absPosY);
		}

		int size = userPosInfoVec.size();

		mainGraphics.setClip(10, 10, 817, 710);
		if (absPosY >= 360 && absPosX >= 420 && absPosX <= mapWidth * 60 - 420 && absPosY <= mapHeight * 60 - 360) {
			mainGraphics.drawImage(mapImage, 10 - absPosX + 7 * MapValue.BLOCK_SIZE,
					10 - absPosY + 6 * MapValue.BLOCK_SIZE, realWidth, realHeight, this);
		} else if (absPosY < 360) {
			if (absPosX < 420) {
				mainGraphics.drawImage(mapImage, 10, 10, realWidth, realHeight, this);
			} else if (absPosX > mapWidth * 60 - 420) {
				mainGraphics.drawImage(mapImage, 10 - mapWidth * 60 + 840, 10, realWidth, realHeight, this);
			} else {
				mainGraphics.drawImage(mapImage, 10 - absPosX + 7 * MapValue.BLOCK_SIZE, 10, realWidth, realHeight,
						this);
			}
		} else if (absPosY > mapHeight * 60 - 360) {
			if (absPosX < 420) {
				mainGraphics.drawImage(mapImage, 10, 10 - mapHeight * 60 + 720, realWidth, realHeight, this);
			} else if (absPosX > mapWidth * 60 - 420) {
				mainGraphics.drawImage(mapImage, 10 - mapWidth * 60 + 840, 10 - mapHeight * 60 + 720, realWidth,
						realHeight, this);
			} else {
				mainGraphics.drawImage(mapImage, 10 - absPosX + 7 * MapValue.BLOCK_SIZE, 10 - mapHeight * 60 + 720,
						realWidth, realHeight, this);
			}
		} else if (absPosX < 420) {
			mainGraphics.drawImage(mapImage, 10, 10 - absPosY + 6 * MapValue.BLOCK_SIZE, realWidth, realHeight, this);
		} else if (absPosX > mapWidth * 60 - 420) {
			mainGraphics.drawImage(mapImage, 10 - mapWidth * 60 + 840, 10 - absPosY + 6 * MapValue.BLOCK_SIZE,
					realWidth, realHeight, this);
		}

		// 다른 유저 그리기
		for (int i = 0; i < size; i++) {
			UserPosInfo userPosInfo = userPosInfoVec.get(i);
			if (!userPosInfo.getId().equals(id)) {
				int posX = userPosInfo.getX();
				int posY = userPosInfo.getY();
				boolean isArmor = userPosInfo.isArmor();
				boolean isWeapon = userPosInfo.isWeapon();
				
				msg = userPosInfo.getMsg();
				if (msg != null && !msg.equals("")) {
					int line = msg.length() / 10;
					mainGraphics.setClip(0, 0, 2000, 2000);
					mainGraphics.setColor(new Color(50, 50, 50, 200));
					mainGraphics.fillRoundRect((posX - absPosX + relPosX), (posY - absPosY + relPosY) - 50 - 25 * (line + 1), 130, 25 * (line + 1),10,10);
					mainGraphics.setColor(Color.WHITE);
					mainGraphics.drawRoundRect((posX - absPosX + relPosX), (posY - absPosY + relPosY) - 50 - 25 * (line + 1), 130, 25 * (line + 1),10,10);
					for (int j = 0; j < line + 1; j++)
						if (j + 1 == line + 1)
							mainGraphics.drawString(msg.substring(j * 10, msg.length()), 5 + (posX - absPosX + relPosX),
									2 + (posY - absPosY + relPosY) - 60 - 25 * (line + 1) + 25 * (j + 1));
						else
							mainGraphics.drawString(msg.substring(j * 10, (j + 1) * 10), 5 + (posX - absPosX + relPosX),
									2 + (posY - absPosY + relPosY) - 60 - 25 * (line + 1) + 25 * (j + 1));
				}
				mainGraphics.setColor(c);
				
				if (userPosInfo.getMovingY() > 3) {
					mainGraphics.setClip(20 + posX - absPosX + relPosX, 20 + posY - absPosY + relPosY - 60, 45, 85);
					mainGraphics.drawImage(characterAction,
							20 + (posX - absPosX + relPosX) - 45 * userPosInfo.getMovingX(),
							20 + (posY - absPosY + relPosY) - 85 * userPosInfo.getMovingY() - 4 - 60, 180, 684, this);
					if(isArmor){
						mainGraphics.drawImage(cArmor,
								20 + (posX - absPosX + relPosX) - 45 * userPosInfo.getMovingX(),
								20 + (posY - absPosY + relPosY) - 85 * userPosInfo.getMovingY() - 4 - 60, 180, 684, this);
					}
				} else {
					mainGraphics.setClip(20 + posX - absPosX + relPosX, 20 + posY - absPosY + relPosY - 60, 38, 86);
					mainGraphics.drawImage(characterAction,
							20 + (posX - absPosX + relPosX) - 38 * userPosInfo.getMovingX(),
							20 + (posY - absPosY + relPosY) - 86 * userPosInfo.getMovingY() - 60, 180, 684, this);
					if(isArmor){
						mainGraphics.drawImage(cArmor,
								20 + (posX - absPosX + relPosX) - 38 * userPosInfo.getMovingX(),
								20 + (posY - absPosY + relPosY) - 86 * userPosInfo.getMovingY() - 60, 180, 684, this);
					}
				}

				if(userPosInfo.isWeapon()){
					mainGraphics.setClip(- 41  + posX - absPosX + relPosX,- 14 + posY - absPosY + relPosY - 60, 128, 116);
					mainGraphics.drawImage(sword,
							- 45 + (posX - absPosX + relPosX) - 128 * userPosInfo.getMovingX(),
							- 14 + (posY - absPosY + relPosY) - 116 * userPosInfo.getMovingY() - 60, 512, 928, this);
				}
			}
		}
		int size2 = monsterVec.size();
		// 몬스터 그리기
		for (int i = 0; i < size2; i++) {
			Monster monster = monsterVec.get(i);

			int posX = monster.getPosX();
			int posY = monster.getPosY();
			
			if(monster.getSkillY()>=0){
				System.out.println("스킬시전2");
				mainGraphics.setClip(0, 0, 1000, 1000);
				mainGraphics.drawImage(skill1, 20 + (posX - absPosX + relPosX) - 63 * monster.getMovingX(),
						20 + (posY - absPosY + relPosY) - 86 * monster.getMovingY() - 60, 252, 344, this);
				if(monster.getSkillY() == 0){
					new Thread(){
						int skill;
						@Override
						public void run(){
							while((skill = monster.getSkillY())>2){
								try {
									sleep(100);
									if(skill != monster.getSkillY())
										return;
									monster.setSkillY(monster.getSkillY()+1);
									repaint();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							monster.setSkillY(-1);
						}
					}.start();
				}
			}

			mainGraphics.setClip(20 + posX - absPosX + relPosX, 20 + posY - absPosY + relPosY - 60, 63, 86);
			mainGraphics.drawImage(squ, 20 + (posX - absPosX + relPosX) - 63 * monster.getMovingX(),
					20 + (posY - absPosY + relPosY) - 86 * monster.getMovingY() - 60, 252, 344, this);

		}

		// 메세지 그리기
		msg = userPosInfo.getMsg();
		if (msg != null && !msg.equals("")) {
			int line = msg.length() / 10;
			mainGraphics.setClip(0, 0, 2000, 2000);			
			mainGraphics.setColor(new Color(50, 50, 50, 200));
			mainGraphics.fillRoundRect((relPosX), (relPosY) - 50 - 25 * (line + 1), 130, 25 * (line + 1),10,10);
			mainGraphics.setColor(Color.WHITE);
			mainGraphics.drawRoundRect((relPosX), (relPosY) - 50 - 25 * (line + 1), 130, 25 * (line + 1),10,10);
			for (int i = 0; i < line + 1; i++)
				if (i + 1 == line + 1)
					mainGraphics.drawString(msg.substring(i * 10, msg.length()), 5 + (relPosX),
							2 + (relPosY) - 60 - 25 * (line + 1) + 25 * (i + 1));
				else
					mainGraphics.drawString(msg.substring(i * 10, (i + 1) * 10), 5 + (relPosX),
							2 + (relPosY) - 60 - 25 * (line + 1) + 25 * (i + 1));
		}
		mainGraphics.setColor(c);

		if (userPosInfo.getMovingY() > 3) {
			mainGraphics.setClip(13 + (relPosX), 20 + (relPosY) - 60, 45, 85);
			mainGraphics.drawImage(characterAction, 13 + (relPosX) - 45 * userPosInfo.getMovingX(),
					20 + (relPosY) - 85 * userPosInfo.getMovingY() - 4 - 60, 180, 684, this);
			if(userPosInfo.isArmor()){
				mainGraphics.drawImage(cArmor, 13 + (relPosX) - 45 * userPosInfo.getMovingX(),
						20 + (relPosY) - 85 * userPosInfo.getMovingY() - 4 - 60, 180, 684, this);
			}
			
		} else {
			mainGraphics.setClip(20 + (relPosX), 20 + (relPosY) - 60, 38, 86);
			mainGraphics.drawImage(characterAction, 20 + (relPosX) - 38 * userPosInfo.getMovingX(),
					20 + (relPosY) - 86 * userPosInfo.getMovingY() - 60, 180, 684, this);
			if(userPosInfo.isArmor()){
				mainGraphics.drawImage(cArmor, 20 + (relPosX) - 38 * userPosInfo.getMovingX(),
						20 + (relPosY) - 86 * userPosInfo.getMovingY() - 60, 180, 684, this);
			}
		}
		if(userPosInfo.isWeapon()){
			mainGraphics.setClip(- 41 +(relPosX),- 14 +(relPosY) -60, 128, 116);
			mainGraphics.drawImage(sword, - 45 +(relPosX) - 128 * userPosInfo.getMovingX(),
					- 14 +(relPosY) - 116 * userPosInfo.getMovingY() - 60, 512, 928, this);
		}

		mainGraphics.setClip(0, 0, FrameValue.WINDOW_WIDTH, FrameValue.WINDOW_HEIGHT);
		mainGraphics.drawImage(background2, 0, 0, 1040, 773, this);
		mainGraphics.drawImage(characterInfo, 820, 6, 214, 68, this);
		mainGraphics.drawImage(inputBox, 5, 720, 825, 44, this);
		mainGraphics.drawImage(inventory, 838, 81, 195, 557, this);
		mainGraphics.drawImage(characterStatus, 827, 628, 206, 133, this);

	}

	@Override
	public void paintComponent(Graphics g) {
		if (userPosInfo != null)
			doubleBuffering();
		g.drawImage(buffer, 0, 0, FrameValue.WINDOW_WIDTH, FrameValue.WINDOW_HEIGHT, this);
	}

	public String getId() {
		return id;
	}

	public Vector<UserPosInfo> getUserPosInfoVec() {
		return userPosInfoVec;
	}

	public UserPosInfo getUserPosInfo() {
		return userPosInfo;
	}

	public Thread getThread() {
		return th;
	}

	public GameMap getGm() {
		return gm;
	}

	public void setGm(GameMap gm) {
		this.gm = gm;
	}

	public int getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}

	public MyCharacterAnimationThread getMyCharacterAnimationThread() {
		return myCharacterAnimationThread;
	}

	public void setMyCharacterAnimationThread(MyCharacterAnimationThread myCharacterAnimationThread) {
		this.myCharacterAnimationThread = myCharacterAnimationThread;
	}

	public Vector<Monster> getMonsterVec() {
		return monsterVec;
	}

	public void setMonsterVec(Vector<Monster> monsterVec) {
		this.monsterVec = monsterVec;
	}

	public void setUserPosInfoVec(Vector<UserPosInfo> userPosInfoVec) {
		this.userPosInfoVec = userPosInfoVec;
	}

	public Monster[][] getMonsterMap() {
		return monsterMap;
	}

	public void setId(String id) {
		this.id = id;

		idLabel.setText(id);
	}

	public void setLevel(int level) {
		this.level = level;

		levelLabel.setText(level + "");
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;

		hpLabel.setText(hp + "");
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
		mpLabel.setText(mp + "");
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
		jobLabel.setText(job);

	}

	public void setExp(int exp) {
		this.exp = exp;
		expLabel.setText(exp+"");
	}
	
	public void setGold(int gold){
		this.gold = gold;
		goldLabel.setText(gold+"");
	}

	public int getExp() {
		return exp;
	}

	public int getLevel() {
		return level;
	}

	public void setItemVec(Vector<Item> itemVec) {
		this.itemVec = itemVec;
		int size = itemVec.size();
		for(int i = 0; i < size; i++){
			Item item = itemVec.get(i);
			ItemJLabel jl = itemJLabelVec.get((int)item.getPosition()-97);
			jl.setText(item.getName());
			jl.setItem(item);
		}
	}
	
	class ItemJLabel extends JLabel{
		private Item item;

		public Item getItem() {
			return item;
		}

		public void setItem(Item item) {
			this.item = item;
		}
		
	}

	public void putItem(Item item) {
		ItemJLabel jl = itemJLabelVec.get((int)item.getPosition()-97);
		jl.setItem(item);
		jl.setText(item.getName());
		
	}

	public JTextArea getChatArea() {
		return chatArea;
	}

	public void setChatArea(JTextArea chatArea) {
		this.chatArea = chatArea;
	}	

}
