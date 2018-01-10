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
