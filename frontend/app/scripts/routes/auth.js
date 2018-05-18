'use strict';

app.config(['$routeProvider', 'USER_ROLES',
  function ($routeProvider, USER_ROLES) {

    $routeProvider

      .when('/login', {
        templateUrl: 'views/login.html',
        controller: 'AuthController',
        data: {
          authorizedRoles: [USER_ROLES.NOT_LOGGED]
        }
      })

      .when('/usuario/senha/:id', {
        templateUrl: 'views/usuario/senha.html',
        controller: 'AuthController',
        data: {
          authorizedRoles: [USER_ROLES.NOT_LOGGED]
        }
      })

      .when('/amnesia/', {
        templateUrl: 'views/amnesia.html',
        controller: 'AuthController',
        data: {
          authorizedRoles: [USER_ROLES.NOT_LOGGED]
        }
      });


  }
]);
