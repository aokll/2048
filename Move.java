package com.javarush.task.task35.task3513;

/**
 * интерфейс Move с одним void методом move. Отметь интерфейс
 * аннотацией @FunctionalInterface, которая будет сигнализировать
 * о том что в этом интерфейсе будет только один абстрактный метод.
 * */
@FunctionalInterface
public interface Move {
    void move();
}
