/**
 *  Giao dien chinh cua tro choi 
 */
package view;

import java.io.InputStream;
import java.net.Socket;
import java.rmi.server.ExportException;
import java.util.Optional;
import java.io.*;
import java.net.*;

import controller.Controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.BoardState;
import model.ComputerPlayer;
import model.Player;
import model.HumanPlayer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class View implements EventHandler<ActionEvent> {
	public static final int WIDTH_BOARD = 24;
	public static final int HEIGHT_BOARD = 24;
	public static final int WIDTH_PANE = 2000;
	public static final int HEIGHT_PANE = 1000;
	private Button btnHuman;
	private Button btnComputer;
	private Button btnExit;
	private Button btnUndo;
	private Button btnLoad;
	private Button btnSave;
	private Button btnAbout;
	private Labeled timePlayer1, timePlayer2;
	private BoardState boardState ;
	private ComputerPlayer computer ;
	//client
	private int playerID;
	private int otherPlayer;
	private ClientSideConnection csc;
	private boolean buttonEnable;
	private String tempstr;
	private int count;
	//
	private HumanPlayer humanPlayer;
	//
	// lop dieu khien
	Controller controller;
	// mang quan co khi danh
	public Button[][] arrayButtonChess;
	// khung view
	public static Stage primaryStage;

	public View() {
		//
		ConnectToServer();
		//
	}
	//
	public Object inputname(){
		JFrame frame = new JFrame();
		Object result = JOptionPane.showInputDialog(frame, "Enter name:");
		return result;
	}

	public void start(Stage primaryStage) {
		try {
			View.primaryStage = primaryStage;
			arrayButtonChess = new Button[WIDTH_BOARD][HEIGHT_BOARD];
			boardState = new BoardState(WIDTH_BOARD, HEIGHT_BOARD);
			//
			humanPlayer = new HumanPlayer(boardState);
			//
			controller = new Controller();
			controller.setView(this);
			controller.setPlayer(humanPlayer);
			//
			BorderPane borderPane = new BorderPane();// t???o c??i borderpane
			BorderPane borderPaneLeft = new BorderPane();
			BorderPane borderPaneRight = new BorderPane();
			BorderPane borderPaneTop = new BorderPane();
			//t???o 2 rectangle ????? ch???a data
			if (playerID == 1){
				otherPlayer = 2;
				buttonEnable = false;

			}
			else{
				otherPlayer = 1;
				buttonEnable = true;
				updateTurn();
			}

			menu(borderPaneRight,1);
			menuGridPane(borderPaneTop);
			menu(borderPaneLeft,0);
			//
			GridPane root = new GridPane();
			Scene scene = new Scene(borderPane, WIDTH_PANE, HEIGHT_PANE);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			borderPane.setPadding(new Insets(0,10,0,10));
			//
			borderPane.setTop(borderPaneTop);
			borderPane.setCenter(root);
			borderPane.setLeft(borderPaneLeft);//ph???i x??a dc c??i borderright th?? c??i kia m???i v?? gi???a
			borderPane.setRight(borderPaneRight);
			//set b??n c??? ra gi???a
			BorderPane.setMargin(root,new Insets(10,60,0,220));
			BorderPane.setMargin(borderPaneLeft,new Insets(5,0,0,30));
			BorderPane.setMargin(borderPaneRight,new Insets(5,100,0,0));
			//
			// mac dinh player 1 di truoc
			//

			//
			controller.setPlayerFlag(1);
			controller.setTimePlayer(timePlayer1, timePlayer2);
			for (int i = 0; i < WIDTH_BOARD; i++) {
				for (int j = 0; j < HEIGHT_BOARD; j++) {
					Button button = new Button();
					//T???o button
					button.setPrefSize(43, 43);
					//set size cho button
					button.setAccessibleText(i + ";" + j);
					//set text cho button
					arrayButtonChess[i][j] = button;
					//g??n button v??o b??n c???, nh???ng ?? ????? nh???n
					root.add(button, j, i);
					int I = i;
					int J = j;
					button.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if (!controller.isEnd()) {
								if (boardState.boardArr[I][J] == 0) {
									buttonEnable = true;
									toggleButtons();
									csc.sendButtonClick(button);
									controller.play(button.getAccessibleText(), arrayButtonChess);
									updateTurn();
								}
							}
						}
					});
				}
			}
			primaryStage.setScene(scene);
			primaryStage.setTitle("Caro");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	Ph???n Client Multiplayer
	 */
	//data field line 51
	public void updateTurn(){
		Thread t = new Thread(new Runnable() {//bug ??? ??o???n n??y
			@Override
			public void run() {
				Platform.runLater(()-> {
					toggleButtons();
				});
				tempstr = csc.receiveButton();
				Platform.runLater(()-> {
					controller.play(tempstr, arrayButtonChess);
				});
				if (controller.isEnd()){
					csc.closeConnection();
				}
				buttonEnable = false;
				toggleButtons();
				//send button ?????n player kia
			}
		});
		t.start();
	}
	public void ConnectToServer(){
		csc = new ClientSideConnection();
	}
	//
	private class ClientSideConnection{
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		private String btnContent;
		//constructor
		public ClientSideConnection(){
			System.out.println("----Client----");
			try {
				socket = new Socket("193.168.1.1",60000);
				dataIn = new DataInputStream(socket.getInputStream());
				dataOut = new DataOutputStream(socket.getOutputStream());
				playerID = dataIn.readInt();//l???y player id t??? server
			}
			catch (IOException ex){
				System.out.println("IOException from constructor CSC");
			}
		}
		//method ????? g???i button ??i
		public void sendButtonClick(Button buttonClicked){
			try {
				dataOut.writeUTF(buttonClicked.getAccessibleText());
				dataOut.flush();
			}
			catch (IOException ex){
				System.out.println("IOException from sendButtonClick CSC");
			}
		}
		//methodd nh???n button
		public String receiveButton(){
			try{
				btnContent = dataIn.readUTF();
				System.out.println(btnContent);
			}
			catch (IOException ex){
				System.out.println("IOException from receiveButton CSC");
			}
			return btnContent;
		}
		//close connection
		public void closeConnection(){
			try {
				socket.close();
				System.out.println("Connection close");
			}
			catch (IOException ex){
				System.out.println("IOException from closeConnection CSC");
			}
		}
	}
	//method ????? disable button
	public void toggleButtons() {
		for (int i = 0;i<WIDTH_BOARD; i++) {
			for (int j=0;j<HEIGHT_BOARD;j++){
				arrayButtonChess[i][j].setDisable(buttonEnable);
				arrayButtonChess[i][j].setOpacity(1);
			}
		}
	}
	/*
	Menu Button
	 */
	private void menuGridPane(BorderPane pane){
		VBox box = new VBox();
		box.setSpacing(10);
		Class<?> clazz = this.getClass();
		//AnchorPane anchorPaneLogo = new AnchorPane();
		//AnchorPane gridPane = new AnchorPane();
		GridPane gridPane = new GridPane();

		// set logo
		/*InputStream input = clazz.getResourceAsStream("/image/Logo.jpg");
		Image image = new Image(input);
		ImageView imgView = new ImageView(image);
		imgView.setFitHeight(230);
		imgView.setFitWidth(260);
		AnchorPane.setTopAnchor(imgView, 10.0);
		AnchorPane.setLeftAnchor(imgView, 30.0);
		AnchorPane.setRightAnchor(imgView, 30.0);
		anchorPaneLogo.add(imgView);


		 */

		// Computer
		btnComputer = new Button("Ch??i v???i m??y");
		btnComputer.setId("btnMenu");
		btnComputer.setOnAction(this);

		gridPane.add(btnComputer,0,0);
		// Human
		btnHuman= new Button("Hai ng?????i ch??i");
		btnHuman.setId("btnMenu");
		btnHuman.setOnAction(this);

		gridPane.add(btnHuman,1,0);

		// Undo
		btnUndo = new Button("Quay l???i");
		btnUndo.setId("btnMenu");
		btnUndo.setOnAction(this);

		gridPane.add(btnUndo,2,0);
		// Save
		btnSave = new Button("L??u l???i");
		btnSave.setId("btnMenu");
		btnSave.setOnAction(this);

		gridPane.add(btnSave,3,0);
		// Load
		btnLoad = new Button("Load l???i");
		btnLoad.setId("btnMenu");
		btnLoad.setOnAction(this);

		gridPane.add(btnLoad,4,0);
		// About
		btnAbout = new Button("Th??ng tin");
		btnAbout.setId("btnMenu");
		btnAbout.setOnAction(this);

		gridPane.add(btnAbout,5,0);
		// exit
		btnExit = new Button("Tho??t");
		btnExit.setId("btnMenu");
		btnExit.setOnAction(this);

		gridPane.add(btnExit,6,0);
		//
		box.getChildren().add(gridPane);
		pane.setLeft(box);
	}
	private void menu(BorderPane pane,int n) {
		VBox box = new VBox();
		box.setSpacing(10);
		Class<?> clazz = this.getClass();
		//AnchorPane anchorPaneLogo = new AnchorPane();
		//AnchorPane gridPane = new AnchorPane();
		GridPane gridPane = new GridPane();

		// set logo
		/*InputStream input = clazz.getResourceAsStream("/image/Logo.jpg");
		Image image = new Image(input);
		ImageView imgView = new ImageView(image);
		imgView.setFitHeight(230);
		imgView.setFitWidth(260);
		AnchorPane.setTopAnchor(imgView, 10.0);
		AnchorPane.setLeftAnchor(imgView, 30.0);
		AnchorPane.setRightAnchor(imgView, 30.0);
		anchorPaneLogo.add(imgView);


		 */

		// Computer
		/*
		btnComputer = new Button("Ch??i v???i m??y");
		btnComputer.setId("btnMenu");
		btnComputer.setOnAction(this);

		gridPane.add(btnComputer,0,0);
		// Human
		btnHuman= new Button("Hai ng?????i ch??i");
		btnHuman.setId("btnMenu");
		btnHuman.setOnAction(this);

		gridPane.add(btnHuman,1,0);

		// Undo
		btnUndo = new Button("Quay l???i");
		btnUndo.setId("btnMenu");
		btnUndo.setOnAction(this);

		gridPane.add(btnUndo,2,0);
		// Save
		btnSave = new Button("L??u l???i");
		btnSave.setId("btnMenu");
		btnSave.setOnAction(this);

		gridPane.add(btnSave,3,0);
		// Load
		btnLoad = new Button("Load l???i");
		btnLoad.setId("btnMenu");
		btnLoad.setOnAction(this);

		gridPane.add(btnLoad,4,0);
		// About
		btnAbout = new Button("Th??ng tin");
		btnAbout.setId("btnMenu");
		btnAbout.setOnAction(this);

		gridPane.add(btnAbout,5,0);
		// exit
		btnExit = new Button("Tho??t");
		btnExit.setId("btnMenu");
		btnExit.setOnAction(this);

		gridPane.add(btnExit,6,0);
		//
		box.getChildren().add(gridPane);



		 */



		// Bottom

		GridPane gridPaneBottom = new GridPane();
		//
		Labeled namePlayer;
		if (n == 0){
			timePlayer1 = new Label("15");
			timePlayer1.setId("timeplayer");

			namePlayer = new Label("Player " + 1);
			gridPaneBottom.add(timePlayer1, 0, 1);
		}
		else {
			timePlayer2 = new Label("15");
			timePlayer2.setId("timeplayer");

			namePlayer = new Label("Player " + 2);
			gridPaneBottom.add(timePlayer2, 0, 1);

		}
		//
		namePlayer.setId("nameplayer");//????? l???y m??u ch???
		//Labeled namePlayer2 = new Label("Player 2");
		//namePlayer2.setId("nameplayer");
		gridPaneBottom.add(namePlayer, 0, 0);
		//gridPaneBottom.add(namePlayer2, 1, 0);
		//box.getChildren().add(gridPaneBottom);
		box.getChildren().add(gridPaneBottom);
		//
		pane.setCenter(box);
	}


	@Override
	public void handle(ActionEvent e) {
		if (e.getSource() == btnExit) {
			primaryStage.close();
		}
		if (e.getSource() == btnHuman) {
			replayHuman();
		}
		if (e.getSource() == btnComputer) {
			replayComputer();
		}
		if (e.getSource() == btnUndo) {
			controller.undo(arrayButtonChess);
		}
		if (e.getSource() == btnLoad) {
			controller.open(arrayButtonChess);
		}
		if (e.getSource() == btnSave) {
			controller.save();
		}
		if (e.getSource() == btnAbout) {
			aboutUs();
		}
	}


	// che do dau voi may
	public void replayComputer() {

		controller.setEnd(false);
		controller.setTimePlayer(timePlayer1, timePlayer2);
		controller.setPlayer(new ComputerPlayer(new BoardState(WIDTH_BOARD, HEIGHT_BOARD)));
		controller.reset(arrayButtonChess);
		gameMode();

	}
	// che do 2 nguoi choi
	public void replayHuman() {
		controller.setEnd(false);
		controller.setTimePlayer(timePlayer1, timePlayer2);
		controller.setPlayer(new HumanPlayer(new BoardState(WIDTH_BOARD, HEIGHT_BOARD)));
		controller.setPlayerFlag(1);
		controller.reset(arrayButtonChess);

	}
	// thong tin ve nhom phat trien
	public void aboutUs() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About us");
		alert.setHeaderText("");
		alert.setContentText("");
		alert.showAndWait();
	}
	// xet xem ai di truoc
	public void gameMode() {
		Alert gameMode = new Alert(AlertType.CONFIRMATION);
		gameMode.setTitle("Ch???n ng?????i ch??i tr?????c");
		gameMode.setHeaderText("B???n c?? mu???n ch??i tr?????c kh??ng ?");
		Optional<ButtonType> result = gameMode.showAndWait();
		if(result.get() == ButtonType.CANCEL) {
			controller.danhCo(WIDTH_BOARD/2 - 1, HEIGHT_BOARD/2,2, arrayButtonChess);
			int[] AScore = {0,3,28,256,2308}; // 0,9,54,162,1458
			int[] DScore = {0,1,9,85,769};   // 0,3,27,99,729
			computer.setAScore(AScore);
			computer.setDScore(DScore);
			controller.setPlayerFlag(1);
		}
		else {
			controller.setPlayerFlag(1);
		}
	}
}
