'use strict';

var app = angular
  .module('app', [
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ngToast',
    'ngBootbox',
    'ng-currency',
    'ui.bootstrap',
    'ui.bootstrap.pagination',
    'ui.calendar',
    'ui.grid',
    'ui.grid.pagination',
    'ui.grid.exporter',
    'ui.mask',
    'nvd3',
    'ngWebsocket',
    'angular.filter',
    'socialLogin',
    'ngFileUpload',
    'angular-loading-bar',
    'Config'
  ]).config(['$routeProvider', 'USER_ROLES', 'socialProvider',
    function ($routeProvider, USER_ROLES, socialProvider) {

      socialProvider.setGoogleKey("547939596694-2r4hd52mojck61ji1r43qhcmh220tpmj.apps.googleusercontent.com");
      socialProvider.setFbKey({
        appId: "",
        apiVersion: "v2.12"
      });

      $routeProvider.otherwise({
        redirectTo: '/',
        data: {
          authorizedRoles: [USER_ROLES.ADMINISTRADOR, USER_ROLES.SINDICO, USER_ROLES.CONSELEHIRO, USER_ROLES.MORADOR, USER_ROLES.VISITANTE]
        }
      });

      $routeProvider.when('/privacidade', {
        templateUrl: 'views/privacidade.html',
        controller: 'AuthController',
        data: {
          authorizedRoles: [USER_ROLES.NOT_LOGGED]
        }
      });

    }
  ]);

app.config(['$httpProvider', function ($httpProvider) {
  $httpProvider.useApplyAsync(true);
  $httpProvider.interceptors.push(['$q', '$rootScope', 'AppService', 'ENV', function ($q, $rootScope, AppService, ENV) {
    return {
      'request': function (config) {
        $rootScope.$broadcast('loading-started');
        var token = AppService.getToken();
        if (config.url.indexOf("http") === -1) {
          if (config.url.indexOf("api") !== -1) {
            config.url = ENV.apiEndpoint + config.url;
          }
        }

        if (token) {
          config.headers['Authorization'] = 'JWT ' + token;
        }

        return config || $q.when(config);
      },
      'response': function (response) {
        $rootScope.$broadcast('loading-complete');
        return response || $q.when(response);
      },
      'responseError': function (rejection) {
        $rootScope.$broadcast('loading-complete');
        return $q.reject(rejection);
      },
      'requestError': function (rejection) {
        $rootScope.$broadcast('loading-complete');
        return $q.reject(rejection);
      }
    };
  }]);
  $httpProvider.interceptors.push(['$injector', function ($injector) {
    return $injector.get('AuthInterceptor');
  }]);
}]);
app.run(['$rootScope', '$location', '$window', 'AUTH_EVENTS', 'APP_EVENTS', 'USER_ROLES', 'AuthService', 'AppService', 'AlertService',
  function ($rootScope, $location, $window, AUTH_EVENTS, APP_EVENTS, USER_ROLES, AuthService, AppService, AlertService) {

    $rootScope.$on('$routeChangeStart', function (event, next) {

      if (next.redirectTo !== '/') {
        var authorizedRoles = next.data.authorizedRoles;
        if (authorizedRoles.indexOf(USER_ROLES.NOT_LOGGED) === -1) {

          if (!AuthService.isAuthorized(authorizedRoles)) {
            event.preventDefault();
            if (AuthService.isAuthenticated()) {
              // user is not allowed
              $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
            } else {
              // user is not logged in
              $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
            }
          }
        }
      }
    });

    $rootScope.$on('event:social-sign-in-success', function (event, userDetails) {
      AuthService.social(userDetails).then(
        function (res) {
          $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
        },
        function (res) {
          AlertService.addWithTimeout('warning', "Os servidores do google/facebook não identificaram o seu email que está cadastrado no sistema");
        });
    });

    $rootScope.$on('event:social-sign-out-success', function (event, logoutStatus) {

    });

    $rootScope.$on(AUTH_EVENTS.notAuthorized, function () {
      $location.path("/login");
    });
    $rootScope.$on(AUTH_EVENTS.notAuthenticated, function () {
      $rootScope.currentUser = null;
      AppService.removeToken();
      $location.path("/login");
    });
    $rootScope.$on(AUTH_EVENTS.loginFailed, function () {
      AppService.removeToken();
      $location.path("/login");
    });
    $rootScope.$on(AUTH_EVENTS.logoutSuccess, function () {
      $rootScope.currentUser = null;
      AppService.removeToken();
      //$window.location.reload();
      $location.path('/login');
    });
    $rootScope.$on(AUTH_EVENTS.loginSuccess, function () {
      // $window.location.reload();
      $location.path('/dashboard');
    });
    $rootScope.$on(APP_EVENTS.offline, function () {
      AlertService.clear();
      AlertService.addWithTimeout('danger', 'Servidor está temporariamente indisponível, tente mais tarde');
    });

    $window.addEventListener('load', function (e) {
      $window.applicationCache.addEventListener('updateready', function (e) {
        if ($window.applicationCache.status === $window.applicationCache.UPDATEREADY) {
          $window.location.reload();
        }
      }, false);
    }, false);

    $window.addEventListener('beforeinstallprompt', function (e) {
      e.userChoice.then(function (choiceResult) {
        console.log(choiceResult.outcome);
        if (choiceResult.outcome == 'dismissed') {
          console.log('User cancelled home screen install');
        } else {
          console.log('User added to home screen');
        }
      });
    });
  }
]);

app.constant('APP_EVENTS', {
  offline: 'app-events-offline'
});

app.constant('AUTH_EVENTS', {
  loginSuccess: 'auth-login-success',
  loginFailed: 'auth-login-failed',
  logoutSuccess: 'auth-logout-success',
  sessionTimeout: 'auth-session-timeout',
  notAuthenticated: 'auth-not-authenticated',
  notAuthorized: 'auth-not-authorized',
  exit: 'exit',
  push: 'push',
  sistema: 'sistema',
  agenda: 'agenda',
  financeiro: 'financeiro',
  quantidade: 'count',
  lista: 'list'
});

app.constant('USER_ROLES', {
  ADMINISTRADOR: 'Administrador',
  VISITANTE: 'Visitante',
  NOT_LOGGED: 'NOT_LOGGED'
});

app.factory('AuthInterceptor', ['$rootScope', '$q', 'AUTH_EVENTS', 'APP_EVENTS',
  function ($rootScope, $q, AUTH_EVENTS, APP_EVENTS) {

    return {
      responseError: function (response) {
        $rootScope.$broadcast({
          // '-1': APP_EVENTS.offline,
          //  0: APP_EVENTS.offline,
          404: APP_EVENTS.offline,
          503: APP_EVENTS.offline,
          412: APP_EVENTS.validate,
          401: AUTH_EVENTS.notAuthenticated,
          419: AUTH_EVENTS.sessionTimeout,
          440: AUTH_EVENTS.sessionTimeout
        }[response.status], response);
        return $q.reject(response);
      }
    };
  }
]);
