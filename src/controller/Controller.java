/**
 * Lop dieu khien chinh 
 */
package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Timer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.BoardState;
import model.ComputerPlayer;
import model.Player;
import model.HumanPlayer;
import model.Point;
import model.TaskTimer;
import view.View;

public class Controller implements IController {
	public View view;
	private Player player; //object player
	private Stack<Point> stack; // ngan xep luu cac nuoc da di
	private Class<?> classImg ; //  lấy ảnh quân cờ
	private InputStream o; //để lưu link ảnh của O
	private InputStream x; // để lưu link ảnh của X
	private Image imageO; // để chứa ảnh của O
	private Image imageX; // để chứa ảnh cùa X
	private boolean end;
	private int tongNuocDi;
	private String playerWin;

	public Controller() {
		getComponents();
	}

	private void getComponents() {
		end = false;
		tongNuocDi = 0;
		playerWin = "";
		stack = new Stack<>();
		classImg = this.getClass();
		o = classImg.getResourceAsStream("/image/O_picture.png"); // để lấy link ảnh của O cho vào input stream
		x = classImg.getResourceAsStream("/image/X_picture.png"); //  để lấy link ảnh của O cho vào input stream
		imageO = new Image(o); //lấy hình ảnh của O
		imageX = new Image(x); //lấy hình ảnh của X
	}

	@Override
	public Point AI(int player) {
		return this.player.movePoint(player);
	}// tra toa do cua nguoi choi va bot sẽ tính toán để đi nc tiếp theo

	@Override
	public int getPlayerFlag() {
		return player.getPlayerFlag();
	} //getter tra ve luot nguoi choi

	@Override
	public void setPlayerFlag(int playerFlag) {
		player.setPlayerFlag(playerFlag);
	} // set den luot nguoi choi

	@Override
	public BoardState getBoardState() {
		return player.getBoardState();
	}// Kiem tra chien thang

	@Override
	public int checkEnd(int x, int y) {// kiểm tra kết thúc
		return player.getBoardState().checkEnd(x, y);
	}


	@Override
	public boolean isEnd() {
		return end;
	} // end game --> View

	@Override
	public void play(String buttonStr, Button[][] a) { //dùng trong View class
		StringTokenizer tokenizer = new StringTokenizer(buttonStr, ";");
		int x = Integer.parseInt(tokenizer.nextToken());//lấy số i với j của mảng ra
		int y = Integer.parseInt(tokenizer.nextToken());
		//Lay text ra từ button c và đổi thành Integer
		if (player instanceof HumanPlayer) { //NẾu player là người chơi cả 2
			getBoardState();//gọi boardstate để khởi tạo boardArr
			if (getPlayerFlag() == 1 && BoardState.boardArr[x][y] == 0) {//Nếu là lượt của người chơi 1 và không có game đang lưu
				danhCo(x, y, 1, a);//người thứ 1 đánh //??
				setPlayerFlag(2);//đến lượt người thứ 2
			} 	else {
				getBoardState();
				if (getPlayerFlag() == 2 && BoardState.boardArr[x][y] == 0) {//Nếu là lượt của người chơi 2	 và không có game đang lưu
					danhCo(x, y, 2, a);//luot nguoi thu 2 đánh
					setPlayerFlag(1);//đén lượt người thứ 1
				}
			}
			//getplayerflag để biết đuọc đến lượt ai đi
		} else {//Nếu player là computer
			if (getPlayerFlag() == 1) {//Nếu người chơi 1 di trc
				if (getBoardState().getPosition(x, y) == 0) {//nếu là game mới
					danhCo(x, y, 1, a);//người chơi 1 đi
					setPlayerFlag(2);//đến lượt ng chơi 2
				}
			}
			if (getPlayerFlag()== 2) {
				Point p = AI(2);//Máy tính toán nước đi
				danhCo(p.x, p.y, 2, a);//máy đi
				setPlayerFlag(1);//chuyển sang lượt người chơi
			}
		}
		if (end) {//nếu end game
			if (player instanceof ComputerPlayer && playerWin.equals("2")) {//Nếu player là computer
				playerWin = "Computer";
			}
			timer1.cancel();//dừng thời gian player1 và 2
			timer2.cancel();

			dialog("Player " + playerWin + " won!");
			return;
		}
		runTimer(getPlayerFlag());//???
	}


	public void danhCo(int x, int y, int player, Button[][] arrayButtonChess) {// đua vô bàn cờ, tọa độ độ của nước đã đánh
		getBoardState().setPosition(x, y, player);//set tọa dộ trên bàn cờ và người chơi nào đang sở hữu nước đó
		if (player == 1) {
			arrayButtonChess[x][y].setGraphic(new ImageView(imageX));//set ô tọa độ (x,y) đó trên bàn cờ là hình X
			Point point = new Point(x, y);//tạo point x,y
			point.setPlayer(1);//đánh dấu nc đó là của ng chơi 1
			stack.push(point);//tạo point set player để lưu nc đi vào stack
			tongNuocDi++;
		} else {
			arrayButtonChess[x][y].setGraphic(new ImageView(imageO));//set ô tọa độ (x,y) đó trên bàn cờ là hình O
			Point point = new Point(x, y);//tạo point x,y
			point.setPlayer(2);//đánh dấu nc đó là của ng chơi 1
			stack.push(point);//lưu nc đi vào stack
			tongNuocDi++;
		}
		if (getBoardState().checkEnd(x, y) == player) {//để check game end hay chưa
			playerWin = player + "";
			end = true;
		}
		if (tongNuocDi == (getBoardState().height * getBoardState().width)) {//NẾu hết dg đi thì player 2 win
			playerWin = 2 + "";
			end = true;
		}

	}

	void print(int[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				System.out.print(a[i][j]);
			}
			System.out.println();
		}
	}
	//lưu trò chơi
	@Override
	public void save() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Luu man choi");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("DATA", "*.dat"),
				new FileChooser.ExtensionFilter("All Images", "*.*"));
		File file = fileChooser.showSaveDialog(View.primaryStage);
		if (file != null) {//nếu file đã ghi từ trước thì tiếp tục ghi tiếp
			ghiFile(file);
		}
	}
	// ghi file
	public void ghiFile(File file) {
		try {
			PrintStream printStream = new PrintStream(file);
			while (!stack.isEmpty()) {
				printStream.println(stack.pop().toString());
			}
			printStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// mở lại màn chơi
	@Override
	public void open(Button[][] arrayButtonChess) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Mo man choi");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("DATA", "*.dat"),
				new FileChooser.ExtensionFilter("All Images", "*.*"));
		File file = fileChooser.showOpenDialog(View.primaryStage);
		if (file != null) {
			load(file);
			reset(arrayButtonChess);

			stack = new Stack<>();//lấy nc đi tứ stack ra
			while (!queue.isEmpty()) {
				Point point = queue.poll();
				stack.push(point);
				danhCo(point.x, point.y, point.player, arrayButtonChess);
				//load lại từng nước bỏ vô stack
			}

		}

	}
	Queue<Point> queue;
	public boolean load(File file) {
		if (file != null) {
			queue = new LinkedList<>();
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = bufferedReader.readLine()) != null) { // doc theo tung dong
					StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
					int x = Integer.parseInt(stringTokenizer.nextToken());
					int y = Integer.parseInt(stringTokenizer.nextToken());
					int player = Integer.parseInt(stringTokenizer.nextToken());
					Point point = new Point(x, y);
					point.setPlayer(player);
					queue.add(point);
				}
				bufferedReader.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	// quay lại 1 nuoc co
	@Override
	public void undo(Button[][] arrayButtonChess) {
		if (!stack.isEmpty()) {//nếu trong stack không trống
			tongNuocDi--;//tổng nước đi trừ đi 1
			Point point = stack.pop();//lấy 1 nc ra từ stack
			getBoardState();//truy cập boardstate
			BoardState.boardArr[point.x][point.y] = 0;//gán x,y trên bảng thành 0
			arrayButtonChess[point.x][point.y].setGraphic(null);//xóa ảnh trên bảng caro
		}
	}
	@Override
	public void setPlayer(Player player) {//dùng dể set player là computer hay người chơi
		this.player = player;
	}

	public EventHandler<ActionEvent> action(String action) {
		return null;
	}

	EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {//gán các nút cho evennt của nó
		@Override
		public void handle(ActionEvent e) {//method handle trong View

		}
	};

	public void dialog(String title) {//dùng trong method play, dùng để hiện bảng kết thuc`1 trò chơi
		Alert alert = new Alert(AlertType.CONFIRMATION);//type alert là loại confirm
		alert.setTitle("Trò chơi kết thúc");
		alert.setHeaderText(title);
		alert.setContentText("Bạn có muốn chơi lại");

		Optional<ButtonType> result = alert.showAndWait();//Shows the dialog and waits for the user response.nhận input là button
		if (result.get() == ButtonType.OK) {
			if (getPlayer() instanceof HumanPlayer) {//Nếu 2 player là người chơi
				view.replayHuman();//tạo bàn cờ mới với 2 người chơi
			} else {
				view.replayComputer();//tạo bàn cờ mới với máy chơi
			}
		} else {
			// su dung khi chon khong hoac dong hoi thoai
		}
	}

	@Override
	public void setView(View view) {//đưa class view vào
		this.view = view;
	}

	@Override
	public void setEnd(boolean end) {//để set kết thúc
		this.end = end;
	}

	public Player getPlayer() {//lấy phayer hiện tại
		return player;
	}

	@Override
	public void reset(Button[][] arrayButtonChess) {//để reset game
		tongNuocDi = 0;
		timer1.cancel();//tắt thời gian
		timer2.cancel();//tắt thời gian
		timePlayer1.setText("15");
		timePlayer2.setText("15");
		getBoardState().resetBoard();//reset lại bàn cờ
		for (int i = 0; i < arrayButtonChess.length; i++) {
			for (int j = 0; j < arrayButtonChess[i].length; j++) {
				arrayButtonChess[i][j].setGraphic(null);//reset lại hình ảnh trên bàn cờ xóa hết X O
			}
		}
	}

	Labeled timePlayer1, timePlayer2;

	@Override
	public void setTimePlayer(Labeled timePlayer1, Labeled timePlayer2) {//Dùng để chỉnh lại thời gian của player
		//dùng trong start stage, replayhuman,replaycomputer
		this.timePlayer1 = timePlayer1;
		this.timePlayer2 = timePlayer2;
	}
	Timer timer1 = new Timer();
	Timer timer2 = new Timer();
	@Override
	public void runTimer(int player) {//dùng để khởi tạo bộ đếm thời gian mới
		if(end){
			timer1.cancel();//NẾu end thì canel bộ đếm thời gian
			timer2.cancel();
		}else{
			timer1.cancel();
			timer2.cancel();
			TaskTimer task1 = new TaskTimer(timePlayer1);
			TaskTimer task2 = new TaskTimer(timePlayer2);
			task1.setController(this);
			task2.setController(this);
			if (player == 1) {//cứ mỗi lần đánh sẽ khởi tạo lại bội dếm thời gian
				timer2.cancel();//1 đánh thì 2 ngừng
				timer1 = new Timer();
				timer1.schedule(task1, 0, 1000);
			} else {
				timer1.cancel();//2 đánh thì 1 ngừng
				timer2 = new Timer();
				timer2.schedule(task2, 0, 1000);
			}
		}
	}
}
