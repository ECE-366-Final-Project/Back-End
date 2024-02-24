# Final Project for ECE-366 "Software Engineering and Large System Design"
### Cooper Union, Spring 2024
> #### Lani Wang, James Ryan, Evan Rosenfeld, Vaibhav Hariani

## Build & Run  
This project requires `maven` to build. You can find maven at 
[maven.apache.org](maven.apache.org) or it can be installed via your 
package manager.  
For OSX: `$ brew install maven`             <br>
For Ubuntu: `# apt install maven`           <br>
Build with (from Back-End/ directory):      <br>
```
$mvn package
```

Run with (from Project/ directory):         <br>
```
$java -jar target/cooper-casino.jar     
``` 

Ping API with (PORT is usually 8080)        <br>
```
$curl "localhost:<PORT>/Ping"
```

Formatting for API requests                 <br>
(localhost only if API is running locally)  <br>
```
$curl "localhost:<PORT>/<COMMAND>?<param1>=<value>&<param2>=<value>"
```