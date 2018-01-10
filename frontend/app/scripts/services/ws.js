'use strict';

app.factory('WS', ['$rootScope', '$websocket',
    function ($rootScope, $websocket) {
                                        var service = {};

                                        // var wsUrl;

                                        // if (window.location.protocol == 'https:') {
                                        //     wsUrl = 'wss://' + window.location.host + '/ws/echo';
                                        // } else {
                                        //     wsUrl = 'ws://' + 'ctcta.cetec.serpro' + '/ws/echo';
                                        // }

                                        // var ws = $websocket.$new({
                                        //     url: wsUrl, protocols: [], subprotocols: ['base46']
                                        // });

                                        // ws.$on('$open', function () {
                                        //     console.log("WS ON");
                                        //     if ($rootScope.currentUser) {
                                        //         ws.$emit("login", $rootScope.currentUser.cpf);
                                        //     }
                                        // });

                                        // ws.$on('$close', function () {
                                        //     console.log("WS OFF");
                                        // });

                                        // ws.$on('$error', function () {
                                        //     console.log("WS ERROR");
                                        // });

                                        // ws.$on('$message', function (emit) {
                                        //     $rootScope.$broadcast(emit.event, {emit: emit});
                                        //     console.log(emit.event + " - " + emit.data);
                                        // });

                                        // service.command = function (command, mensagem) {
                                        //     ws.$emit(command, mensagem);
                                        // };

                                        return service;
                                      }]);
