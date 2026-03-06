import java.io.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;

class Captcha {

    private Image imageToDraw;
    private Image imageToBuffer;
    private Image imageFont;
    private Image imageFontToBuffer;

    // символи, що входять до шрифта
    private static char [] symbols = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'K', 'M', 'P', 'T',
                                      'W', 'Y', 'Z', '2', '3', '4', '6', '7', '8', '9'};
    private static int symbolsCount = 22;
    // кiлькiсть символiв у captcha
    private static int symbolsCaptchaCount = 4;
    // позицiї лiвих верхнiх кутiв символiв у captcha
    private int [] positions = {11, 39, 67, 94};
    // висота i максимальна ширина символа у captcha
    private static int symbolHeight = 17;
    private static int symbolWidth = 26;
    private String captchaString;   

    public Captcha(String fileName) {
        captchaString = "";
        SetImage(fileName);
    }

    public void SetImage (String fileName) {
	    try {
	        imageToDraw = ImageIO.read(new File(fileName));
	        BufferedImage imageToBuffer = ImageIO.read(new File(fileName));

	        // ширина i висота вiдкритої captcha
	        int wCaptcha = imageToBuffer.getWidth();
	        int hCaptcha = imageToBuffer.getHeight();
            int w = wCaptcha;
            // висота символiв у captcha
            int h = symbolHeight;

            // масив пiкселiв зображення
	        int [] rgbsImage = new int[wCaptcha*hCaptcha];
	        // беремо всi пiкселi
	        imageToBuffer.getRGB(0, 0, wCaptcha, hCaptcha, rgbsImage, 0, wCaptcha);
            // беремо один компонент кольору
	        for(int i=0;i<wCaptcha * hCaptcha;i++)
		        rgbsImage[i] = (rgbsImage[i] & (0xFF00)) >> 8;
	        // тестовий запис 1
	        ///saveArray1(rgbsImage,wCaptcha,hCaptcha,"1 Pixels original green.txt");
	        // виконуємо фiльтрацiю i беремо лише пiкселi, необхiднi для розпiзнавання
            int [] rgbs = new int[w*h];
            int shift = 5 * w;
	        for(int i=shift;i<(22*w);i++)
		        if (rgbsImage[i] > 200)
		            rgbs[i-shift] = 0;
		        else
		  	        rgbs[i-shift] = 1;
	        // тестовий запис 2
	        ///saveArray0(rgbs,w,h,"2 Pixels converted.txt");

            // вiдкриваємо файл з символами шрифта
	        imageFont = ImageIO.read(new File("font.png"));
	        BufferedImage imageFontToBuffer = ImageIO.read(new File("font.png"));
	        // визначаємо параметри зображення шрифта
	        int w_font = imageFontToBuffer.getWidth();
            int h_font = imageFontToBuffer.getHeight();
            // видiляємо мiсце в пам'ятi для пiкселiв
            int [] rgbsFont = new int[w_font * h_font];
            // читаємо пiкселi
            imageFontToBuffer.getRGB(0, 0, w_font, h_font, rgbsFont, 0, w_font);
            // беремо один компонент кольору
            for (int i=0; i<w_font * h_font; i++) 
		        rgbsFont[i] = (rgbsFont[i] & (0xFF00)) >> 8;
	        // тестовий запис 3
	        ///saveArray1(rgbsFont,w_font,h_font,"3 Pixels font original green.txt");
		    // виконуємо фiльтрацiю з використання вiдповiдного класу
            Filter.threshold(rgbsFont,w_font,h_font,200,0,1);
	        // тестовий запис 4
	        ///saveArray0(rgbsFont,w_font,h_font,"4 Pixels font converted.txt");

            // максимальна ширина символiв шрифта = 26
            int maxLenFont = symbolWidth;
            int maxLenRect = symbolWidth;
            // висота символiв шрифта = 17
            int maxHeightRect = symbolHeight;

            // масив символiв captcha - видiляємо мiсце
            int [][] captcha = new int [symbolsCaptchaCount][maxLenRect*maxHeightRect];
            // читаємо символи з captcha
            getCaptchaRectangles(rgbs,w,h,captcha,maxLenRect,maxHeightRect,symbolsCaptchaCount);
            // тестовий запис 5
            ///for (int i = 0; i < symbolsCaptchaCount; i++)
            ///    saveArray0(captcha[i],maxLenRect,maxHeightRect,"5 " + i + " captcha.txt");    

            // кiлькiсть символiв шрифта - 22
            int [][] font = new int [symbolsCount][maxLenRect*maxHeightRect];
            // видiляємо символи шрифта i записуємо в масив
            getFontRectangles(rgbsFont,w_font,h_font,font,maxLenRect,maxHeightRect,symbolsCount);
            // тестовий запис 6
            ///for (int i = 0; i < symbolsCount; i++)
            ///    saveArray0(font[i],maxLenRect,maxHeightRect,"6 " + i + " font.txt");    

            // змiннi для визначення найменшої вiдстанi
            int dist, minDist, iMinDist;
            // порiвнюємо символи в captcha i шрифтi
            for (int i = 0; i < symbolsCaptchaCount; i++) {
                iMinDist = 0;
                minDist = getDistance(captcha[i],font[iMinDist],maxLenRect,maxHeightRect);
                for (int j = 1; j < symbolsCount; j++) {
                	dist = getDistance(captcha[i],font[j],maxLenRect,maxHeightRect);
                	if (dist < minDist) {
                		minDist = dist;
                		iMinDist = j;
                	}
                }
                // додаємо до рядка розшифровку знайденого символа
                captchaString = captchaString + symbols[iMinDist];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // метод для повернення розшифрованого рядка
    public String GetString () {
        return captchaString;
    }

    public void getFontRectangles(int [] src, int w, int h, int [][] rects, int wR,int hR, int count){
      	// прапорцi для визначення стовпчикiв з символами
       	int [] elements = new int [w];
       	for (int i = 0; i < w; i++) elements[i] = 0;
       	// перевiряємо всi стовпчики i знаходимо непустi
       	int index = 0;
       	for (int j = 0; j < h; j++)
       	    for (int i = 0; i < w; i++) {
       	        if (src[index] != 0)
       	            elements[i] = 1;
       	        index++;
       	    }
       	// параметри прямокутника для вирiзання символiв
       	int rectWBegin = 0;
       	int rectWidth = 0;
       	int rectHBegin = 0;
       	int rectHeight = symbolHeight;
       	// змiннi для додаткових обрахункiв
       	int wdt;
       	int hgt;
       	// iндекс в масивi призначення
       	int dstIndex;
       	// iндекс у вихiдному масивi
       	int srcIndex;
       	// беремо count прямокутникiв
       	for (int c = 0; c < count; c++) {
       		// знаходимо початок наступного пррямокутника
            while (elements[rectWBegin] == 0) rectWBegin++;
            // знаходимо ширину прямокутника
            rectWidth = 1;
            while (elements[rectWBegin+rectWidth] != 0) rectWidth++;
            rectWidth--;
            // маємо прямокутник [(rectWBegin,rectHBegin) - (rectWBegin+rectWidth,rectHBegin+rectHeight)]
            // або (в залежностi вiд структури шрифта)
            //                [(rectWBegin,0) - (rectWBegin+rectWidth,16)]
            // беремо iндекс першого символа
            index = rectWBegin;
            // беремо ширину фрагменту
            wdt = (wR>rectWidth+1) ? rectWidth+1 : wR;
            // беремо висоту фрагменту
            hgt = rectHeight;
            // записуємо прямокутник
            srcIndex = index;
            for (int i = 0; i < hgt; i++) {
              	dstIndex = i * wR;
                for (int j = 0; j < wdt; j++) {
                    rects[c][dstIndex] = src[srcIndex];
                    dstIndex++;
                    srcIndex++;
                }
                index += w;
                srcIndex = index;
            }
            rectWBegin = rectWBegin + rectWidth + 1;
      	}
    }

    public void getCaptchaRectangles(int [] src, int w, int h, int [][] rects, int wR,int hR, int count){
        // прапорець непустих стовпчикiв пiкселiв
        int [] elements = new int [w];
        for (int i = 0; i < w; i++) elements[i] = 0;
        // пiдраховуємо кiлькiсть непустих пiкселiв у зображеннi
        int index = 0;
        for (int j = 0; j < h; j++)
            for (int i = 0; i < w; i++) {
                elements[i] += src[index];
                index++;
            }
        // вирiзаємо прямокутник
        int rectWBegin;
        int rectWidth;
        int rectHBegin = 0;
        int rectHeight = symbolHeight;
        int wdt;
        int hgt;
        int dstIndex;
        int srcIndex;

        for (int c = 0; c < count; c++) {
            rectWBegin = positions[c];
            // початкова ширина символа
            rectWidth = 19;
            if ((elements[rectWBegin - 1]>=rectHeight-1) && (elements[rectWBegin - 2]>=rectHeight-3)) {
                rectWBegin -= 2;
                rectWidth = symbolWidth;
            }
            // беремо прямокутник
            index = rectWBegin;
            // визначаємо ширину i висоту прямокутника
            wdt = (wR>rectWidth+1) ? rectWidth+1 : wR;
            hgt = rectHeight;
            // записуємо прямокутник
            srcIndex = index;
            for (int i = 0; i < hgt; i++) {
                dstIndex = i * wR;
                for (int j = 0; j < wdt; j++) {
                    rects[c][dstIndex] = src[srcIndex];
                    dstIndex++;
                    srcIndex++;
                }
                index += w;
                srcIndex = index;
            }
        }
    }

    public int getDistance(int [] rect1, int [] rect2, int w, int h){
      	// початкове значення вiдстанi
       	int dist = 0;
       	// обчислюємо вiдстань
       	for (int i = 0; i < w*h; i++)
       		dist += rect1[i] == rect2[i] ? 0 : 1;
       	return dist;
    }

    public void saveArray1(int [] array, int w, int h, String FileName) {
        try {
            FileWriter writer = new FileWriter(new File(FileName));
            int index = 0;
            for (int j = 0; j < h; j++) {
	            for (int i = 0; i < w; i++) {
	                if (array[index]<10)
	                    writer.write("  " + array[index] + " ");
	                else if (array[index]<100)
	                    writer.write(" " + array[index] + " ");
	                else
                        writer.write(array[index] + " ");
                    index++;
                }
                writer.write("\n");
	        }
	        writer.close();
	    } catch (IOException e) {
	       	e.printStackTrace();
	    }
    }

    public void saveArray0(int [] array, int w, int h, String FileName) {
        try {
            FileWriter writer = new FileWriter(new File(FileName));
            int index = 0;
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    writer.write(" " + array[index]);
                    index++;
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}