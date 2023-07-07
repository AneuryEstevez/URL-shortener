<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>Home</title>

    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <script src="/bootstrap/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
</head>
<body>
    <header class="text-bg-dark py-3">
        <div class="container">
            <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
                <ul class="nav col-12 col-lg-auto me-lg-auto justify-content-center">
                    <li><a href="/" class="nav-link px-2 text-white">Home</a></li>
                    <#if user??>
                        <li><a href="/shortenedlinks" class="nav-link px-2 text-white">Manage Shortened Links</a></li>
                        <#if user.rol == "ADMIN">
                            <li><a href="/users" class="nav-link px-2 text-white">Manage Users</a></li>
                        </#if>
                    </#if>
                </ul>
                <div class="text-end">
                    <#if user??>
                        <a href="/logout"  class="btn btn-light">Log-out</a>
                    <#else>
                        <button class="btn btn-outline-light me-2" data-bs-toggle="modal" data-bs-target="#loginModal">Login</button>
                        <button class="btn btn-light" data-bs-toggle="modal" data-bs-target="#registerModal">Sign-up</button>
                    </#if>
                </div>
            </div>
        </div>
    </header>

    <!-- Login Modal -->
    <div class="modal fade" id="loginModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">Login</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form action="/login" method="post" id="login">
                        <div class="form-floating text-dark mb-3">
                            <input type="text" class="form-control" id="floatingUsername" placeholder="Username" required name="username">
                            <label for="floatingUsername">Username</label>
                        </div>
                        <div class="form-floating text-dark mb-3">
                            <input type="password" class="form-control" id="floatingPassword" placeholder="Password" required name="password">
                            <label for="floatingPassword">Password</label>
                        </div>
                        <div class="checkbox mb-3 d-flex justify-content-center">
                            <label>
                                <input type="checkbox" value="remember-me" name="checked"> Remember me
                            </label>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline-dark" type="button" data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-dark" form="login">Login</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Register Modal -->
    <div class="modal fade" id="registerModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">Register</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form action="/register" method="post" id="register">
                        <div class="form-floating text-dark mb-3">
                            <input type="text" class="form-control" id="floatingUsername2" placeholder="Username" required name="username">
                            <label for="floatingUsername2">Username</label>
                        </div>
                        <div class="form-floating text-dark mb-3">
                            <input type="password" class="form-control" id="floatingPassword2" placeholder="Password" required name="password">
                            <label for="floatingPassword2">Password</label>
                        </div>
                        <div class="form-floating text-dark mb-3">
                            <input type="password" class="form-control" id="confirmPassword" placeholder="Confirm Password" required name="confirmPassword">
                            <label for="confirmPassword">Confirm Password</label>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline-dark" type="button" data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-dark" form="register">Register</button>
                </div>
            </div>
        </div>
    </div>
</body>
</html>