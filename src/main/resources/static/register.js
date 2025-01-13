document.getElementById("submitBtn").addEventListener("click", function () {
    const formData = new FormData(document.getElementById("registrationForm"));
    const data = {};
    formData.forEach((value, key) => {
        data[key] = value;
    });

    // fetch("http://localhost:9090/api/register", {
    fetch("http://88.222.241.45:9080/api/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
    })
    .then((response) => {
        if (response.ok) {
            return response.json(); // Parse the response JSON
        } else {
            throw new Error("Registration failed!");
        }
    })
    .then((responseData) => {
        console.log("Response Data:", responseData);
        // console.log("Generated Token:", responseData.authToken); // Use the actual key
        alert("Registration successful!");
        
        window.location.href = "gs-login.html";
    })
    .catch((error) => {
        console.error("Error:", error);
        alert("An error occurred!");
    });
});