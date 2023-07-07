<#include "layout.ftl">
<body>
<#--    <header class="text-bg-dark py-3">-->
<#--        <div class="container">-->
<#--            <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">-->
<#--                <ul class="nav col-12 col-lg-auto me-lg-auto justify-content-center">-->
<#--                    <li><a href="/home" class="nav-link px-2 text-white">Home</a></li>-->
<#--                    <li><a href="/shortenedlinks" class="nav-link px-2 text-white">Manage Shortened Links</a></li>-->
<#--                    <li><a href="/users" class="nav-link px-2 text-secondary">Manage Users</a></li>-->
<#--                </ul>-->
<#--                <div class="text-end">-->
<#--                    <a href="/home"  class="btn btn-light">Log-out</a>-->
<#--                </div>-->
<#--            </div>-->
<#--        </div>-->
<#--    </header>-->
    <div class="card card-body m-3">
        <table class="table">
            <thead>
            <tr>
                <th scope="col" style="width: 70%">Username</th>
                <th scope="col" style="width: 15%">Role</th>
                <th scope="col" style="width: 15%"></th>
            </tr>
            </thead>
            <tbody>
            <#list users as user>
            <tr>
                <td>${user.name}</td>
                <td>${user.rol}</td>
                <td>
                    <div class="input-group">
                    <#if isAdmin == true && user.rol == "USER">
                        <!-- Button trigger modal -->
                        <button type="button" class="btn btn-outline-success" data-bs-toggle="modal" data-bs-target="#editModal-${user.id}" style="width: 50%">Edit</button>
                        <a href="/users/delete/${user.id}" class="btn btn-outline-danger" style="width: 50%">Delete</a>
                    <#else>
                        <button type="button" class="btn btn-outline-success" data-bs-toggle="modal" data-bs-target="#editModal-${user.id}" style="width: 100%">Edit</button>
                    </#if>
                    </div>
                    <!-- Modal -->
                    <form action="/users/${user.id}" method="post">
                        <div class="modal fade" id="editModal-${user.id}" tabindex="-1" aria-labelledby="editModal" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content px-3">
                                    <div class="modal-header mb-3">
                                        <h3 class="modal-title fs-5" id="exampleModalLabel">Edit User</h3>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="form-floating mb-3">
                                        <input disabled type="text" class="form-control" id="floatingUsername" placeholder="${user.name}" value="${user.name}">
                                        <label for="floatingUsername">Username</label>
                                    </div>
                                    <div class="form-floating mb-3">
                                        <select class="form-select" id="floatingSelect" aria-label="Floating label select example" name="rol">
                                            <#if user.rol == "USER">
                                                <option selected value="User">User</option>
                                                <option value="Admin">Admin</option>
                                            <#else>
                                                <option value="User">User</option>
                                                <option selected value="Admin">Admin</option>
                                            </#if>
                                        </select>
                                        <label for="floatingSelect">Role</label>
                                    </div>
                                    <div class="modal-footer">
                                        <div class="input-group" style="width: 100%;">
                                            <button class="btn btn-outline-dark" type="submit" style="width: 50%;">Edit</button>
                                            <button class="btn btn-dark" type="button" data-bs-dismiss="modal" style="width: 50%;">Cancel</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </td>
            </tr>
            </#list>
            </tbody>
        </table>
    </div>
</body>