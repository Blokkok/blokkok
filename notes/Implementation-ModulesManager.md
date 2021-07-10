# ModulesManager
ModulesManager is a manager class that manages the modules stored in `appData/modules/`. Using this, you can get, list, import modules with just a function call. Do not modify the modules folder outside `ModuleManager`

## Implementation
 - Function: `initialize(Context)` is a function that initializes the manager, this should be called at the start of the launcher activity. What this function does is to initialize some variables inside the object and also mkdirs the modules folder inside the app data directory.

 - Data class: `ModuleMetadata` is a data that stores a module metadata that is from the `meta.json` file

 - Function: `getModules()` is a function used to list / get every modules. This function returns a `List<ModuleMetadata>` of each and every modules inside the module directory

 - Function: `getModule(String)` is a function used to get a module based on it's id

Structure of the modules folder:
```
modules/
L project_id    - Random id assigned by the manager
|  L meta.json  - Metadata of this current module
|  L module.jar - The jar file of this module
L ...
```

TODO: Implement importing modules
