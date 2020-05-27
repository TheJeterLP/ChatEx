**ChatEx** ![](https://github.com/TheJeterLP/ChatEx/workflows/Java%20CI%20with%20Maven/badge.svg)
![](https://jitpack.io/v/TheJeterLP/ChatEx.svg)

![ChatEx Logo](https://i.imgur.com/ng1GIOg.png)

Developer requirements
------------

* The Java 8 or newer JDK
* Maven
* Git 

Developer Informations
------------
If you want to contribute, feel free to fork the project and create pull requests. Your help is really appreciated!
Every developer of the ChatEx team is able to create branches and merge it into master. Once the development of a feature has started, the developer creates a new branch named after the feature. Once a feature is finished, the branch becomes merged to the master branch to prevent commit conflicts.
Everybody who is not from the developer team, can make pull requests to the main branch and we will review them and maybe merge them.

To clone: 

```
git clone https://github.com/TheJeterLP/ChatEx.git
```

To create a new branch, go to [Github](https://github.com/TheJeterLP/ChatEx), click on branch master and create a new branch.


To switch to a branch
````
git pull origin BRANCHNAME
git checkout BRANCHNAME
````

To merge the master branch to a branch:
```
git pull origin master
git checkout BRANCHNAME
git merge master
```

To merge a branch to the master branch:
```
git pull origin master
git checkout master
git merge BRANCHNAME
```

Commit  Structure
------------
If you make a commit, use the tags below in your commit. For example:
```
[MOD] Change the way how players get stored in the database
```

So always prefix your commit message with one of these tags:

* [MOD] : You have modified something in the existing code
* [ADD] : You've added something new to the code
* [FIX] : Fixed a problem/bug
* [OPTIMIZATION] : Some optimization done in the code
* [DEV] : Something only related to development
* [DEBUG] : Related to help the debugging.
* [IGNORE] : Related to the .gitignore file

If you have already commited something, but want to add another commit, 
just type ```git commit -amend```
BUT: Only use this if you did not push already. If you pushed already, you need to make a new commit.

Used IDEs
------------
* [NetBeans](https://netbeans.org) (used by TheJeterLP)
  * Built-in Maven support
  * Built-in Git support
