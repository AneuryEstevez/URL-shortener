<#include "layout.ftl">
<body>
    <div class="m-2">
        <h2><a href="/${shortLink.shortenedUrl}" class="text-black" style="text-decoration: none">${host}/${shortLink.shortenedUrl}</a></h2>
        <h3><a href="${shortLink.url}" class="text-black" style="text-decoration: none">${shortLink.url}</a></h3>
        <p>${shortLink.date} by
            <#if shortLink.user?has_content>
                ${shortLink.user.name}
            <#else>
                No user
            </#if>
        </p>
    </div>

    <input type="hidden" id="urlId" value="${shortLink.id}">

<#--    <div class="container shadow-sm rounded my-3" style="max-height: 350px; max-width: 750px">-->
<#--        <div class="row">-->
<#--            <h3>Dates</h3>-->
<#--        </div>-->
<#--        <div class="row align-content-center text-center">-->
<#--            <canvas id="datesChart" class="m-auto p-1" style="max-height: 300px; max-width: 750px"></canvas>-->
<#--        </div>-->
<#--    </div>-->

<#--    <div class="container shadow-sm rounded my-3" style="max-height: 350px; max-width: 750px">-->
<#--        <div class="row">-->
<#--            <h3>Browsers</h3>-->
<#--        </div>-->
<#--        <div class="row align-content-center text-center">-->
<#--            <canvas id="browsersChart" class="m-auto p-1" style="max-height: 300px; max-width: 750px"></canvas>-->
<#--        </div>-->
<#--    </div>-->

<#--    <div class="container shadow-sm rounded my-3" style="max-height: 350px; max-width: 750px">-->
<#--        <div class="row">-->
<#--            <h3>Operating Systems</h3>-->
<#--        </div>-->
<#--        <div class="row align-content-center text-center">-->
<#--            <canvas id="osChart" class="m-auto p-1" style="max-height: 300px; max-width: 750px"></canvas>-->
<#--        </div>-->
<#--    </div>-->

    <div class="row mx-3">

    <div class="shadow rounded m-3" style="max-height: 350px; max-width: 750px">
        <h3>Dates</h3>
        <div class="align-content-center text-center">
            <canvas id="datesChart" class="p-1 mb-1" style="max-height: 300px; max-width: 750px"></canvas>
        </div>
    </div>


    <div class="shadow rounded m-3" style="max-height: 350px; max-width: 750px">
        <h3>Browsers</h3>
        <div class="align-content-center text-center">
            <canvas id="browsersChart" class="p-1" style="max-height: 300px; max-width: 750px"></canvas>
        </div>
    </div>

    <div class="shadow rounded m-3" style="max-height: 350px; max-width: 750px">
        <h3>Operating Systems</h3>
        <div class="align-content-center text-center">
            <canvas id="osChart" class="p-1" style="max-height: 300px; max-width: 750px"></canvas>
        </div>
    </div>

    <div class="shadow rounded m-3" style="max-height: 350px; max-width: 750px;">
        <h3>IPs</h3>
        <div class="align-content-center text-center scrollable-container" style="max-height: 300px; max-width: 750px; overflow-y: auto;">
            <table class="table" id="ipsTable">
                <thead>
                    <tr>
                        <th scope="col">IP</th>
                        <th scope="col">Visits</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>

    </div>

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.29.1/moment.min.js"></script>
    <script src="/bootstrap/js/statistics.js"></script>

</body>