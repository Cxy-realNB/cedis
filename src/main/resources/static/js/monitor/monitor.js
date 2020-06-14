let resultContainer = new Map();
const renderHtml = (result) => {
    let html = "<option value=''>Select a connection</option>";
    result.forEach((e) => {
        resultContainer.set(e.clientParams.name, e);
        html += e.open == true ? "<option value='" + e.clientParams.name + "'>" + e.clientParams.name + "</option>" : "<option value='" + e.clientParams.name + "' disabled='disabled' style='background-color: #D3D3D3'>" + e.clientParams.name + "</option>";
    });
    document.getElementById("connectionsSelect").innerHTML = html;
};

const getConnections = () => {
    advanceRequest("GET", "/v1/executor/list", "", renderHtml, consoleError);
};

function refreshAndRender(name) {
    if (name == "") return;
    let dealFunc = (result) => {
        result.clientConfig = JSON.parse(result.clientConfig);
        resultContainer.set(result.clientParams.name, result);
        renderDetail(name);
    };
    advanceRequest("GET", "/v1/monitor/" + name, "", dealFunc, consoleError);
};

function renderDetail(name) {
    if (name == "") return;
    let ele = resultContainer.get(name);
    document.getElementById("redisConfig").innerText = JSON.stringify(ele.clientConfig, null, 2);
    document.getElementById("connectionInfo").innerText = ele.clientInfo;
    if (ele.cluster == false) {
        document.getElementById("clusterInfo").innerText = "It is not a cluster nodes";
        document.getElementById("clusterNodes").innerText = "It is not a cluster nodes.";
    }else {
        document.getElementById("clusterInfo").innerText = ele.clusterInfo;
        document.getElementById("clusterNodes").innerText = ele.clusterNodes;
    }
}

getConnections();