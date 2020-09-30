const LocalStrategy = require('passport-local').Strategy;
const JWTStrategy = require('passport-jwt').Strategy;

module.exports = function(passport, serverHash, usersDatabase, tokenDatabase) 
{
    let passportSignIn = new LocalStrategy({
        usernameField: "username",
        passwordField: "passwordHash",
        }, async (username, passwordClientHash, done) => {
            try {
                const user = await usersDatabase.getUser(username);
                const passwordsMatch = await serverHash.compare(passwordClientHash, user.salt, user.passwordServerHash);

                if (passwordsMatch) {
                    return done(null, user);
                } else {
                    return done(null, false, { message: 'Incorrect username / password.' });
                }
            } catch (error) {
                if(error.body)
                    done(null, false,{
                        "message": "Invalid credentials"
                    } );
                else
                    done(error)
        }
    });

    let passportToken = new JWTStrategy({
        jwtFromRequest: req => req.cookies["jwt"],
        secretOrKey: process.env["JWT_KEY"],
        },
        async (jwtPayload, done) => {
            let {valid, data} = await tokenDatabase.validateToken(jwtPayload)
            if(valid === false){
                return done(null, false, data);
            }
            else if (valid === null){
                return done(data)
            }
    
            return done(null, data);
        }
    );

    // custom Callback to return our own error messages
    function customReturnHandler(type){
		return function(req, res, next) {
			passport.authenticate(type, function(err, user, info) {
				if(err){
					return res.status(500).json(err)
				}
				else if(info)
					return res.status(401).json({
                        "error": info.message
                    })
				else
					req.login(user, {session: false}, _=> next() )
							
			}, {session: false})(req, res, next);
		}
	}

    // Utilities for auth type
	let localAuth = customReturnHandler("local");
	let jwtAuth = customReturnHandler("jwt");

    return {
        passportSignIn: passportSignIn,
        passportToken: passportToken,
        localAuth: localAuth,
        jwtAuth: jwtAuth
	}
}