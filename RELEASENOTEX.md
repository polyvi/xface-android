

## 3.2.0-rc3 (Fri Feb 28 2014)


 *  Set VERSION to 3.2.0-rc3 (via xsrc)
 *  Update JS snapshot to version 3.2.0-rc3 (via xsrc)
 *  add launcher xfacelib method
 *  Add a few methods in XCryptor
 *  README.md: `android update` to `android-19`.
 *  Fix NPE when POLLING bridge mode is used.
 *  Add RELEASENOTES for 3.4.0
 *  Updating NOTICE to include Square for OkHttp
 *  Update JS snapshot to version 3.5.0-dev (via coho)
 *  CB-5398 Apply KitKat content URI fix to all content URIs
 *  CB-5398 Work-around for KitKat content: URLs not rendering in <img> tags
 *  CB-5908: add splascreen images to template
 *  Added Log.e when Config is not initialised but accessed
 *  CB-5395: Make scheme and host (but not path) case-insensitive in whitelist
 *  Fix broken build from prev. commit (copy & paste error?)
 *  Ignore multiple onPageFinished() callbacks & onReceivedError due to stopLoading()
 *  Removing addJavascriptInterface support from all Android versions lower than 4.2 due to security vulnerability
 *  CB-4984 Don't create on CordovaActivity name
 *  CB-5917 Add a loadUrlIntoView overload that doesn't recreate plugins.
 *  Use thread pool for load timeout.
 *  CB-5715 For CLI, hide assets/www and res/xml/config.xml by default
 *  CB-5793 ant builds: Rename AndroidManifest during -post-build to avoid Eclipse detecting ant-build/ as a project when importing
 *  CB-5889 Make update script find project name instead of using "null" for CordovaLib
 *  CB-5889 Add a message in the update script about needing to import CordovaLib when using an IDE.
 *  Update JS snapshot to version 3.5.0-dev (via coho)
 *  Set VERSION to 3.5.0-dev (via coho)
 *  Fix type "LANCH" -> "LAUNCH"
 *  CB-5793 Make ant work-around work on windows.
 *  CB-5793 Add work-around for library references not working with custom output directory (ugh).
 *  CB-5793 Forgot to update ant path for clean.
 *  CB-5793 Don't clean before build and change output directory to ant-build to avoid conflicts with Eclipse.
 *  CB-4910 Fix CLI's eclipse project template not working on windows due to "*" in the virtual folder name.
 *  CB-5803 Fix cordova/emulate on windows.
 *  CB-5801 Add spawn work-around on windows for it not being able to execute .cmd files
 *  CB-5801 exec->spawn in build to make sure compile errors are shown.
 *  CB-5799 Update version of OkHTTP to 1.3
 *  Remove package.json within bin/ since we never intend to ship bin/ as an npm module
 *  CB-4910 Update CLI project template to point to config.xml at the root now that it's not in www/ by default.
 *  Silence excessive logging from scroll events
 *  CB-5504: Adding onDestroy to app plugin to deregister telephonyReceiver
 *  CB-5715 Add Eclipse .project file to create template.
 *  CB-5447 Removed android:debuggable=“true” from project template.
 *  CB-5714 Fix of android build when too big output stops build with error due to buffer overflow.
 *  Fix incorrect MIME type for .js files loaded through CordovaResourceAPI.
 *  Remove 2 X console.log from exec.js
 *  CB-5592 Set MIME type for openExternal when scheme is file:
 *  Add RELEASENOTES for 3.3.0 release
 *  Backfill 3.2.0 release notes
 *  CB-5489: clean up docs for deprecated methods
 *  CB-5504: Moving code to the App plugin inside Cordova, the place where the grey area beween plugin and platform exists
 *  CB-5047: Adding a defaults.xml template
 *  CB-5481 Fix for Cordova trying to get config.xml from the wrong namespace
 *  Add missing semicolon
 *  Spelling fixes
 *  CB-5144 Spelling & grammar fixes in README.
 *  Forgot Apache Headers on MessageTest
 *  Update JS snapshot to version 3.4.0-dev (via coho)
 *  Set VERSION to 3.4.0-dev (via coho)
 *  prevent ClassNotFound exception for emtpy class name
 *  CB-5487: Remote Debugging is on when your Android app is debuggable.
 *  Updating the README
 *  Making the object less chatty
 *  Updating tests to KitKat, and making the tests more thread-safe
 *  Incrementing API target
 *  CB-5445: Adding onScrollChanged and the ScrollEvent object. (Forgot to add the WebView)
 *  CB-5445: Adding onScrollChanged and the ScrollEvent object
 *  Updated CordovaWebView to experiment with onScrollChanged messages
 *  Moving the console.log out of run() method
 *  CB-5422: Don't require JAVA_HOME to be defined
 *  Thanks for Benn Mapes for making this process easy.  Updating the Android API level.
 *  CB-5490: add javadoc target to ant script
 *  CB-5471: add deprecation javadoc/annotation
 *  Add javadoc comments to source classes
 *  CB-5255: Checking in the Google Check, TODO: Add Amazon FireOS check
 *  Add getUri method in XPathResolver
 *  XPathResolver supports file url 'cdvfile://localhost/<filesystemType>/<path to file>'
 *  Modify lib codes to fix register a new filesystem error
 *  Revert commit 'Change preset dir from workspace to temporary root of filesystem'
 *  Change preset dir from workspace to temporary root of filesystem
 *  Remove 'xFace/workspace' module in webviewclient
 *  Delete redundant tags in xface_string.xml
 *  xFace library read the config.xml to change the way
 *  Fixed bug: phone call or message event has no reponse when portal receives them
 *  Update JS snapshot to version 3.3.0-dev (via xsrc)


## 3.3.0 (Fri Jun 20 2014)


 *  Update JS snapshot to version 3.3.0 (via xsrc)
 *  解决自动测试AMS能力时，卸载app时，appview为null还继续clearCache造成的崩溃问题
 *  修改桥接模式为ONLINE_EVENT(同cordova一致)，以解决input标签在非全屏模式下无法使用的问题
 *  Update JS snapshot to version 3.4.0-dev (via xsrc)
 *  Set VERSION to 3.4.0-dev (via xsrc)
 *  解决自动测试AMS能力时，卸载app时，appview为null还继续clearCache造成的崩溃问题
 *  修改桥接模式为ONLINE_EVENT(同cordova一致)，以解决input标签在非全屏模式下无法使用的问题
