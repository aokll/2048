package com.javarush.task.task35.task3513;

import java.util.*;

public class Model {

    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;
    private Tile[][] gameTiles;
    private static final int FIELD_WIDTH = 4;
    int score = 0; //текущий счет.
    // Счет увеличивается после каждого слияния
    int maxTile = 2; //максимальный вес плитки.
    // Если выполняется условие слияния плиток,
    // проверяем является ли новое значения больше максимального
    // и при необходимости меняем значение поля maxTile

    public Model() {
        resetGameTiles();
    }

    Tile[][] getGameTiles() {
        return gameTiles;
    }

    void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            int index = (int) (Math.random() * emptyTiles.size()) % emptyTiles.size();
            Tile emptyTile = emptyTiles.get(index);
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private List<Tile> getEmptyTiles() {
        final List<Tile> list = new ArrayList<Tile>();
        for (Tile[] tileArray : gameTiles) {
            for (Tile t : tileArray)
                if (t.isEmpty()) {
                    list.add(t);
                }
        }
        return list;
    }
    /**Сжатие плиток, таким образом, чтобы все пустые плитки были
     * справа, т.е. ряд {4, 2, 0, 4} становится рядом {4, 2, 4, 0}
     *
     * Создать пустой массив той же длины, что и исходный.
     * Посчитать количество нулей в исходном массиве (пусть это N).
     * Скопировать все ненулевые элементы в новый массив, начиная с позиции N+1.
     * **/
    private boolean compressTiles(Tile[] tiles) {
        int insertPosition = 0;
        boolean result = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (!tiles[i].isEmpty()) {
                if (i != insertPosition) {
                    tiles[insertPosition] = tiles[i];
                    tiles[i] = new Tile();
                    result = true;
                }
                insertPosition++;
            }
        }
        return result;
    }
    /**Слияние плиток одного номинала, т.е. ряд {4, 4, 2, 0} становится рядом {8, 2, 0, 0}.
     Обрати внимание, что ряд {4, 4, 4, 4} превратится в {8, 8, 0, 0}, а {4, 4, 4, 0} в {8, 4, 0, 0}.**/

    private boolean mergeTiles(Tile[] tiles) {
        boolean result = false;
        LinkedList<Tile> tilesList = new LinkedList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (tiles[i].isEmpty()) {
                continue;
            }

            if (i < FIELD_WIDTH - 1 && tiles[i].value == tiles[i + 1].value) {
                int updatedValue = tiles[i].value * 2;
                if (updatedValue > maxTile) {
                    maxTile = updatedValue;
                }
                score += updatedValue;
                tilesList.addLast(new Tile(updatedValue));
                tiles[i + 1].value = 0;
                result = true;
            } else {
                tilesList.addLast(new Tile(tiles[i].value));
            }
            tiles[i].value = 0;
        }

        for (int i = 0; i < tilesList.size(); i++) {
            tiles[i] = tilesList.get(i);
        }

        return result;
    }

    public void left() {
        if (isSaveNeeded){
            saveState(gameTiles);
        }
        boolean moveFlag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }
        if (moveFlag) {
            addTile();
        }
        isSaveNeeded = true;

    }
    public void up() {
        saveState(gameTiles);
        gameTiles = counterclockwiseRotation(gameTiles, 1);
        left();
        gameTiles = counterclockwiseRotation(gameTiles, 3);
    }
    public void right() {
        saveState(gameTiles);
        gameTiles = counterclockwiseRotation(gameTiles, 2);
        left();
        gameTiles = counterclockwiseRotation(gameTiles, 2);
    }
    public void down() {
        saveState(gameTiles);
        gameTiles = counterclockwiseRotation(gameTiles, 3);
        left();
        gameTiles = counterclockwiseRotation(gameTiles, 1);
    }
    public Tile[][] counterclockwiseRotation(Tile [][] arr, int n){
        int k = 0;
        int m = arr.length - 1;

        Tile[][] arrResult = new Tile[arr.length][arr.length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (m < 0){
                    m = arr.length - 1;
                }
                arrResult[m][k] = arr[i][j];
                m--;
            }
            k++;
        }
        switch (n){
            case 1: break;
            case 2:
                Tile[][] arr2 = counterclockwiseRotation(arrResult, 1);
                return arr2;
            case 3:
                Tile[][] arr30 = counterclockwiseRotation(arrResult, 1);
                Tile[][] arr31 = counterclockwiseRotation(arr30,1);
                return arr31;
        }
        return arrResult;
    }

    private int getEmptyTilesCount() {
        return getEmptyTiles().size();
    }

    private boolean isFull() {
        return getEmptyTilesCount() == 0;
    }

    /**
     * canMove возвращает true в случае, если в текущей позиции возможно
     * сделать ход так, чтобы состояние игрового поля изменилось. Иначе - false.
     * */

    boolean canMove() {
        if (!isFull()) {
            return true;
        }

        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                Tile t = gameTiles[x][y];
                if ((x < FIELD_WIDTH - 1 && t.value == gameTiles[x + 1][y].value)
                        || ((y < FIELD_WIDTH - 1) && t.value == gameTiles[x][y + 1].value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**Приватный метод saveState с одним параметром типа Tile[][] будет
    / сохранять текущее игровое состояние и счет в стеки с помощью метода
    / push и устанавливать флаг isSaveNeeded равным false.*/


    private void saveState(Tile[][] arr){
        Tile[][] saveGame = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
               saveGame[i][j] = new Tile(gameTiles[i][j].value);
            }
        }
        previousStates.push(saveGame);
        previousScores.push(score);
        isSaveNeeded = false;
    }
    /**
    /Публичный метод rollback будет устанавливать текущее игровое
     /состояние равным последнему находящемуся в стеках с помощью метода pop.
    / */
    public void rollback(){
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }
    /**
    / метод randomMove в классе Model, который будет вызывать один
     из методов движения случайным образом. Можешь реализовать это
     вычислив целочисленное n = ((int) (Math.random() * 100)) % 4. Это
     число будет содержать целое псевдослучайное число в диапазоне [0..3],
     по каждому из которых можешь вызывать один из методов left, right, up, down.
     */
    public void randomMove(){
       int n = ((int) (Math.random() * 100)) % 4;
       switch (n){
           case 0:
               left();
               break;
           case 1:
               right();
               break;
           case 2:
               up();
               break;
           case 3:
               down();
               break;
       }
    }
    /**boolean hasBoardChanged - будет возвращать true, в случае, если
     * вес плиток в массиве gameTiles отличается от веса плиток в верхнем
     * массиве стека previousStates. Обрати внимание на то, что мы не должны
     * удалять из стека верхний элемент, используй метод peek.*/
    public boolean hasBoardChanged(){
        int weightNow = 0;
        int weightPrev = 0;
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                weightNow += gameTiles[i][j].value;
                weightPrev += previousStates.peek()[i][j].value;
            }
        }
        if (weightNow != weightPrev)return  true;
        else return false;
    }
    public MoveEfficiency getMoveEfficiency(Move move){
        move.move();
        MoveEfficiency moveEfficiency = new MoveEfficiency(getEmptyTiles().size(),score,move);
        if (!hasBoardChanged()) return new MoveEfficiency(-1,0,move);
        rollback();
        return moveEfficiency;
    }
    public void autoMove(){
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());

        for (int i = 0; i < 4; i++) {
            switch (i){
                case 0:
                    /**
                     * передали аргумент в метод getMoveEfficiency используя оператор "::"
                     * */
                    priorityQueue.offer(getMoveEfficiency(this::left));
                    break;
                case 1:
                    /**
                     * передали аргумент в метод getMoveEfficiency используя лямбда-выражение
                     * */
                    priorityQueue.offer(getMoveEfficiency(()-> right()));
                    break;
                case 2:
                    /**
                     * передали аргумент в метод getMoveEfficiency используя анонимный класс
                     * */
                    priorityQueue.offer(getMoveEfficiency(new Move() {
                        @Override
                        public void move() {
                            up();
                        }
                    }));
                    break;
                case 3:
                    priorityQueue.offer(getMoveEfficiency(this::down));
                    break;
            }
        }
        /**
         * Возьмем верхний элемент и выполним ход связанный с ним.
         * */
        priorityQueue.peek().getMove().move();

    }


}
