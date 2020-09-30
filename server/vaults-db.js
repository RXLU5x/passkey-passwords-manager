const request = require("request-promise");
const baseUrl = "http://127.0.0.1:9200"

module.exports = (Vaults) => 
{
    return {
        updateVault: updateVault,
        getVault: getVault,
        createVault: createVault,
        deleteVault: deleteVault,
        checkVaults: checkVaults
    }

    async function updateVault(vaultId, vaultDto, userIdentifier, timeTag)
    {
        let options = 
        {
            method: "POST",
            uri: baseUrl + "/vaults/_update_by_query/",
            body: {
                "query": {
                    "bool": {
                      "must": [
                        {
                          "match": {
                            "_id": vaultId
                          }
                        },
                        {
                          "match": {
                            "userIdentifier": userIdentifier
                          }
                        },
                        {
                            "match": {
                              "timeTag": timeTag
                            }
                          }
                      ]
                    }
                  },
                  "script": {
                    "lang": "painless",
                    "source": `ctx._source.timeTag=${Date.now()}L;ctx._source.vaultHeader=params.vaultHeader;ctx._source.vaultBody=params.vaultBody`,
                    "params": {
                      "vaultHeader": vaultDto.vault_header,
                      "vaultBody": vaultDto.vault_body
                    }
                  }
                },
            json: true
        };

        return request(options)
            .catch(body => 
                Promise.reject(
                    {
                        "code": 404,
                        "body": "Not found. " + body.message 
                    }
                )
            );
    }

    async function getVault(vaultId, userIdentifier)
    {
        let options = 
        {
            method: "GET",
            uri: baseUrl + "/vaults/_doc/" + vaultId,
            json: true
        };
        
        return request(options)
            .then(body =>
                {
                    if(body._source.userIdentifier == userIdentifier && body._source.userIdentifier != null){
                        delete body._source.userIdentifier;
                        return body._source;
                    }
                    else{
                        return Promise.reject({
                            error: true,
                            message: "User doesn't match."
                        })
                    }

                }
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

    async function createVault(username, vaultDto)
    {      
        let date = Date.now(); 
        let options = 
        {
            method: "POST",
            uri: baseUrl + "/vaults/_doc",
            body: {
                "userIdentifier": username,
                "timeTag": date,
                "vaultHeader": vaultDto.vault_header,
                "vaultBody": vaultDto.vault_body
            },
            json: true
        };

        return request(options)
            .then(result =>{
                return{
                    "_id": result["_id"],
                    "timeTag": date
                }
            })
            .catch(body => 
                Promise.reject(
                    {
                        "code": 409,
                        "body": {
                            "error": "Conflict. A vault with this id already exists." 
                        }
                    }
                )
            );
    }

    async function deleteVault(userIdentifier, vaultId)
    {
        let options = 
        {
            method: "POST",
            uri: baseUrl + "/vaults/_delete_by_query/",
            body: {
                "query": {
                    "bool": {
                      "must": [
                        {
                          "match": {
                            "_id": vaultId
                          }
                        },
                        {
                          "match": {
                            "userIdentifier": userIdentifier
                          }
                        }
                      ]
                    }
                  }
                },
            json: true
        };

        return request(options)
            .catch(body => 
                Promise.reject(
                    {
                        body: body
                    }
                )
            );
    }

    async function checkVaults(userIdentifier, type, map){
        switch(type){
            case "All":
                return allVaults(userIdentifier);
            case "Only":
                return onlyVaults(userIdentifier, map);
            case "If":
                return ifVaults(userIdentifier, map);
            default :
                return Promise.reject(
                    {
                        code: "400",
                        body: {
                            "error": "Invalid type for check operation."
                        }
                    }
                )
        }
    }

    async function allVaults(userIdentifier){
        let options = 
        {
            method: "GET",
            uri: baseUrl + "/vaults/_search",
            body: {
                "query": {
                    "match": {
                        "userIdentifier": userIdentifier
                    }
                }
            },
            json: true
        };

        return request(options)
            .then(result=>{
                let hits = result["hits"]["hits"];
                return hits.map( elem =>{
                    let obj = elem["_source"];

                    obj["id"] = elem["_id"]
                    delete obj["userIdentifier"]

                    return obj;
                });
            })
            .catch(body => 
                Promise.reject(
                    {
                        body: body
                    }
                )
            );
    }

    // Only those matched
    async function onlyVaults(userIdentifier, map){
        return allVaults(userIdentifier)
            .then(result=>{
                return result.filter(elem =>{
                    let timeTag = map.get(elem["id"]);
                    map.delete(elem["id"]);

                    if(timeTag === undefined){
                        return false; // Only requested
                    }
                    else if(elem["timeTag"] > timeTag){
                        return true;
                    }
                    else{
                        return false;
                    }
                })
            })
            .then(result=>{
                map.forEach((id, timeTag) => {
                    result.push({
                        "id": id,
                        "delete": true
                    });
                });
                return result;
            })
    }

    // If those matched
    async function ifVaults(userIdentifier, map){
        return allVaults(userIdentifier)
            .then(result=>{
                return result.filter(elem =>{
                    let timeTag = map.get(elem["id"]);
                    map.delete(elem["id"]);

                    if(timeTag === undefined){
                        return true; // All if missing
                    }
                    else if( elem["timeTag"] > timeTag){
                        return true;
                    }
                    else{
                        return false;
                    }
                })
            })
            .then(result=>{
                map.forEach((id, timeTag) => {
                    result.push({
                        "id": id,
                        "delete": true
                    });
                });
                return result;
            })
    }
}