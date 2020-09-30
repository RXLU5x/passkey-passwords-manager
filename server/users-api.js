

module.exports = function(passport, router, usersDatabase, tokenDatabase, User, crypto) 
{	
	/* Router setup */

	router.post("/signin", passport.localAuth, signin);
	router.post("/signup", signup);
    router.post("/signout", passport.jwtAuth, signout);
    router.post("/delete", passport.localAuth, deleteUser);

	return router

	/* Users API request handlers */

	// Creates new user, logs him in and sets cookie to JWT
	function signup(request, response) 
	{
        return User.signupUser(
			request.body.firstname, 
            request.body.lastname,
            request.body.email,
            request.body.username, 
            request.body.passwordHash
		)
		.then(currUser => 
			{
				if(currUser.missingInfo())
				{
					return Promise.reject(
						{
							"code": 400,
							"body": "Bad request. Missing parameters."
						}
					) 
				} 
				else 
					return currUser
			}
		)
		.then(currUser => usersDatabase.createUser(currUser))
		.then(async function(user) 
			{                    
				let payload = await crypto.jwtPayload(user);
				let {error, id} = await tokenDatabase.registerToken(payload)
				
				if(error)
					return Promise.reject(error)
				else
				{
					let jwt = crypto.jwtCreate(payload, id)
					
					request.login(payload, {session: false}, async err => 
						{
							if(err) 
								sendUnauthorized(response, err)
							else 
								response.cookie('jwt', jwt, { httpOnly: true, secure: false, sameSite: "strict" })
									.json({"info": "Your account has been created sucessufully!"})
						}
					)
				}
			}
		)
		.catch(error => response.status(error.code || 500).json(error.body || "Unknown internal server error"))
	}

	// Handles user log in
	function signin(request, response) 
	{
		return Promise.resolve(request.user)
		.then(async function(user) 
			{                    
				let payload = await crypto.jwtPayload(user);
				let {error, id} = await tokenDatabase.registerToken(payload)
				
				let jwt = await crypto.jwtCreate(payload, id)	

				if(error)
					return Promise.reject(error)
				else
				{
					request.login(payload, {session: false}, async err => 
						{
							if(err) 
								sendUnauthorized(response, err)
							else 
								response.cookie('jwt', jwt, { httpOnly: true, secure: false, sameSite: "strict" })
										.json({"info": "Your account has signed in sucessufully!"})
						}
					)
				}
			}
		)
		.catch(error => response.status(error.code || 500).json(error.body || "Unknown internal server error"))		
	}

	// Handles user log out
	// Returns session info
	async function signout(request, response) 
	{
        request.logout();
		response.status(200)
				.cookie('jwt', "", {expires: new Date(Date.now())})
				.json({"info": "Your account has signout sucessufully!"})
		
		return
    }

	// Handles the deletion of all stored user data
	// The user is also logged out
    function deleteUser(request, response) 
	{
		return usersDatabase
			.getUser(request.body.username)
			.then(currUser => usersDatabase.deleteUser(currUser))
			.then(() => 
				{
					request.logout();
					response.cookie('jwt', "", {expires: new Date(Date.now())});
					response.status(200).json({"info": "Your account has been deleted sucessufully!"})
				}
			)
			.catch(error => response.status(error.code || 500).json(error.body || "Unknown internal server error"))
	}

    /* Auxiliary methods */  

	// Handles unauthorized requests
	function sendUnauthorized(response, error)
	{
		response.status(403).json( 
			{ 
				status:"Unauthorized", 
				message: error 
			} 
		)
	}
}