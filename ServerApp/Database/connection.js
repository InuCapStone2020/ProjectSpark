var mysql = require('mysql');
//interlock to mysql
var connection = mysql.createConnection({
    host: "[endpoint address]",
    user: "[masteruser id]",
    database: "[databse to access]",
    password: "[pw]",
    port: 3306 //mysql port
});
