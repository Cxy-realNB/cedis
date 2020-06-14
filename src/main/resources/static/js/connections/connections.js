const connection = {
    "name" : "",
    "host" : "",
    "port" : "",
    "auth" : ""
};

const getConnections = () => {
    advanceRequest("GET", "/v1/executor/list", "", renderHtml, consoleError);
};

const getConnectionsByName = (name) => {
    advanceRequest("GET", "/v1/executor/" + name, "", renderHtmlByName, consoleError);
};

const renderHtmlByName = (e) => {
    let html = "";
    let icon = e.cluster == true ? "<i class=\"icon-share\"></i>" : "<i class=\"fa fa-database\"></i>";
    let openButton = e.open == true ? "<button onclick='shiftState(\""+ e.clientParams.name +"\", \"false\")' type=\"button\" class=\"btn btn-success\">Opening</button>" : "<button onclick='shiftState(\""+ e.clientParams.name +"\", \"true\")' type=\"button\" class=\"btn btn-danger\">Closed</button>";
    let version = e.redisVersion ? e.redisVersion : "NULL";
    html +=
        "<div class='search-content'><div class='row'>" +
        "<div class='col-sm-2 col-xs-6'><h2 class='search-title'>" + e.clientParams.name + "<i onclick='deleteConnection(\"" + e.clientParams.name + "\")' class=\"fa fa-trash\" style='color: red'></i></h2><p class='search-desc'>uri : " + e.clientParams.host + ":" + e.clientParams.port + "</p></div>" +
        "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + icon + "</p><p class='search-counter-label uppercase'>IsCluster</p></div>" +
        "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + e.clients + "</p><p class='search-counter-label uppercase'>Clients</p></div>" +
        "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + version  +"</p><p class='search-counter-label uppercase'>Version</p></div>" +
        "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + e.nodeSize +"</p><p class='search-counter-label uppercase'>NodeSize</p></div>" +
        "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + e.dbSizeSum +"</p><p class='search-counter-label uppercase'>KeySize</p></div>" +
        "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + e.databases +"</p><p class='search-counter-label uppercase'>Databases</p></div>" +
        "<div class='col-sm-2 col-xs-4'></div>" +
        "<div class='col-sm-1 col-xs-4'></div>" +
        "<div class='col-sm-1 col-xs-2'>" + openButton + "</div>" +
        "</div></div>";
    document.getElementById("liElement_" + e.clientParams.name).innerHTML = html;
};

const renderHtml = (result) => {
    let html = "";
    result.forEach(e => {
        let icon = e.cluster == true ? "<i class=\"icon-share\"></i>" : "<i class=\"fa fa-database\"></i>";
        let openButton = e.open == true ? "<button onclick='shiftState(\""+ e.clientParams.name +"\", \"false\")' type=\"button\" class=\"btn btn-success\">Opening</button>" : "<button onclick='shiftState(\""+ e.clientParams.name +"\", \"true\")' type=\"button\" class=\"btn btn-danger\">Closed</button>";
        let version = e.redisVersion ? e.redisVersion : "NULL";
        html += "<li id='liElement_" + e.clientParams.name + "' onclick='forwardToManagement(\""+ e.clientParams.name +"\")' style='cursor:pointer' class='search-item clearfix'>" +
            "<div class='search-content'><div class='row'>" +
            "<div class='col-sm-2 col-xs-6'><h2 class='search-title'>" + e.clientParams.name + "<i onclick='deleteConnection(\"" + e.clientParams.name + "\")' class=\"fa fa-trash\" style='color: red'></i></h2><p class='search-desc'>uri : " + e.clientParams.host + ":" + e.clientParams.port + "</p></div>" +
            "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + icon + "</p><p class='search-counter-label uppercase'>IsCluster</p></div>" +
            "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + e.clients + "</p><p class='search-counter-label uppercase'>Clients</p></div>" +
            "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + version  +"</p><p class='search-counter-label uppercase'>Version</p></div>" +
            "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + e.nodeSize +"</p><p class='search-counter-label uppercase'>NodeSize</p></div>" +
            "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + e.dbSizeSum +"</p><p class='search-counter-label uppercase'>KeySize</p></div>" +
            "<div class='col-sm-1 col-xs-4'><p class='search-counter-number'>" + e.databases +"</p><p class='search-counter-label uppercase'>Databases</p></div>" +
            "<div class='col-sm-2 col-xs-4'></div>" +
            "<div class='col-sm-1 col-xs-4'></div>" +
            "<div class='col-sm-1 col-xs-2'>" + openButton + "</div>" +
            "</div></div></li>";
    });
    if (html == "") html = "No Data";
    $("#connectionContainerUl").html(html);
};

const downloadJson = (result) => {
    let ele = document.createElement('a');
    ele.setAttribute('href', 'data:text/plain;charset=utf-8,' + JSON.stringify(result, null, 2));
    ele.setAttribute('download', "connections.json");
    if (document.createEvent) {
        let event = document.createEvent('MouseEvents');
        event.initEvent('click', true, true);
        ele.dispatchEvent(event);
    } else {
        ele.click();
    }
};

const alertThenRender = (result) => {
    alertMessage(result);
    getConnections();
};

function uploadJson(document) {
    let fileData = document.files[0];
    let reader = new FileReader();
    reader.readAsDataURL(fileData);
    reader.onload = function(e) {
        let base64 = this.result;
        let json = window.atob(base64.split(",")[1]);
        if(confirm('Do you want to upload? It may be cost some times, wait patiently please.')){
            advanceRequest("POST", "/v1/executor/list/closed", json, alertThenRender, consoleError);
        }
    }
}

function createConnectionModalShow() {
    $("#createConnectionModal").modal('show');
}

function exportConnections() {
    baseRequest("GET", "/v1/executor/clientParams", "", downloadJson, consoleError);
}

function persistConnections() {
    baseRequest("PUT", "/v1/executor/clientParams", "", alertMessage, consoleError);
}

function createConnection() {
    let  formData = new FormData(document.getElementById("connectionForm"));
    formData.forEach((value, key) => connection[key] = value);
    if (connection.name != "" && connection.host != "" && connection.port != "") {
        advanceRequest("POST", "/v1/executor/", JSON.stringify(connection), alertThenRender, consoleError);
    }else{
        alert("Name, host and port can not be empty.");
    }
}

function deleteConnection(name) {
    advanceRequest("DELETE", "/v1/executor/" + name, "", getConnections, consoleError);
    window.event.cancelBubble = true;
}

function searchConnection(name) {
    if (name == null || name == "") {
        getConnections();
    }else {
        advanceRequest("GET", "/v1/executor/" + name, "", renderHtml, consoleError);
    }
}

function shiftState(name, state) {
    let dealFunction = (result) => {
        if (result.success == true) {
            getConnectionsByName(name);
        }else {
            alertMessage(result);
        }
    };
    let data = {
        "name" : name,
        "state" : state
    };
    advanceRequest("PUT", "/v1/executor/state" , JSON.stringify(data), dealFunction, consoleError);
    window.event.cancelBubble = true;
}

function forwardToManagement(name) {
    let dealFunction = (result) => {
        if (result.message == "false" || result.message == "true") {
            if (result.message == "true") {
                window.location.href = "/management?name=" + name;
            }else {
                alert("This connection is closed! Make it be opening, please.");
            }
        }else {
            alert(name + "Not found!");
        }
    };
    baseRequest("GET", "/v1/executor/state/" + name, "", dealFunction, consoleError);
}

getConnections();


