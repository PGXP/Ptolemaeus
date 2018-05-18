'use strict';

app.config(['$routeProvider', 'USER_ROLES',
  function ($routeProvider, USER_ROLES) {

    $routeProvider

      .when('/usuario', {
        templateUrl: 'views/usuario/list.html',
        controller: 'UsuarioController',
        data: {
          authorizedRoles: [USER_ROLES.ADMINISTRADOR, USER_ROLES.SINDICO]
        }
      })

      .when('/usuario/edit', {
        templateUrl: 'views/usuario/edit.html',
        controller: 'UsuarioController',
        data: {
          authorizedRoles: [USER_ROLES.ADMINISTRADOR]
        }
      })

      .when('/usuario/edit/:id', {
        templateUrl: 'views/usuario/edit.html',
        controller: 'UsuarioController',
        data: {
          authorizedRoles: [USER_ROLES.ADMINISTRADOR, USER_ROLES.VISITANTE]
        }
      });

  }
]);
