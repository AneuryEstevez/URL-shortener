<#include "layout.ftl">
<body>
<#--    <header class="text-bg-dark py-3">-->
<#--        <div class="container">-->
<#--            <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">-->
<#--                <ul class="nav col-12 col-lg-auto me-lg-auto justify-content-center">-->
<#--                    <li><a href="/home" class="nav-link px-2 text-white">Home</a></li>-->
<#--                    <li><a href="/shortenedlinks" class="nav-link px-2 text-secondary">Manage Shortened Links</a></li>-->
<#--                    <li><a href="/users" class="nav-link px-2 text-white">Manage Users</a></li>-->
<#--                </ul>-->
<#--                <div class="text-end">-->
<#--                    <a href="#"  class="btn btn-light">Log-out</a>-->
<#--                </div>-->
<#--            </div>-->
<#--        </div>-->
<#--    </header>-->
    <div class="card card-body m-3">
        <table class="table">
            <thead>
            <tr>
                <th scope="col" style="width: 30%">Shortened Link</th>
                <th scope="col" style="width: 40%">Original Link</th>
                <th scope="col" style="width: 10%">Visits</th>
                <th scope="col" style="width: 20%"></th>
            </tr>
            </thead>
            <tbody>
            <#if urls??>
                <#list urls as url>
                    <tr>
                        <td><a href="/${url.shortenedUrl}" class="text-black link-offset-2-hover link-underline-opacity-0 link-underline-opacity-75-hover link-underline-dark">${host}/${url.shortenedUrl}</a></td>
                        <td><a href="${url.url}" class="text-black link-underline-opacity-0 link-underline-dark">${url.url}</a></td>
                        <td>${url.visits}</td>
                        <td>
                            <div class="input-group">
                                <a href="/shortenedLinks/${url.id}" class="btn btn-outline-success" style="width: 50%">Summary</a>
                                <a href="/shortenedlinks/delete/${url.id}" class="btn btn-outline-danger" style="width: 50%">Delete</a>
                            </div>
                        </td>
                    </tr>
                </#list>
            </#if>
        </table>
    </div>
</body>