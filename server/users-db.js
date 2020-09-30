const request = require("request-promise");
const baseUrl = "http://127.0.0.1:9200"

module.exports = (Users) => {
    return{
        createUser: createUser,
        getUser: getUser,
        deleteUser: deleteUser
    }

    async function getUser(username)
    {
        let options = 
        {
            method: "GET",
            uri: baseUrl + "/users/_doc/" + username,
            json: true
        };
        
        return request(options)
            .then(body =>
                Users.databaseUser(
                    body._source.firstname, 
                    body._source.lastname, 
                    body._source.email, 
                    body._source.username, 
                    body._source.passwordHash, 
                    body._source.salt
                )
            )
            .catch(body => 
                Promise.reject(
                    {
                        "code": 404,
                        "body": "Not found. " + body.message 
                    }
                )
            )
    }

    async function createUser(user)
    {
        let options = 
        {
            method: "POST",
            uri: baseUrl + "/users/_create/" + user.username,
            body: user,
            json: true
        };

        return request(options)
            .then(body => user)
			.catch(body => 
                Promise.reject(
                    {
                        "code": 409,
                        "body": "Conflict. A user with this username already exists." 
                    }
                )
            );
    }

    async function deleteUser(user)
    {
        let options = 
        {
            method: "POST",
            uri: baseUrl + "/users,vaults,tokens/_delete_by_query",
            body:{
                "query": {
                    "multi_match": {
                      "query": user.username,
					  "fields": ["_id", "userIdentifier", "username"]
                    }
                }
            },
            json: true
        };

        return request(options)
            .then(body => {
                if(body.deleted != 0 && body.deleted === body.total) return true
                else 
                    return null
            })
            .catch(body => 
                Promise.reject(
                    {
                        "code": 502,
                        "body": "Bad gateway. " + body.error.reason
                    }
                )
            );
    }
}