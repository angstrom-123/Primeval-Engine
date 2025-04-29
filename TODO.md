## TODO

# Map editor
-Currently map edits are saved to the editor's resources but not saved to the 
core's resources. There is a failure as the saveData that is used for saving is 
not present in the core and the saveName is null. 
Alternatively, the save name can be passed in as a param no matter what and instead
of null meaning overwrite, an existing file name passed in will overwrite automatically.
This value will be specified outside of the savedata object so that the core
does not have to use it


# Core 
- Drawing floors and ceilings
- Collisions
- Sprites
- Interactable objects
- Level selection / dynamic loading
