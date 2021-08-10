# Contributing
First of all, thank you so much for having the interest in contributing to blokkok!

If you spot a gramatical error in this document, feel free to propose a pull request about it. and if you have a different opinion or some ideas for this document, you could talk about it in the discussions page and we'll discuss about it.

**I have a question about blokkok!**
You should head on to the FAQ page in the wiki [here](/todo), and if you don't find your question there, you can either ask it in the discussions page, or ask it in our [discord server](https://discord.gg/auNNjt7wVd).

## Stuff you should know before contributing

### How Blokkok is structured
Blokkok is meant to be a modular app where it's features are separated from it's app source. This repository you're in right now doesn't really contain anything (note: currently, we have the `main` to still have the unmodularized version, we're currently trying to modularize it into modules in the branch `stripped-modular`, if you want to contribute, do not contribute on the main branch), it only holds the [modsys library](https://github.com/Blokkok/blokkok-modsys) which contains the module management, importing modules, loading modules etc. Other than that, every features of this app are intended to be made and extended by modules.

Modules made by us are contained in the [blokkok-modules](https://github.com/Blokkok/blokkok-module) repository. If you wanted to modify the preincluded IDE, you should head on to there and find the things that you like to modify.

### Proposing a feature
If you wanted to submit a feature, **do not pull request it directly** to the repository. Please talk about it in the discussions page first, we will need to discuss about the feature if we would like for it to be added or maybe not.

### Commiting
Please do try to follow the commiting style if you wanted to contribute, it makes the repository much more clean and consistent.
 - Do not squash commits
 - Explain what the commit does or what did you do in the commit message with less than 50 characters
 - Use the commit description if you can't fit what you did in the commit message
 - Use one of these prefixes in your commit messages:
   - `fix` When you fixed a bug or maybe a flaw within the codebase
   - `feat` When you added something within the codebase (can be anything)
   - `tweak` When you do a little tweak in the codebase, like a tiny UI change
   - `chore` When you do fixed something but it doesn't affect the app in terms of functionality
   - `refactor` When you refactored the code, like cleaning up the code

## Writing a documentation
If you're planning to write a documentation for blokkok, try to be as friendly as possible and try to explain some technical terms the reader might not understand yet.

Again, Thanks for having the interest in contributing on blokkok, we would like to see your first contribution!
