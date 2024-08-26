# Filament demo - 3D printer objects

A demo app showcasing how to use [Filament](https://google.github.io/filament) to easily integrate 3D models into a Compose Android application.

The app was the base for a presentation I gave in 2024 and the order of commits reflects the increasing complexity of aspects described in the presentation.

https://github.com/user-attachments/assets/ca6ad00a-2532-4955-8a0a-7638fb855612

## Tech stack & Open-source libraries

- Jetpack Libraries:
  - Jetpack Compose: Android’s modern toolkit for declarative UI development.
  - ViewModel: Manages UI-related data and is lifecycle-aware, ensuring data survival through configuration changes.
  - Navigation: Facilitates screen navigation.
- Architecture
  - MVVM with unidirectional data flow
- [Filament](https://google.github.io/filament): the main open-source library used to load, manipulate and display 3D GLB models in the app

## License
```xml
Designed and developed by 2024 Daniele Bonaldo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
