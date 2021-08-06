# APIs
This document lists every communication defined within this app

## Functions

 - `/get_application_context` returns `android.content.Context`
   Provides the application's context

 - `/main_drawer_menu` returns `android.view.Menu`
   Provides the drawer menu, you can add an item to this drawer on your own

 - `/support_fragment_manager` returns `androidx.fragment.app.FragmentManager`
   Provides the main activity support fragment manager, this can be used to do stuff with fragments

 - `/main_fragment_container_id` returns `Int`
   Provides the main activity fragment container id, this can be used with the supportFragmentManager to replace the main fragment container

 - `/drawer_fragment_container_id` return `Int`
   Provides the fragment container id used by the drawer, this can be used to do stuff with fragments within the container that the drawer controls

## Broadcast

 - `/on_crash` returns `[kotlin.Throwable]`
   This broadcast will get called right when the app has crashed before the DebugActivity appears
