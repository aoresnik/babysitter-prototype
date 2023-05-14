# BabySitter

Run bash scripts via a web interface and babysit their execution.

## Run in development environment

Open in IntelliJ IDEA and run:
* **babysitter-server-prototype** as Quarkus application via Gradle target `dev`  
* **babysitter-prototype** as an Angular CLI application

To test running scripts via SSH:
* Run `vagrant up` in the **dev-vms/test-target-vm** directory.
 
  Starts up a test target VM with scripts to be run over SSH. Requires Linux and configured libvirt provider.
