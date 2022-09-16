# Eye&Pupil Tracker

Android application able to recognize, with the help of two neural networks, the eyes and pupils of the faces captured by the phone's camera.

![merge](https://github.com/DaniDF/eye_pupil_tracker/blob/master/Report/LatexPdf/merge.png)

## Folders

> + [Android][lk_andr]: android application
> + [CSVConverter][lk_csv]: XML generator according to csv fiftyone dataset file
> + [LabelImgModifier][lk_mod]:  XML generator according to [LabelImage](https://github.com/tzutalin/labelImg) dataset file
> + [Models][lk_tf]: all about neural networks
> + [Remote_training][lk_rt]: tools for neural network training
> + [Report][lk_rp]: Latex Report Pdf + PowerPoint Presentation + Jupyter Notebook

[lk_andr]: https://github.com/DaniDF/sistemiDigitali2022/tree/master/Android	"Android"
[lk_csv]: https://github.com/DaniDF/sistemiDigitali2022/tree/master/CSVConverter "CSVConverter"
[lk_rt]: https://github.com/DaniDF/sistemiDigitali2022/tree/master/Remote_training "Remote training"
[lk_rp]: https://github.com/DaniDF/sistemiDigitali2022/tree/master/Report "Report"
[lk_tf]: https://github.com/DaniDF/sistemiDigitali2022/tree/master/Models/tflite "Models"
[lk_mod]: https://github.com/DaniDF/sistemiDigitali2022/tree/master/LabelImgModifier/LabelImgModifier "LabelImgModifier"

## Quick start

1. Download the apk file [here][lk_apk]
2. Install it on android device

<img src="https://github.com/DaniDF/eye_pupil_tracker/blob/master/Report/LatexPdf/ProgettoAndroid/Images/CameraApp_Screen_home.jpg" width="250">

## Overview

* [**Play game**](#Play-game): an easy demo game, a quiz game provided by [OpentDB][lk_opentdb] APIs
* [**Nerd mode**](#Nerd-mode) (_top right_): shows all the information acquired from the camera
* [**Calibration**](#Calibration)

[ lk_apk ]: https://github.com/DaniDF/sistemiDigitali2022/releases/download/v1.0.2/GazeDetection_v2.1.6.apk	"Apk download link"
[ lk_opentdb ]: https://opentdb.com "OpentDB official site"

### Play game

> In this part moving the eyes you will move the red dot. Moving the red dot over an answer you will confirm it.
> Enjoy the game

<img src="https://github.com/DaniDF/eye_pupil_tracker/blob/master/Report/LatexPdf/ProgettoAndroid/Game/Images/CameraApp_Screen_quiz.jpg" width="250">

### Nerd mode

> In this mode pressing the button **_Start preview_** the front camera preview will be showed on top of the screen. There is a **_Switch camera_** button that allows you to choose between front and main rear camera.
>
> With the button **_Analyze_** the _eye-tracking_ starts and the corresponding boxes and labels will be showed, the central slider is usefull for filter the results by accuracy.
>
> With the **_Eye_** button the _gaze-tracking_ starts and the result is showed on top of the _eye-tracking_'s results.

<img src="https://github.com/DaniDF/eye_pupil_tracker/blob/master/Report/LatexPdf/ProgettoAndroid/NerdMode/Images/CameraApp_Screen_nerd_layout.jpg" width="250">

### Calibration

<img src="https://github.com/DaniDF/eye_pupil_tracker/blob/master/Report/LatexPdf/ProgettoAndroid/Calibration/Images/CameraApp_Screen_calibration.jpg" width="250">

## Authors

+ Luigi di Nuzzo @[luigidinuzzo](https://github.com/luigidinuzzo)
+ Daniele Foschi @[DaniDF](https://github.com/DaniDF)
+ Filippo Veronesi @[filippoveronesi](https://github.com/filippoveronesi)
