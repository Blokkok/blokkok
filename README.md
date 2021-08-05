# Blokkok
Blokkok is an open-source modular Android App IDE for Android. Every components of the IDE are separated from each other by modules, every modules work together to create an IDE interface for the user to use. Since the plain blokkok app is well, just plain, you can create anything you like with it by modules.

## Getting started
These steps will show you on how to build blokkok on your own

### Cloning
Cloning this repository isn't a single step process, you would need to install `git-lfs` since we've used it to version large files such as `android.jar`, `ecj.jar`, etc.
 - First, as I've already mentioned above, you need to install the `git-lfs` git extension. You can see it's page [here](https://git-lfs.github.com/). If you use Arch Linux, it's avaliable in the community repository as `git-lfs`, so you could just use pacman to install it directly.

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

## Contributing
You can read the [CONTRIBUTING.md](https://github.com/Blokkok/blokkok/tree/main/CONTRIBUTING.md) file for details on this project's code of conduct, and the process for submitting pull requests to this project, good luck!

## Versioning
This project uses [semver](https://semver.org/) for versioning, you can head on to their page to check what's up

## License
This project is licensed under the [GNU GPLv3 LICENSE](https://www.gnu.org/licenses/gpl-3.0.en.html), check the [LICENSE](https://github.com/Blokkok/blokkok/tree/main/LICENSE) file for details
