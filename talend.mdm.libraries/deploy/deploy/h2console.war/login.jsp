<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <head>
    <body bgcolor="#ffffff">
        <form method="POST" action='<%= response.encodeURL("j_security_check") %>'>
            <h1>H2 Console</h1>
            <table border="0">
                <tr>
                    <td>Username:</td>
                    <td><input type="text" name="j_username"></td>
                </tr>
                <tr>
                    <td>Password:</td>
                    <td><input type="password" name="j_password"></td>
                </tr>
           </table>
           <input type="submit" value="Login">
        </form>
    </body>
</html>