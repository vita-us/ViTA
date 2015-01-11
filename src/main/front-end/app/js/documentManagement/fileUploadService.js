(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaServices');

  vitaDirectives.service('FileUpload', ['$http', function($http) {
    // http://uncorkedstudios.com/blog/multipartformdata-file-upload-with-angularjs
    this.uploadFileToUrl = function(file, uploadUrl, onSuccess, onError) {
      var fd = new FormData();
      fd.append('file', file);

      // TODO this is how parameters can be passed. They should be taken from the UI
      var parameters = { relationTimeStepCount: 30, wordCloudItemsCount: 20, stopWordListEnabled: true };
      fd.append('parameters', new Blob([ JSON.stringify(parameters) ], {type: 'application/json'}));

      $http.post(uploadUrl, fd, {
        transformRequest: angular.identity,
        headers: {
          'Content-Type': undefined
        }
      }).success(function(data, status) {
        if (onSuccess instanceof Function) {
          onSuccess(data, status);
        }
      }).error(function(data, status) {
        if (onError instanceof Function) {
          onError(data, status);
        }
      });
    };
  }]);

})(angular);
