'use strict';

var app = angular
  .module('app', [
    'ngAria',
    'ngMessages',
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngAnimate',
    'ngTouch',
    'ui.bootstrap',
    'notification',
    'ngWebsocket',
    'ngFileUpload',
    'Config'
  ])
  .config([
    '$routeProvider',
    '$httpProvider',
    'USER_ROLES',
    function($routeProvider, USER_ROLES) {

      $routeProvider.when('/403', {
        templateUrl: 'views/403.html',
        data: { authorizedRoles: [USER_ROLES.NOT_LOGGED] }
      });

      $routeProvider.when('/arquivos', {
        templateUrl: 'views/arquivos.html',
        controller: 'ArquivosController',
        data: { authorizedRoles: [USER_ROLES.NOT_LOGGED] }
      });

      $routeProvider.when('/login', {
        templateUrl: 'views/login.html',
        controller: 'AuthController',
        data: { authorizedRoles: [USER_ROLES.NOT_LOGGED] }
      });

      $routeProvider.when('/dashboard', {
        templateUrl: 'views/dashboard/dashboard.html',
        controller: 'DashboardController',
        data: { authorizedRoles: [USER_ROLES.NOT_LOGGED] }
      });

      $routeProvider.otherwise({
        redirectTo: '/dashboard',
        data: { authorizedRoles: [USER_ROLES.NOT_LOGGED] }
      });
    }
  ]);

app.config([
  '$httpProvider',
  '$websocketProvider',
  '$sceDelegateProvider',
  function($httpProvider, $websocketProvider, $sceDelegateProvider) {
    // $websocketProvider.$setup({
    //   reconnect: true,
    //   reconnectInterval: 21000,
    //   enqueue: true
    // });

    $sceDelegateProvider.resourceUrlWhitelist(['self']);

    $httpProvider.interceptors.push([
      '$q',
      '$rootScope',
      'AppService',
      'ENV',
      function($q, $rootScope, AppService, ENV) {
        return {
          request: function(config) {
            $rootScope.$broadcast('loading-started');

            var token = AppService.getToken();

            if (config.url.indexOf('api') !== -1) {
              if (config.url.indexOf(ENV.apiEndpoint) === -1) {
                config.url = ENV.apiEndpoint + config.url;
              }
            }

            if (token) {
              config.headers['Authorization'] = 'Token ' + token;
            }

            return config || $q.when(config);
          },
          response: function(response) {
            $rootScope.$broadcast('loading-complete');
            return response || $q.when(response);
          },
          responseError: function(rejection) {
            $rootScope.$broadcast('loading-complete');
            return $q.reject(rejection);
          },
          requestError: function(rejection) {
            $rootScope.$broadcast('loading-complete');
            return $q.reject(rejection);
          }
        };
      }
    ]);

    $httpProvider.interceptors.push([
      '$injector',
      function($injector) {
        return $injector.get('AuthInterceptor');
      }
    ]);
  }
]);

app.run([
  '$rootScope',
  '$location',
  '$window',
  'AUTH_EVENTS',
  'APP_EVENTS',
  'USER_ROLES',
  'AuthService',
  'AppService',
  'AlertService',
  'WS',
  '$notification',
  '$http',
  function(
    $rootScope,
    $location,
    $window,
    AUTH_EVENTS,
    APP_EVENTS,
    USER_ROLES,
    AuthService,
    AppService,
    AlertService,
    WS,
    $notification,
    $http
  ) {
    $rootScope.$on('$routeChangeStart', function(event, next) {
      if (next.redirectTo !== '/') {
        var authorizedRoles = next.data.authorizedRoles;

        if (
          authorizedRoles[0] !== undefined &&
          authorizedRoles.indexOf(USER_ROLES.NOT_LOGGED) === -1
        ) {
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

    $rootScope.$on(AUTH_EVENTS.quantidade, function(emit, args) {
      $rootScope.$apply(function() {
        $rootScope.conectados = args.emit.data;
      });
    });

    $rootScope.$on(AUTH_EVENTS.notAuthorized, function() {
      console.log('notAuthorized');
      $location.path('/403');
    });

    $rootScope.$on(AUTH_EVENTS.notAuthenticated, function() {
      console.log('notAuthenticated');
      $rootScope.currentUser = null;
      AppService.removeToken();
      $location.path('/login');
    });

    $rootScope.$on(AUTH_EVENTS.sessionTimeout, function() {
      console.log('sessionTimeout');
    });

    $rootScope.$on(AUTH_EVENTS.loginFailed, function() {
      console.log('loginFailed');
      AppService.removeToken();
      $location.path('/login');
    });

    $rootScope.$on(AUTH_EVENTS.logoutSuccess, function() {
      console.log('logoutSuccess');
      WS.command('logout', $rootScope.currentUser.nome);
      $rootScope.currentUser = null;
      AppService.removeToken();
      $location.path('/dashboard');
    });

    $rootScope.$on(AUTH_EVENTS.loginSuccess, function() {
      $location.path('/dashboard');
      WS.command('login', $rootScope.currentUser.nome);
    });

    $rootScope.$on(APP_EVENTS.offline, function() {
      AlertService.addWithTimeout(
        'danger',
        'Servidor esta temporariamente indisponível, tente mais tarde'
      );
    });

    // Check if a new cache is available on page load.
    $window.addEventListener(
      'load',
      function(e) {
        $window.applicationCache.addEventListener(
          'updateready',
          function(e) {
            console.log($window.applicationCache.status);
            if (
              $window.applicationCache.status ===
              $window.applicationCache.UPDATEREADY
            ) {
              // Browser downloaded a new app cache.
              $window.location.reload();
              alert('Uma nova versão será carregada!');
            }
          },
          false
        );
      },
      false
    );
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
  sistema: 'sistema',
  mensagem: 'mensagem',
  produto: 'produto',
  fase: 'fase',
  quantidade: 'qtde'
});

app.constant('USER_ROLES', {
  ANALISE: 'ANALISE',
  PROSPECCAO: 'PROSPECCAO',
  INTERNALIZACAO: 'INTERNALIZACAO',
  SUSTENTACAO: 'SUSTENTACAO',
  DECLINIO: 'DECLINIO',
  ADMINISTRADOR: 'ADMINISTRADOR',
  CADASTRADOR: 'CADASTRADOR',
  CONSULTOR: 'CONSULTOR',
  LEGADO: 'LEGADO',
  NOT_LOGGED: 'NOT_LOGGED'
});

app.factory('AuthInterceptor', [
  '$rootScope',
  '$q',
  'AUTH_EVENTS',
  'APP_EVENTS',
  function($rootScope, $q, AUTH_EVENTS, APP_EVENTS) {
    return {
      responseError: function(response) {
        $rootScope.$broadcast(
          {
            0: APP_EVENTS.offline,
            404: APP_EVENTS.offline,
            503: APP_EVENTS.offline,
            401: AUTH_EVENTS.notAuthenticated,
            403: AUTH_EVENTS.notAuthorized,
            419: AUTH_EVENTS.sessionTimeout,
            440: AUTH_EVENTS.sessionTimeout
          }[response.status],
          response
        );

        return $q.reject(response);
      }
    };
  }
]);

app.value('version', '1.0.0');

angular.module('Config', [])

.constant('ENV', {name:'production',apiEndpoint:'/pto/'})

;
'use strict';

app.controller('ApplicationController', ['$rootScope', 'USER_ROLES', 'AuthService', '$notification',
    function ($rootScope, USER_ROLES, AuthService, $notification) {




    }]);
'use strict';

app.controller('ArquivosController', [
  '$scope',
  '$rootScope',
  '$http',
  'Upload',
  '$timeout',
  '$location',
  '$routeParams',
  'AlertService',
  'ArquivosService',
  function(
    $scope,
    $rootScope,
    $http,
    Upload,
    $timeout,
    $location,
    $routeParams,
    AlertService,
    ArquivosService
  ) {
    $scope.uploadFiles = function(files, errFiles) {
      $scope.files = files;
      $scope.errFiles = errFiles;
      angular.forEach(files, function(file) {
        file.upload = Upload.upload({
          url: 'api/v1/arquivos/upload',
          data: { file: file }
        });

        file.upload.then(
          function(response) {
            $timeout(function() {
              file.result = response.data;
            });
          },
          function(response) {
            if (response.status > 0)
              $scope.errorMsg = response.status + ': ' + response.data;
          },
          function(evt) {
            file.progress = Math.min(
              100,
              parseInt(100.0 * evt.loaded / evt.total)
            );
          }
        );
      });
    };
  }
]);

'use strict';
app.controller('AuthController', ['$scope', '$rootScope', 'AUTH_EVENTS', 'AuthService', '$notification',
    function ($scope, $rootScope, AUTH_EVENTS, AuthService, $notification) {

        $scope.credentials = {
            username: '',
            password: ''
        };
        function error(data, status) {
            $("[id$='-message']").text("");
            switch (status) {
                case 412:
                case 422:
                    $.each(data, function (i, violation) {
                        $("#" + violation.property + "-message").text(violation.message);
                    });
                    break;
                case 401:
                    $("#message").html("Usuário ou senha inválidos.");
                    break;
            }
        }


        $scope.login = function (credentials) {
            AuthService.login(credentials).then(function () {
                $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
            },
                function (response) {
                    $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
                    error(response.data, response.status);
                });
        };

        $scope.logout = function () {
            $rootScope.$broadcast(AUTH_EVENTS.logoutSuccess);
        };

    }]);
'use strict';

app.controller('DashboardController', [
  '$scope',
  'ftsService',
  function($scope, ftsService) {
    $scope.texto = '';
    $scope.resultados = [];

    $scope.buscaFTS = function() {
      ftsService.buscaFTS($scope.texto).then(function(result) {
        $scope.resultados = result;
      });
    };
  }
]);

'use strict';

app.directive('uiLinhabar', ['$rootScope', '$anchorScroll', function($rootScope, $anchorScroll) {
        return {
            restrict: 'AC',
            template: '<span class="bar"></span>',
            link: function(scope, el, attrs) {
                el.addClass('linhabar hide');
                scope.$on('$routeChangeStart', function(e) {
                    $anchorScroll();
                    el.removeClass('hide').addClass('active');
                });
                scope.$on('$routeChangeSuccess', function(event, toState, toParams, fromState) {
                    event.targetScope.$watch('$viewContentLoaded', function() {
                        el.addClass('hide').removeClass('active');
                    })
                });
            }
        }
    }]);

app.directive('backButton', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('click', function() {
                history.back();
                scope.$apply();
            });
        }
    };
});

app.directive('alerts', function() {
    return {
        restrict: 'E',
        templateUrl: 'partials/alerts.html'
    };
});

app.directive("loadingIndicator", function() {
    return {
        restrict: "A",
        templateUrl: 'partials/loading.html',
        link: function(scope, element, attrs) {

            scope.$on("loading-started", function(e) {
                element.css({"display": ""});
            });

            scope.$on("loading-complete", function(e) {
                element.css({"display": "none"});
            });

        }
    };
});

app.directive("autofill", function() {
    return {
        require: "ngModel",
        link: function(scope, element, attrs, ngModel) {
            scope.$on("autofill:update", function() {
                ngModel.$setViewValue(element.val());
            });
        }
    };
});

app.directive("appVersion", ["version", function(version) {
        return function(scope, elm, attrs) {
            elm.text(version);
        };
    }]);

app.directive("hasRoles", ["AuthService", function(AuthService) {
        return {
            restrict: "A",
            link: function(scope, element, attributes) {

                var paramRoles = attributes.hasRoles.split(",");

                if (!AuthService.isAuthorized(paramRoles)) {
                    element.remove();
                }
            }
        };
    }]);

app.directive("confirmButton", ["$timeout", function($timeout) {
    return {
        restrict: 'A',
        scope: {
            actionOK: '&confirmAction',
            actionCancel: '&cancelAction'
        },
        link: function(scope, element, attrs) {
            var buttonId, html, message, nope, title, yep;
            buttonId = Math.floor(Math.random() * 10000000000);
            attrs.buttonId = buttonId;
            message = attrs.message || "Tem certeza?";
            yep = attrs.yes || "Sim";
            nope = attrs.no || "Não";
            title = attrs.title || "Confirm";

            element.bind('click', function(e) {

                var box = bootbox.dialog({
                    message: message,
                    title: title,
                    buttons: {
                        success: {
                            label: yep,
                            className: "btn-success",
                            callback: function() {
                                $timeout(function() {
                                    scope.$apply(scope.actionOK);
                                });
                            }
                        },
                        danger: {
                            label: nope,
                            className: "btn-danger",
                            callback: function() {
                                scope.$apply(scope.actionCancel);
                            }
                        }
                    }
                });

            });
        }
    };
}]);

app.directive('validationMsg', ['ValidationService', function(ValidationService) {
        return {
            restrict: 'E',
            scope: {
                propriedade: '@'
            },
            template: "<div class='error text-danger' ng-show='msg'><small class='error' >{{msg}}</small></div>",
            controller: ["$scope", function($scope) {
                $scope.$watch(function() {
                    return ValidationService.validation[$scope.propriedade];
                },
                    function(msg) {
                        $scope.msg = msg;
                    }
                );
            }]
        };
    }]);


app.directive("maxLength", ['$compile', 'AlertService', function($compile, AlertService) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, elem, attrs, ctrl) {
                attrs.$set("ngTrim", "false");
                var maxlength = parseInt(attrs.maxLength, 10);
                ctrl.$parsers.push(function(value) {
                    if (value !== undefined && value.length !== undefined) {
                        if (value.length > maxlength) {
                            AlertService.addWithTimeout('warning', 'O valor máximo de caracteres (' + maxlength + ') para esse campo já foi alcançado');
                            value = value.substr(0, maxlength);
                            ctrl.$setViewValue(value);
                            ctrl.$render();
                        }
                    }
                    return value;
                });
            }
        };
    }]);

app.directive("hasRolesDisable", ["AuthService", function(AuthService) {
        return {
            restrict: "A",
            link: function(scope, element, attributes) {

                var paramRoles = attributes.hasRolesDisable.split(",");

                if (!AuthService.isAuthorized(paramRoles)) {
                    angular.forEach(element.find('input, select, textarea, button, a'), function(node) {
                        var ele = angular.element(node);
                        ele.attr("disabled", "true");
                    });
                }
            }
        };
    }]);

app.directive('ngEnter', function() {
    return function(scope, element, attrs) {
        element.bind("keydown keypress", function(event) {
            if (event.which === 13) {
                scope.$apply(function() {
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});

app.directive('queryBuilder', ['$compile', function ($compile) {
    return {
        restrict: 'E',
        scope: {
            group: '=',
            fields: '='
        },
        templateUrl: 'partials/queryBuilderDirective.html',
        compile: function (element, attrs) {
            var content, directive;
            content = element.contents().remove();
            return function (scope, element, attrs) {
                scope.hideGroup = true;
                
                scope.count = 1;
                scope.limit = 5;
                scope.maxCount = false;
                
                scope.operators = [
                    {name: 'E'},
                    {name: 'OU'}
                ];

                scope.conditions = [
                    {name: '='},
                    {name: '<>'},
                    {name: '<'},
                    {name: '<='},
                    {name: '>'},
                    {name: '>='}
                ];
                
                scope.addCondition = function () {
                    scope.group.rules.push({
                        condition: '=',
                        field: '=',
                        data: '',
                    });
                    scope.count += 1;
                    if (scope.count === scope.limit) {
                        scope.maxCount = true;
                    }
                };

                scope.removeCondition = function (index) {
                    scope.group.rules.splice(index, 1);
                    scope.count -= 1;
                    scope.maxCount = false;
                };

                scope.addGroup = function () {
                    scope.group.rules.push({
                        group: {
                            operator: 'E',
                            rules: []
                        }
                    });
                };

                scope.removeGroup = function () {
                    "group" in scope.$parent && scope.$parent.group.rules.splice(scope.$parent.$index, 1);
                };

                directive || (directive = $compile(content));

                element.append(directive(scope, function ($compile) {
                    return $compile;
                }));
            }
        }
    }

}]);




'use strict';

app.filter('tamanho', function() {
    return function humanFileSize(bytes) {
        var thresh = 1024;
        if (bytes < thresh)
            return bytes + ' B';
        var units = ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        var u = -1;
        do {
            bytes /= thresh;
            ++u;
        } while (bytes >= thresh);
        return bytes.toFixed(1) + ' ' + units[u];
    };
});

app.filter('tipoArquivo', function() {
    var tipos = {};
    var path = "images/filetypes/";
    tipos['image'] = path + "doc.png";
    tipos['doc'] = path + "doc.png";
    tipos['xls'] = path + "xls.png";
    tipos['zip'] = path + "zip.png";
    tipos['pdf'] = path + "pdf.png";
    tipos['unknow'] = path + "unknow.png";

    return function(tipo) {
        var url = tipos['unknow'];
        if (tipo.indexOf('image') > -1) {
            url = tipos['image'];
        } else if (tipo.indexOf('doc') > -1) {
            url = tipos['doc'];
        } else if (tipo.indexOf('odt') > -1) {
            url = tipos['odt'];
        } else if (tipo.indexOf('xls') > -1) {
            url = tipos['xls'];
        } else if (tipo.indexOf('zip') > -1) {
            url = tipos['zip'];
        } else if (tipo.indexOf('rar') > -1) {
            url = tipos['zip'];
        } else if (tipo.indexOf('tar') > -1) {
            url = tipos['zip'];
        } else if (tipo.indexOf('gz') > -1) {
            url = tipos['zip'];
        } else if (tipo.indexOf('pdf') > -1) {
            url = tipos['pdf'];
        }
        return url;
    };
});

var operacoes = {
    CRIAR: {icone: "glyphicon glyphicon-plus", badge: "info"},
    ATUALIZAR: {icone: "glyphicon glyphicon-floppy-disk", badge: "primary"},
    APROVAR: {icone: "glyphicon glyphicon-thumbs-up", badge: "success"},
    REPROVAR: {icone: "glyphicon glyphicon-thumbs-down", badge: "danger"},
    FINALIZAR: {icone: "glyphicon glyphicon-ok", badge: "warning"},
    EXCLUIR: {icone: "glyphicon glyphicon-alert", badge: "danger"}
};

app.filter('operacaoIcone', function() {
    return function(operacao) {
        return operacoes[operacao].icone;
    };
});

app.filter('operacaoClass', function() {
    return function(operacao) {
        return operacoes[operacao].badge;
    };
});

app.filter('startFrom', function() {
    return function(input, start) {
        if (!input)
            return input;
        start = +start; //parse to int
        return input.slice(start);
    };
});

app.filter('range', function() {
    return function(input, total) {
        total = parseInt(total);
        for (var i = 0; i < total; i++)
            input.push(i);
        return input;
    };
});

app.filter('version', ["version", function(version) {
    return function(text) {
        return String(text).replace(/\%VERSION\%/mg, version);
    };
}]);

app.filter('trunk', function() {
    return function(value, wordwise, max, tail) {
        if (!value)
            return '';

        max = parseInt(max, 10);
        if (!max)
            return value;
        if (value.length <= max)
            return value;

        value = value.substr(0, max);
        if (wordwise) {
            var lastspace = value.lastIndexOf(' ');
            if (lastspace != -1) {
                value = value.substr(0, lastspace);
            }
        }

        return value + (tail || ' …');
    };
});

app.filter('buscaPor', function() {
    return function(arr, searchString) {
        if (!searchString) {
            return arr;
        }
        var result = [];
        searchString = searchString.toLowerCase();
        angular.forEach(arr, function(objeto) {
            if (objeto.name.toLowerCase().indexOf(searchString) !== -1) {
                result.push(objeto);
            }
        });
        return result;
    };
});

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
'use strict';

app.factory('ArquivosService', [
  '$http',
  function($http) {
    var service = {};

    service.excluir = function(id) {
      return $http.delete('api/arquivos/' + id).then(function(res) {
        return;
      });
    };

    service.carregarAnexos = function(id) {
      return $http.get('api/arquivos/' + id).then(function(res) {
        return res.data;
      });
    };

    return service;
  }
]);

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


'use strict';

app.factory('DashboardService', [
  '$http',
  function($http) {
    var service = {};

    service.get = function() {
      return $http.get('api/v1').then(function(res) {
        return res.data;
      });
    };

    return service;
  }
]);

'use strict';

app.factory('ftsService', ['$http', '$q', function($http, $q) {
        var service = {};

        service.buscaFTS = function(texto) {
            var deferred = $q.defer();
            texto = texto.replace('/', '|');
            $http({
                url: 'api/v1/arquivos/fts/' + texto,
                method: "GET"
            }).success(function(data) {
                deferred.resolve(data);
            }).error(function(data, status) {
                deferred.reject([data, status]);
            });

            return deferred.promise;
        };

        return service;
    }]);

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
