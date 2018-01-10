'use strict';

app.factory('AlertService', ['$rootScope', '$timeout', '$notification',
    function ($rootScope, $timeout, $notification) {
        var alertService = {};

        // create an array of alerts available globally
        $rootScope.alerts = [];

        alertService.addWithTimeout = function (type, msg, timeout) {
            var alert = alertService.add(type, msg);
            $timeout(function () {
                alertService.closeAlert(alert);
            }, timeout ? timeout : 4000);
        };

        alertService.add = function (type, msg, timeout) {
            $rootScope.alerts.push({
                'type': type,
                'msg': msg
            });
        };


        alertService.closeAlert = function (alert) {
            return this.closeAlertIdx($rootScope.alerts.indexOf(alert));
        };

        alertService.closeAlertIdx = function (index) {
            return $rootScope.alerts.splice(index, 1);
        };

        alertService.notification = function (titulo, mensagem) {
            $notification(titulo, {
                body: mensagem,
                dir: 'auto',
                delay: 10000,
                focusWindowOnClick: true
            });
        };

        return alertService;
    }]);