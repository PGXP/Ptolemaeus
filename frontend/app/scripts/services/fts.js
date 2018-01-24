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
