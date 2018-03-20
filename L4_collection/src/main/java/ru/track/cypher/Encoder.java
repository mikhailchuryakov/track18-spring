package ru.track.cypher;

import java.util.Arrays;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Класс умеет кодировать сообщение используя шифр
 */
public class Encoder {

    /**
     * Метод шифрует символы текста в соответствие с таблицей
     * NOTE: Текст преводится в lower case!
     *
     * Если таблица: {a -> x, b -> y}
     * то текст aB -> xy, AB -> xy, ab -> xy
     *
     * @param cypherTable - таблица подстановки
     * @param text - исходный текст
     * @return зашифрованный текст
     */
    public String encode(@NotNull Map<Character, Character> cypherTable, @NotNull String text) {
        char[] symbols = text.toLowerCase().toCharArray();
        StringBuilder sb = new StringBuilder();

        for(char sym : symbols){
            if (sym >= 'a' && sym <= 'z') sb.append(cypherTable.get(sym));
            else sb.append(sym);
        }

        return sb.toString();
    }
}
