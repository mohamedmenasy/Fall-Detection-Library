#  Fall Detection Library 

An Android library to detect the fall of a mobile user, it can be used in medical applications. 

The fall detection was based on the following paper:

*Ge, Yujia, and Bin Xu. “Detecting Falls Using Accelerometers by Adaptive Thresholds in Mobile Devices.” Journal of Computers, vol. 9, no. 7, 2014, doi:10.4304/jcp.9.7.1553-1559.*

## Features

### Non-technical features

- Detects a free fall of the device. The library can differentiate a free fall from other kinds of device motion, like shaking, rotating, and moving.
- The detection working while no user interface is in the foreground.
- When a free fall ends, the library stores the event with its duration in a Room database.

### Technical features

- Written completely in Kotlin.
- Used Koltin [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html).
- Used Room database.
- Used Material design
- Provide complete unit tests.
- Provide Espresso tests.

## Demo

[![Demo Vedio](http://img.youtube.com/vi/KTe6MlUodhQ/0.jpg)](http://www.youtube.com/watch?v=KTe6MlUodhQ)

## Running the app

##### Using android studio

Just import the project and hit `run` button.

##### Using command line 

```
$ adb shell am start -n 'com.mohamedmenasy.falldetection.demo/com.mohamedmenasy.falldetection.demo.MainActivity'
```

## Running the tests

###### To run the library tests

```bash
./gradlew app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.mohamedmenasy.falldetection.FallDetectionDaoTest

./gradlew app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.mohamedmenasy.falldetection.FallDetectionServiceTest
```

###### To run MainActivity Test:

```
$ ./gradlew app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.mohamedmenasy.falldetection.demo.MainActivityTest
```

## Todo

Enhance the accuracy of detection by examining the following researches:

1. Ballı S., Sağbaş E.A., Peker M. (2019) A Mobile Solution Based on Soft Computing for Fall Detection. In: Paiva S. (eds) Mobile Solutions and Their Usefulness in Everyday Life. EAI/Springer Innovations in Communication and Computing. Springer, Cham
2. 何 文武. “Design and Implementation of Fall Monitoring APP Based on Android.” *Computer Science and Application*, vol. 09, no. 07, 2019, pp. 1426–1433., doi:10.12677/csa.2019.97160.
3. Ni, Jenny, et al. “Fall Guard.” *Proceedings of the 2019 4th International Conference on Biomedical Signal and Image Processing (ICBIP 2019)  - ICBIP '19*, 2019, doi:10.1145/3354031.3354055.
4. Tran, Hai Anh, et al. “A New Fall Detection System on Android Smartphone: Application to a SDN-Based IoT System.” *2017 9th International Conference on Knowledge and Systems Engineering (KSE)*, 2017, doi:10.1109/kse.2017.8119425.
5. W. Yi, O. Sarkar, T. Gonnot, E. Monsef and J. Saniie, "6LoWPAN-enabled fall detection and health monitoring system with Android smartphone," *2016 IEEE International Conference on Electro Information Technology (EIT)*, Grand Forks, ND, 2016, pp. 0174-0178.

## Author

- **Mohamed Nabil** - *Initial work* - [linkedin](<https://www.linkedin.com/in/mohamedmenasy/>)