'use strict';

app.factory('AuthService', ['$http', 'AppService', '$rootScope',
    function ($http, AppService, $rootScope) {

        var authService = {};

        authService.login = function (credentials) {

            return $http
                .post('api/auth', credentials)
                .success(function (res, status, headers) {

                    AppService.removeToken();

                    AppService.setToken(headers('Set-Token'));

                    $rootScope.currentUser = AppService.getUserFromToken();

                    return res;
                }
                );

        };

        function getRoles(grupos) {
            var roles = [];

            if (grupos) {
                for (var i = 0; i < grupos.length; i++) {
                    for (var j = 0; j < grupos[i].perfis.length; j++) {
                        if (roles.indexOf(grupos[i].perfis[j]) == -1) {
                            roles.push(grupos[i].perfis[j]);
                        }
                    }
                }
            }

            return roles;
        }

        authService.isAuthenticated = function () {
            return $rootScope.currentUser ? true : false;
        };

        authService.isAuthorized = function (authorizedRoles) {

            if (authService.isAuthenticated()) {

                if (!angular.isArray(authorizedRoles)) {
                    authorizedRoles = [authorizedRoles];
                }

                var hasAuthorizedRole = false;

                var grupos = $rootScope.currentUser.grupos;

                if (grupos !== undefined && grupos !== null) {
                    for (var i = 0; i < authorizedRoles.length; i++) {
                        for (var j = 0; j < grupos.length; j++) {
                            for (var k = 0; k < grupos[j].perfis.length; k++) {
                                if (authorizedRoles[i].indexOf(grupos[j].perfis[k]) !== -1) {
                                    hasAuthorizedRole = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                return false;
            }

            return hasAuthorizedRole;
        };

        return authService;
    }]);

