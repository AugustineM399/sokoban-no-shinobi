# Patch Notes, v1.1.1

## Version 1
### 08/30/2026 - 1.1.1 - Screenshot fix and packaging
 * Mostly backend updates, such as putting code in a package
 * Fixed bug where taking a screenshot also zoomed the camera

### 03/16/2025 - 1.1 - The Camera Update
 * Added a simple "camera" system for zooming in/out/panning

### 12/31/2024 - 1.0 - LEVEL EDITOR
 * Resizable windows
 * Fully GUI-based, no more cmd line
 * Added different "movelogic" settings: "standard", "simplebarriers", and "barriers" move the player and boxes in new and exciting ways! (Random levels use Standard)
 * Proper file-picker implemented
 * Separate executable level-editor added, with GUI & file management
 * maxSteps for tiles is no longer limited to 127. File format updated slightly to allow for numbers up to 2 ^ 31 - 1 (Java integer limit).
 * Bottom text area expanded slightly
 * Regular game window now displays titles when playing levels from file

## Version 0
### 06/26/2024 - 0.0.1 - Quick fixes
 * Fewer crashes
 * Added "exit" command to quit games without winning
 * New random level logic with fewer boxes and none on the edges (should reduce impossible games)

### 06/07/2024 - 0.0 - IT BEGINS
 * New game