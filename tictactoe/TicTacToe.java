package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.*;

public class TicTacToe extends JFrame {

    public TicTacToe() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tic Tac Toe");
        setVisible(true);

        Board board = new Board();
        StatusBar statusBar = new StatusBar();
        ToolBar toolBar = new ToolBar();
        Container contentPane = getContentPane();
        contentPane.add(toolBar, BorderLayout.PAGE_START);
        contentPane.add(board, BorderLayout.CENTER);
        contentPane.add(statusBar, BorderLayout.PAGE_END);

        createMenu();

        setResizable(false);
        setSize(450, 500);

        new Game();
    }

    void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuGame = new JMenu("Game");
        menuGame.setName("MenuGame");

        JMenuItem menuHumanHuman = new JMenuItem("HumanHuman");
        menuHumanHuman.setName("MenuHumanHuman");
        menuHumanHuman.addActionListener(e -> ToolBar.changePlayersType(0, 0));

        JMenuItem menuHumanRobot = new JMenuItem("HumanRobot");
        menuHumanRobot.setName("MenuHumanRobot"); 
        menuHumanRobot.addActionListener(e -> ToolBar.changePlayersType(0, 1));

        JMenuItem menuRobotHuman = new JMenuItem("RobotHuman");
        menuRobotHuman.setName("MenuRobotHuman");
        menuRobotHuman.addActionListener(e -> ToolBar.changePlayersType(1, 0));

        JMenuItem menuRobotRobot = new JMenuItem("RobotRobot");
        menuRobotRobot.setName("MenuRobotRobot");
        menuRobotRobot.addActionListener(e -> ToolBar.changePlayersType(1, 1));

        JMenuItem menuExit = new JMenuItem("Exit");
        menuExit.setName("MenuExit");
        menuExit.addActionListener(e -> System.exit(0));

        menuGame.add(menuHumanHuman);
        menuGame.add(menuHumanRobot);
        menuGame.add(menuRobotHuman);
        menuGame.add(menuRobotRobot);
        menuGame.addSeparator();
        menuGame.add(menuExit);

        menuBar.add(menuGame);
        setJMenuBar(menuBar);
    }
}

class Game {

    static enum Status {NOTSTART, ONPLAY, XWIN, OWIN, DRAW};
    static int turn = 0;
    static int[][] gameBoard;
    static Random rand;
    static Status status;

    Game() {
        gameBoard = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameBoard[i][j] = -1;
            }
        } 
        rand = new Random();
        setStatus(Status.NOTSTART);
    }

    static void start() {
        setStatus(Status.ONPLAY);
        Cell.enabled(true);
        ToolBar.buttonPlayer1.setEnabled(false);
        ToolBar.buttonPlayer2.setEnabled(false);
        if (ToolBar.buttonPlayer1.getType() == 1) {
            robotMove();
        }
    }

    static void setStatus(Status s) {
        status = s;
        displayStatus();
    }

    static void displayStatus() {
        String msg = "";
        String pattern = "";
        String XO = turn==0 ? "X" : "O";
        String player = turn==0 ? ToolBar.buttonPlayer1.getTypeName() : ToolBar.buttonPlayer2.getTypeName();
        switch (status) {
            case NOTSTART:
                msg = "Game is not started";
                break;
            case ONPLAY:
                pattern = "The turn of {0} Player ({1})";
                msg = MessageFormat.format(pattern, player, XO);
                break;
            case XWIN:
            case OWIN:
                pattern = "The {0} Player ({1}) wins";
                msg = MessageFormat.format(pattern, player, XO);
                break;
            case DRAW:
                msg = "Draw";
                break;
        }
        StatusBar.setStatus(msg);
    }

    static void next() {
        turn ^= 1;
        
        if (turn == 0) {
            if (ToolBar.buttonPlayer1.getType() == 1) {
                robotMove();
            }
        } else {
            if (ToolBar.buttonPlayer2.getType() == 1) {
                robotMove();
            }           
        }
        displayStatus();
    }

    static int getTurn() {
        return turn;
    }

    static void judge() {
        if (checkWin()) {
            if (turn == 0) {    // turn has next turn
                setStatus(Status.XWIN);
            } else {
                setStatus(Status.OWIN);
            }
            Cell.enabled(false);
            return;
        }
        if (isGameOver()) {
            setStatus(Status.DRAW);
            Cell.enabled(false);
            return;
        }
        setStatus(Status.ONPLAY);
    }

    static boolean isGameOver() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard[i][j] == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            for (int t = 0; t < 2; t++) {
                if (rowCheck(t, i)) {
                    return true;
                }
                if (colCheck(t, i)) {
                    return true;
                }
            }
        }
        for (int t = 0; t < 2; t++) {
            if (diagonalCheck(t)) {
                return true;
            }
            if (reverseDiagonalCheck(t)) {
                return true;
            }
        }
        return false;
    }

    static boolean rowCheck(int checkTurn, int i) {
        for (int j = 0; j < 3; j++) {
            if (gameBoard[i][j] != checkTurn) {
                return false;
            }
        }
        return true;
    }

    static boolean colCheck(int checkTurn, int j) {
        for (int i = 0; i < 3; i++) {
            if (gameBoard[i][j] != checkTurn) {
                return false;
            }
        }
        return true;
    }

    static boolean diagonalCheck(int checkTurn) {
        for (int i = 0; i < 3; i++) {
            if (gameBoard[i][i] != checkTurn) {
                return false;
            }
        }
        return true;
    }

    static boolean reverseDiagonalCheck(int checkTurn) {
        for (int i = 0; i < 3; i++) {
            if (gameBoard[i][2 - i] != checkTurn) {
                return false;
            }
        }
        return true;
    }

    static boolean setMove(Cell cell) {
        String name = cell.getName().substring("Button".length());
        String rowName = name.substring(0, 1);
        String colName = name.substring(1);
        int i = 0;
        int j = 0;
        switch (rowName) {
            case "A": i = 0;break;
            case "B": i = 1;break;
            case "C": i = 2;break;
        }
        switch (colName) {
            case "1": j = 0;break;
            case "2": j = 1;break;
            case "3": j = 2;break;
        }
        if (gameBoard[i][j] != -1) {
            return false;
        }
        gameBoard[i][j] = turn;
        return true;
    }

    static void reset() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameBoard[i][j] = -1;
            }
        }
        Cell.clear(); 
        turn = 0;
        setStatus(Status.NOTSTART);
        ToolBar.buttonPlayer1.setEnabled(true);
        ToolBar.buttonPlayer2.setEnabled(true);
    }

    static void robotMove() {
        while (true) {
            int n = Math.abs(rand.nextInt()) % 9;
            int i = n / 3;
            int j = n % 3;
            if (gameBoard[i][j] == -1) {
                gameBoard[i][j] = turn;
                Cell.getCell(i, j).move(true);
                return;
            }
        }
    }
}

class Player extends JButton {
    int type;
    String name;

    Player(String name, int type) {
        this.type = type;
        setName("Button" + name);
        setText(getTypeName());
        addActionListener(e -> typeChange());
    }

    int getType() {
        return type;
    }

    void setType(int type) {
        this.type = type;
    }

    String getTypeName() {
        if (type == 0) {
            return "Human";
        } else {
            return "Robot";
        }
    }

    void typeChange() {
        type ^= 1;
        setText(getTypeName());        
    }
}

class StartReset extends JButton {
    int type;
    String name;

    StartReset(String name, int type) {
        this.type = type;
        setName("Button" + name);
        setText(getTypeName());
        addActionListener(e -> typeChange());
    }

    int getType() {
        return type;
    }

    String getTypeName() {
        if (type == 0) {
            return "Start";
        } else {
            return "Reset";
        }
    }

    void typeChange() {
        type ^= 1;
        setText(getTypeName());
        if (type == 0) {
            Game.reset();
        } else {
            Game.start();
        }
    }

    void gameStart() {
        type = 1;
        setText(getTypeName());
        Game.start();
    }
}

class ToolBar extends JPanel {

    static Player buttonPlayer1;
    static Player buttonPlayer2;
    static StartReset buttonStartReset;

    ToolBar() {
        buttonPlayer1 = new Player("Player1", 0);
        buttonPlayer2 = new Player("Player2", 0);
        buttonStartReset = new StartReset("StartReset", 0);
        add(buttonPlayer1);
        add(buttonStartReset);
        add(buttonPlayer2);
    }

    static void changePlayersType(int player1Type, int player2Type) {
        buttonPlayer1.setType(player1Type);
        buttonPlayer1.setText(buttonPlayer1.getTypeName());
        buttonPlayer2.setType(player2Type);
        buttonPlayer2.setText(buttonPlayer2.getTypeName());
        ToolBar.gameStart();
    }

    static void gameStart() {
        buttonStartReset.gameStart();
    }
}

class StatusBar extends JPanel {

    static JLabel labelStatus;

    StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        labelStatus = new JLabel("Game is not started");
        labelStatus.setName("LabelStatus");
		add(labelStatus);
	}

    static String getStatus() {
        return labelStatus.getText();
    }

    static void setStatus(String msg) {
        labelStatus.setText(msg);
    }
}

class Board extends JPanel {

    Board() {

        Cell buttonA1 = new Cell("A1");
        Cell buttonA2 = new Cell("A2");
        Cell buttonA3 = new Cell("A3");
        Cell buttonB1 = new Cell("B1");
        Cell buttonB2 = new Cell("B2");
        Cell buttonB3 = new Cell("B3");
        Cell buttonC1 = new Cell("C1");
        Cell buttonC2 = new Cell("C2");
        Cell buttonC3 = new Cell("C3");

        setLayout(new GridLayout(3, 3));
        add(buttonA3);
        add(buttonB3);
        add(buttonC3);
        add(buttonA2);
        add(buttonB2);
        add(buttonC2);
        add(buttonA1);
        add(buttonB1);
        add(buttonC1);
    }
}

class Cell extends JButton {

    static ArrayList<Cell> cellList = new ArrayList<>();
    String name;

    Cell(String name) {
        setName("Button" + name);
        setText(" ");
        setFont(new Font("Arial", Font.BOLD, 56));
        setFocusPainted(false);
        addActionListener(e -> move(false));
        setEnabled(false);
        cellList.add(this);
    }

    void move(boolean robotFlag) {
//        if (ToolBar.buttonStartReset.getType() == 0) {
//            return;
//        }
        if (Game.status != Game.Status.ONPLAY) {  // game already ended
            return;
        }
        if (!robotFlag && !Game.setMove(this)) {
            return;
        }
        if (Game.getTurn() == 0) {
            setText("X");
        } else {
            setText("O");
        }
        Game.judge();
        if (Game.status == Game.Status.ONPLAY) {
            Game.next();
        }
    }

    static void clear() {
        for (Cell cell: cellList) {
            cell.setText(" ");
            cell.setEnabled(false);
        }
    }

    static void enabled(boolean flag) {
        for (Cell cell: cellList) {
            cell.setEnabled(flag);
        }
    }

    static Cell getCell(int i, int j) {
        String rowName = "A";
        if (i == 1) {
            rowName = "B";
        } else if (i == 2) {
            rowName = "C";
        }
        String colName = "1";
        if (j == 1) {
            colName = "2";
        } else if (j == 2) {
            colName = "3";
        }
        String cellName = rowName + colName;
        for (Cell cell: cellList) {
            if (cell.getName().endsWith(cellName)) {
                return cell;
            }   
        }
        return null;     
    }
}