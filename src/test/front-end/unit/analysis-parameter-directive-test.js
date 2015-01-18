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

  it('should change string model when input value changes', function() {
    var inputElement = element.find('#' + scope.analysisParameters[5].name);
    inputElement.val('bla').trigger('input');
    expect(scope.analysisParameters[5].value).toBe('bla');
  });

  it('should change number model when input value changes', function() {
    var inputElement = element.find('#' + scope.analysisParameters[0].name);
    inputElement.val(15).trigger('input');
    expect(scope.analysisParameters[0].value).toBe(15);
  });

  it('should change boolean model when input value changes', function() {
    var oldValue = scope.analysisParameters[2].value;
    var inputElement = element.find('#' + scope.analysisParameters[2].name);
    inputElement.trigger('click');
    expect(scope.analysisParameters[2].value).toBe(!oldValue);
  });

  it('should change enum model when input value changes', function() {
    var newValue = scope.analysisParameters[4].values[0];
    var inputElement = element.find('#' + newValue.name);
    inputElement.prop("checked", true).trigger("click");
    expect(scope.analysisParameters[4].value).toBe(newValue.name);
  });

});
