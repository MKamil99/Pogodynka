# WeatherApp
### Table of contents
* [Project description](#project-description)
* [Used technologies](#used-technologies)
* [How to compile it?](#how-to-compile-it)
* [How to use it?](#how-to-use-it)
* [Project status](#project-status)
* [Credits](#credits)

### Project description
**WeatherApp** is a mobile application that uses [**OpenWeatherMap API**](https://openweathermap.org/) to present information 
about **current weather** (actual temperature, perceptible temperature, weather description, atmospheric pressure, air humidity, 
sunrise, sunset), **daily forecast for next 7 days** and **hourly forecast for next 24 hours**. To find specific data you can 
**enter the name** of the city by yourself or **use GPS signal**. The application consists of **two design modes**: Standard Mode 
(default one available after launching the app) and Senior Mode (with bigger fonts, full weekdays' names, etc.).

### Used technologies
* Kotlin 1.5.10 - langauge in which the project has been written,
* Retrofit 2.9.0 - library responsible for communicating with API,
* Glide 4.11.0 - library responsible for displaying pictures with given url,
* Play-Services-Location 18.0.0 - library responsible for using GPS,
* Material 1.3.0 - library responsible for using Material Design components,
* Navigation 2.3.5 - library responsible for moving between the fragments,
* Room 2.3.0 - library responsible for storing weather data locally.

### How to compile it?
WeatherApp uses REST API with API KEY. If you just want to test it on your mobile phone, you can download an .apk 
from Releases and install it. Otherwise, if you want to change something in the code and compile it by yourself, 
you'll have to **generate your own API KEY** as it is not contained in this repository. To do it, follow the steps 
from this [instruction](https://openweathermap.org/appid) and replace
the value of apiKey in the **line 28 of model/api/ApiRequest.kt file** with your key:
```
private const val apiKey = "YOUR_API_KEY"     // place your apiKey here
```

### How to use it?
After launching the application for the first time, you'll see **"NO DATA" caption** instead of the weather information. In further sessions
the **application will remember the data** from the past, but this time use one of the three buttons placed in the app bar to find forecasts
by the city name or your location. The main screen contains also everything that has been mentioned in [project description](#project-description).

<p align="center">
<img src="https://user-images.githubusercontent.com/43967269/122641730-64242a00-d107-11eb-81dd-d2a6de9c20f9.png" alt="MainScreen1">
<img src="https://user-images.githubusercontent.com/43967269/122641732-64bcc080-d107-11eb-8d5f-5c87eadadfa7.png" alt="MainScreen2">
</p>

First button allows user to **enter the name of the city** and get its weather data. If given city name doesn't exist in OpenWeatherMap 
database, the application will show a **snackbar** with appropriate information. Snackbars can also pop up if user tries to get the data
without internet connection. Second button is used to get weather data by current location. This option requires enabling GPS signal
(and permission to use it), so if it is disabled, again, appropriate snackbar will be displayed. The last button is responsible for
**changing display mode** from Standard to Senior and inversly. Each of these three features is represented by **Material Design Dialog**.

<p align="center">
<img src="https://user-images.githubusercontent.com/43967269/122641733-64bcc080-d107-11eb-8f33-3a1f55ec5d17.png" alt="Dialog1">
<img src="https://user-images.githubusercontent.com/43967269/122641735-65555700-d107-11eb-81cd-2a399ca8d371.png" alt="Dialog2">
<img src="https://user-images.githubusercontent.com/43967269/122641736-65eded80-d107-11eb-9c7a-3c73044da668.png" alt="Dialog3">
</p>

As it was mentioned in [project description](#project-description), the **Senior Mode** looks different than Standard Mode - the fonts and the icons
are bigger in the main content as well as in the dialogs and in the snackbars, weekdays' names are in full representation and whole text has no transparency.

<p align="center">
<img src="https://user-images.githubusercontent.com/43967269/122641737-65eded80-d107-11eb-8249-5b0f783858ac.png" alt="SeniorMainScreen">
<img src="https://user-images.githubusercontent.com/43967269/122641738-65eded80-d107-11eb-866e-e2448bb91a76.png" alt="SeniorDialog1">
<img src="https://user-images.githubusercontent.com/43967269/122641739-66868400-d107-11eb-97d6-1364ac8b1974.png" alt="SeniorDialog3">
</p>

### Project status
Although it would be great to have a widget with weather forecast, WeatherApp is considered as **finished**.

### Credits
Launcher icon made by [Those Icons](https://www.flaticon.com/authors/those-icons) from [Flaticon](https://www.flaticon.com/).
