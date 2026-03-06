# CAPTCHA Recognition Program

## Overview

This project implements a simple CAPTCHA recognition system written in Java.
The program processes CAPTCHA images, applies basic image preprocessing techniques, and attempts to recognize the characters contained in the image.

The project demonstrates fundamental concepts of **image processing and pattern recognition**, including thresholding, contrast adjustment, noise filtering, and symbol matching.

The system is evaluated on a dataset of CAPTCHA images and compares predicted results with the expected labels.

---

## Project Structure

```
Captcha_recognition_program/
│
├── allimages/            # Dataset of CAPTCHA images
├── font.png              # Reference font used for symbol recognition
├── test.txt              # Ground truth labels for each CAPTCHA image
│
├── Captcha.java          # Core class for CAPTCHA processing and recognition
├── Filter.java           # Image preprocessing filters
├── ImageTest2.java       # Main program for testing recognition accuracy
│
├── Captcha.class
├── Filter.class
├── ImageTest2.class
```

---

## How It Works

The recognition pipeline consists of several steps:

### 1. Image Loading

The program reads CAPTCHA images from the dataset.

### 2. Image Preprocessing

Several filters are applied to improve recognition:

* **Thresholding** – converts the image to binary values
* **Contrast enhancement** – improves visibility of characters
* **Noise removal** – reduces background artifacts

These filters are implemented in `Filter.java`.

### 3. Character Segmentation

The CAPTCHA is assumed to contain **4 characters**.
The program extracts character regions based on predefined positions.

### 4. Symbol Recognition

Each extracted symbol is compared with characters from `font.png`.
The closest match is selected as the predicted character.

### 5. Evaluation

The program reads the correct CAPTCHA strings from `test.txt` and compares them with predicted results to measure accuracy.

---

## Running the Program

Compile the Java files:

```
javac Captcha.java
javac Filter.java
javac ImageTest2.java
```

Run the test program:

```
java ImageTest2
```

The program will:

* process CAPTCHA images from `allimages/`
* recognize characters
* print predictions
* show which predictions differ from the ground truth

---

## Dataset

The dataset contains **310 CAPTCHA images**.

Each image corresponds to one line in `test.txt`, which contains the correct 4-character CAPTCHA string.

Example:

```
Image: image0.png
Label: 86D9
```

---

## Technologies Used

* **Java**
* **Java ImageIO**
* Basic **image processing techniques**
* **Pattern matching**

---

## Possible Improvements

This project uses a rule-based recognition approach. Future improvements could include:

* using **machine learning / deep learning** for CAPTCHA recognition
* applying **CNN models**
* improving **character segmentation**
* expanding the dataset for better accuracy

---

## Educational Purpose

This project was created as a learning exercise to explore:

* basic image processing
* CAPTCHA structure
* character recognition techniques


