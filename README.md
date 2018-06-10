# Project description
[![Build Status on Travis:](https://travis-ci.org/dkhmelenko/Varis-Android.svg?branch=master)](https://travis-ci.org/dkhmelenko/Varis-Android)
[![codecov.io](https://codecov.io/github/dkhmelenko/Varis-Android/branch/develop/graph/badge.svg)](https://codecov.io/github/dkhmelenko/Varis-Android)

This project is unofficial Android client for Travis CI. 

It uses [API v2](http://docs.travis-ci.com/api/#overview) from Travis CI for fetching all required information about repositories, build tasks and build history.
The app has the following features:
- user login to Travis CI and Travis CI Pro
- list of repositories
- build history
- build log
- restart/cancel build
- search with history
- intent filter for opening build links

[![Varis on Google Play Store](/screenshots/google-play-badge.png)](https://play.google.com/store/apps/details?id=com.khmelenko.lab.varis)

![Repositories list](/screenshots/main_screen.png)    ![Build history](/screenshots/build_history.png)    ![Build Details](/screenshots/build_details.png)

## Copyrights
*This app is neither offered by Travis CI GmbH nor is this app and/or the provider of this app in any way affiliated with Travis CI GmbH and/or its products.*

# Open source libraries
- [Android appcompat v7](https://github.com/android/platform_frameworks_support/tree/master/v7/appcompat)
- [Retrofit](https://github.com/square/retrofit)
- [OkHttp](https://github.com/square/okhttp)
- [Gson](https://code.google.com/p/google-gson/)
- [Dagger 2](https://github.com/google/dagger)
- [Butter Knife](http://jakewharton.github.io/butterknife/)
- [RxJava](https://github.com/ReactiveX/RxJava)
- [CircleImageView](https://github.com/hdodenhof/CircleImageView)
 
# Contribution
In case you have ideas or found an issue, don't hesitate to create pull request or an issue.

# Current status

Currently the main app module is `app-v3`. In this module the following updates are happening:
- migration to Kotlin
- changing app architecture to use [Android Achitecture components](https://developer.android.com/topic/libraries/architecture/)
- Migration to [API v3](https://docs.travis-ci.com/user/triggering-builds)

The module `app` is currently NOT developed anymore. However, it is working fine and is using  [API v2](http://docs.travis-ci.com/api/#overview).

# License

[Apache Licence 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Copyright 2015 Dmytro Khmelenko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
