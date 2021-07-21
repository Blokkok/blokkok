# Blokkok
Blokkok is an open-source modular Android App IDE for Android

## Cloning
Cloning this repository isn't a single step process, you would need to install `git-lfs` since we've used it to version large files such as `android.jar`, `ecj.jar`, etc.
 - First, as I've already mentioned above, you need to install the `git-lfs` git extension. You can see it's page [here](https://git-lfs.github.com/). If you use Arch Linux, it's avaliable in the community repository as `git-lfs`, so you could just use pacman to install it directly.

 - Second, clone this repository as how you do it normally 
   ```console
   $ git clone https://github/Blokkok/blokkok
   ```
 - Lastly, don't forget to pull the files from the LFS storage, sum of all of the files inside the LFS storage are approximately 45MB.
   ```console
   $ git lfs pull
   ```
