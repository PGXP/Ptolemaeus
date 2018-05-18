'use strict';

app.factory('UsuarioService', ['$http', function ($http) {
    var service = {};

    service.get = function (id) {
        return $http.get('admin/api/v1/usuarios/' + id).then(function (res) {
            return res;
        });
    };

    service.delete = function (id) {
        return $http.delete('admin/api/v1/usuarios/' + id).then(function (res) {
            return res;
        });
    };

    service.save = function (usuario) {
        return $http({
            url: 'admin/api/v1/usuarios',
            method: usuario.id ? "PUT" : "POST",
            data: usuario
        }).then(
            function (res) {
                return res;
            }
        );
    };

    service.findAll = function () {
        return $http.get('admin/api/v1/usuarios/condominio').then(function (res) {
            return res;
        });
    };

    service.searchByEmail = function (email) {
        return $http({
            url: 'admin/api/v1/usuarios/email/' + email,
            method: "GET"
        }).then(
            function (res) {
                return res;
            }
        );
    };

    return service;
}]);
