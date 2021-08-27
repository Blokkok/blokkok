<img src="https://blokkok.ga/img/favicon.png" align="left" height="130px" width="130px">

# Blokkok
[![Hits-of-Code](https://hitsofcode.com/github/Blokkok/blokkok?branch=stripped-modular)](https://hitsofcode.com/github/Blokkok/blokkok/view?branch=stripped-modular)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/Blokkok/blokkok)
[![Discord](https://img.shields.io/discord/862973601354874880)](https://discord.gg/auNNjt7wVd)
![Website](https://img.shields.io/website?url=https%3A%2F%2Fblokkok.ga)
![GitHub issues](https://img.shields.io/github/issues/Blokkok/blokkok)

<br />

Blokkok is an open-source modular Android App IDE for Android. Every components of the IDE are separated from each other by modules, every modules work together to create an IDE interface for the user to use. Since the plain blokkok app is well, just plain, you can create anything you like with it by modules.

## Getting started
These steps will show you on how to build blokkok on your own

### Cloning
Cloning this repository isn't a single step process, you would need to install `git-lfs` since we've used it to version large files such as `android.jar`, `ecj.jar`, etc.
 - First, as I've already mentioned above, you need to install the `git-lfs` git extension. You can see it's page [here](https://git-lfs.github.com/).

 - Second, clone this repository as how you do it normally 
   ```console
   $ git clone https://github.com/Blokkok/blokkok
   ```
 - Third, make sure to clone it's submodules
   ```console
   $ git submodule update --init
   ```
 - Lastly, don't forget to pull the files from the LFS storage, sum of all of the files inside the LFS storage are approximately 45MB.
   ```console
   $ git lfs pull
   ```

### Building
Building this app is fairly simple, if you have android studio, you can open this project in it and click run. If you don't, you will need to have java installed in your machine, then run `./gradlew assembleDebug` or `.\gradlew assembleDebug` (if on windows) in the project directory. After some time, the APK will be available in the `app/build/outputs/apk/debug` directory, have fun!

## Communication APIs
If you're looking on what communication APIs this app provides, you can checkout the documentation [COMMUNICATION_APIS.md](https://github.com/Blokkok/blokkok/tree/main/COMMUNICATION_APIS.md)

## Reporting bugs
Only report bugs related to the plain app in this repository, other Blokkok builtin IDE functionalities should be posted on the [blokkok-modules](https://github.com/blokkok/blokkok-modules) issues page since that's where the functionalities live in.

## Contributing
You can read the [CONTRIBUTING.md](https://github.com/Blokkok/blokkok/tree/main/CONTRIBUTING.md) file for details on this project's code of conduct, and the process for submitting pull requests to this project, good luck!

## Versioning
This project uses [semver](https://semver.org/) for versioning, you can head on to their page to check what's up

## License
This project is licensed under the [GNU GPLv3 LICENSE](https://www.gnu.org/licenses/gpl-3.0.en.html), check the [LICENSE](https://github.com/Blokkok/blokkok/tree/main/LICENSE) file for details

## Contact
 - Contact page: https://blokkok.ga/#contact <!-- update this -->
 - Discord server: https://discord.gg/auNNjt7wVd <!-- update this -->
 - Email: blokkokofficial@gmail.com

## Acknowledgements
 - Modular communication API was inspired from the Linux kernel
 - Blokkok is a rewrite of the dead OpenBlocks
 - How OpenBlocks was born from knowing that Sketchware users like to modify Sketchware to make their own features, so then it's better if the app itself is modular so people doesn't need to know a lot about reverse engineering to modify it
 - Thank you TheClashFruit for leading the team and making a website for blokkok
 - Thank you Iyxan23 for programming the app and it's module library
 - Thank you Sketchub for being a partner with us since the beginning
 - Thank you for the old openblocks community being supportive about this project
 - Thank you for the Sketchware Pro server for providing us with knowledge in the android build system
 - Thank you to tyron for making his [ApkBuilder](https://github.com/tyron12233/ApkBuilder) open-source
 - Thank you others who have contributed in this project in the past
 - And finally, Thank YOU for being interested in blokkok!
