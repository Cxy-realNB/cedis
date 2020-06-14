let name = getQueryVariable("name");
let monitor = {};
let databases;
let currentKey;
let currentRedisObj = {};
let cursorMap = new Map();
cursorMap.set(1, 0);
let page = {
    "name" : name,
    "db" : 0,
    "cursor" : 0,
    "limit" : 10,
    "draw" : 0,
    "page" : 1
};

const createDatabaseSelection = (result) => {
    monitor = result;
    databases = monitor.databases;
    let html = "";
    for (let i = 0; i < databases; i ++) {
        html += "<option>" + i + " (size : " + monitor.dbSize[i] + ")</option>";
    }
    document.getElementById("databasesSelect").innerHTML = html;
    document.getElementById("databasesSelect").selectedIndex = page.db;
};

let table = {};
var initialTable = function (){
    document.getElementById("keyTableContainer").innerHTML = "<table class=\"table table-striped table-bordered table-hover table-checkable order-column\" id=\"keyTable\"></table>";
    table = $("#keyTable").dataTable({
        language: lang,
        bSort: false,
        autoWidth: false,
        stripeClasses: ["odd", "even"],
        processing: false,
        serverSide: true,
        bFilter: false,
        destroy: true,
        bLengthChange: true,
        orderMulti: false,
        order: [],
        pagingType: "bootstrap_cxyNumber",
        bScrollInfinite: true,
        ajax: function (data, callback, settings) {
            page.limit = data.length;
            page.draw = data.draw;
            page.cursor = cursorMap.get(data.start / data.length);
            adjustPage(data);
            let dealFunction = (result) => {
                page.cursor = result.cursor;
                cursorMap.set(data.start / data.length + 1, result.cursor);
                callback(result);
            };
            advanceRequest("GET", "/v1/keys/page", page, dealFunction, consoleError);
        },
        lengthMenu:[[10, 50, 100, 1000, 10000, -1],
            [10, 50, 100, 1000, 10000, "ALL"]],
        pageLength: 10,
        columns: [
            { "title":"<label class='mt-checkbox mt-checkbox-single mt-checkbox-outline'><input id='allCheck' type='checkbox' class='group-checkable'/><span></span></label>", "data": function(row){
                    return '<label class="mt-checkbox mt-checkbox-single mt-checkbox-outline"><input type="checkbox" name=\"single-key\" class="group-checkable" value="'+row+'" /><span></span></label>';
            }},
            { "title":"key", "data": function (row) {
                    return "<a onclick='getValueByKey(\"" + row + "\")'>" + row + "</a>";
            }},
            { "title":"action", "data": function (row) {
                return "<button type=\"button\" class=\"btn btn-warning\" onclick='showRenameModal(\"" + row + "\")'>rename</button>";
            }}
        ]
    }).api();
    $("#keyTable_filter").css("display","none");
}

function adjustPage(data) {
    if (page.limit != data.length) {
        data.start = 0;
        cursorMap.clear();
        cursorMap.set(1, 0);
    }
}

function showRenameModal(key) {
    document.getElementById("renameKeyH4").innerHTML = "<i class=\"fa fa-key\"></i>  " + key;
    document.getElementById('newKeyName').value = "";
    $("#renameKeyModal").modal("show");
}

function showAddKeyModal() {
    $("#addKeyModal").modal("show");
    document.querySelector("#addKeyForm select[name=type]").value = "STRING";
    showExplanation("STRING");
}

function renameKey(oldKey, newKey) {
    let data = {
        "oldKey" : oldKey.trim(),
        "newKey" : newKey.trim(),
        "db" : page.db,
        "name" : name
    }
    advanceRequest("PUT", "/v1/keys/rename/", JSON.stringify(data), consoleInfo, consoleError);
}

function getValueByKey(key) {
    if (currentKey == null && key == null) {
        alert("You should select a key first.");
        return;
    }else {
        if (key) {
            currentKey = key;
        }
        let dealFunction = (result) => {
            currentRedisObj = result;
            let html = "<table class=\"table table-striped table-bordered table-hover table-checkable order-column\"><tbody>" +
                "<tr><td> key </td><td>" + result.key + "</td></tr>" +
                "<tr><td> type </td><td><span class=\"label label-sm label-info\">" + result.dataType + "</span></td></tr>" +
                "<tr rowspan=\"10\"><td> value </td><td><textarea class='form-control' rows='10' id='redisObjectValue' style='border: none' >" + JSON.stringify(result.value, null, 2) + "</textarea></td></tr>" +
                "<tr><td> ttl </td><td>" + result.ttl + "</td></tr>" +
                "</tbody></table>";
            document.getElementById("valueContainer").innerHTML = html;
        };
        advanceRequest("GET", "/v1/keys/value/" + currentKey, "name=" + name + "&db=" + page.db, dealFunction, consoleError);
    }
}

function updateValue() {
    if (currentKey == currentRedisObj.key) {
        let newValue = document.getElementById("redisObjectValue").value;
        if (newValue != JSON.stringify(currentRedisObj.value, null, 2)) {
            if(confirm('Do you want to update the key? (You can not update the type and you should refer the format for add redis object, you can click create button to read more.)')) {
                let alertMessageThenReloadKey = (result) => {
                    alertMessageIfFailed(result);
                    getValueByKey();
                }
                let data = {};
                data.name = name;
                data.db = page.db;
                data.value = newValue;
                data.type = currentRedisObj.dataType;
                data.key = currentRedisObj.key;
                advanceRequest("PUT", "/v1/keys/", JSON.stringify(data), alertMessageThenReloadKey, consoleError);
            }
        }else {
            alert("Value do not change.");
        }
    }else {
        alert("Server error! Select key again please.");
    }
}

function getKeysAndMatch(match) {
    if (match != "*" && match != "" && match != null) {
        let dealFunction = (result) => {
            table = {};
            let html = "<tbody>";
            if (result.length == 0) {
                html += "<tr><td colspan=\"3\">No records found</td></tr>";
            }else {
                result.forEach((e) => {
                    html += "<tr role=\"row\" class=\"odd\"><td><label class=\"mt-checkbox mt-checkbox-single mt-checkbox-outline\"><input type=\"checkbox\" class=\"group-checkable\" name=\"single-key\" value=\""+ e +"\"><span></span></label></td><td><a onclick='getValueByKey(\"" + e + "\")'>" + e +"</a></td><td><button onclick='showRenameModal(\"" + e + "\")' type=\"button\" class=\"btn btn-warning\">rename</button></td></tr>";
                });
            }
            html += "</tbody>";
            document.getElementById("keyTableContainer").innerHTML = "<table class=\"table table-striped table-bordered table-hover table-checkable order-column\" id=\"keyTable\">"
                + "<thead><tr role=\"row\"><th class=\"\" rowspan=\"1\" colspan=\"1\"><label class=\"mt-checkbox mt-checkbox-single mt-checkbox-outline\"><input id=\"allCheck\" type=\"checkbox\" class=\"group-checkable\"><span></span></label></th><th class=\"\" rowspan=\"1\" colspan=\"1\">key</th><th>action</th></tr></thead>"
                + html
                + "</table>";
        };
        advanceRequest("GET", "/v1/keys/" + name, "db=" + document.getElementById("databasesSelect").selectedIndex + "&match=" + match, dealFunction, consoleError);
    }else{
        initialTable();
    }
}

function getMonitor(name) {
    advanceRequest("GET", "/v1/monitor/" + name, "", createDatabaseSelection, consoleError);
}

function getRefreshDatabases(name) {
    $("#loadingModal").modal("show");
    let fun = advanceSyncRequest("GET", "/v1/monitor/refreshDB/" + name, "", "", consoleError);
    $.when(fun).done(function(data){
        consoleLog(data)
        $("#loadingModal").modal("hide");
        createDatabaseSelection(data);
    });
}

function selectDatabase(db) {
    page.db = db;
    page.cursor = 0;
    document.getElementById('keyName').value = "";
    refresh();
}

function refresh() {
    getRefreshDatabases(name);
    $("#allCheck").prop("checked", false);
    if (JSON.stringify(table) !== '{}') {
        table.ajax.reload();
    }else {
        initialTable();
    }
}

function showExplanation(type) {
    let explanation = "";
    switch (type) {
        case "STRING" :
            explanation = "Input string value.";
            break;
        case "HASH" :
            explanation = "You can input a json. e.g. {\"name\" : \"smith\"}";
            break;
        case "LIST" :
            explanation = "You can input some items comma separated.";
            break;
        case "SET" :
            explanation = "You can input some items comma separated, and do not use duplicate item, it will be ignored.";
            break;
        case "ZSET" :
            explanation = "You can input some items comma separated and sequenced, and do not use duplicate item, it will be ignored. In addition you should add score and value, the score is in front of value, e.g. 1.0,xx,2.0,bb,3.0,kk.";
            break;
    }
    document.querySelector("#addKeyForm textarea[name=explanation]").value = explanation;
}

function addRedisKey() {
    let data = {};
    let  formData = new FormData(document.getElementById("addKeyForm"));
    formData.forEach((value, key) => data[key.trim()] = value.trim());
    data.name = name;
    data.db = page.db;
    advanceRequest("POST", "/v1/keys/", JSON.stringify(data), alertMessage, consoleError);
}

function deleteKeys() {
    let data = $("input[name=single-key]:checked");
    let keys = [];
    for (let i = 0; i < data.length; i++) {
        keys.push(data[i].value);
    }
    if (data.length == 0) {
        alert("You should select some keys.");
    }else {
        if(confirm('Do you want to delete selected keys?')){
            let deleteVo = {
                "keys": keys,
                "name" : name,
                "db" : page.db
            };
            let alertMessageThenReload = (result) => {
                alertMessage(result);
                refresh();
            };
            advanceArrRequest("DELETE", "/v1/keys/", JSON.stringify(deleteVo), alertMessageThenReload, consoleError);
        }
    }
}

$("body").on("click", "#allCheck", function(){
    $("input[name=single-key]").prop("checked", this.checked);
});

$("body").on("click", "input[name=single-key]", function(){
    if ($("input[name=single-key]:not(:checked)").length > 0) {
        $("#allCheck").prop("checked", false);
    }else {
        $("#allCheck").prop("checked", true);
    }
});

initialTable();
getMonitor(name);