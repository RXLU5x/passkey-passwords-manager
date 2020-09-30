const iterations = 100000;
const keylen = 256/8;
const digest = 'sha256'
const size_salt = 256/8; //NIST SP 800-132

const JWT = require("jsonwebtoken");
const jwt_rand = 64/8;


module.exports = (util, crypto) =>{

    // Promisify versions
    this.randomBytes = util.promisify(crypto.randomBytes)
    const pbkdf2 = util.promisify(crypto.pbkdf2) // Returned key will be Buffer of raw bytes
    let pbkdf2Sync = crypto.pbkdf2Sync

    // Exposed methods

    // Input should be hex encoded strings
    // salt and hash output should be Buffer.toString("hex"), (javascript string of hex characters)
    // Note: node crypto api is moving to handle all data as a utf8 buffer (if input is not a buffer)
    this.consume = async (input, providedSalt)=>{
        if (providedSalt)
            return Promise.resolve(providedSalt)
                .then( (bytesString)=> 
                    pbkdf2(input, bytesString, iterations, keylen, digest) 
                )
                .then( 
                    (passwordHash)=> passwordHash.toString("hex") 
                )
                .then( 
                    (passwordHashHex) => passwordHashHex 
                )
            .catch( ()=> Promise.resolve(null))     
        else
            return this.randomBytes(size_salt)
                .then( (bytes)=> bytes.toString('hex'))
                .then( (bytesString)=>({
                    bytesString: bytesString,
                    passwordHash: pbkdf2Sync(input, bytesString, iterations, keylen, digest)
                }))
                .then( (value)=>({ bytesString: value.bytesString, passwordHashHex: value.passwordHash.toString("hex")}) )
                .then( (value) => ({ passwordHashHex: value.passwordHashHex, saltHex: value.bytesString }) )
            .catch( (error)=> Promise.resolve(null))          
    },

    this.compare = async (input, salt, target)=>{
        return this
            .consume(input, salt)
            .then( (passwordHashHex)=> 
                crypto.timingSafeEqual(Buffer.from(passwordHashHex, "hex"), Buffer.from(target, "hex") ) 
            )
    }

        // Create payload for jwt from user
        this.jwtPayload = async function(user){
            let instant = Date.now();
            const payload = {
                username: user.username,
                iat: instant,
                exp: instant + parseInt(process.env["JWT_EXPIRATION_MS"]),
                random: await this.randomBytes(jwt_rand)
            };
            return payload;
        }

        // Creates JWT as tokens
        this.jwtCreate = function(payload, id){
            payload.id = id;
            return JWT.sign(JSON.stringify(payload), process.env["JWT_KEY"], {algorithm: "HS512"});
        }


    return this 
}