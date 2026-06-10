# COMP3170 Assignment 1 Report

### Student 1 Name: [Cadigal]
### Student 1 ID: [47100192]

### Student 2 Name: [Tanish]
### Student 2 ID: [47896345]

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
|Java JDK version used for compilation|22.0.2|
|Java compiler compliance level used for compilation|21|
|Java JRE version used for execution| 22.0.2 (build 22.0.2+9-70)|
|Eclipse version|Version: 2026-03 (4.39.0)|
|Your screen dimensions (width x height)|1920 x 1080|
|Your computer type (Mac/PC)|PC|
|Your computer make and model|Acer Nitro AN515-45|
|Your computer Operating System and version|Windows 11 Home Single Language, 25H2|

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
| - Mesh & normals                    | YES 	  |
| - Bezier mesh (*Challenge*)         | YES 	  |
| - UVs & texturing                   | YES 	  |
| Trees                               | YES 	  |
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
| - Day – Sun (diffuse & ambient)     | YES 	  |
| - Day – Sun (specular)              | YES 	  |
| - Night – Headlights (point)        | YES 	  |
| - Night – Headlight cone            | YES 	  |
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

All lighting is computed per-fragment in `simple.frag` using the world-space surface
normal from the vertex shader, following the Phong model (ambient + diffuse + specular).
Day / Night modes are toggled with `3` and select which light is active. Texture
colours are decoded to linear (`pow(colour, 2.2)`) before lighting and the final result
is gamma-encoded (`pow(result, 1/2.2)`) for display.
 
---
 
## 1. Day – Diffuse & Ambient for a point on the car
 
During the day the scene is lit by a **directional light** representing the sun. It is
infinitely far away, so its rays are parallel and it is described by a single direction
shared by the whole scene (a direction only, no position).
 
A point on the car is lit by an **ambient** term plus a **diffuse** term.
 
The **diffuse** term measures how directly the surface faces the sun, using the dot
product of the unit surface normal **N** and the unit direction to the light **L**,
clamped to be non-negative:
 
    diffuse = max(0, N · L)
 
Since N and L are unit vectors, N · L = cos θ, where θ is the angle between them. A car
panel facing the sun (θ = 0°) is fully lit; as it turns away the value drops; once it
faces away, max(0, …) clamps it to 0. Unlike the flat desert, a point on the car has a
normal N that points in whatever direction that panel faces (the bonnet up-and-forward, a
door sideways, etc.), so different panels receive different light — this is what makes the
car read as a 3-D shape. As the car drives and turns, each panel's normal changes relative
to the fixed sun direction, so panels brighten and darken.
 
The **ambient** term is a constant colour added everywhere regardless of orientation,
approximating scattered skylight so unlit panels are not pure black. The ambient intensity
is **(0.25, 0.25, 0.25)**, chosen so shadowed panels stay faintly visible.
 
The terms are summed and multiply the (linear) texture colour:
 
    lighting    = ambient + lightColour · max(0, N · L)
    finalColour = textureColour · lighting
 
(The car body also adds the specular)
 
**Third-person camera:** diffuse and ambient are view-independent — they depend only on
the panel's normal and the sun direction, not the camera. A given car point looks the same
brightness from any third-person camera position.
Provide appropriate diagrams as well as the relevant equations used in the calculation of the following cases. Assume a third-person camera is used in each.

* The day-time diffuse and ambient lighting values for a point on the car.
* The day-time specular lighting values for a point on the car.
* The night-time diffuse and ambient lighting for a point on the desert. 

### Headlight cone
Illustrate how you determine whether a point should be lit within the cone of the headlight.

## Camera 

* Illustrate the viewport and scissor rectangle for the Map camera for a window with resolution 800x600 pixels. Label the corners of each rectangle with coordinates in screen space, NDC, World and Viewport coordinates.

* Illustrate how you calculate the position and view volume of the Third-Person camera.
