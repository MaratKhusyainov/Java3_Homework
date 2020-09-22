package Hometask6.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Censorship {
    static Map<String,String> censorshipMap = new HashMap<>();


    // Отделяем знаки препинания от слов ("дурак?" --> "дурак", "?") и проводим цензуру:
    public static String[] censor(String[] message){
//        System.out.println(Arrays.toString(message));
        ArrayList<String> mess = new ArrayList<>();
        int indexStart = 0;
        int indexEnd;

        for (String word: message){
            char[] chars = word.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if(!Character.isLetter(chars[i])){
                    mess.add(String.valueOf(chars[i]));

                } else {
                    if((i-1)>=0 && Character.isLetter(chars[i-1])){
                        if((i+1)<chars.length && Character.isLetter(chars[i+1])){
                            continue;
                        } else {
                            indexEnd = i;
                            char[] newWord = new char[indexEnd - indexStart + 1];
                            int k = 0;
                            for (int j = indexStart; j <= indexEnd; j++) {
                                newWord[k] = chars[j];
                                k++;
                            }
                            //кладет в ArrayList легальное слово вместо нехорошего (если слово легальное, оставляет его):
                            mess.add(changeToLegalWord(String.valueOf(newWord)));
                        }
                    } else {
                        indexStart = i;
                        if (chars.length==1) mess.add(String.valueOf(chars));
                    }
                }
            }
        }
        String[] cropedMess = new String[mess.size()];
//        System.out.println("cropped and censored mess "+Arrays.toString(mess.toArray(cropedMess)));
        return mess.toArray(cropedMess);
    }


    // Проверяем, легально ли слово, если нет, заменяем его легальным из словаря.
    public static String changeToLegalWord(String word) {
        if(censorshipMap.containsKey(word)) {
            return censorshipMap.get(word);
        }
        return word;
    }

    // Соединяет массим  слов в сообщение:
    public static String joinString(String[] message) {
        boolean isWord;
        StringBuilder joinedMess = new StringBuilder();

        //проверяем первое слово в массиве слов сообщения:
        char[] charWord = (message[0]).toCharArray();
        //если это слово, а не знак препинания:
        if (Character.isLetter(charWord[0])) {
            isWord = true;
            //добавляем его в строку сообщения:
            joinedMess.append(message[0]);
        } else {
            isWord = false;
        }
        //проверяем все остальные слова в массиве слов сообщения:
        if (message.length > 1) {
            for (int i = 1; i < message.length; i++) {
                char[] chWord = message[i].toCharArray();
                //если это слово, а не знак препинания:
                if (Character.isLetter(chWord[0])) {
                    joinedMess.append(" ");
                    joinedMess.append(message[i]);
                    isWord = true;
                } else {
                    joinedMess.append(message[i]);
                    isWord = false;
                }
            }
        }
        return joinedMess.toString();
    }

}

