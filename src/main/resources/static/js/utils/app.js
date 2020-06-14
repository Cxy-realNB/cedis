function baseRequest(type, url, data, successFun, errFun) {
    $.ajax({
        url: url,
        type: type,
        contentType: "application/json",
        cache: false,
        data: data,
        dataType: "json",
        success: successFun,
        error: errFun
    });
}

function advanceArrRequest(type, url, data, successFun, errFun) {
    $.ajax({
        url: url,
        type: type,
        contentType: "application/json",
        cache: false,
        data: data,
        traditional:true,
        dataType: "json",
        success: successFun,
        error: errFun,
        beforeSend: function () {
            $("#loadingModal").modal("show");
        },
        complete: function () {
            $("#loadingModal").modal("hide");
        }
    });
}

function advanceRequest(type, url, data, successFun, errFun) {
    $.ajax({
        url: url,
        type: type,
        contentType: "application/json",
        cache: false,
        data: data,
        dataType: "json",
        success: successFun,
        error: errFun,
        beforeSend: function () {
            $("#loadingModal").modal("show");
        },
        complete: function () {
            $("#loadingModal").modal("hide");
        }
    });
}

function advanceSyncRequest(type, url, data, successFun, errFun) {
    let defer = $.Deferred();
    $.ajax({
        url: url,
        type: type,
        contentType: "application/json",
        cache: false,
        data: data,
        dataType: "json",
        success: function(data){
            defer.resolve(data)
        },
        error: function(data){
            defer.resolve(data)
        },
    });
    return defer.promise();
}

function getQueryVariable(variable) {
    let query = window.location.search.substring(1);
    let vars = query.split("&");
    for (let i = 0; i < vars.length; i ++) {
        let pair = vars[i].split("=");
        if(pair[0] == variable) return pair[1];
    }
    return(false);
}

function jsonToUrl(json) {
    return Object.keys(json).map((key) => key + "=" + json[key]).join("&");
}

const lang ={
    "aria": {
        "sortAscending": ": activate to sort column ascending",
        "sortDescending": ": activate to sort column descending"
    },
    "emptyTable": "No data available in table",
    "info": "Showing _START_ to _END_ of _TOTAL_ records",
    "infoEmpty": "No records found",
    "infoFiltered": "(filtered1 from _MAX_ total records)",
    "lengthMenu": "Show _MENU_",
    "search": "Search:",
    "zeroRecords": "No matching records found",
    "paginate": {
        "previous":"Prev",
        "next": "Next",
        "last": "Last",
        "first": "First"
    }
};

const consoleLog = (result) => {
    console.log(result)
};

const consoleError = (result) => {
    console.error(result)
};

const consoleInfo = (result) => {
    console.info(result)
};

const consoleDebug = (result) => {
    console.debug(result)
};

const consoleWarn = (result) => {
    console.warn(result)
};

const alertMessage = (result) => {
    alert(result.message)
};

const alertMessageIfFailed = (result) => {
    if (result.success != true) {
        alert(result.message)
    }
};

const alertMessageJSON = (result) => {
    alert(JSON.stringify(result, null, 2))
};