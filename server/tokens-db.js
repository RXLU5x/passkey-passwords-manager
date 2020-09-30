const request = require("request-promise");
const baseUrl = "http://127.0.0.1:9200"

module.exports = function(){
    return{
        registerToken: registerToken,
        validateToken: validateToken
    }

    function registerToken(payload){
        let ret = {
            error: null,
            id: null
        };
        let options = 
        {
            method: "POST",
            uri: baseUrl + "/tokens/_doc",
            body: payload,
            json: true
        };

        return request(options)
            .then( body => {
                ret.id = body["_id"];
                return ret;
            })
            .catch(_=> {
                ret.error = {
                    "code": 500,
                    "body": "Unable to create session token."
                }
                return ret;
            })
    }

    function validateToken(payload){
        function reject(reason){
            return {
                "error": reason
            }
        }

        let ret = {
            valid: null,
            data: null
        };
        let options = 
        {
            method: "GET",
            uri: baseUrl + "/tokens/_doc/" + payload.id,
            body: payload,
            json: true
        }

        return request(options)
            .then( body =>{

                ret.valid = true;
                ret.data = payload.username;

                if(payload.exp >= Date.now() ){
                    valid = false
                    data = reject("Invalid token.");
                }
                if(body.username != payload.username){
                    valid = false
                    data = reject("Invalid token.");
                }
                if(body.exp != payload.exp){
                    valid = false
                    data = reject("Invalid token.");
                }
                if(body.iap != payload.iap){
                    valid = false
                    data = reject("Invalid token.");
                }
                if(body.random != payload.random){
                    valid = false
                    data = reject("Invalid token.");
                }


                return ret;
            })
            .catch(_=>{
                ret.data = {
                    "error": "Unable to verify session token."
                }
                return ret;
            })
    }
}