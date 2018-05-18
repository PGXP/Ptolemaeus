'use strict';

app.factory('AuthService', ['$http', 'AppService', '$rootScope', '$interval', 'AlertService',
  function ($http, AppService, $rootScope, $interval, AlertService) {

    var authService = {};

    authService.getLembrar = function () {
      return AppService.getLembrar();
    };

    authService.setLembrar = function () {
      return AppService.setLembrar();
    };

    authService.change = function (credentials) {

      return $http({
        url: 'pto/api/auth/change',
        method: "POST",
        data: credentials
      }).then(
        function (res) {
          AlertService.addWithTimeout('success', res.mensagem);
          return res;
        }
      );
    };

    authService.login = function (credentials) {
      AppService.removeToken();
      return $http({
        url: 'pto/api/auth',
        method: "POST",
        data: credentials
      }).then(
        function (res) {
          if (res.data.key) {
            AppService.setToken(res.data.key);
            $rootScope.currentUser = AppService.getUserFromToken();
            authService.subscribe($rootScope.currentUser.params['Vapid']);

            if (navigator.credentials && navigator.credentials.preventSilentAccess && $rootScope.lembrar) {
              navigator.credentials.store(credentials).then(function () {});
            }


          }
          return res;
        }
      );
    };

    authService.foto = function (email) {
      return $http({
        url: 'pto/api/auth/foto',
        method: "POST",
        data: email
      }).then(
        function (res) {
          return res.data;
        }
      );
    };

    authService.social = function (social) {
      AppService.removeToken();
      return $http({
        url: 'pto/api/auth/social',
        method: "POST",
        data: social
      }).then(
        function (res) {
          if (res.data.key) {
            AppService.setToken(res.data.key);
            $rootScope.currentUser = AppService.getUserFromToken();
            authService.subscribe($rootScope.currentUser.params['Vapid']);
          }
          return res.data;
        });
    };

    authService.amnesia = function (credentials) {

      return $http({
        url: 'pto/api/auth/amnesia',
        method: "POST",
        data: credentials
      }).then(
        function (res) {
          AlertService.addWithTimeout('warning', res.mensagem);
          return res;
        }
      );
    };

    authService.retoken = function () {
      return $http({
        url: 'pto/api/auth',
        method: "GET"
      }).then(
        function (res) {
          AppService.removeToken();
          AppService.setToken(res.data.key);
          $rootScope.currentUser = AppService.getUserFromToken();
          return res;
        }
      );
    };

    authService.getUser = function (id) {
      return $http({
        url: 'pto/api/auth/user/' + id,
        method: "GET"
      }).then(
        function (res) {
          return res;
        }
      );
    };

    authService.findUsers = function () {
      return $http({
        url: 'pto/api/auth/user',
        method: "GET"
      }).then(
        function (res) {
          return res;
        }
      );
    };

    authService.setUser = function (usuario) {
      return $http({
        url: 'pto/api/auth/user',
        method: "POST",
        data: usuario
      }).then(
        function (res) {
          return res;
        }
      );
    };

    authService.subscribe = function (vapid) {
      if ('serviceWorker' in navigator) {
        var key = urlBase64ToUint8Array(vapid);
        navigator.serviceWorker.register('/service-worker.js').then(function (registration) {
          // Registration was successful
          console.log('ServiceWorker registration successful with scope: ', registration.scope);

          registration.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: key
          }).then(
            function (pushSubscription) {
              return $http({
                url: 'pto/api/auth/fingerprint',
                method: "POST",
                data: pushSubscription
              }).then(
                function (res) {
                  return res;
                }
              );
            },
            function (error) {
              console.log('ServiceWorker subscribe failed: ', error);
            }
          );

        }).catch(function (err) {
          console.log('ServiceWorker registration failed: ', err);
        });

      }
    }

    $interval(function () {
      if ($rootScope.currentUser) {
        authService.retoken();
      }
    }, 333333);

    authService.logout = function () {
      AppService.removeToken();
    };

    authService.isAuthenticated = function () {
      if (!$rootScope.currentUser) {
        $rootScope.currentUser = AppService.getUserFromToken();
      }
      return $rootScope.currentUser ? true : false;
    };

    authService.isAuthorized = function (authorizedRoles) {

      if (authService.isAuthenticated()) {

        if (!angular.isArray(authorizedRoles)) {
          authorizedRoles = [authorizedRoles];
        }

        var hasAuthorizedRole = false;

        var perfil = $rootScope.currentUser.roles;

        if (perfil !== undefined && perfil !== null) {
          for (var i = 0; i < authorizedRoles.length; i++) {
            for (var p = 0; p < perfil.length; p++) {
              if (authorizedRoles[i] === perfil[p]) {
                hasAuthorizedRole = true;
                break;
              }
            }
          }
        }
      } else {
        return false;
      }

      return hasAuthorizedRole;
    };

    function urlBase64ToUint8Array(base64String) {
      const padding = '='.repeat((4 - base64String.length % 4) % 4);
      const base64 = (base64String + padding)
        .replace(/\-/g, '+')
        .replace(/_/g, '/');

      const rawData = window.atob(base64);
      const outputArray = new Uint8Array(rawData.length);

      for (var i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
      }
      return outputArray;
    }

    return authService;
  }
]);
