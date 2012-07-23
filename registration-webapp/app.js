var express = require('express')
    ,cloudfoundry = require("cloudfoundry")
    ,jqtpl = require("jqtpl")
    ;


var app = express.createServer();

app.configure(function() {
    app.use(app.router);
    app.use(express.bodyParser());
    app.use(express.query());
    app.use(express.static(__dirname + '/public'));

    app.set("view engine", "html");
    app.register(".html", require("jqtpl").express);

});

app.configure('development',
function() {
    app.use(express.errorHandler({
        dumpExceptions: true,
        showStack: true
    }));
});

var port = cloudfoundry.port || 3000;

app.listen(port,
	function() {
	    console.log("Express server listening on port %d in %s mode", port, app.settings.env);
	});


app.get('/register.html', function(req, res){
    res.render('register', { phoneNumber: req.query.phoneNumber });
});

app.post('/register.html', function(req, res){
    var j = {status: 'ok'};
    res.writeHead(200, {'Content-Type': 'application/json'});
    res.end(JSON.stringify(j) + '\n');
});