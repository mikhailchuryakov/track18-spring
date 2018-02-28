package ru.track;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * Задание 1: Реализовать два метода
 *
 * Формат файла: текстовый, на каждой его строке есть (или/или)
 * - целое число (int)
 * - текстовая строка
 * - пустая строка (пробелы)
 *
 * Числа складываем, строки соединяем через пробел, пустые строки пропускаем
 *
 *
 * Пример файла - words.txt в корне проекта
 *
 * ******************************************************************************************
 *  Пожалуйста, не меняйте сигнатуры методов! (название, аргументы, возвращаемое значение)
 *
 *  Можно дописывать новый код - вспомогательные методы, конструкторы, поля
 *
 * ******************************************************************************************
 *
 */
public class CountWords {

    String skipWord;

    public CountWords(String skipWord) {
        this.skipWord = skipWord;
    }

    /**
     * Метод на вход принимает объект File, изначально сумма = 0
     * Нужно пройти по всем строкам файла, и если в строке стоит целое число,
     * то надо добавить это число к сумме
     * @param file - файл с данными
     * @return - целое число - сумма всех чисел из файла
     */
    public long countNumbers(File file) throws Exception {
        long result = 0;
        try (
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    result += Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    //
                }
            }
        } catch (IOException e) {
            System.err.println("error message");
            throw e;
        }
        return result;
    }


    /**
     * Метод на вход принимает объект File, изначально результат= ""
     * Нужно пройти по всем строкам файла, и если в строка не пустая и не число
     * то надо присоединить ее к результату через пробел
     * @param file - файл с данными
     * @return - результирующая строка
     */
    public String concatWords(File file) throws Exception {
        StringBuilder str = new StringBuilder("");
        try (
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equals(skipWord)) {
                    if (!checkNumber(line)) str.append(line).append(" ");
                }
            }
        } catch (IOException e) {
            System.err.println("Error message");
            throw e;
        }
        return str.toString();
    }

    private boolean checkNumber(String line){
        char[] array = line.toCharArray();
        int i = 0;
        for (char arr: array){
            if (arr < '0' || arr > '9')
                return false;
        }
        return true;
    }
}

