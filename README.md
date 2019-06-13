CAMERAPREVIEWFILTER
========================================================================================================================
Architecture - MVP
========================================================================================================================
Short architecture description

Application consists of three modules:

Model - not needed in current application state

View - module that contains UI and UI related actions, such as initializing/showing/hiding actual UI, getting RGB colors, providing 
display information and holder for camera stream to Presenter.

Presenter - module that controls work of application, interacts with View using ViewInterface, setups camera, applying color filter to
preview and is responsible for camera and preview session state callbacks.
