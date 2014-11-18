var appPort, documentViewPort;

function forwardMessage(message, port) {
  if (typeof port !== 'undefined' && message.type !== 'REGISTER') {
    port.postMessage(message);
  }
}

var ports = [];

onconnect = function(event) {
  var port = event.ports[0];
  ports.push(port);
  port.start();

  port.addEventListener('message', function(event) {
    var message = event.data;

    if (message.sender === 'APP') {
      appPort = port;
      forwardMessage(message, documentViewPort);
    } else if (message.sender === 'DOCUMENT_VIEW') {
      documentViewPort = port;
      forwardMessage(message, appPort);
    }
  });
};
