function login() {
    let flag = true;
    let formData = new FormData(document.getElementById("loginForm"));
    let data = {};
    formData.forEach((value, key) => {
        if(!value) {
            flag = false;
        }else {
            data[key] = value;
        };
    });
    if (flag == false) {
        alert("Username and password can not be null!");
    }else {
        let dealFunc = (result) => {
            if (result.success == true) {
                window.location.href = result.message;
            }else {
                alertMessage(result);
            }
        };
        advanceRequest("POST", "/login", JSON.stringify(data), dealFunc, consoleError);
    }
}
