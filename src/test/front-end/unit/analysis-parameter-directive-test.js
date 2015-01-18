describe('Analysis Parameter Directive', function() {
  var scope, element;

  beforeEach(module('vita', 'templates'));

  beforeEach(inject(function($rootScope, $compile, $httpBackend, TestData) {

    scope = $rootScope.$new();

    scope.analysisParameters = TestData.analysisParameters.parameters;
    scope.analysisParameters.forEach(function(parameter) {
      parameter.value = parameter.defaultValue;
    });

    element = '<div data-analysis-parameter data-analysis-parameters="analysisParameters"></div>';

    element = $compile(element)(scope);
    scope.$digest();
  }));

  it('should set correct default values', function() {
    scope.analysisParameters.forEach(function (parameter) {
      var inputElement = element.find('#' + parameter.name);
      if(parameter.attributeType === 'boolean') {
        expect(inputElement.is(':checked')).toBe(parameter.value);
      } else if(parameter.attributeType === 'int') {
        expect(parseInt(inputElement.val())).toBe(parameter.value);
      } else if(parameter.attributeType === 'string') {
        expect(inputElement.val()).toBe(parameter.value);
      } else if(parameter.attributeType === 'enum') {
        inputElement = element.find('#' + parameter.value);
        expect(inputElement.val()).toBe(parameter.value);
      }
    });
  });

});
