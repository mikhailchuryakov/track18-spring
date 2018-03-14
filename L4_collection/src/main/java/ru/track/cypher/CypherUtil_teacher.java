package ru.track.cypher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Вспомогательные методы шифрования/дешифрования
 */
public class CypherUtil_teacher {

    public static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Генерирует таблицу подстановки - то есть каждой буква алфавита ставится в соответствие другая буква
     * Не должно быть пересечений (a -> x, b -> x). Маппинг уникальный
     *
     * @return таблицу подстановки шифра
     */
    @NotNull
    public static Map<Character, Character> generateCypher() {
        Map<Character, Character> cypher = new HashMap<>();
        char[] chars = SYMBOLS.toCharArray();

        List<Character> shuffled = new ArrayList<Character>();
        for (char c : chars) {
            shuffled.add(c);
        }
        Collections.shuffle(shuffled);

        for (int i = 0; i < chars.length; i++) {
            cypher.put(chars[i], shuffled.get(i));
        }

        return cypher;
    }

    public static boolean isLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }
}
