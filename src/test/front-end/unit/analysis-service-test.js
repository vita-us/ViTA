describe('AnalysisService', function() {

  beforeEach(module('vitaServices'));

  it('should allow a stop', inject(function($httpBackend, Analysis) {
    $httpBackend.expectPUT('webapi/documents/doc13a/analysis/stop').respond(undefined);

    Analysis.stop('doc13a');

    $httpBackend.flush();
  }));

  it('should allow a restart', inject(function($httpBackend, Analysis) {
    $httpBackend.expectPUT('webapi/documents/doc13a/analysis/restart').respond(undefined);

    Analysis.restart('doc13a');

    $httpBackend.flush();
  }));

});
