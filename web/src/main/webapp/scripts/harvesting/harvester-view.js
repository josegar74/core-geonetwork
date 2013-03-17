//=====================================================================================
//===
//=== HarvesterView
//===
//=====================================================================================

function HarvesterView()
{
	var prefix       = '???';
	var privilTransf = null;
	var resultTransf = null;
	
//=====================================================================================
//===
//=== API methods
//===
//=====================================================================================

this.setPrefix = function(p)
{
	prefix = p;
}

//=====================================================================================

this.setPrivilTransf = function(transf)
{
	privilTransf = transf;
}

//=====================================================================================

this.setResultTransf = function(transf)
{
	resultTransf = transf;
}

//=====================================================================================

this.getResultTip = function(node)
{
	return resultTransf.transformToText(node);
}

//=====================================================================================
//=== Set/Get methods
//=====================================================================================

this.setEmptyCommon = function()
{
	$(prefix+'.name')      .value = '';
	if($(prefix+'.useAccount')) {
	$(prefix+'.useAccount').checked = true;
	$(prefix+'.username')  .value = '';
	$(prefix+'.password')  .value = '';
	}
	
	$(prefix+'.oneRunOnly').checked = false;

	$(prefix+'.every.days') .value = '0';
	$(prefix+'.every.hours').value = '1';
	$(prefix+'.every.mins') .value = '30';
	
	$(prefix+'.validate').checked = false;
	this.unselectImportXslt();

    this.clearOwnerGroups();
	this.removeAllGroupRows();
	this.unselectCategories();
}

//=====================================================================================

this.setDataCommon = function(node)
{
	var site   = node.getElementsByTagName('site')   [0];
	var options= node.getElementsByTagName('options')[0];
	var content= node.getElementsByTagName('content')[0];
    var owner  = node.getElementsByTagName('owner')[0];
	hvutil.setOption(site, 'name',     prefix+'.name');
	hvutil.setOptionIfExists(site, 'use',      prefix+'.useAccount');
	hvutil.setOptionIfExists(site, 'username', prefix+'.username');
	hvutil.setOptionIfExists(site, 'password', prefix+'.password');
	hvutil.setOption(options, 'oneRunOnly', prefix+'.oneRunOnly');
	var every = new Every(hvutil.find(options, 'every'));
	$(prefix+'.every.days') .value = every.days;
	$(prefix+'.every.hours').value = every.hours;
	$(prefix+'.every.mins') .value = every.mins;
	hvutil.setOption(content, 'validate', prefix+'.validate');
	hvutil.setOption(content, 'importxslt', prefix+'.importxslt');
    hvutil.setOption(owner, 'user', prefix+'.owner');
    hvutil.setOption(owner, 'group', prefix+'.ownergroup');
}

//=====================================================================================

this.getDataCommon = function()
{
	var days  = $F(prefix+'.every.days');
	var hours = $F(prefix+'.every.hours');
	var mins  = $F(prefix+'.every.mins');
	
	var data;
	if($(prefix+'.useAccount')) {
		data =
        {
            //--- site
            NAME     : $F(prefix+'.name'),

            USE_ACCOUNT: $(prefix+'.useAccount').checked,
            USERNAME   : $F(prefix+'.username'),
            PASSWORD   : $F(prefix+'.password'),

            //--- options
            EVERY        : Every.build(days, hours, mins),
            ONE_RUN_ONLY : $(prefix+'.oneRunOnly').checked,

            //--- content
            IMPORTXSLT   : $F(prefix+'.importxslt'),
            VALIDATE     : $(prefix+'.validate').checked,

            // owner
            OWNER_USER   : $F(prefix+'.owner'),
            OWNER_GROUP  : $F(prefix+'.ownergroup')
        }
	}
	else {
		data =
		{
			//--- site	
			NAME     : $F(prefix+'.name'),
			
			//--- options		
			EVERY        : Every.build(days, hours, mins),
			ONE_RUN_ONLY : $(prefix+'.oneRunOnly').checked,

			//--- content		
			IMPORTXSLT   : $F(prefix+'.importxslt'),
			VALIDATE     : $(prefix+'.validate').checked,

            // owner
            OWNER_USER   : $F(prefix+'.owner'),
            OWNER_GROUP  : $F(prefix+'.ownergroup')
		}
	}
	
	return data;
}

//=====================================================================================

this.isDataValidCommon = function()
{
	var days  = $F(prefix+'.every.days');
	var hours = $F(prefix+'.every.hours');
	var mins  = $F(prefix+'.every.mins');
	
	if (Every.build(days, hours, mins) == 0)
	{
		alert(loader.getText('everyZero'));
		return false;
	}
		
	return true;
}

//=====================================================================================
//=== Owner methods
//=====================================================================================

this.clearOwners = function()
{
	$(prefix+ '.owner').options.length = 0;
}

//=====================================================================================

this.addOwner = function(id, label)
{
	gui.addToSelect(prefix+'.owner', id, label);
}

//=====================================================================================

this.clearOwnerGroups = function()
{
	$(prefix+ '.ownergroup').value = '';
    $(prefix+ '.ownergroup_sel').options.length = 0;
}

//=====================================================================================
//=== Groups methods
//=====================================================================================

this.clearGroups = function() 
{ 
	$(prefix+ '.groups').options.length = 0;
}

//=====================================================================================

this.addGroup = function(id, label)
{
	gui.addToSelect(prefix+'.groups', id, label);
}

//=====================================================================================

this.getSelectedGroups = function() 
{ 
	var ctrl = $(prefix+'.groups');
	
	var result = [];
	
	for (var i=0; i<ctrl.options.length; i++)
		if (ctrl.options[i].selected)
			result.push(ctrl.options[i]);
			
	return result;
}

//=====================================================================================

this.addEmptyGroupRows = function(groups)
{
	for (var i=0; i<groups.length; i++)
	{
		var option = groups[i];
		var doc    = Sarissa.getDomDocument();
		var group  = doc.createElement('group');
		var groupId= option.value;
		
		group.setAttribute('id', groupId);
	
		this.addGroupRow(group);
	}
}

//=====================================================================================

this.addGroupRow = function(group)
{
	//--- retrieve group's name from the list of loaded groups
	
	var id   = group.getAttribute('id');
	var name = '???';
	var ctrl = $(prefix+'.groups');
	
	//--- discard group if it has already been added
	
	var list = $(prefix+'.privileges').getElementsByTagName('TR');
	
	for (var i=1; i<list.length; i++)
	{
		//-- string is '<prefix>.group.<id>'
		var groupID = list[i].getAttribute('id').split('.')[2];
		
		if (id == groupID)
			return;
	}
	
	//--- retrieve group's name
	
	for(var i=0; i<ctrl.options.length; i++)
		if (ctrl.options[i].value == id)
		{
			name = xml.textContent(ctrl.options[i]);
//			ctrl.options[i].style.background = '#D0D0D0';
			break;
		}
	
	group.setAttribute('name', name);
	
	//--- add group's row
	
	var xslRes = privilTransf.transform(group);
	
	//--- add the new privilege row to list
	gui.appendTableRow(prefix+'.privileges', xslRes);
}

//=====================================================================================

this.removeGroupRow = function(groupId)
{
	Element.remove(groupId);
}

//=====================================================================================

this.removeAllGroupRows = function()
{
	gui.removeAllButFirst(prefix+'.privileges');
}

//=====================================================================================

this.addGroupRows = function(node)
{
	var privil = node.getElementsByTagName('privileges');
	
	if (privil.length == 0)
		return;
		 
	var list = privil[0].getElementsByTagName('group');
	
	for (var i=0; i<list.length; i++)
		this.addGroupRow(list[i]);
}

//=====================================================================================

this.getPrivileges = function()
{
	var data = [];
	var list = $(prefix+'.privileges').getElementsByTagName('TR');
	
	for (var i=1; i<list.length; i++)
	{
		var trElem    = list[i];
		var inputList = trElem.getElementsByTagName('INPUT');
		var groupData = [];
		
		//-- the string is '<prefix>.group.<id>'
		var groupID = trElem.getAttribute('id').split('.')[2];
		
		for (var j=0; j<inputList.length; j++)
			if (inputList[j].checked)
				groupData.push(inputList[j].name);
				
		if (groupData.length != 0)
			data.push(
			{
				GROUP      : groupID,
				OPERATIONS : groupData
			});
	}
	
	return data;
}

//=====================================================================================
//=== ImportXslt methods
//=====================================================================================

this.clearImportXslt = function() 
{ 
	$(prefix+ '.importxslt').options.length = 0;
}

//=====================================================================================

this.addImportXslt = function(id,name)
{
	gui.addToSelect(prefix+'.importxslt', id, name);
}

//=====================================================================================

this.unselectImportXslt = function() 
{ 
	var ctrl = $(prefix+'.importxslt');
	
	for (var i=0; i<ctrl.options.length; i++)
		ctrl.options[i].selected = false;
}

//=====================================================================================
//=== Categories methods
//=====================================================================================

this.clearCategories = function() 
{ 
	$(prefix+ '.categories').options.length = 0;
}

//=====================================================================================

this.addCategory = function(id, label)
{
	gui.addToSelect(prefix+'.categories', id, label);
}

//=====================================================================================

this.getSelectedCategories = function() 
{ 
	var ctrl = $(prefix+'.categories');
	
	var result = [];
	
	for (var i=0; i<ctrl.options.length; i++)
		if (ctrl.options[i].selected)
			result.push({ ID : ctrl.options[i].value });
			
	return result;
}

//=====================================================================================

this.unselectCategories = function() 
{ 
	var ctrl = $(prefix+'.categories');
	
	for (var i=0; i<ctrl.options.length; i++)
		ctrl.options[i].selected = false;
}

//=====================================================================================

this.selectCategories = function(node)
{ 
	var categs = node.getElementsByTagName('categories');
	
	if (categs.length == 0)
		return;
		
	var list = categs[0].getElementsByTagName('category');
	
	for (var i=0; i<list.length; i++)
		selectCategory(list[i]);
}

this.loadUserGroups = function() {
    var ctrl = $(prefix+'.ownergroup_sel');

    if ($(prefix+".owner").value == "") {
        ctrl.options.length =0;

        return;
    }
    var pars = "id=" + $(prefix+".owner").value;

    $(prefix+'.loading_groups').show();
    var ajaxRequest = new Ajax.Request(
        getGNServiceURL('xml.usergroups.list'),
        {
            method: 'get',
            parameters: pars,

            onSuccess: function(req) {
                ctrl.options.length =0;

                //Response received
                var groups = req.responseXML.documentElement;

                for (i = 0; i < groups.getElementsByTagName('group').length; i++) {
                    var group = groups.getElementsByTagName('group')[i];

                    var id = group.getElementsByTagName('id')[0].firstChild.data;
                    var name = group.getElementsByTagName('name')[0].firstChild.data;
                    if (parseInt(id) > 1) {
                        var selected =  (id ==  $(prefix+'.ownergroup').value);
                        gui.addToSelect(prefix+'.ownergroup_sel', id, name, selected);
                    }
                }

                $(prefix+'.ownergroup').value = $(prefix+'.ownergroup_sel').value;
                $(prefix+'.loading_groups').hide();

            },

            onFailure: function(req) {
                // Clear previous groups if any error
                ctrl.options.length =0;
                $("loading_groups").hide();

            }
        }
    );
}

this.updateUserGroupSelection = function() {
    var ctrl_sel = $(prefix+'.ownergroup_sel');
    var ctrl = $(prefix+'.ownergroup');
    ctrl.value = ctrl_sel.value;
}

//=====================================================================================
//===
//=== Private methods
//===
//=====================================================================================

function selectCategory(categ)
{
	var id   = categ.getAttribute('id');
	var ctrl = $(prefix+'.categories');
	
	for (var i=0; i<ctrl.options.length; i++)
		if (ctrl.options[i].value == id)
		{
			ctrl.options[i].selected = true;
			return;
		}
}

//=====================================================================================
}

