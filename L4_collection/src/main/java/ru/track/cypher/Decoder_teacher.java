package ru.track.cypher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class Decoder_teacher {

    // Расстояние между A-Z -> a-z
    public static final int SYMBOL_DIST = 32;

    private Map<Character, Character> cypher;

    /**
     * Конструктор строит гистограммы открытого домена и зашифрованного домена
     * Сортирует буквы в соответствие с их частотой и создает обратный шифр Map<Character, Character>
     *
     * @param domain - текст по кторому строим гистограмму языка
     */
    public Decoder_teacher(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);

        cypher = new LinkedHashMap<>();

        Iterator<Character> encryptedIter = encryptedDomainHist.keySet().iterator();
        Iterator<Character> domainIter = domainHist.keySet().iterator();

        while (encryptedIter.hasNext()) {
            char encryptedCh = encryptedIter.next();
            char domainCh = domainIter.next();
            cypher.put(encryptedCh, domainCh);
        }

        System.out.println("decoder cypher: " + cypher);

    }

    public Map<Character, Character> getCypher() {
        return cypher;
    }

    /**
     * Применяет построенный шифр для расшифровки текста
     *
     * @param encoded зашифрованный текст
     * @return расшифровка
     */
    @NotNull
    public String decode(@NotNull String encoded) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < encoded.length(); i++) {
            char ch = encoded.charAt(i);
            builder.append(CypherUtil_teacher.isLetter(ch) ? cypher.get(Character.toLowerCase(ch)).charValue() : ch);
        }
        return builder.toString();
    }

    /**
     * Считывает входной текст посимвольно, буквы сохраняет в мапу.
     * Большие буквы приводит к маленьким
     *
     *
     * @param text - входной текст
     * @return - мапа с частотой вхождения каждой буквы (Ключ - буква в нижнем регистре)
     * Мапа отсортирована по частоте. При итерировании на первой позиции наиболее частая буква
     */
    @NotNull
    Map<Character, Integer> createHist(@NotNull String text) {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                if (ch < 'Z') {
                    ch += SYMBOL_DIST;
                }
                // Накручиваем счетчик в гистограмме
                if (map.containsKey(ch)) {
                    int count = map.get(ch);
                    map.put(ch, count + 1);
                } else {
                    map.put(ch, 1);
                }
            }
        }

        // Нам нужно отсортировать пары из мапы, для этого переложим их в List и напишем Comparator
        List<Map.Entry<Character, Integer>> entries = new LinkedList<>(map.entrySet());
        entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // Будем добавлять записи в порядке сортировки в LinkedHashMap
        Map<Character, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : entries) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }

}
