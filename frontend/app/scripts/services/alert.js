'use strict';

app.factory('AlertService', ['$rootScope', '$timeout', 'AUTH_EVENTS', 'Win', 'ngToast',
  function ($rootScope, $timeout, AUTH_EVENTS, Win, ngToast) {
    var alertService = {};

    if (window.Notification) {
      if (Notification.permission === 'denied') {
        console.log('The user has blocked notifications.');
        //return;
      } else {
        Notification.requestPermission();
      }
    }

    alertService.addWithTimeout = function (type, msg, timeout) {
      var alert = alertService.add(type, msg);
    };

    alertService.add = function (type, msg, timeout) {
      if (type && msg) {
        ngToast.create({
          className: type,
          content: msg
        });
      }
    };

    alertService.showMessageForbiden = function () {
      this.addWithTimeout('danger', 'Você não tem permissão para executar essa operação');
    };

    alertService.mobile = function (message) {
      if (!(document.documentMode || /Edge/.test(navigator.userAgent))) {
        return new Promise(function (resolve, reject) {
          var messageChannel = new MessageChannel();
          messageChannel.port1.onmessage = function (event) {
            if (event.data.error) {
              reject(event.data.error);
            } else {
              resolve(event.data);
            }
          };
          navigator.serviceWorker.controller.postMessage(message, [messageChannel.port2]);
        });
      } else {
        Win.notification(message);
      }
    };

    $rootScope.$on(AUTH_EVENTS.push, function (emit, args) {
      alertService.mobile(args.emit.data);
    });

    return alertService;
  }
]);
