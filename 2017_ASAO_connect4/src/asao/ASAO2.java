/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asao;

import java.util.Date;
import uci.acm.challenge.Player.Cell;
import uci.acm.challenge.Player.DemoWithIA;
import uci.acm.challenge.Player.Player;
import uci.acm.challenge.Player.PlayerDemo;
import uci.acm.challenge.Player.PlayerMap;
import uci.acm.challenge.Player.University;

/**
 *
 * @author FrankS
 */

public class ASAO2 extends Player {

    public static void main(String[] args) throws Exception {
        uci.acm.challenge.Player.Players.registTactic(PlayerDemo.class);
        uci.acm.challenge.Player.Players.registTactic(DemoWithIA.class);
        uci.acm.challenge.Player.Players.registTactic(ASAO.class);
        uci.acm.challenge.Player.Players.registTactic(ASAO2.class);
        uci.acm.challenge.Run.start();
    }

    public int player;
    public int FirstToPlayer;
    public int BIG_POSITIVE_NUMBER;
    public int BIG_NEGATIVE_NUMBER;
    public int MAX_DEPTH;
    public int ROWS, COLUMNS;
    public int boardPiece, enemyBoardPiece, ownBoardPieceValue;
    public long OPERATIONS;
    public int COTA;
    public int Iteration;
    
    public ASAO2() {
        FirstToPlayer = 0;
        Iteration = 0;
        COTA = 2000;
        BIG_POSITIVE_NUMBER = (int) 1e9;
        BIG_NEGATIVE_NUMBER = -BIG_POSITIVE_NUMBER;
    }

    @Override
    public String MyName() {
        return "A-SAO2";
    }

    @Override
    public University MyUniversity() {
        return University.UO_SAM;
    }
    
    public int getBoardPieceValue(int bPiece) {
        return bPiece == 0 ? 0 : bPiece == boardPiece ? 1 : -1;
    }
    
    public class vp {
        int count;
        ii []posibleMove;
        public vp (){
            count = 0;
            posibleMove = new ii[ROWS*COLUMNS];
        }
        public vp(vp p){
            count = p.count;
            for(int i=0; i<count; i++)
                posibleMove[i] = new ii(p.posibleMove[i]);
        }
        public void push_back(ii p) {
            posibleMove[count++] = new ii(p);
        }
        public void clear(){
            count = 0;
        }
    }
    
    public boolean validRow(int row){
        return row >= 0 && row < ROWS;
    }

    public boolean validColumn(int column){
        return column >= 0 && column < COLUMNS;
    }

    public int pushIn (Board state, int column, int bPiece){
        if (state.map[0][column] != 0 || !validColumn(column)) {
            return -1;
        }
        boolean isColumnEverFilled = false;
        int row = 0;
        for (int i = 0; i < ROWS - 1; i++) {
            if (state.map[i + 1][column] != 0) {
                isColumnEverFilled = true;
                row = i;
                break;
            }
        }
        if (!isColumnEverFilled) {
            row = ROWS - 1;
        }
        state.map[row][column] = bPiece;
        return row;
    }

    public void removeLast (Board state, int column){
        if (state.map[ROWS - 1][column] == 0 || !validColumn(column))
            return;
        for (int i = 0; i < ROWS; i++) {
            if(state.map[i][column] != 0){
                state.map[i][column] = 0;
                break;
            }
        }
    }

    public void insertIn (Board state, int row, int column, int bPiece){
        if (state.map[0][column] != 0 || !validRow(row) || !validColumn(column))
            return;
        for(int i=0; i < row; i++)
            state.map[i][column] = state.map[i+1][column];
        state.map[row][column] = bPiece;
    }

    public void quit (Board state, int row, int column, int bPiece){
        if (state.map[row][column] != bPiece || !validRow(row) || !validColumn(column))
            return;
        for(int i=row; i > 0; i--)
            state.map[i][column] = state.map[i-1][column];
        state.map[0][column] = 0;
    }

    public ii choose(vp choice) {
        if(choice.count == 0)
            return new ii(-1, -1);
        else 
            return choice.posibleMove[(int) Math.floor(Math.random() * choice.count)];
    }

    public boolean canPlay(Board state, int column) {
        if (state.map[0][column] != 0 || !validColumn(column))
            return false;
        return true;
    }

    public boolean canPlay5(Board state, int row, int column, int bPiece) {
        if (state.map[row][column] != bPiece || !validRow(row) || !validColumn(column))
            return false;
        return true;
    }
    
    public ii getStateValue(Board state) {
        int winnerBoardPiece = 0;
        int chainValue = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                int tempRight = 0, tempBottom = 0, tempBottomRight = 0, tempTopRight = 0;
                for (int k = 0; k <= 3; k++) {
                    if (j + k < COLUMNS) {
                        tempRight += getBoardPieceValue(state.map[i][j + k]);
                    }
                    if (i + k < ROWS) {
                        tempBottom += getBoardPieceValue(state.map[i + k][j]);
                    }
                    if (i + k < ROWS && j + k < COLUMNS) {
                        tempBottomRight += getBoardPieceValue(state.map[i + k][j + k]);
                    }
                    if (i - k >= 0 && j + k < COLUMNS) {
                        tempTopRight += getBoardPieceValue(state.map[i - k][j + k]);
                    }
                }
                chainValue += tempRight * tempRight * tempRight;
                chainValue += tempBottom * tempBottom * tempBottom;
                chainValue += tempBottomRight * tempBottomRight * tempBottomRight;
                chainValue += tempTopRight * tempTopRight * tempTopRight;
                if (Math.abs(tempRight) == 4) {
                    winnerBoardPiece = tempRight > 0 ? boardPiece : enemyBoardPiece;
                }
                else if (Math.abs(tempBottom) == 4) {
                    winnerBoardPiece = tempBottom > 0 ? boardPiece : enemyBoardPiece;
                }
                else if (Math.abs(tempBottomRight) == 4) {
                    winnerBoardPiece = tempBottomRight > 0 ? boardPiece : enemyBoardPiece;
                }
                else if (Math.abs(tempTopRight) == 4) {
                    winnerBoardPiece = tempTopRight > 0 ? boardPiece : enemyBoardPiece;
                }
            }
        }
        return new ii(winnerBoardPiece, chainValue);
    }
    
    public int transformValues(int returnValue, int winnerBoardPiece, int depth) {
        boolean isWon = winnerBoardPiece == boardPiece;
        boolean isLost = winnerBoardPiece == enemyBoardPiece;
        if (isWon) {
            returnValue = BIG_POSITIVE_NUMBER - 100;
            returnValue -= depth * depth;
        }
        else if (isLost) {
            returnValue = BIG_NEGATIVE_NUMBER + 100;
            returnValue += depth * depth;
        }
        return returnValue;
    }
    
    public ip getMove(Board state, int depth, int alpha, int beta, int t) {
        ii stateValue = getStateValue(state);
        boolean isWon = stateValue.row == boardPiece;
        boolean isLost = stateValue.row == enemyBoardPiece;
        if (depth >= MAX_DEPTH || isWon || isLost) {
            return new ip(transformValues(stateValue.col * ownBoardPieceValue, stateValue.row, depth), new ii(-1, -1));
        }
        return depth % 2 == 0
               ? minState(new Board(state), depth + 1, alpha, beta, t + 1)
               : maxState(new Board(state), depth + 1, alpha, beta, t + 1);
    }
    
    public ip maxState(Board state, int depth, int alpha, int beta, int t) {
        int value = BIG_NEGATIVE_NUMBER;
        vp moveQueue = new vp();
        if(t%10==0 || (t+1)%10==0) {
            for(int row = 0; row < ROWS; row++){
                for (int column = 0; column < COLUMNS; column++) {
                    if (canPlay5(state, row, column, boardPiece)) {
                        quit(state, row, column, boardPiece);
                        ip _b = getMove(new Board(state), depth, alpha, beta, t);
                        insertIn(state, row, column, boardPiece);
                        int nextValue = _b.value;
                        if (nextValue > value) {
                            value = nextValue;
                            moveQueue.clear();
                            moveQueue.push_back(new ii(row,column));
                        }
                        else if (nextValue == value) {
                            moveQueue.push_back(new ii(row,column));
                        }
                        if (value > beta) {
                            return new ip(value, choose(moveQueue));
                        }
                        alpha = Math.max(alpha, value);
                    }
                }
            }
        }
        else {
            for (int column = 0; column < COLUMNS; column++) {
                if (canPlay(state, column)) {
                    pushIn(state, column, boardPiece);
                    ip _b = getMove(new Board(state), depth, alpha, beta, t);
                    removeLast(state, column);
                    int nextValue = _b.value;
                    if (nextValue > value) {
                        value = nextValue;
                        moveQueue.clear();
                        moveQueue.push_back(new ii(-1,column));
                    }
                    else if (nextValue == value) {
                        moveQueue.push_back(new ii(-1,column));
                    }
                    if (value > beta) {
                        return new ip(value, choose(moveQueue));
                    }
                    alpha = Math.max(alpha, value);
                }
            }
        }
        return new ip(value, choose(moveQueue));
    }
    
    public ip minState(Board state, int depth, int alpha, int beta, int t) {
        int value = BIG_POSITIVE_NUMBER;
        vp moveQueue = new vp();
        if(t%10==0 || (t+1)%10==0) {
            for(int row = 0; row < ROWS; row++){
                for (int column = 0; column < COLUMNS; column++) {
                    if (canPlay5(state, row, column, enemyBoardPiece)) {
                        quit(state, row, column, enemyBoardPiece);
                        ip _b = getMove(new Board(state), depth, alpha, beta, t);
                        insertIn(state, row, column, enemyBoardPiece);
                        int nextValue = _b.value;
                        if (nextValue < value) {
                            value = nextValue;
                            moveQueue.clear();
                            moveQueue.push_back(new ii(row,column));
                        }
                        else if (nextValue == value) {
                            moveQueue.push_back(new ii(row,column));
                        }
                        if (value < alpha) {
                            return new ip(value, choose(moveQueue));
                        }
                        beta = Math.min(beta, value);
                    }
                }
            }
        }
        else {
            for (int column = 0; column < COLUMNS; column++) {
                if (canPlay(state, column)) {
                    pushIn(state, column, enemyBoardPiece);
                    ip _b = getMove(new Board(state), depth, alpha, beta, t);
                    removeLast(state, column);
                    int nextValue = _b.value;
                    if (nextValue < value) {
                        value = nextValue;
                        moveQueue.clear();
                        moveQueue.push_back(new ii(-1,column));
                    }
                    else if (nextValue == value) {
                        moveQueue.push_back(new ii(-1,column));
                    }
                    if (value < alpha) {
                        return new ip(value, choose(moveQueue));
                    }
                    beta = Math.min(beta, value);
                }
            }
        }
        return new ip(value, choose(moveQueue));
    }
    
    public ip getAction(Board state, int t) {
        ip action = maxState(new Board(state), 0, BIG_NEGATIVE_NUMBER, BIG_POSITIVE_NUMBER, t);
        /*if(action.move.row == -1){
            System.out.print("AI " + boardPiece + " in move " + t + " can choose column " + action.move.row + " " + action.move.col + " with value of " + action.value);
        }
        else {
            System.out.print("AI " + boardPiece + " in move " + t + " can eliminate " + action.move.row + " " + action.move.col + " with value of " + action.value);
        }*/
        return action;
    }
    
    public class ii {
        int row;
        int col;
        public ii(int r, int c){
            row = r;
            col = c;
        }
        public ii(ii p){
            row = p.row;
            col = p.col;
        }
    }
    
    public class ip {
        int value;
        ii move;
        public ip(int v, ii m){
            value = v;
            move = m;
        }
        public ip(ip p){
            value = p.value;
            move = p.move;
        }
    }
    
    public class Board {
        int [][]map;
        public Board () {
            map = new int[ROWS][COLUMNS];
            for (int i = 0; i < ROWS; i++) 
                for (int j = 0; j < COLUMNS; j++) 
                    map[i][j] = 0;
        }
        public Board (Board p) {
            map = new int[ROWS][COLUMNS];
            for (int i = 0; i < ROWS; i++)
                for (int j = 0; j < COLUMNS; j++)
                    map[i][j] = p.map[i][j];
        }
    }

    public int enemy(int player) {
        return player == 1 ? 2 : 1;
    }
    
    @Override
    public int iterate(PlayerMap map) throws Exception {
        if(FirstToPlayer == 0){
            ROWS = map.CountRows();
            COLUMNS = map.CountColumns();
            for(int i=0; i < map.CountColumns(); i++){
                if(map.getCell(map.CountRows()-1, i) == Cell.Oponent){
                    FirstToPlayer = 2;
                    break;
                }
            }
            if(FirstToPlayer == 2){
                player = 2;
                Iteration = 2;
            }
            else {
                player = 1;
                Iteration = 1;
            }
            
            boardPiece = player;
            enemyBoardPiece = enemy(player);
            ownBoardPieceValue = getBoardPieceValue(boardPiece);
            
            if(FirstToPlayer == 0) {
                FirstToPlayer = 1;
                return map.CountColumns() / 2;
            }
        }
        else Iteration += 2;
        Board T = new Board();
        for(int i=0; i<ROWS; i++)
            for(int j=0; j<COLUMNS; j++)
                T.map[i][j] = (map.getCell(i, j) == Cell.Free) ? 0 : (map.getCell(i, j) == Cell.My) ? player : enemy(player);
                
        OPERATIONS = 0;
        ip res;
        ii bestPlay = new ii(0,0);
        int bestMove = 0;
        for(int i=1; ;i++){
            MAX_DEPTH = i;
            long __time = (new Date()).getTime();
            res = getAction(new Board(T), Iteration);
            OPERATIONS += (new Date()).getTime() - __time;
            //System.out.println(". OPERATIONS: " + OPERATIONS + ", DEPTH: " + MAX_DEPTH);
            bestPlay = res.move;
            if(OPERATIONS > COTA)
                break;
        }
        bestMove = bestPlay.col;
        //System.out.println("AI " + boardPiece + " choose column " + bestPlay.row + " " + bestPlay.col);
        return bestMove;
    }
    
    @Override
    public int[] iterate5(PlayerMap map) throws Exception {
        Iteration += 2;
        Board T = new Board();
        for(int i=0; i<ROWS; i++)
            for(int j=0; j<COLUMNS; j++)
                T.map[i][j] = (map.getCell(i, j) == Cell.Free) ? 0 : (map.getCell(i, j) == Cell.My) ? player : enemy(player);
        
        OPERATIONS = 0;
        ip res;
        ii bestPlay = new ii(-1,-1);
        int []bestMove = new int[2];
        for(int i=1; ;i++){
            MAX_DEPTH = i;
            long __time = (new Date()).getTime();
            res = getAction(new Board(T), Iteration);
            OPERATIONS += (new Date()).getTime() - __time;
            //System.out.println(". OPERATIONS: " + OPERATIONS + ", DEPTH: " + MAX_DEPTH);
            bestPlay = res.move;
            if(OPERATIONS > COTA)
                break;
        }
        bestMove[0] = bestPlay.row;
        bestMove[1] = bestPlay.col;
        //System.out.println("AI " + boardPiece + " eliminate " + bestPlay.row + " " + bestPlay.col);
        return bestMove;
    } 
}

