import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import uci.challenge.player.Numbers;
import uci.challenge.player.Player;
import uci.challenge.player.PlayerPiece;
import uci.challenge.player.PlayerTable;
import uci.challenge.player.Position;
import uci.challenge.player.University;
import uci.challenge.player.actions.GeneralAction;
import uci.challenge.player.actions.Play;
import uci.challenge.player.actions.Wait;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Frank
 */
public class NOT_CONNECTED_TEST implements Player {

private Vector<Domino> MY_PIECES = new Vector<>();
private Vector<Domino> OP_PIECES = new Vector<>();
private int MY_POINTS = 0;
private int OP_POINTS = 0;
private Board BOARD = new Board(-1, -1);
    
private final int INFINITE = 1000000;
private final int INFINITE2 = 10000000;
private int MAX_LEVEL = 6;

private boolean FIRST = true;
private int PLAYER_TURN = 1;

long __TIME, TIMER = 0;
long COTA = 9990;

@Override
public String MyName() {
    return "NOT_CONNECTED";
}

@Override
public University MyUniversity() {
    return University.UO_SAM;
}

@Override
public GeneralAction iterate(PlayerTable table, List<PlayerPiece> pieces) {
    
    GeneralAction action = new Wait();
    
    // executed at the first time 
    if(FIRST) {
        
        FIRST = false;
    	// decides the side of pieces 
    	for(int i = 0; i <= 6; i++) {
            for(int j = i; j <= 6; j++) {
                Domino tile = new Domino(i,j);
                UUID uuid = contains(pieces, tile);
                if( uuid != null ) {
                    tile.uuid = uuid;
                    MY_PIECES.add(tile);
                }
                else OP_PIECES.add(tile);
            }
    	}
        
        if( table.isFirst() )
            PLAYER_TURN = 1;
        else {
            PLAYER_TURN = 2;
            OP_PIECES.remove(new Domino(table.getLPiece()));
            //-----------------------------------------------
            int point = table.getLeft().getValue() + table.getRight().getValue();
            OP_POINTS += point % 5 == 0 ? point : 0;
            //------------------------------------------------
        }
        //-----------------------------------------------
        //MY_PIECES.sort(new Comparator<Domino>() {
        //    @Override
        //    public int compare(Domino d1, Domino d2) {
        //        if(d1.left + d1.right < d2.left + d2.right) return 1;
        //        if(d1.left + d1.right > d2.left + d2.right) return -1;
        //        return 0;
        //    }
        //});
        //-----------------------------------------------
    }
    else { // rest of the game
        // update opponent pieces
        Domino pieceLeft = new Domino(table.getLPiece());
        Domino pieceRight = new Domino(table.getRPiece());
        //-----------------------------------------------
        MY_POINTS = table.getMyPoints().intValue();
        //-----------------------------------------------
        
        if( OP_PIECES.remove(pieceLeft) || OP_PIECES.remove(pieceRight) ) {
            //-----------------------------------------------
            int point = table.getLeft().getValue() + table.getRight().getValue();
            if ( table.getPieces().size() > 1 ) {
                point += pieceLeft.isDouble() ? table.getLeft().getValue() : 0;
                point += pieceRight.isDouble() ? table.getRight().getValue() : 0;
            }
            OP_POINTS += point % 5 == 0 ? point : 0;
            //-----------------------------------------------
        }
    }
    
    //-----------------------------------------------
    //MY_POINTS = table.getMyPoints().intValue();
    //OP_POINTS = table.getOponentPoints().intValue();
    //-----------------------------------------------
    
    if (!table.isFirst()) {
        BOARD = new Board(table);
        if ( !BOARD.canPlay(MY_PIECES) ) {
            OP_POINTS += 10;
            return action;
        }
    }
    
    int START_LEVEL = 5;
    int ps = MY_PIECES.size();
    if(ps == 14)      START_LEVEL = 10;
    else if(ps == 13) START_LEVEL = 12;
    else if(ps == 12) START_LEVEL = 13;
    else if(ps >= 10) START_LEVEL = 16;
    else if(ps >=  8) START_LEVEL = 22;
    else if(ps >=  5) START_LEVEL = 30;
    else              START_LEVEL = 40;
    
    //System.out.println( "--------------------------------------------------------------------------------------\n"
    //        + "PLAYER " + PLAYER_TURN + " " + MY_PIECES.size() + "/" + OP_PIECES.size() + " " + MY_POINTS + " " + OP_POINTS );
    
    value_move temp = null;
    int value = 0;
    Domino domino = new Domino(-1,-1);
    Position position = Position.Left;
    
    int[] _VALUE = new int[4];
    int COUNT = 0;
    TIMER = 0;
    
    for(int level = START_LEVEL; level <= 60; level ++) {
        COUNT ++;
        MAX_LEVEL = level;
        
        __TIME = (new Date()).getTime();
        State state = new State(BOARD.clone(), new Vector<Domino>(MY_PIECES), new Vector<Domino>(OP_PIECES), MY_POINTS, OP_POINTS );
        temp = alphaBeta(state, 0, -INFINITE2, +INFINITE2, 0);
        TIMER += (new Date()).getTime() - __TIME;
                        
        if ( temp == null ) {
            System.out.println(TIMER + " NULL");
            break;
        }
        else {
            System.out.println(TIMER + " OK");
            value = temp.value;
            domino = temp.move;
            position = temp.position;
        }
        
        System.out.println( "" + level + " " + value + " " + domino.left + ":" + domino.right + " " + position);
        
        //TOTAL_CALLS += CALLS;
        //RATIO = (int) Math.round( 1.0 * CALLS / _CALLS ) + 1;
        
        /*if(CALLS * RATIO > 10000000) {
            System.out.println("CALLS EXCEDED " + (CALLS + CALLS * RATIO) );
            //break;
        }
        
        if( CALLS == _CALLS ) {
            System.out.println("EQUAL RESULT");
            //break;
        }
        
        if( TOTAL_CALLS > 20000000 ) {
            System.out.println("TOTAL CALL EXCEDED " + TOTAL_CALLS);
            //break;
        }
        
        if( TOTAL_CALLS + CALLS * RATIO > 25000000 ) {
            System.out.println("TOTAL CALLS EXCEDED " + (TOTAL_CALLS + CALLS * RATIO) );
            //break;
        }
        
        if( TIMER > 4500 ) {
            //System.out.println("TIME EXCEDED " + TIMER);
            break;
        }*/
        
        if( COUNT > 1 && value == _VALUE[0] && value == _VALUE[1] ) {
            //System.out.println("EQUAL VALUE");
            break;
        }
        
        _VALUE[1] = _VALUE[0];
        _VALUE[0] = value;
    }
    
    //System.out.println("L" + level + " " + TIMER + " " + value.value + " [" + value.move.left +":"+ value.move.right + "] " + value.position);
        
    if ( domino.left != -1 ) {
        MY_PIECES.remove( domino );
        action = Play.from( position, domino.toPlayerPiece() );
    } else OP_POINTS += 10;
    
    //System.out.println("/////////////////////////////////////////////////////////////////////////////////");
    return action;
}

private UUID contains(List<PlayerPiece> pieces, Domino tile) {
    for(PlayerPiece p : pieces) {
        if ( tile.isEqual(p) )
            return p.getUUID();
    }
    return null;
}

private class State {
    public Board board;
    public Vector<Domino> myPieces;
    public Vector<Domino> opPieces;
    public int myPoints;
    public int opPoints;
    public State( Board b, Vector<Domino> mpc, Vector<Domino> opc, int mpt, int opt ) {
        board = b;
        myPieces = mpc;
        opPieces = opc;
        myPoints = mpt;
        opPoints = opt;
    }
    @Override
    public State clone() {
        return new State(board.clone(), new Vector<>(myPieces), new Vector<>(opPieces), myPoints, opPoints);
    }
    public int winner() {
        int value = -1;
        if ( isFinal() ) {
            int myTotal = myPiecesPoints();
            int opTotal = opPiecesPoints();
            if ( myPieces.isEmpty() ) value = 0;
            else if ( opPieces.isEmpty() ) value = 1;
            else if ( myPoints > opPoints ) value = 0;
            else if ( myPoints < opPoints ) value = 1;
            else if ( myTotal < opTotal ) value = 0;
            else if ( myTotal > opTotal ) value = 1;
            else if ( PLAYER_TURN == 2 ) value = 0;
            else value = 1; 
        }
        return value;
    }
    public boolean isFinal() {
        return myPieces.isEmpty() || opPieces.isEmpty() 
                || ( !board.empty() && !board.canPlay(myPieces) && !board.canPlay(opPieces) );
    }
    public int myPiecesPoints() {
        int res = 0;
        for( Domino d : myPieces ) {
            res += d.points();
        }
        return res;
    }
    public int opPiecesPoints() {
        int res = 0;
        for( Domino d : opPieces ) {
            res += d.points();
        }
        return res;
    }
    public int heuristic(int level) {
        int value = 0;
        int w = winner();
        if ( w >= 0 ) {
            if ( w == 0 ) { // my player
                value += +INFINITE + 5 * myPoints;
                value += myPiecesPoints() + opPiecesPoints();
                value -= 10 * level;
            }
            else { // opponent player
                value -= +INFINITE + 5 * opPoints;
                value -= myPiecesPoints() + opPiecesPoints();
                value += 10 * level;
            }
        }
        else {
            int mySize = myPieces.size();
            int opSize = opPieces.size();
            int[] myNum = new int[7];
            for(Domino d : myPieces) {
                myNum[d.left] ++;
                if(d.left != d.right)
                    myNum[d.right] ++;
            }
            int[] opNum = new int[7];
            for(Domino d : opPieces) {
                opNum[d.left] ++;
                if(d.left != d.right)
                    opNum[d.right] ++;
            }
            // death tile
            for(Domino d : myPieces) {
                if ( d.left == d.right && myNum[d.left] + opNum[d.left] == 1 && !board.canPlay(d) ) {
                    mySize ++;
                }
            }
            for(Domino d : opPieces) {
                if ( d.left == d.right && myNum[d.left] + opNum[d.left] == 1 && !board.canPlay(d) ) {
                    opSize ++;
                }
            }
            int diff = opSize - mySize - (level % 2); // OK
            value += 20 * Math.abs(diff) * diff;
            value += Math.abs( myPoints - opPoints ) * ( myPoints - opPoints ) / 5;  // OK
            for(int i = 0; i <= 6; i++) {
                value += 10 * Math.abs(myNum[i] - opNum[i]) * (myNum[i] - opNum[i]); // OK
            }
        }
        return value;
    }
}

public class value_move {
    public int value;
    public Domino move;
    public Position position;
    public value_move(int v, Domino m, Position p){
        value = v;
        move = m;
        position = p;
    }
}

private value_move alphaBeta(State state, int level, int alpha, int beta, int player) {
    
    int winner = state.winner();
    
    // get up the max of levels OR terminate game 
    if( level >= MAX_LEVEL || winner >= 0 ) { // winner >= 0 ? isFinal, there are a winner
        if ( TIMER + (new Date()).getTime() - __TIME > COTA )
            return null;
        int stateValue = state.heuristic(level);
        return new value_move( stateValue, new Domino(-1, -1), Position.Left );
    }
    
    if ( player == 0 ) {
        
        int value = -INFINITE2;
        Domino move = new Domino(-1, -1);
        Position position = Position.Left;
        
        if ( !state.board.empty() && !state.board.canPlay(state.myPieces) ) {
            State _state = state.clone();
            _state.opPoints += 10;
            
            value_move inv = alphaBeta(_state, level+1, alpha, beta, 1);
            if ( inv == null ) return null;
            
            value = inv.value;
        }
        else if ( state.board.empty() ) {
            for(Domino d : state.myPieces) {
                State _state = state.clone();
                _state.myPieces.remove(d.clone());
                _state.board.play(d, Position.Left);
                int point = ( d.points() % 5 == 0 ) ? d.points() : 0;
                _state.myPoints += point;
                
                value_move inv = alphaBeta(_state, level+1, alpha, beta, 1);
                if ( inv == null ) return null;
                
                int nextValue = inv.value;
                if (nextValue > value) {
                    value = nextValue;
                    move = d;
                    position = Position.Left;
                }
                alpha = Math.max(alpha, value);
                if ( alpha >= beta ) break;
            }
        }
        else for(Domino d : state.myPieces) {
            Vector<Position> positions = state.board.canPlayIn(d);
            boolean _break = false;
            for ( Position p : positions ) {
                State _state = state.clone();
                _state.myPieces.remove(d.clone());
                _state.board.play(d, p);
                _state.myPoints += _state.board.points();
                
                value_move inv = alphaBeta(_state, level+1, alpha, beta, 1);
                if ( inv == null ) return null;
                
                int nextValue = inv.value;
                if (nextValue > value) {
                    value = nextValue;
                    move = d;
                    position = p;
                }
                alpha = Math.max(alpha, value);
                if ( alpha >= beta ) {
                    _break = true;
                    break;
                }
            }
            if ( _break ) break;
        }
        return new value_move(value, move, position);
    }
    else {
        int value = +INFINITE2;
        Domino move = new Domino(-1, -1);
        Position position = Position.Left;
        
        if ( !state.board.canPlay(state.opPieces) ) {
            State _state = state.clone();
            _state.myPoints += 10;
            
            value_move inv = alphaBeta(_state, level+1, alpha, beta, 0);
            if ( inv == null ) return null;
            
            value = inv.value;
        }
        else for(Domino d : state.opPieces) {
            Vector<Position> positions = state.board.canPlayIn(d);
            boolean _break = false;
            for ( Position p : positions ) {
                State _state = state.clone();
                _state.opPieces.remove(d.clone());
                _state.board.play(d, p);
                _state.opPoints += _state.board.points();
                
                value_move inv = alphaBeta(_state, level+1, alpha, beta, 0);
                if ( inv == null ) return null;
                
                int nextValue = inv.value;
                if (nextValue < value) {
                    value = nextValue;
                    move = d;
                    position = p;
                }
                beta = Math.min(beta, value);
                if ( alpha >= beta ) {
                    _break = true;
                    break;
                }
            }
            if ( _break ) break;
        }
        return new value_move(value, move, position);
    }
}

public class Domino {
    public int left, right;
    public UUID uuid;
    public Domino(){
        left = right = -1;
    }
    public Domino(int l, int r){
        if(l > r){
            int t = l;
            l = r;
            r = t;
        }
        left = l;
        right = r;
    }
    public Domino(PlayerPiece p) {
        int l = p.getLeft().getValue();
        int r = p.getRight().getValue();
        if( l > r ) {
            int t = l;
            l = r;
            r = t;
        }
        left = l;
        right = r;
    }
    public boolean isDouble(){
        return left == right;
    }
    public boolean isEqual(PlayerPiece p) {
        int l = p.getLeft().getValue();
        int r = p.getRight().getValue();
        if( l > r ) {
            int t = l;
            l = r;
            r = t;
        }
        return left == l && right == r;
    }
    public int points(){
        return left + right;
    }
    public PlayerPiece toPlayerPiece() {
        return PlayerPiece.from(Numbers.getFrom(left), Numbers.getFrom(right), uuid);
    }
    @Override
    public boolean equals(Object o){
        return (left == ((Domino) o).left) && (right == ((Domino) o).right);
    }
    @Override
    public Domino clone(){
        return new Domino(left, right);
    }
}

public class Board {
    public int left;
    public int right;
    public boolean leftDb;
    public boolean rightDb;
    
    public Board(int l, int r){
        left = l;
        right = r;
        leftDb = rightDb = false;
    }
    public Board(int l, int r, boolean ld, boolean rd){
        left = l;
        right = r;
        leftDb = ld;
        rightDb = rd;
    }
    public Board(PlayerTable table){
        left = table.getLeft().getValue();
        right = table.getRight().getValue();
        leftDb = table.getLPiece().isDouble();
        rightDb = table.getRPiece().isDouble();
    }
    public boolean empty() {
        return left == -1;
    }
    public boolean play(Domino tile, Position position){
        if(position == Position.Left) {
            if( empty() ) {
                left = tile.left;
                right = tile.right; 
                leftDb = rightDb = tile.isDouble();
            }
            else if(left == tile.left) {
                left = tile.right;
                leftDb = tile.isDouble();
            }
            else if(left == tile.right) {
                left = tile.left;
                leftDb = tile.isDouble();
            }
            else return false;
        }
        else {
            if(right == tile.left) {
                right = tile.right; 
                rightDb = tile.isDouble();
            }
            else if(right == tile.right) {
                right = tile.left; 
                rightDb = tile.isDouble();
            }
            else return false;
        }
        return true;
    }
    public int points() {
        int t = left + right;
        t += leftDb ? left : 0;
        t += rightDb ? right : 0;
        return (t % 5 == 0) ? t : 0;
    }
    public boolean capicua() {
        return left == right;
    }
    public boolean canPlay(Domino tile){
        return left == tile.left || right == tile.left || left == tile.right || right == tile.right;
    }
    public boolean canPlay(Vector<Domino> pieces){
        for(Domino p : pieces)
            if(canPlay(p)) 
                return true;
        return false;
    }
    public boolean canPlayPosition(Domino tile, Position pos){
        if(pos == Position.Left)
            return left == tile.left || left == tile.right;
        return right == tile.left || right == tile.right;
    }
    public Vector<Position> canPlayIn(Domino tile){
        Vector<Position> res = new Vector<Position>();
        if( canPlayPosition( tile, Position.Left ) )
            res.add(Position.Left);
        if( !capicua() && canPlayPosition( tile, Position.Right ) )
            res.add(Position.Right);
        return res;
    }
    @Override
    public Board clone(){
        return new Board(left, right, leftDb, rightDb);
    }
}
}
