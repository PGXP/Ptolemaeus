'use strict';

app.factory('WebSocket', ['$rootScope', '$websocket', 'AUTH_EVENTS', function ($rootScope, $websocket, AUTH_EVENTS) {

  var service = {};

  if ('WebSocket' in window || 'MozWebSocket' in window) {

    var wsUrl = 'wss://app.condominiofacil.net/admin/push/cds';

    var ws = $websocket.$new({
      url: wsUrl,
      protocols: [],
      subprotocols: ['base46'],
      lazy: false,
      reconnect: true,
      reconnectInterval: 7777,
      mock: false,
      enqueue: false
    });

    ws.$on('$open', function () {
      console.log("WS OPEN");
      if ($rootScope.currentUser) {
        ws.$emit("login", $rootScope.currentUser.identity);
      }
    });

    ws.$on('$close', function () {
      console.log("WS CLOSE");
    });

    ws.$on('$error', function () {
      console.log("WS ERROR");
    });

    ws.$on('$message', function (emit) {
      $rootScope.$broadcast(emit.event, {
        emit: emit
      });
    });

    service.command = function (command, mensagem) {
      ws.$emit(command, mensagem);
    };

    $rootScope.$on(AUTH_EVENTS.logoutSuccess, function () {
      ws.$emit("logout", "");
    });

    $rootScope.$on(AUTH_EVENTS.loginSuccess, function () {
      ws.$emit("login", $rootScope.currentUser.identity);
    });

    $rootScope.$on(AUTH_EVENTS.quantidade, function (emit, args) {
      $rootScope.$apply(function () {
        $rootScope.conectados = args.emit.data;
      });
    });

    $rootScope.$on(AUTH_EVENTS.lista, function (emit, args) {
      $rootScope.$apply(function () {
        $rootScope.lista = JSON.parse(args.emit.data);
      });
    });

  } else {
    console.log("This browser doesn't support WebSocket");
  }

  return service;
}]);
