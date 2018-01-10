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
