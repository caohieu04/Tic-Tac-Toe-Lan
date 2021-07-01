/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;
public class BoardState {
	// board
	public static int[][] boardArr;
	public int width;
	public int height;

	public BoardState(int width, int height) {
		boardArr = new int[width][height];
		this.height = height;
		this.width = width;
	}

	public void resetBoard(){
		boardArr = new int[width][height];
	}
	//win cond
	public int checkEnd(int row, int col) {
		int r = 0, c = 0;
		int i;
		boolean human, pc;
		// ngang
		while (c < width - 4) {
			human = true;
			pc = true;
			for (i = 0; i < 5; i++) {
				if (boardArr[row][c + i] != 1)
					human = false;
				if (boardArr[row][c + i] != 2)
					pc = false;
			}
			if (human) {
				if(widthBlock(c, row, 2))
					return 1;}
			if (pc)
			{
				if(widthBlock(c, row, 1))
					return 2;}
			c++;
		}

		//doc
		while (r < height - 4) {
			human = true;
			pc = true;
			for (i = 0; i < 5; i++) {
				if (boardArr[r + i][col] != 1)
					human = false;
				if (boardArr[r + i][col] != 2)
					pc = false;
			}
			if (human) {
				if( heightBlock(r, col, 2))
					return 1;}
			if (pc) {
				if(heightBlock(r, col, 1))
					return 2;}
			r++;
		}

		// \
		r = row;
		c = col;
		while (r > 0 && c > 0) {
			r--;
			c--;
		}
		while (r < height - 4 && c < width - 4) {
			human = true;
			pc = true;
			for (i = 0; i < 5; i++) {
				if (boardArr[r + i][c + i] != 1)
					human = false;
				if (boardArr[r + i][c + i] != 2)
					pc = false;
			}
			if (human) {
				if (downTiltBlock(r, c, 2))

					return 1;
			}
			if (pc) {
				if (downTiltBlock(r, c, 1))
					return 2;
			}
			r++;
			c++;
		}

		// /
		r = row;
		c = col;
		while (r < height - 1 && c > 0) {
			r++;
			c--;
		}

		while (r >= 4 && c < height - 4) {
			human = true;
			pc = true;
			for (i = 0; i < 5; i++) {
				if (boardArr[r - i][c + i] != 1)
					human = false;
				if (boardArr[r - i][c + i] != 2)
					pc = false;
			}
			if (human) {
				if (upTiltBlock(r, c, 2))
					return 1;
			}
			if (pc) {
				if (upTiltBlock(r, c, 1))
					return 2;
			}
			r--;
			c++;
		}
		return 0;
	}
	public int getPosition(int x, int y) {
		return boardArr[x][y];
	}

	public void setPosition(int x, int y, int player) {
		boardArr[x][y] = player;
	}
	private boolean widthBlock(int c, int row, int b){
		/*return ((a != 0 && boardArr[row][a - 1] != b) || boardArr[row][a + 5] != b);
		switch a:{
		case 4: {
			if (getPosition(row, a + 1) == b) return false;
			break;
		}
			case height:{
				if (getPosition(row, a - 5) == b) return false;
				break;
			}
			default {

			}
		}*/
		if(c == 0) return true;
		else if(c + 5 == width) return true;
		else return boardArr[row][c + 5] != b || boardArr[row][c + (-1)] != b;
	}
	private boolean heightBlock(int r, int col, int b){
		if(r == 0) return true;
		else if(r + 5 == height) return true;
		else return boardArr[r +(- 1)][col] != b || boardArr[r + 5][col] != b;
	}

	private boolean downTiltBlock(int r, int c, int b){
		if((r + 5 == height && c + 5 == width) || ( r == 0 && c == 0)) return true;
		if(r == 0 || c == 0) return true;
		else if(r + 5 == height || c + 5 == width ) return true;
		else return boardArr[r + (-1)][c + (- 1)]  != b || boardArr[r + 5][c + 5] != b;
	}


	private boolean upTiltBlock(int r, int c, int b){
		if((r == 4 && c == 0) || (r == height && c + 5 == width)) return true;
		if(r  == height || c  == 0) return true;
		else if(r  == 4 || c + 5 == width ) return true;
		else {
			return boardArr[r + 1][c + (-1)]  != b || boardArr[r - 5][c + 5] != b;
		}
	}
}
