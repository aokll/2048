package com.javarush.task.task35.task3513;

/**
 * описывающий эффективность хода
 * */
public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private Integer numberOfEmptyTiles; //количество пустых плиток
    private Integer score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        int v = numberOfEmptyTiles.compareTo(o.numberOfEmptyTiles);
        if (v != 0) return v;
        else return score.compareTo(o.score);
    }
}
