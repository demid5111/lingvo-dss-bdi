### Info
This is a modified version of [this tutorial][tutorial_hello].

### Setup

Dependencies:
1. lingvo-dss-all.jar To get it, clone the [repo][ldss_core] and use the instructions there to build core logic jar.
2. lingvo-dss-sources.jar To get it, clone the [repo][ldss_core] and use the instructions there to build jar with sources.
3. jfxrt.jar (for Pair usage). On macOS it is placed in **/Library/Java/JavaVirtualMachines/jdk1.8.0_71.jdk/Contents/Home/jre/lib/ext/**.

Put them all in the **lib** folder as at least on macOS references from other places does not work properly.

Also, set the language level of the project to **1.8** in *Project->Properties* as the core algorithm is compiled for this target.


[tutorial_hello]: http://jacamo.sourceforge.net/tutorial/coordination/
[ldss_core]: https://github.com/demid5111/lingvo-dss