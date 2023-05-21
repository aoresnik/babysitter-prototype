# BabySitter

Run bash scripts or other programs via a web interface and babysit their execution.

Allows interaction with scripts via web-based terminal emulator (xterm.js). Scripts can be
local on the same server as the backend app (**babysitter-server-prototype**) or remote on a different 
server, run via SSH.

## Run in development environment

Open in IntelliJ IDEA and run:
* **babysitter-server-prototype** as Quarkus application via Gradle target `dev`  
* **babysitter-prototype** as an Angular CLI application

To test running scripts via SSH:
* Run `vagrant up` in the **dev-vms/test-target-vm** directory.
 
  Starts up a test target VM with scripts to be run over SSH. Requires Linux and configured libvirt provider.
