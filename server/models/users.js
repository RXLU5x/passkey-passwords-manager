module.exports = function(hashServer) 
{
    return {
        signupUser: async function(firstname, lastname, email, username, password){
            this.firstname = firstname
            this.lastname = lastname
            this.email = email
            this.username = username
            let values = await hashServer.consume(password)
            this.passwordHash =  values.passwordHashHex || null
            this.salt = values.saltHex || null

            this.missingInfo = ()=> {
                if(!this.firstname || !this.lastname || !this.email || !this.username || !this.passwordHash || !this.salt)
                   return true
                else
                    return false
            }  
            
            return this
        },

        databaseUser: async function(firstname, lastname, email, username, passwordServerHash, saltHex){
            this.firstname = firstname
            this.lastname = lastname
            this.email = email,
            this.username = username,
            this.passwordServerHash = passwordServerHash,
            this.salt = saltHex

            this.missingInfo = ()=> {
                if(!this.firstname || !this.lastname || !this.email || !this.username || !this.passwordServerHash || this.salt)
                   return true
                else
                    return false
            }  
            
            return this
        },

        deleteUser: async function(username, email, passwordServerHash, salt){
            this.email = email
            this.username = username           
            this.passwordHash =  values.passwordHashHex || null
            this.salt = values.saltHex || null

            this.missingInfo = ()=> {
                if(!this.email || !this.username || !this.passwordHash)
                   return true
                else
                    return false
            }  

            return this;
        }
    }
}