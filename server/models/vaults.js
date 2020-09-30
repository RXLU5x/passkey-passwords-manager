module.exports = function() 
{
    return {
        vaultInfo: async function(header, body){
            this.header = header
            this.body = body

            this.missingInfo = ()=> {
                if(!this.header || !this.body)
                   return true
                else
                    return false
            }  
            
            return this
        }
    }
}