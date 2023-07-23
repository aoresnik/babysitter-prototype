# BabySitter prototype

NOTE: This is a prototype project to explore an idea. It's not production quality, does not represent best practices and
has known bugs.

The idea is to have the simplest possible way, via a web app, for both:
* Admins to provide scripts for various maintenance and other chores for infrastructure side of e.g. project resources 
  or machine resources (such as providing CI/CD API keys), restrict who can run what and view audit logs of who ran what.
* Users to run those scripts when they need them, without having to deal with SSH, servers, etc.

Everything that's planned for it to do can be done with existing tools: `ssh`, `sudo`, `tmux`, but setting up and 
maintaining configuration them with consistent security is time-consuming and error-prone.

The most important parts - permissions and parameters - are not implemented yet.

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## UI

Run bash scripts or other programs via a web interface and "babysit" their execution.

![screenshot-commands.png](doc%2Fimages%2Fscreenshot-commands.png)

Allows interaction with scripts via web-based terminal emulator (xterm.js). 

![screenshot-execution.png](doc%2Fimages%2Fscreenshot-execution.png)

The link to session can be sent to other user, which can continue the session (session sharing not yet supported).

Commands are currently supported as local executables on the same server as the backend app 
(**babysitter-server-prototype**) or remote on a different server, run via SSH.

## Run in development environment

Open in IntelliJ IDEA and run:
* **babysitter-server-prototype** as Quarkus application via Gradle target `dev`  
* **babysitter-prototype** as an Angular CLI application

To test running scripts via SSH:
* Run `vagrant up` in the **dev-vms/test-target-vm** directory before running the **babysitter-prototype** project.
 
  Starts up a test target VM with scripts to be run over SSH. Requires Linux and configured libvirt provider.

Open http://localhost:4200 in the browser to access the app.

Open http://localhost:8080/q/dev/io.quarkus.quarkus-resteasy-reactive/endpoints to access Quarkus list of API endpoints
