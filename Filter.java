import java.lang.Math;

class Filter {

	// пороговий фiльтр
	// imageArray - масив, що обробляється
	// w - ширина рисунка, записаного в масивi
	// h - висота рисунка, записаного в масивi
	// threshold - значення порогу
	// moreThanThreshold - значення, на яке мiняється елемент масиву, якщо його попереднє значення
	//       бiльше порогового
	// lessThanThreshold - значення, на яке мiняється елемент масиву, якщо його попереднє значення
	//       менше порогового
	// В РЕЗУЛЬТАТi мiняється масив imageArray
    public static void threshold (int [] imageArray, int w, int h, int threshold,
    	int moreThanThreshold, int lessThanThreshold) {

	    for(int i=0; i<w*h; i++)
		    if (imageArray[i] > threshold)
		        imageArray[i] = moreThanThreshold;
		    else
			    imageArray[i] = lessThanThreshold;
	}
	 public static void Contrast(int [] array, int w, int h) {

	    int [] histo = new int[256];
	    int min = 255, max = 0;

	    // шукаємо найбільше та найменше значення кольору пікселів
        for (int i = 0; i < h; i++)
        	for (int j = 0; j < w; j++) {
                if (array[i*w+j] < min) min = array[i*w+j];
	                if (array[i*w+j] > max) max = array[i*w+j];
	                }

	    // якщо в зображенні колір лише один - використовуємо його
	     if (min == max)
	         histo[min] = 127;
	     else
	    // інакше - будуємо розподіл кольорів
	         for (int i = min; i < max + 1; i++)
	             histo[i] = (i - min) * 255 / (max - min);

	   // міняємо значення кольорів пікселів, роблячи зображення більш контрастним
	     for (int i = 0; i < h; i++)
	          for (int j = 0; j < w; j++)
	              array[i*w+j] = histo[array[i*w+j]];
	}


    public static void BlackStuff(int [] array, int w, int h) {

	     // робимо копію масиву зображення
	     int [] img = new int [w*h];
	     for (int i = 0; i < h; i++)
	          for (int j = 0; j < w; j++)
	               img[i*w+j] = array[i*w+j];

	     // видаляємо вертикальні лінії
	     for (int i = 0; i < h; i++)
	          for (int j = 0; j < w; j++)
	               if (i > 0 && i < h - 1)
	                  if (img[i*w+j] < img[(i-1)*w+j] &&
	                            (img[i*w+j] - img[(i-1)*w+j]) * (img[i*w+j] - img[(i+1)*w+j]) > 5000)
	                            array[i*w+j] = array[(i-1)*w+j];

	     // видаляємо горизонтальні лінії
	     for (int i = 0; i < h; i++)
	          for (int j = 0; j < w; j++)
	               if (j > 0 && j < w - 1)
	                   if (img[i*w+j] < img[i*w+j-1] &&
	                            (img[i*w+j] - img[i*w+j-1]) * (img[i*w+j] - img[i*w+j+1]) > 5000)
	                            array[i*w+j] = array[i*w+j-1];
	 }

	  public static void Smooth(int [] array, int w, int h) {

	      int SSIZE = 3;
	      // робимо копію масиву зображення
	      int [] img = new int [w*h];
	      for (int i = 0; i < h; i++)
	          for (int j = 0; j < w; j++)
	              img[i*w+j] = array[i*w+j];

	      for (int i = SSIZE/2; i < h - SSIZE/2; i++)
	          for (int j = SSIZE/2; j < w - SSIZE/2; j++) {
	              int val = 0;
	              for (int di = 0; di < SSIZE; di++)
	                  for (int dj = 0; dj < SSIZE; dj++)
	                       val += img[(i+di-SSIZE/2)*w+j+dj-SSIZE/2];
	                       array[i*w+j] = val / (SSIZE * SSIZE);
	           }

	      // видаляємо границі
	       for (int i = 0; i < h; i++) {
	           array[i*w] = array[i*w+1];
	           array[i*w+w-1] = array[i*w+w-2];
	       }
	       for (int j = 0; j < w; j++) {
	           array[j] = array[w+j];
	           array[(h-1)*w+j] = array[(h-2)*w+j];
	       }
	   }

	   public static void Median(int [] array, int w, int h) {

	      int MSIZE = 3;
	      // робимо копію масиву зображення
	      int [] img = new int [w*h];
	      for (int i = 0; i < h; i++)
	          for (int j = 0; j < w; j++)
	              img[i*w+j] = array[i*w+j];

	      int [] val = new int [MSIZE*MSIZE];

	      for (int i = 0; i < h; i++)
	          for (int j = 0; j < w; j++)
	              array[i*w+j] = 255;

	      for (int i = MSIZE/2; i < h - MSIZE/2; i++)
	          for (int j = MSIZE/2; j < w - MSIZE/2; j++) {
	               for (int di = 0; di < MSIZE; di++)
	                   for (int dj = 0; dj < MSIZE; dj++) {
	                            //getpixel(img, x + j - SSIZE/2, y + i - SSIZE/2, &r, &g, &b);
	                            //val[i * MSIZE + j] = r;
	                       val[di*MSIZE+dj] = img[(i+di-MSIZE/2)*w+j+dj-MSIZE/2];
	                   }

	                    // bubble sort
	                    for (int di = 0; di < MSIZE * MSIZE / 2 + 1; di++)
	                        for (int dj = di + 1; dj < MSIZE * MSIZE; dj++)
	                            if (val[di] > val[dj]) {
	                                int k = val[di];
	                                val[di] = val[dj];
	                                val[dj] = k;
	                            }

	                    array[i*w+j] = val[MSIZE * MSIZE / 2];
	                }
	     }
	     public static void Scale(int [] array, int w, int h, float ratio) {

			  int w_new = (int)Math.floor(ratio * w);
			  int h_new = (int)Math.floor(ratio * h);
			  // робимо копію масиву зображення
			  int [] img = new int [w_new*h_new];
			  for (int i = 0; i < h_new; i++)
			      for (int j = 0; j < w_new; j++)
			           array[i*w_new+j] = img [(int)Math.floor((i/ratio)*w+j/ratio)];
	    }


}