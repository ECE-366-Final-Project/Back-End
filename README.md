# Final Project for ECE-366 "Software Engineering and Large System Design"
### Cooper Union, Spring 2024
> #### Lani Wang, James Ryan, Evan Rosenfeld, Vaibhav Hariani

## Cloning the Repo
Clone the repo (with the recursive flag!) by running
```
$ git clone --recurse-submodules https://github.com/ECE-366-Final-Project/Back-End.git
```

## Build & Run  
Build the project with
```
$ mvn package
```

Initialize the project with <br>
```
$ docker compose up --build -d
```
Ensure that ports `8080` and `5432` are not already bound! <br>

## API
Sample ping request (Default port is `8080`)        <br>
```
$ curl "localhost:<PORT>/Ping"
```

Formatting for API requests                 <br>
(localhost only if API is running locally)  <br>
```
$ curl "localhost:<PORT>/<COMMAND>?<param1>=<value>&<param2>=<value>"
```
