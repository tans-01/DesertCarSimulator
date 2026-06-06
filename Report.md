# COMP3170 Assignment 1 Report

### Student 1 Name: [Your name here]
### Student 1 ID: [Your ID here]

### Student 2 Name: [Your name here]
### Student 2 ID: [Your ID here]

## Your Development Environment
### Cadigal (47100192)

| Spec                                                | Answer                                                                  |
|-----------------------------------------------------|-------------------------------------------------------------------------|
| Java JDK version used for compilation               | Amazon Corretto 21.0.3 AArch64                                          |
| Java compiler compliance level used for compilation | 21 - Record patterns, pattern matching for switch                       |
| Java JRE version used for execution                 | Same as JDK                                                             |
| Eclipse version                                     | N/A, IntelliJ IDEA 2024.2.4 (Community Edition) Build #IC-242.23726.103 |
| Your screen dimensions (width x height)             | 2560x1600 13.3 inch Retina display (4 sub pixels per pixel)             |
| Your computer type (Mac/PC)                         | MacBook Air                                                             |
| Your computer make and model                        | M1, 2020, 16GB                                                          |
| Your computer Operating System and version          | OSX Tahoe 26.1 (25B78)                                                  |

### Tanish (47896345)

|Spec|Answer|
|----|-----|
|Java JDK version used for compilation|-|
|Java compiler compliance level used for compilation|-|
|Java JRE version used for execution|-|
|Eclipse version|-|
|Your screen dimensions (width x height)|-|
|Your computer type (Mac/PC)|-|
|Your computer make and model|-|
|Your computer Operating System and version|-|

## Features Attempted
Complete the table below indicating the features you have attempted. This will be used as a guide by your marker for what elements to look for, and dictate your <b>Completeness</b> mark.

| Feature                             | Attempted | 
|-------------------------------------|-----------|
| Debug modes                         |           | 
| - Wireframe mode                    | YES       |
| -  Normals mode                     | YES       |
| Desert                              |           |
| - Mesh & normals                    | YES       |
| - UVs & texture                     | YES       |
| Road                                |           |
| - Mesh & normals                    | YES / NO  |
| - Bezier mesh (*Challenge*)         | YES / NO  |
| - UVs & texturing                   | YES / NO  |
| Trees                               | YES / NO  |
| Car                                 |           |
| - Meshes & normals                  | YES       |
| - UVs & Textures                    | YES       |
| - Window Transparency               | YES       |
| - Driving                           | YES       |
| - Wheels                            | YES       |
| - Animating wheels                  | YES       |
| Cameras                             |           |
| - Map                               | YES       |
| - Third-person                      | YES       |
| Light                               |           |
| - Day – Sun (diffuse & ambient)     | YES / NO  |
| - Day – Sun (specular)              | YES / NO  |
| - Night – Headlights (point)        | YES / NO  |
| - Night – Headlight cone            | YES / NO  |
| Skybox                              | YES       |
| Effects- Heat shimmer (*Challenge*) | YES       |

# Documentation

Documentation is marked separately from implementation but should reflect the approach taken in your code. You can attempt documentation questions for features you have not implemented or completed, but should clearly indicate that this is the case. 

Documentation should include both diagrams and relevant equations to explain your solution. 

**Note**: Copy/pasting images directly from the lecture notes (or other sources) will get zero marks (and may be treated as academic misconduct).

Where requested, meshes should be drawn to scale in model coordinates, including:
* The origin
* The X and Y axes
* The coordinates of each vertex
* The triangles that make the mesh

## Scene Graph

* Include a drawing (pen-and-paper or digital) of the scene graph used in your project.
* Where there are multiple copies of an object at the same point in the graph (e.g. the trees) only a single instance needs to be shown.

## Road Mesh

Illustrate how you construct the road mesh, including:
* vertices
* normals
* construction into triangles in the index buffer

## Lighting
Provide appropriate diagrams as well as the relevant equations used in the calculation of the following cases. Assume a third-person camera is used in each.

* The day-time diffuse and ambient lighting values for a point on the car.
* The day-time specular lighting values for a point on the car.
* The night-time diffuse and ambient lighting for a point on the desert. 

### Headlight cone
Illustrate how you determine whether a point should be lit within the cone of the headlight.

## Camera 

* Illustrate the viewport and scissor rectangle for the Map camera for a window with resolution 800x600 pixels. Label the corners of each rectangle with coordinates in screen space, NDC, World and Viewport coordinates.

* Illustrate how you calculate the position and view volume of the Third-Person camera.
