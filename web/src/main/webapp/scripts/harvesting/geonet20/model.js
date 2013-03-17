//=====================================================================================
//===
//=== Model (type:Geonetwork 2.0.X)
//===
//=====================================================================================

gn20.Model = function(xmlLoader)
{
	var loader = xmlLoader;

	this.getUpdateRequest = getUpdateRequest;
    this.retrieveUsers    = retrieveUsers;

//=====================================================================================

    function retrieveUsers(callBack)
    {
        new InfoService(loader, 'users', callBack);
    }

//=====================================================================================

function getUpdateRequest(data)
{
	var request = str.substitute(updateTemp, data);
	
	var list = data.SEARCH_LIST;
	var text = '';
		
	for (var i=0; i<list.length; i++)
		text += str.substitute(searchTemp, list[i]);
	
	return str.replace(request, '{SEARCHES}', text);
}

//=====================================================================================

var updateTemp = 
' <node id="{ID}" type="{TYPE}">'+ 
'    <site>'+
'      <name>{NAME}</name>'+
'      <host>{HOST}</host>'+
'      <port>{PORT}</port>'+
'      <servlet>{SERVLET}</servlet>'+
'      <account>'+
'        <use>{USE_ACCOUNT}</use>'+
'        <username>{USERNAME}</username>'+
'        <password>{PASSWORD}</password>'+
'      </account>'+
'    </site>'+
    
'    <searches>'+
'       {SEARCHES}'+
'    </searches>'+

'    <owner>'+
'       <user>{OWNER_USER}</user>'+
'       <group>{OWNER_GROUP}</group>'+
'    </owner>'+

'    <options>'+
'      <every>{EVERY}</every>'+
'      <oneRunOnly>{ONE_RUN_ONLY}</oneRunOnly>'+
'    </options>'+

'    <content>'+
'      <validate>{VALIDATE}</validate>'+
'      <importxslt>{IMPORTXSLT}</importxslt>'+
'    </content>'+

'  </node>';

//=====================================================================================

var searchTemp = 
'    <search>'+
'      <freeText>{TEXT}</freeText>'+
'      <title>{TITLE}</title>'+
'      <abstract>{ABSTRACT}</abstract>'+
'      <keywords>{KEYWORDS}</keywords>'+
'      <digital>{DIGITAL}</digital>'+
'      <hardcopy>{HARDCOPY}</hardcopy>'+
'      <siteId>{SITE_ID}</siteId>'+
'    </search>';


//=====================================================================================
}
