# WeatherApp
### Table of contents
* [Project description](#project-description)
* [Used technologies](#used-technologies)
* [How to compile it?](#how-to-compile-it)
* [How to use it?](#how-to-use-it)
* [Project status](#project-status)

### Project description
**WeatherApp** is a mobile application that uses **OpenWeatherMap API** to present information about **current weather**
(actual temperature, perceptible temperature, weather description, atmospheric pressure, air humidity, sunrise, sunset), 
**daily forecast for next 7 days** and **hourly forecast for next 24 hours**. To find specific data you can **enter the
name** of the city by yourself or **use GPS signal**. The application consists of **two design modes**: Standard Mode 
(default one available after launching the app) and Senior Mode (with bigger fonts, full weekdays' names, etc.).

### Used technologies
* Kotlin 1.4.30 - langauge in which the project has been written,
* Retrofit 2.9.0 - library responsible for communicating with API,
* Glide 4.11.0 - library responsible for displaying pictures with given url,
* Play-Services-Location 18.0.0 - library responsible for using GPS,
* Lifecycle 2.3.0 - library responsible for using LiveData objects and MVVM architecture,
* Material 1.3.0 - library responsible for using Material Design components,
* Navigation 2.3.4 - library responsible for moving between the fragments.

### How to compile it?
WeatherApp uses REST API with API KEY. If you just want to test it on your mobile phone, you can download an .apk 
from Releases and install it. Otherwise, if you want to change something in the code and compile it by yourself, 
you'll have to **generate your own API KEY** as it is not contained in this repository. To do it, follow the steps 
from this [instruction](https://developers.themoviedb.org/3/getting-started/introduction) and replace
the value of apiKey in the **line 24 of model/api/ApiRequest.kt file** with your key:
```
private const val apiKey = "YOUR_API_KEY"     // place your apiKey here
```

### How to use it?
After launching the application, there is a possibility that you'll see **"NO DATA" caption** instead of the weather information.
That means that you have no internet connection or you haven't enabled GPS. It is worth to launch both of them before launching the app,
as **it tries to get data from current location just after starting running**. If you forget to do it, don't worry - you can do it manually
thanks to the three buttons placed in the app bar. The main screen contains also all the data that has been mentioned in 
[project description](#project-description).

<p align="center">
<img src="https://user-images.githubusercontent.com/43967269/114026491-95b28680-9876-11eb-8d71-fc73218a9fe7.png" alt="MainScreen1">
<img src="https://user-images.githubusercontent.com/43967269/114026496-96e3b380-9876-11eb-9229-b09d1119fd48.png" alt="MainScreen2">
</p>

First button allows user to **enter the name of the city** and get its weather data. If given city name doesn't exist in OpenWeatherMap 
database, the application will show a **snackbar** with appropriate information. Snackbars can also pop up if user tries to get the data
without internet connection. Second button is used to get weather data by current location. This option requires enabling GPS signal
(and permission to use it), so if it is disabled, again, aprropriate snackbar will be displayed. The last button is responsible for
**changing display mode** from Standard to Senior and inversly. Each of these three features is represented by **Material Design Dialog**.

<p align="center">
<img src="https://user-images.githubusercontent.com/43967269/114026482-94815980-9876-11eb-8d3c-02e5b28c35c5.png" alt="Dialog1">
<img src="https://user-images.githubusercontent.com/43967269/114026484-9519f000-9876-11eb-991f-d32e7fc772c5.png" alt="Dialog2">
<img src="https://user-images.githubusercontent.com/43967269/114026485-9519f000-9876-11eb-87d6-bdbdff98af32.png" alt="Dialog3">
</p>

As it was mentioned in [project description](#project-description), the **Senior Mode** looks different than Standard Mode - the fonts and the icons
are bigger in the main content as well as in the dialogs and in the snackbars, weekdays' names are in full representation and whole text has no transparency.

<p align="center">
<img src="https://user-images.githubusercontent.com/43967269/114026487-95b28680-9876-11eb-9682-3beee44ddcbc.png" alt="SeniorMainScreen">
<img src="https://user-images.githubusercontent.com/43967269/114026488-95b28680-9876-11eb-9c45-bfef20d59070.png" alt="SeniorDialog1">
<img src="https://user-images.githubusercontent.com/43967269/114026494-964b1d00-9876-11eb-9189-6a99eb5208d2.png" alt="SeniorDialog3">
</p>

### Project status
The project is useful and almost complete, but there are still some **features that should be added**:
* **saving the state** of the application to have the weather data even without internet connection 
and to eliminate necessity of writing the city name each time we launch the app,
* **widget** with current weather which could be constantly shown on the phone's home screen,
* **autorefreshing** the data after certain amount of time (user's decision),
* **changing language** (Polish-English) or second version of the application.
