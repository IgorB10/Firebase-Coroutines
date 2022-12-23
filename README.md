# Firebase-Coroutines


This library provides a set of functions that allow you to use Coroutines with the Firebase Realtime Database in Android applications.

Some of the key features of the library include:

Asynchronous reads and writes to the database using Coroutines. This allows you to perform database operations in a non-blocking way, making it easy to integrate with your application's UI.
Support for listening to real-time updates from the database using Flow. This allows you to easily set up a stream of updates that you can subscribe to in your code.
Automatic conversion of data from the database into Kotlin objects using data binding. This can reduce the amount of boilerplate code needed to convert between database data and your application's data models.
To use the library, you will need to add it to your project and make sure that you have the appropriate dependencies (such as the Firebase Realtime Database and Kotlin Coroutines) in your build.gradle file.

Here are some examples of how you might use the library:


Usage
-----

```
val database = FirebaseDatabase.getInstance()
val reference = database.getReference("some/path")
```

Read a value from the database as a specific object type

```
val value: SomeObject = reference.readValue(SomeObject::class.java)
```

Read a value from the database as a generic type

```
val value: T = reference.readValue<T>()
```

Save a values to the database
```
reference.saveValue(someValue)

// Save a value to the database with a specific key
reference.saveValue("key", someValue)
```

Push a new value to the database and get the generated key
```
val key = reference.pushValue(someValue).key
```

Subscribe to real-time updates from the database as a specific object type
```
val flow: Flow<SomeObject> = reference.subscribeOnDataChange(SomeObject::class.java)
flow.collect { value ->
    // do something with value
}

// Subscribe to real-time updates from the database as a generic type
val flow: Flow<T> = reference.subscribeOnDataChange<T>()
flow.collect { value ->
    // do something with value
}
```


Download
--------

```
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```

```
dependencies {
  implementation 'com.github.IgorB10:Firebase-Coroutines:0.0.1'
}
```


License
-------

    Copyright 2022 Ihor Bykov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
