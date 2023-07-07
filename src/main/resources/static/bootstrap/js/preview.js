const linkPreviewApiUrl = "https://api.linkpreview.net";
const linkPreviewApiKey = "90f1e4b2c78610d8991f84f03536e7c3"; // replace with your API key

const urlInput = document.getElementById("myInput");
const linkPreview = document.getElementById("link-preview");

urlInput.addEventListener("input", async () => {
    const url = urlInput.value;

    if (url.trim() !== "") {
        const response = await fetch(`${linkPreviewApiUrl}?key=${linkPreviewApiKey}&q=${url}`);
        const data = await response.json();

        if (response.ok) {
            linkPreview.innerHTML = `
            <div id="myDiv" class="card card-body text-center mt-3">
                <a href="${data.url}" target="_blank">
                  <img src="${data.image}" alt="${data.title}">
                </a>
            </div>
        `;
        } else {
            linkPreview.innerHTML = "Error: " + data.message;
        }
    } else {
        linkPreview.innerHTML = "";
    }
});
