(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('DocumentViewWorkerConstants', function() {
    this.APP = 'APP';
    this.DOCUMENT_ID = 'DOCUMENT_ID';
    this.DOCUMENT_ID_REQUEST = 'DOCUMENT_ID_REQUEST';
    this.DOCUMENT_VIEW = 'DOCUMENT_VIEW';
    this.OCCURRENCES = 'OCCURRENCES';
    this.REGISTER = 'REGISTER';
  });

})(angular);
