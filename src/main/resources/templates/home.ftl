<#include "layout.ftl">

<style>
    #myDiv img {
        max-width: 25%;
        height: 25%;
    }
</style>

<body>
<div class="card card-body text-center m-3">
    <form action="/shortenUrl" method="post">
        <div class="input-group">
            <span class="input-group-text">Enter a URL</span>
            <input type="url" class="form-control" placeholder="https://www.google.com/" name="url" id="myInput" required>
            <button class="btn btn-outline-dark" type="submit">Shorten</button>
        </div>
        <div id="link-preview"></div>
    </form>
</div>
<script src="/bootstrap/js/qrcode.js"></script>
<div class="card card-body mt-3 mx-3">
    <#if urls?has_content>
        <table class="table">
            <thead>
            <tr>
                <th scope="col" style="width: 30%">Shortened Link</th>
                <th scope="col" style="width: 30%">Original Link</th>
                <th scope="col" style="width: 10%">Visits</th>
                <th scope="col" style="width: 20%"></th>
            </tr>
            </thead>
            <tbody>
            <#list urls as url>
                <tr>
                    <td><a href="/${url.shortenedUrl}" class="text-black link-offset-2-hover link-underline-opacity-0 link-underline-opacity-75-hover link-underline-dark">${host}/${url.shortenedUrl}</a></td>
                    <td><a href="${url.url}" class="text-black link-underline-opacity-0 link-underline-dark">${url.url}</a></td>
                    <td>${url.visits}</td>
                    <td>
                        <div class="input-group">
                            <#if isAdmin?? && isAdmin == true>
                                <a class="btn btn-outline-success" style="width: 20%" data-bs-toggle="modal" data-bs-target="#qrModal-${url.shortenedUrl}">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-qr-code" viewBox="0 0 16 16">
                                        <path d="M2 2h2v2H2V2Z"></path>
                                        <path d="M6 0v6H0V0h6ZM5 1H1v4h4V1ZM4 12H2v2h2v-2Z"></path>
                                        <path d="M6 10v6H0v-6h6Zm-5 1v4h4v-4H1Zm11-9h2v2h-2V2Z"></path>
                                        <path d="M10 0v6h6V0h-6Zm5 1v4h-4V1h4ZM8 1V0h1v2H8v2H7V1h1Zm0 5V4h1v2H8ZM6 8V7h1V6h1v2h1V7h5v1h-4v1H7V8H6Zm0 0v1H2V8H1v1H0V7h3v1h3Zm10 1h-1V7h1v2Zm-1 0h-1v2h2v-1h-1V9Zm-4 0h2v1h-1v1h-1V9Zm2 3v-1h-1v1h-1v1H9v1h3v-2h1Zm0 0h3v1h-2v1h-1v-2Zm-4-1v1h1v-2H7v1h2Z"></path>
                                        <path d="M7 12h1v3h4v1H7v-4Zm9 2v2h-3v-1h2v-1h1Z"></path>
                                    </svg>
                                </a>
                                <a href="/shortenedLinks/${url.id}" class="btn btn-outline-success" style="width: 40%">Summary</a>
                                <a href="/shortenedlinks/delete/${url.id}" class="btn btn-outline-danger" style="width: 40%">Delete</a>
                            <#else>
                                <a class="btn btn-outline-success" style="width: 20%"  data-bs-toggle="modal" data-bs-target="#qrModal-${url.shortenedUrl}">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-qr-code" viewBox="0 0 16 16">
                                        <path d="M2 2h2v2H2V2Z"></path>
                                        <path d="M6 0v6H0V0h6ZM5 1H1v4h4V1ZM4 12H2v2h2v-2Z"></path>
                                        <path d="M6 10v6H0v-6h6Zm-5 1v4h4v-4H1Zm11-9h2v2h-2V2Z"></path>
                                        <path d="M10 0v6h6V0h-6Zm5 1v4h-4V1h4ZM8 1V0h1v2H8v2H7V1h1Zm0 5V4h1v2H8ZM6 8V7h1V6h1v2h1V7h5v1h-4v1H7V8H6Zm0 0v1H2V8H1v1H0V7h3v1h3Zm10 1h-1V7h1v2Zm-1 0h-1v2h2v-1h-1V9Zm-4 0h2v1h-1v1h-1V9Zm2 3v-1h-1v1h-1v1H9v1h3v-2h1Zm0 0h3v1h-2v1h-1v-2Zm-4-1v1h1v-2H7v1h2Z"></path>
                                        <path d="M7 12h1v3h4v1H7v-4Zm9 2v2h-3v-1h2v-1h1Z"></path>
                                    </svg>
                                </a>
                                <a href="/shortenedLinks/${url.id}" class="btn btn-outline-success" style="width: 80%">Summary</a>
                            </#if>
                        </div>
                    </td>

                    <div class="modal fade" id="qrModal-${url.shortenedUrl}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h1 class="modal-title fs-5" id="exampleModalLabel">Scan it!</h1>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div id="qrcode-${url.shortenedUrl}" style="display: flex; justify-content: center; align-items: center}"></div>

                                    <script>
                                        var qrcode = new QRCode(document.getElementById("qrcode-${url.shortenedUrl}"), {
                                            text: "${host}/${url.shortenedUrl}",
                                            colorDark: "#000000",
                                            colorLight: "#ffffff",
                                            correctLevel: QRCode.CorrectLevel.H
                                        });
                                    </script>
                                </div>
                                <div class="modal-footer">
                                    <a class="btn btn-outline-dark" data-bs-dismiss="modal">Close</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </tr>
            </#list>
            </tbody>
        </table>
    <#else>
        <h3 class="text-center">Shorten your first link!</h3>
    </#if>
</div>

<script src="/bootstrap/js/preview.js"></script>
</body>