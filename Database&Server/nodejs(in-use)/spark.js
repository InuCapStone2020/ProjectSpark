var mysql = require("mysql");
var express = require("express");
var bodyParser = require("body-parser"); //using body-parser for body parsing
var app = express();
var url = require("url");

app.use(bodyParser.json()); //format is json
app.use(bodyParser.urlencoded({ extended: true })); //querystring module

//using Port 3000 for listening
app.listen(3000, () => {
    console.log("Server is acting...");
});

//interlock to mysql
var connection = mysql.createConnection({
    host: "", //end point address
    user: "", //masteruser id
    database: "", //database to access
    password: "", //database pw
    timezone: "KST", //timezone
    port: 3306
});

app.post("/addition", (req, res) => {
    var number = req.body.data.length;

    var num = [];
    var subNum = [];
    var date = [];
    var time = [];
    var region = [];
    var content = [];
    var event = [];

    var qs = [];

    var i = 0;
    while (i < number) {
        num.push(req.body.data[i].NUM);
        subNum.push(req.body.data[i].SUBNUM);
        date.push(req.body.data[i].M_DATE);
        time.push(req.body.data[i].M_TIME);
        region.push(req.body.data[i].REGION);
        content.push(req.body.data[i].CONTENT);
        event.push(req.body.data[i].EVENT);
        var temp = [num[i], subNum[i], date[i], time[i], region[i], content[i], event[i]];
        qs.push(temp);
        i++;
    }

    //sql for insert to db
    var insertSQL = "INSERT INTO Message_List (NUM, SUBNUM, M_DATE, M_TIME, REGION, CONTENT, EVENT) VALUES (?, ?, ?, ?, ?, ?, ?)";

    var j = 0;
    var ret = "Error";
    while (j < number) {
        connection.query(insertSQL, qs[j], (err, result) => {
            if (err) {
                ret = "Error";
                console.log(err);
            } else {
                ret = "Insert Successful";
            }
            res.json({
                message: ret
            });
        });
        j++;
    }
});

app.get("/search", (req, res) => {
    var _url = req.url;
    var querydata = url.parse(_url, true).query;

    if (querydata.region) {
        var region = querydata.region;
    } else {
        var region = req.body.region;
    }
    if (region == "") {
        region = "전체 전체";
    }

    if (querydata.sdate) {
        var s_date = querydata.sdate;
    } else {
        var s_date = req.body.sdate;
    }
    if (s_date == "") {
        s_date = "2000-01-01";
    }

    if (querydata.edate) {
        var e_date = querydata.edate;
    } else {
        var e_date = req.body.edate;
    }
    if (e_date == "") {
        e_date = "9999-12-31";
    }

    if (querydata.event) {
        var event = querydata.event;
    } else {
        var event = req.body.event;
    }
    if (event != "") {
        var splitEvent = event.split("','");
        event = splitEvent;
    } else {
        event = ["전염병", "자연 재해", "기타"];
    }

    if (querydata.page) {
        var page = querydata.page;
    } else {
        var page = req.body.page;
    }
    if (page == "") {
        page = 1;
    }
    var offset = (page - 1) * 10;

    var cnt = [];

    if (region != "전체 전체") {
        var splitRegion = region.split("','");
        var cnt = splitRegion.length;
        var i = 0;
        while (i < cnt) {
            var spt = splitRegion[i].split(" ");
            if (spt[1] != "전체") {
                splitRegion.push(spt[0] + " 전체");
                i = i + 1;
            } else {
                splitRegion.splice(i, 1, "^" + spt[0]);
            }
        }
        var mergeRegion = splitRegion.join("|");
        region = mergeRegion;

        var countSQL = "select count(*) as count from Message_List where (region REGEXP ?) and (m_date between ? and ?) and (event in(?))";
        var qs = [region, s_date, e_date, event];

        connection.query(countSQL, qs, (err, result) => {
            if (err) {
                console.log(err);
            } else {
                cnt = result;
            }
        });

        var searchSQL = "select * from Message_List where (region REGEXP ?) and (m_date between ? and ?) and (event in(?)) order by num desc, subnum asc limit ?, 10";
        qs = [region, s_date, e_date, event, offset];

        connection.query(searchSQL, qs, (err, result) => {
            if (err) {
                console.log(err);
            } else {
                res.json({
                    cnt,
                    result
                });
            }
        });
    } else {
        var countSQL = "select count(*) as count from Message_List where (m_date between ? and ?) and (event in(?))";
        var qs = [s_date, e_date, event];

        connection.query(countSQL, qs, (err, result) => {
            if (err) {
                console.log(err);
            } else {
                cnt = result;
            }
        });

        var searchSQL = "select * from Message_List where (m_date between ? and ?) and (event in(?)) order by num desc, subnum asc limit ?, 10";
        qs = [s_date, e_date, event, offset];

        connection.query(searchSQL, qs, (err, result) => {
            if (err) {
                console.log(err);
            } else {
                res.json({
                    cnt,
                    result
                });
            }
        });
    }
});

app.get("/mindate", (req, res) => {
    var mindateSQL = "SELECT MIN(M_DATE) as mindate from Message_List";

    connection.query(mindateSQL, (err, result) => {
        if (err) {
            console.log(err);
        } else {
            res.json(result);
        }
    });
});

app.get("/weekcount", (req, res) => {
    var weekcountSQL =
        "select m1.region as region, count(*) as count from (select region from Message_List where m_date between subdate(curdate(),6) and curdate() group by region) as m1, (select region from Message_List where m_date between subdate(curdate(),6) and curdate()) as m2 where case when substring_index(m1.region, ' ', -1) != '전체' then m2.region in (concat(substring_index(m1.region, ' ', 1), ' 전체'), m1.region) when substring_index(m1.region, ' ', -1)='전체' then m2.region like concat(substring_index(m1.region, ' ', 1),'%') END group by m1.region order by m1.region";

    connection.query(weekcountSQL, (err, result) => {
        if (err) {
            console.log(err);
        } else {
            res.json(result);
        }
    });
});

/*
app.get("/weekdetail", (req, res) => {
    var _url = req.url;
    var querydata = url.parse(_url, true).query;
    if (querydata.region) {
        var region = querydata.region;
    } else {
        var region = req.body.region;
    }

    var splitRegion = region.split("','");
    var cnt = splitRegion.length;
    var i = 0;
    while (i < cnt) {
        var spt = splitRegion[i].split(" ");
        if (spt[1] != "전체") {
            splitRegion.push(spt[0] + " 전체");
            i = i + 1;
        } else {
            splitRegion.splice(i, 1, "^" + spt[0]);
        }
    }
    var mergeRegion = splitRegion.join("|");
    region = mergeRegion;

    var weekdetailSQL = "select * from Message_List where (m_date between subdate(curdate(),6) and curdate()) and (region REGEXP ?) order by num desc, subnum asc";
    var qs = [region];

    connection.query(weekdetailSQL, qs, (err, result) => {
        if (err) {
            console.log(err);
        } else {
            res.json(result);
        }
    });
});
*/

app.get("/notice", (req, res) => {
    var noticeSQL = "";

    var _url = req.url;
    var querydata = url.parse(_url, true).query;
    if (querydata.region) {
        var region = querydata.region;
    } else {
        var region = req.body.region;
    }

    if (querydata.interval) {
        var interval = querydata.interval;
    } else {
        var interval = req.body.interval;
    }

    if (region == "") {
        region = "전체 전체";
    }

    if (interval == "") {
        interval = 60;
    }

    if (region != "전체 전체") {
        var splitRegion = region.split("','");
        var cnt = splitRegion.length;
        var i = 0;
        while (i < cnt) {
            var spt = splitRegion[i].split(" ");
            if (spt[1] != "전체") {
                splitRegion.push(spt[0] + " 전체");
                i = i + 1;
            } else {
                splitRegion.splice(i, 1, "^" + spt[0]);
            }
        }
        var mergeRegion = splitRegion.join("|");
        region = mergeRegion;

        noticeSQL =
            "select num, subnum, m_date, m_time, region, content, event from (select *, concat(m_date,' ', m_time) as m_dt from Message_List) as ml_dt where (ml_dt.m_dt > DATE_SUB(now(),INTERVAL ? minute)) and (region REGEXP ?)";
    } else {
        noticeSQL =
            "select num, subnum, m_date, m_time, region, content, event from (select *, concat(m_date,' ', m_time) as m_dt from Message_List) as ml_dt where ml_dt.m_dt > DATE_SUB(now(),INTERVAL ? minute)";
    }

    var qs = [interval, region];

    connection.query(noticeSQL, qs, (err, result) => {
        if (err) {
            console.log(err);
        } else {
            res.json(result);
        }
    });
});

app.patch("/renewal", (req, res) => {
    var number = req.body.data.length;

    var event = [];
    var num = [];
    var subNum = [];

    var qs = [];

    var i = 0;
    while (i < number) {
        num.push(req.body.data[i].NUM);
        subNum.push(req.body.data[i].SUBNUM);
        event.push(req.body.data[i].EVENT);
        var temp = [event[i], num[i], subNum[i]];
        qs.push(temp);
        i++;
    }

    var updateSQL = "UPDATE Message_List SET EVENT=? WHERE NUM=? AND SUBNUM=?";

    var j = 0;
    var ret = "Error";
    while (j < number) {
        connection.query(updateSQL, qs[j], (err, result) => {
            if (err) {
                ret = "Error";
                console.log(err);
            } else {
                ret = "Update Successful";
            }
            res.json({
                message: ret
            });
        });
        j++;
    }
});

app.delete("/elimination", (req, res) => {
    var number = req.body.data.length;

    var num = [];
    var subNum = [];

    var qs = [];

    var i = 0;
    while (i < number) {
        num.push(req.body.data[i].NUM);
        subNum.push(req.body.data[i].SUBNUM);
        var temp = [num[i], subNum[i]];
        qs.push(temp);
        i++;
    }

    var delSQL = "DELETE FROM Message_List WHERE NUM=? AND SUBNUM=?";

    var j = 0;
    var ret = "Error";
    while (j < number) {
        connection.query(delSQL, qs[j], (err, result) => {
            if (err) {
                ret = "Error";
                console.log(err);
            } else {
                ret = "Eliminate Successful";
            }
            res.json({
                message: ret
            });
        });
        j++;
    }
});
