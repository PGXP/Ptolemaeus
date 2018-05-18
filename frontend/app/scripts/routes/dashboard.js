'use strict';

app.config(['$routeProvider', 'USER_ROLES',
    function ($routeProvider, USER_ROLES) {

        $routeProvider

            .when('/', {
                templateUrl: 'views/dashboard/dashboard.html',
                controller: 'DashboardController',
                data: {
                    authorizedRoles: [USER_ROLES.ADMINISTRADOR, USER_ROLES.SINDICO, USER_ROLES.CONSELHEIRO, USER_ROLES.MORADOR, USER_ROLES.FUNCIONARIO, USER_ROLES.VISITANTE]
                }
            })

            .when('/agenda', {
                templateUrl: 'views/dashboard/agenda.html',
                controller: 'AgendaController',
                data: {
                    authorizedRoles: [USER_ROLES.ADMINISTRADOR, USER_ROLES.SINDICO, USER_ROLES.CONSELHEIRO, USER_ROLES.MORADOR, USER_ROLES.FUNCIONARIO, USER_ROLES.VISITANTE]
                }
            })

            .when('/dashboard', {
                templateUrl: 'views/dashboard/dashboard.html',
                controller: 'DashboardController',
                data: {
                    authorizedRoles: [USER_ROLES.ADMINISTRADOR, USER_ROLES.SINDICO, USER_ROLES.CONSELEHIRO, USER_ROLES.MORADOR, USER_ROLES.VISITANTE]
                }
            });



    }]);
