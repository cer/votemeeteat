var express = require('express')
    ,cloudfoundry = require("cloudfoundry")
    ,jqtpl = require("jqtpl")
    ,amqpHelper = require("./amqphelper")

    ;


var app = express.createServer();

app.configure(function() {
    app.use(express.bodyParser());
    app.use(express.query());
    app.use(app.router);
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


app.get('/schedule.html', function(req, res){
    res.render('schedule', { phoneNumber: req.query.phoneNumber });
});

var amqpConnection = amqpHelper.initializeAmqp();

var crudUsersExchange = undefined;

app.post('/schedule.html', function(req, res){
    var j = {status: 'ok'};

    var b = req.body
    var message = { name: b.name, phoneNumber: b.phoneNumber, longitude: b.longitude, latitude: b.latitude };
    
    crudUsersExchange.publish("scheduleVme", message, {
        mandatory: true,
        contentType: "text/plain"  // application/json ends up as binary in SI     
    });

    res.writeHead(200, {'Content-Type': 'application/json'});
    res.end(JSON.stringify(j) + '\n');
});


console.log("Registering amqp onReady");

amqpConnection.on('ready',
    function() {      
        amqpConnection.exchange("scheduleVme", options = {passive: true},
            function(exchange) {
                crudUsersExchange = exchange;
        }); 
    });
