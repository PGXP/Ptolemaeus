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
