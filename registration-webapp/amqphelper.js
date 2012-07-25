var amqp = require('amqp')
, cloudfoundry = require('cloudfoundry')
;
           
exports.initializeAmqp = function () {
  var connectionParams = { host: 'localhost' }
  if (cloudfoundry.port) {
      var rabbitmqService = cloudfoundry.rabbitmq['votemeeteat-rabbitmq']
      console.log(rabbitmqService)
      connectionParams = {url: rabbitmqService.credentials.url}
  }
  var con = amqp.createConnection(connectionParams, {}, function () { console.log("Ready callback invoked");});
  con.on('error', function() { console.log("AMQP connection error")});
  return con;
}
