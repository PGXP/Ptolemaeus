'use strict';

app.controller('UsuarioController', ['$q', '$scope', '$location', '$routeParams', '$rootScope',
    'AlertService', 'ValidationService', 'GrupoService', 'UsuarioService', 'ConstanteService', 'AuthService',
    function ($q, $scope, $location, $routeParams, $rootScope,
        AlertService, ValidationService, GrupoService, UsuarioService, ConstanteService, AuthService) {

        $scope.usuario = {};
        $scope.usuarios = [];
        $scope.palavraChave = "";

        $scope.dateOpened = false;

        $scope.currentPage = 1;
        $scope.pageSize = 7;

        $scope.openData = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.dateOpened = !$scope.dateOpened;
        };

        $scope.dateOptions = {
            formatYear: 'yyyy',
            startingDay: 7
        };

        $scope.findAuxiliar = function () {
            $q.all([
                ConstanteService.perfils()
            ]).then(function (result) {
                $scope.perfils = result[0].data;
            });
        };

        var id = $routeParams.id;
        var path = $location.$$url;

        if (path === '/usuario') {
            ValidationService.clear();
            $scope.findAuxiliar();
            UsuarioService.findAll().then(
                function (res) {
                    $scope.usuarios = res.data;
                },
                function (res) {

                    var data = res.data;
                    var status = res.status;
                    var message = res.message;

                    if (status === 401) {
                        AlertService.addWithTimeout('warning', message);
                    } else if (status === 412 || status === 422) {
                        ValidationService.registrarViolacoes(data);
                    } else if (status === 403) {
                        AlertService.showMessageForbiden();
                    }

                }
            );
        }

        if (path === '/usuario/edit') {
            ValidationService.clear();
            $scope.findAuxiliar();
            $scope.usuario = {};
        }

        if (path === '/usuario/edit/' + id) {
            ValidationService.clear();
            $scope.findAuxiliar();
            UsuarioService.get(id).then(
                function (res) {
                    $scope.usuario = res.data;
                    if ($scope.usuario.niver)
                        $scope.usuario.niver = new Date($scope.usuario.niver + 'T07:00:00Z');
                },
                function (res) {

                    var data = res.data;
                    var status = res.status;
                    var message = res.message;

                    if (status === 401) {
                        AlertService.addWithTimeout('warning', message);
                    } else if (status === 412 || status === 422) {
                        ValidationService.registrarViolacoes(data);
                    } else if (status === 403) {
                        AlertService.showMessageForbiden();
                    }

                }

            );
        }

        $scope.searchByEmail = function () {

            UsuarioService.searchByEmail($scope.usuario.email).then(
                function (res) {
                    $scope.usuario = res.data;
                    if ($scope.usuario.niver)
                        $scope.usuario.niver = new Date($scope.usuario.niver + 'T07:00:00Z');
                    AlertService.addWithTimeout('success', 'Usuário importado');
                },
                function (res) {

                    var data = res.data;
                    var status = res.status;
                    var message = res.data[0].error;

                    if (status === 412) {
                        AlertService.addWithTimeout('warning', message);
                    } else if (status === 422) {
                        ValidationService.registrarViolacoes(data);
                    } else if (status === 403) {
                        AlertService.showMessageForbiden();
                    }

                }
            );
        };

        $scope.new = function () {
            $location.path('/usuario/edit');
        };

        $scope.save = function () {

            UsuarioService.save($scope.usuario).then(
                function (res) {
                    AlertService.addWithTimeout('success', 'Usuário salvo com sucesso');
                    $location.path('/usuario');
                },
                function (res) {

                    var data = res.data;
                    var status = res.status;
                    var message = res.message;

                    if (status === 500) {
                        AlertService.addWithTimeout('warning', message);
                    } else if (status === 412 || status === 422) {
                        ValidationService.registrarViolacoes(data);
                    } else if (status === 403) {
                        AlertService.showMessageForbiden();
                    }

                }
            );
        };

        $scope.delete = function () {
            UsuarioService.delete($scope.usuario.id).then(
                function () {
                    AlertService.addWithTimeout('success', 'Usuário removido do condomínio');
                    $location.path('/usuario');
                    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
                },
                function (res) {

                    var data = res.data;
                    var status = res.status;
                    var message = res.message;

                    if (status === 500) {
                        AlertService.addWithTimeout('warning', "A remoção desse item pode prejudicar o sistema");
                    } else if (status === 401) {
                        AlertService.addWithTimeout('warning', message);
                    } else if (status === 412 || status === 422) {
                        ValidationService.registrarViolacoes(data);
                    } else if (status === 403) {
                        AlertService.showMessageForbiden();
                    }

                }
            );
        };

        $scope.edit = function (id) {
            $location.path('/usuario/edit/' + id);
        };

        $scope.setLembrar = function () {
            AuthService.setLembrar();
        }

        $scope.gridOptions = {
            enableSorting: true,
            paginationPageSizes: [13],
            paginationPageSize: 13,
            data: 'usuarios',
            columnDefs: [
                { field: 'fullname', name: 'Nome', width: "300" },
                { field: 'email', name: 'Email', width: "300" },
                { field: 'perfil', name: 'Pefil', width: "100" },
                { field: 'fone', name: 'Fone', width: "150" },

                { name: 'Ação', cellTemplate: '<a has-roles="Administrador,Sindico" ng-click="grid.appScope.edit(row.entity.id)" class="btn btn-warning btn-xs"><i class="icon-refresh"></i> Alterar</a>', width: "100" }]
        };

    }]);
