'use strict'

/* Dependencies */

// Universal dependencies
const express = require("express");
const https = require("https")
const fs = require('fs')
const passport = require("passport");
const util = require('util');
const cryptoNode = require("crypto")
var cookieParser = require('cookie-parser')
const json = express.json();
const app = express();

// Crypto module
const crypto = require("./crypto")(util, cryptoNode);

// Models
const Users = require("./models/users")(crypto);

// Databases
const vaultsDatabase = require("./vaults-db")();
const usersDatabase = require("./users-db")(Users);
const tokenDatabase = require("./tokens-db")();

// Passport midlewares
const passportStrategies = require("./passport-strategies")(passport, crypto, usersDatabase, tokenDatabase); 
passport.use(passportStrategies.passportSignIn);
passport.use(passportStrategies.passportToken);
passport.localAuth = passportStrategies.localAuth; 
passport.jwtAuth = passportStrategies.jwtAuth;

// Express pre-processing middlewares 
app.use(passport.initialize());
app.use(cookieParser())
app.use(json);
app.options(cors);

// Routers
const vaultsApiRouter = express.Router();
const usersApiRouter = express.Router();

// API endpoints
const usersApi = require("./users-api")(passport, usersApiRouter, usersDatabase, tokenDatabase, Users, crypto);
const vaultsApi = require("./vaults-api")(vaultsApiRouter, vaultsDatabase);

/* Server configuration */

// Port setup
const PORT = process.argv[2] || 8080;

// Rotes middlewares setup
app.use("/api/users", usersApi);
app.use("/api/vaults", passport.jwtAuth, vaultsApi);
app.use("*", noRouteFound);

// TLS setup
var tlsOptions = {
	key: fs.readFileSync('cert/key.pem'),
	cert: fs.readFileSync('cert/cert.pem')
};  
  
// Listening
https.createServer(tlsOptions, app).listen(PORT, () => console.log("Listening on port " + PORT));

/* Auxiliary Functions */

// No route found
function noRouteFound(request, response)
{
	response.status(404).json({error: "Not Found", reason: "Route to " + request.originalUrl + " using " + request.method + " method doesn't exist"});
}

// Prevent simple-requests
function cors(request, response, next){
	response.header("Access-Control-Allow-Origin", "*");
	response.header("Access-Control-Allow-Methods", "GET,POST");
	response.header("Access-Control-Allow-Credentials", "true");
    response.header("Access-Control-Allow-Headers",  "Origin, X-Requested-With, Content-Type, Accept");

	next();
}