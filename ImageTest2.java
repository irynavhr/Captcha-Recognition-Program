import java.io.*;
import java.io.File;
import java.io.IOException;

import java.util.Date;

public class ImageTest2
{
    public static void main(String[] args)
   	{
        // занальна кiлькiсть captcha
        int counter = 0;
        // кiлькiсть таких, що не спiвпали з вiдповiддю
        int counterDifferent = 0;

        // час початку тесту
        long startTime = new Date().getTime();

        try {
            // читаємо файл з вiдповiдями
            BufferedReader br = new BufferedReader(new FileReader("./test.txt"));
            // читаємо чергову вiдповiдь з файла
            String line = br.readLine();
            // номер першої captcha
            int index = 0;
            // iм'я файла
            String fileName = "";

            // поки не пройшли всi captcha
            while ((line != null) && (index < 310)) {
                // генеруємо iм'я чергового файла
                fileName = "./allimages/image" + Integer.toString(index) + ".png";
                // номер наступного файла на 1 бiльший
                index++;
                // читаємо i розпiзнаємо captcha
                Captcha captcha = new Captcha(fileName);
                // збiльшуємо лiчильник оброблених
                counter++;
                // виводимо результати роботи
                System.out.print("Файл: " + fileName + " captcha: " + captcha.GetString() + " для порiвняння: " +
                                  line + " результат: ");
                // якщо результати не спiвпадають - збiльшуємо кiлькiсть невгаданих
                if (!(captcha.GetString().equals(line))) {
                    counterDifferent++;
                    System.out.println("Нi");
                } else System.out.println("Так");
                // читаємо чергову вiдповiдь
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // час закiнчення роботи тесту
        long endTime = new Date().getTime();
        // виводимо кiнцевi результати на екран
        System.out.println("Результат:\n Загалом: " + Integer.toString(counter) + "; вiдрiзняються: " +
                           Integer.toString(counterDifferent));
        System.out.println("Час роботи: " + (endTime - startTime) + " мс; тобто " +
                           ((endTime - startTime)/counter) + " мс на captcha.");
	}
}
