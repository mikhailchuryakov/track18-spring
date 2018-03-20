package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class Decoder {

    private Map<Character, Character> cypher;

    /**
     * Конструктор строит гистограммы открытого домена и зашифрованного домена
     * Сортирует буквы в соответствие с их частотой и создает обратный шифр Map<Character, Character>
     *
     * @param domain - текст по кторому строим гистограмму языка
     */
    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);

        Iterator<Character> domainIter = domainHist.keySet().iterator();
        Iterator<Character> encryptedIter = encryptedDomainHist.keySet().iterator();

        cypher = new LinkedHashMap<>();

        while (domainIter.hasNext()){
            cypher.put(encryptedIter.next(), domainIter.next());
        }
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
        char[] symbols = encoded.toLowerCase().toCharArray();
        StringBuilder sb = new StringBuilder();

        for(char sym : symbols){
            if (sym >= 'a' && sym <= 'z') sb.append(getCypher().get(sym));
            else sb.append(sym);
        }
        return sb.toString();
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
        char[] symbols = text.toLowerCase().toCharArray();
        Map<Character, Integer> hist = new HashMap<>();

        for(char sym : symbols){
            if (sym >= 'a' && sym <= 'z') {
                if (!hist.containsKey(sym)) hist.put(sym, 1);
                else hist.put(sym, hist.get(sym) + 1);
            }
        }

        List<Map.Entry<Character, Integer>> entries = new LinkedList<>(hist.entrySet());
        entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map<Character, Integer> sortedHist = new LinkedHashMap<>();
        for(Map.Entry<Character, Integer> entry : entries) sortedHist.put(entry.getKey(), entry.getValue());

        return sortedHist;
    }
}
