# AngularJS fix dependency plugin for IntelliJ IDEA-based IDEs

![Build](https://github.com/aider/angularjs-fix-dependency/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

### **Dependency injection**

Dependency injection is one of AngularJS's best patterns. It makes testing much simpler, as well as making it more clear upon which any particular object depends. AngularJS is very flexible on how things can be injected. The simplest version requires just passing the name of the dependency into the function for the module:
```
var app = angular.module('app',[]);

app.controller('MainCtrl', function($scope, $timeout){
$timeout(function(){
console.log($scope);
}, 1000);
});
```
Here, it's very clear that MainCtrl depends on $scope and $timeout.

This works well until you're ready to go to production and want to minify your code. Using UglifyJS the example becomes the following:
```
var app=angular.module("app",[]);app.controller("MainCtrl",function(e,t){t(function(){console.log(e)},1e3)})
```
Now how does AngularJS know what MainCtrl depends upon? AngularJS provides a very simple solution to this; pass the dependencies as an array of strings, with the last element of the array being a function which takes all the dependencies as parameters.
```
app.controller('MainCtrl', ['$scope', '$timeout', function($scope, $timeout){
$timeout(function(){
console.log($scope);
}, 1000);
}]);
```
This then minifies to code with clear dependencies that AngularJS knows how to interpret:
```
app.controller("MainCtrl",["$scope","$timeout",function(e,t){t(function(){console.log(e)},1e3)}])
```
<!-- Plugin description -->
This plugin inspection that fixes angularjs dependency injections
from 
```
var app = angular.module('app',[]);

app.controller('MainCtrl', function($scope, $timeout){
$timeout(function(){
console.log($scope);
}, 1000);
});
```
to
```
app.controller('MainCtrl', ['$scope', '$timeout', function($scope, $timeout){
$timeout(function(){
console.log($scope);
}, 1000);
}]);
```

[comment]: <> (This specific section is a source for the [plugin.xml]&#40;/src/main/resources/META-INF/plugin.xml&#41; file which will be extracted by the [Gradle]&#40;/build.gradle.kts&#41; during the build process.)

[comment]: <> (To keep everything working, do not remove `<!-- ... -->` sections. )
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "angularjs-fix-dependency"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/aider/angularjs-fix-dependency/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
