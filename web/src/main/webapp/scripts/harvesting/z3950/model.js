//=====================================================================================
//===
//=== Model (type:z3950)
//===
//=====================================================================================

z3950.Model = function(xmlLoader)
{
	HarvesterModel.call(this);	

	var loader    = xmlLoader;
	var callBackF = null;
	var callBackStyleSheets = null;

	this.retrieveImportXslts = retrieveImportXslts;
    this.retrieveUsers    = retrieveUsers;
	this.retrieveGroups    = retrieveGroups;
	this.retrieveCategories= retrieveCategories;
	this.retrieveIcons     = retrieveIcons;
	this.retrieveRepositories = retrieveRepositories;
	this.getUpdateRequest  = getUpdateRequest;

//=====================================================================================

function retrieveUsers(callBack)
{
    new InfoService(loader, 'users', callBack);
}

//=====================================================================================

function retrieveGroups(callBack)
{
	new InfoService(loader, 'groups', callBack);
}

//=====================================================================================

function retrieveCategories(callBack)
{
	new InfoService(loader, 'categories', callBack);
}

//=====================================================================================

function retrieveImportXslts(callBack)
{
	callBackStyleSheets = callBack;	

	var request = ker.createRequest('type', 'importStylesheets');
	ker.send('xml.harvesting.info', request, ker.wrap(this, retrieveXslts_OK));
}

//=====================================================================================

function retrieveXslts_OK(xmlRes)
{
	if (xmlRes.nodeName == 'error')
		ker.showError(loader.getText('cannotRetrieve'), xmlRes);
	else
	{
		var data = [];
		var list = xml.children(xml.children(xmlRes)[0]);
		
		for (var i=0; i<list.length; i++)
			data.push(xml.toObject(list[i]));
		
		callBackStyleSheets(data);
	}
}

//=====================================================================================

function retrieveRepositories(callBack)
{
	new InfoService(loader, 'z3950repositories', callBack);
}

//=====================================================================================

function retrieveIcons(callBack)
{
	callBackF = callBack;	

	var request = ker.createRequest('type', 'icons');
	
	ker.send('xml.harvesting.info', request, ker.wrap(this, retrieveIcons_OK));
}

//-------------------------------------------------------------------------------------

function retrieveIcons_OK(xmlRes)
{
	if (xmlRes.nodeName == 'error')
		ker.showError(loader.getText('cannotRetrieve'), xmlRes);
	else
	{
		var data = [];
		var list = xml.children(xml.children(xmlRes)[0]);
		
		for (var i=0; i<list.length; i++)
			data.push(xml.textContent(list[i]));
		
		callBackF(data);
	}
}

//=====================================================================================

function getUpdateRequest(data)
{
	var request = str.substitute(updateTemp, data);

	// turn repositories into a list
	var text = '';
	var list = data.REPOSITORIES;
	if (list != null) {
		for (var i=0; i<list.length; i++) {
			text += '<repository id="'+list[i].ID+'"/>';
		}
		request = str.replace(request, '{REPOSITORY_LIST}', text);
	}
	
	return this.substituteCommon(data, request);
}

//=====================================================================================

var updateTemp = 
' <node id="{ID}" type="{TYPE}">'+ 
'    <site>'+
'      <name>{NAME}</name>'+
'      <repositories>'+
'        {REPOSITORY_LIST}'+
'      </repositories>'+
'      <query>{QUERY}</query>'+
'      <icon>{ICON}</icon>'+
'      <account>'+
'        <use>{USE_ACCOUNT}</use>'+
'        <username>{USERNAME}</username>'+
'        <password>{PASSWORD}</password>'+
'      </account>'+
'    </site>'+
'    <options>'+
'      <every>{EVERY}</every>'+
'      <oneRunOnly>{ONE_RUN_ONLY}</oneRunOnly>'+
'    </options>'+
'    <content>'+
'      <validate>{VALIDATE}</validate>'+
'      <importxslt>{IMPORTXSLT}</importxslt>'+
'    </content>'+
'    <owner>'+
'       <user>{OWNER_USER}</user>'+
'       <group>{OWNER_GROUP}</group>'+
'    </owner>'+
'    <privileges>'+
'       {PRIVIL_LIST}'+
'    </privileges>'+
'    <categories>'+
'       {CATEG_LIST}'+
'    </categories>'+
'  </node>';

//=====================================================================================
}
