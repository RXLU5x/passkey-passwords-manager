module.exports = (router, vaultsDatabase) => 
{    
    /* Router setup */

	router.post("/", updateVault);
	router.get("/", getVault);
    router.put("/create", createVault);
    router.delete("/delete", deleteVault);
    router.post("/check", checkVaults)

    return router
    
    function updateVault(request, response)
    {
        let vaultDTO = request.body.vault;

        if(!request.query["id"]){
            response.status(400).json("Missing vault id")
            return ;
        }
        else if(!request.body || !vaultDTO.vault_header || !vaultDTO.vault_body || !request.body.timeTag){
            response.status(400).json("Missing fields.")
            return ;
        }

        return vaultsDatabase
            .updateVault(request.query["id"], vaultDTO, request["user"], request.body.timeTag)
            .then(status =>{
                if(status.updated === 1)
                    return response.status(200).json({success: true})
                else
                    return  Promise.reject(
                        {
                            "code": 400,
                            "body": {
                                "error": "Vault doesn't match. Update local copy."
                            }
                        }
                    )
            })
            .catch(error => response.status(error.code || 500).json(error.body || "Unknown internal server error"))
    }

    function getVault(request, response)
    {
        if(!request.query["id"]){
            response.status(400).json("Missing vault id")
            return 
        }

        return vaultsDatabase
            .getVault(request.query["id"], request["user"])
            .then(vault => response.status(200).json({
                timeTag: vault.timeTag,
                vault: {
                    "vault_header": vault.vaultHeader,
                    "vault_body": vault.vaultBody
                }
            }))
            .catch(error => response.status(error.code || 500).json(error.body || "Unknown internal server error"))
    }

    function createVault(request, response){
        if(!request.body || !request.body.vault_header || !request.body.vault_body){
            response.status(400).json("Missing vault body and/or header.")
            return 
        }

        return vaultsDatabase
            .createVault(request["user"], request.body)
            .then(status => response.status(200).json(
                {
                    "id": status["_id"], 
                    "timeTag": status["timeTag"]
                }
            )).catch(error => response.status(error.code || 500).json(error.body || "Unknown internal server error") )
    }

    function deleteVault(request, response){
        if(!request.query["id"]){
            response.status(400).json("Missing vault id")
            return ;
        }

        return vaultsDatabase
            .deleteVault(request["user"], request.query["id"])
            .then(status => response.status(200).json(status))
            .catch(error => response.status(error.code || 500).json(error.body || "Unknown internal server error") )
    }

    function checkVaults(request, response){
        if(!request.body["type"]){
            response.status(400).json("Missing list type.")
            return ;
        }

        let map = new Map();
        if(request.body["type"] != "All"){
            request.body["vaults"].forEach(element => {
                let id = element["id"];
                let timeTag = element["timeTag"];
                if(id != null && timeTag != null){
                    map.set(id, timeTag);
                }
            });
        }

        return vaultsDatabase
            .checkVaults(request["user"], request.body["type"], map)
            .then(result =>{
                let body = result.map( elem =>{

                    elem["vault"] = {
                        "vault_header": elem["vaultHeader"],
                        "vault_body": elem["vaultBody"]
                    }

                    delete elem.vaultHeader;
                    delete elem.vaultBody;

                    return elem;
                })
                return response.status(200).json({
                    vaults: body
                });
            })
            .catch(error => response.status(error.code || 500).json(error || "Unknown internal server error") )
    }
}