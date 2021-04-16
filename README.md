CameraUnit Demo App
=====

![video](https://github.com/oppo/CameraUnit/blob/main/screenshots/Screenshot_video_interface.jpg "video screen shot")
![capture](https://github.com/oppo/CameraUnit/blob/main/screenshots/Screenshot_capture_interface.jpg "capture screen shot")

## About CameraUnit

[CameraUnit][CameraUnitLink] is an open interface for OPPO's imaging capabilities,
which can eliminate the gap between the system's imaging capabilities and third-party applications. 
Users can also get the same shooting experience as OPPO cameras in third-party applications.
Provides a lightweight, fast and effective way to access the camera functions of the Coloros system.

This project provides a solution for accessing the ability to CameraUnit SDK,
see [Configuration and initialization instructions][CameraUnitInstructions]
allowing you to refer to its call logic during the use of the SDK.

So far, this project has access to the following functions (depending on device model support):

| Video Function                | Photo Function           | Preview Function            |
| :----:                        | :----:                   | :----:                      |
| Video Stabilization           | Ultra Wide Angle Capture | Multi-Camera                |
| Video Super Stabilization     | Portrait Capture         | Capture HDR                 |
| Video Super Stabilization Pro | Night Capture            |                             |
| AI Night Video                |                          |                             |
| Video HDR                     |                          |                             |
| Video SlowMotion              |                          |                             |

Supported devices:

| A Series           | F Series           | R Series            | Other Series           |
| :----:             | :----:             | :----:              | :----:                 |
| OPPO A Series      | OPPO Find X2       | OPPO Reno4          | OPPO F17 Pro           |
|                    | OPPO Find X3       | OPPO Reno4 Lite     |                        |
|                    |                    | OPPO Reno4 pro      |                        |
|                    |                    | OPPO Reno4 F        |                        |
|                    |                    | OPPO Reno5          |                        |
|                    |                    | OPPO Reno6          |                        |

## About CameraUnit Demo App
Now, we provide a sample program to show you the calling process of the interface, 
you can also directly rely on the `camerax module` to your project for quick integration.

## Status
Version 1.0.0 is now released and stable. Updates are released periodically with new features and bug fixes.

Comments/Bugs/Questions/PR are always welcome! Please read [CONTRIBUTING.md][contributing] on how to report issues.

Build
-----
Building project with gradle is fairly straight forward:

```shell
git clone git@github.com:oppo/CameraUnit.git
cd CameraUnit
./gradlew :app:assembleRelease
```

**Note**: Make sure your *Android SDK* has the *Android Support Repository* installed, and that your `$ANDROID_HOME` environment
variable is pointing at the SDK or add a `local.properties` file in the root project with a `sdk.dir=...` line.

Development
-----------
Follow the steps in the [Build](#build) section to setup the project and then edit the files however you wish.
[Android Studio][android-studio] cleanly imports both CameraUnit's source and tests and is the recommended way to work with CameraUnit.

To open the project in Android Studio:

1. Go to *File* menu or the *Welcome Screen*
2. Click on *Open...*
3. Navigate to CameraUnit's root directory.
4. Select `setting.gradle`

## Issues & Getting Help
To report a specific problem or feature request, [open a new issue on Github][open-new-issue]. For questions, suggestions, or
anything else, email [CameraUnit's discussion group][discussion].

Please make sure to read the [Issue Reporting Checklist][issue-reporting-guidelines]
before opening an issue. Issues not conforming to the guidelines may be closed immediately.

## Contribution
Please make sure to read the [CONTRIBUTING.md][contributing] before making a pull request. 
For more details, see the [Contributing docs page][contributing-page].

Thank you to all the people who already contributed to this project!

## Thanks
* Airbnb lottie project member for [lottie](https://github.com/airbnb/lottie).
* FloatingActionButton project member for [FloatingActionButton](https://github.com/Clans/FloatingActionButton).
* CircleImageView project member for [CircleImageView](https://github.com/hdodenhof/CircleImageView).
* Glide project member for [glide](https://github.com/bumptech/glide).
* PermissionsDispatcher project member for [PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher).
* Everyone who has contributed code and reported issues!

## Author
@LiuCun at oppo, [@JianGuo Yang](https://github.com/lgyjg) at oppo.

## License
[Apache License 2.0][license]

Copyright (c) 2021 OPPO. All rights reserved.

[CameraUnitLink]: https://open.oppomobile.com/newservice/capability?pagename=cameraunit
[CameraUnitInstructions]: https://open.oppomobile.com/wiki/doc#id=10751
[issue-reporting-guidelines]: #
[open-new-issue]: https://github.com/oppo/CameraUnit/issues
[android-studio]: https://developer.android.com/studio
[contributing-page]: https://github.com/oppo/CameraUnit/blob/main/CONTRIBUTING.md
[discussion]: https://github.com/oppo/CameraUnit/issues
[contributing]: https://github.com/oppo/CameraUnit/blob/main/CONTRIBUTING.md
[license]: https://www.apache.org/licenses/LICENSE-2.0
