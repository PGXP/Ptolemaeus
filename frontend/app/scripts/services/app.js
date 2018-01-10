'use strict';

app.factory('AppService', ['$window', '$rootScope', function ($window, $rootScope) {

        var tokenKey = "CitrinoToken";

        var service = {};

        service.getToken = function () {

            var token = $window.localStorage.getItem(tokenKey);
            if (token && token !== undefined && token !== null && token !== "null") {
                if (!$rootScope.currentUser) {
                    $rootScope.currentUser = service.getUserFromToken();
                }
                return token;
            }
            return null;
        };

        service.setToken = function (token) {
            $window.localStorage.setItem(tokenKey, token);
        };

        service.removeToken = function () {
            $window.localStorage.removeItem(tokenKey);
        };

        service.getUserFromToken = function () {
            var token = $window.localStorage.getItem(tokenKey);

            var user = null;

            if (token !== null && typeof token !== undefined) {
                var encoded = token.split('.')[1];
                var dados = JSON.parse(urlBase64Decode(encoded));
                user = JSON.parse(dados.user);
            }

            return user;
        }

        function urlBase64Decode(str) {
            var output = str.replace('-', '+').replace('_', '/');
            switch (output.length % 4) {
                case 0:
                    break;
                case 2:
                    output += '==';
                    break;
                case 3:
                    output += '=';
                    break;
                default:
                    throw 'Illegal base64url string!';
            }
            return window.atob(output);
        }

        return service;
    }]);

app.service('Session', function () {

    this.create = function (userId, userRole) {
        this.userId = userId;
        this.userRole = userRole;
    };

    this.destroy = function () {
        this.userId = null;
        this.userRole = null;
    };

    return this;
});